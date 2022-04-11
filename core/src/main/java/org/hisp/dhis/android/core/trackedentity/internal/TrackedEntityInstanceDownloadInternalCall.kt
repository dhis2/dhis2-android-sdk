/*
 *  Copyright (c) 2004-2021, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

@Reusable
internal class TrackedEntityInstanceDownloadInternalCall @Inject constructor(
    private val queryFactory: TrackerQueryBundleFactory,
    private val persistenceCallFactory: TrackedEntityInstancePersistenceCallFactory,
    private val endpointCallFactory: TrackedEntityInstancesEndpointCallFactory,
    private val apiCallExecutor: RxAPICallExecutor,
    private val lastUpdatedManager: TrackedEntityInstanceLastUpdatedManager
) {

    fun downloadTeis(
        params: ProgramDataDownloadParams,
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {

        return Observable.create { emitter ->
            val iterables = BundleIterables(
                0, true, 0, mutableMapOf(), mutableListOf(), mutableListOf()
            )
            val bundles: List<TrackerQueryBundle> = queryFactory.getQueries(params)

            for (bundle in bundles) {
                iterables.teisCount = 0
                iterables.bundleOrgUnitPrograms = mutableMapOf()
                iterables.orgUnitsBundleToDownload = bundle.orgUnits().toMutableList()
                bundle.orgUnits()
                    .ifEmpty { listOf(null) }
                    .forEach { orgUnit ->
                        iterables.bundleOrgUnitPrograms[orgUnit] = when (orgUnit) {
                            null -> listOf(TEIsByProgramCount(null, 0))
                            else ->
                                bundle.commonParams().programs
                                    .map { TEIsByProgramCount(it, 0) }
                        }.toMutableList()
                    }
                var iterationCount = 0
                do {
                    iterateBundle(bundle, params, iterables, relatives)
                    iterationCount++
                } while (iterationNotFinished(bundle, params, iterables, iterationCount))

                if (params.uids().isEmpty()) {
                    lastUpdatedManager.update(bundle)
                }
            }
            emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance::class.java, true))
            emitter.onComplete()
        }
    }

    private fun iterationNotFinished(
        bundle: TrackerQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        iterationCount: Int
    ): Boolean {
        return params.limitByProgram() != true &&
            iterables.teisCount < bundle.commonParams().limit &&
            iterables.orgUnitsBundleToDownload.isNotEmpty() &&
            iterationCount < max(bundle.commonParams().limit * BUNDLE_SECURITY_FACTOR, BUNDLE_ITERATION_LIMIT)
    }

    private fun iterateBundle(
        bundle: TrackerQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        val limitPerCombo = getBundleLimit(bundle, params, iterables)

        for (orgUnitUid in iterables.bundleOrgUnitPrograms.keys) {
            iterables.emptyOrCorruptedPrograms = emptyList<String?>().toMutableList()
            val orgunitPrograms = iterables.bundleOrgUnitPrograms[orgUnitUid]

            val pendingTeis = bundle.commonParams().limit - iterables.teisCount
            iterables.bundleLimit = min(limitPerCombo, pendingTeis)

            if (iterables.bundleLimit <= 0 || orgunitPrograms.isNullOrEmpty()) {
                iterables.orgUnitsBundleToDownload = (iterables.orgUnitsBundleToDownload - orgUnitUid).toMutableList()
                continue
            }

            iterateBundleProgram(orgUnitUid, bundle, params, iterables, relatives)

            iterables.bundleOrgUnitPrograms[orgUnitUid] = iterables.bundleOrgUnitPrograms[orgUnitUid]!!.filter {
                !iterables.emptyOrCorruptedPrograms.contains(it.program)
            }.toMutableList()
        }
    }

    private fun iterateBundleProgram(
        orgUnitUid: String?,
        bundle: TrackerQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        for (bundleProgram in iterables.bundleOrgUnitPrograms[orgUnitUid]!!) {
            if (iterables.teisCount >= bundle.commonParams().limit) {
                break
            }
            val trackerQueryBuilder = TrackerQuery.builder()
                .commonParams(
                    bundle.commonParams().copy(
                        program = bundleProgram.program,
                        limit = iterables.bundleLimit
                    )
                )
                .lastUpdatedStr(lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()))
                .orgUnit(orgUnitUid)
                .uids(params.uids())

            val result = getTEIsForOrgUnitProgramCombination(
                trackerQueryBuilder,
                iterables.bundleLimit,
                bundleProgram.teiCount,
                params.overwrite(),
                relatives
            )

            iterables.teisCount += result.teiCount
            bundleProgram.teiCount += result.teiCount
            iterables.successfulSync = iterables.successfulSync && result.successfulSync

            if (result.emptyProgram || !result.successfulSync) {
                iterables.emptyOrCorruptedPrograms = (iterables.emptyOrCorruptedPrograms + bundleProgram.program)
                    .toMutableList()
            }
        }
    }

    private fun getBundleLimit(
        bundle: TrackerQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables
    ): Int {
        return when {
            params.uids().isNotEmpty() -> params.uids().size
            params.limitByProgram() != true -> {
                val numOfCombinations = iterables.bundleOrgUnitPrograms.values.map { it.size }.sum()
                val pendingTeis = bundle.commonParams().limit - iterables.teisCount

                if (numOfCombinations == 0) 0
                else ceil(pendingTeis.toDouble() / numOfCombinations.toDouble()).roundToInt()
            }
            else -> bundle.commonParams().limit - iterables.teisCount
        }
    }

    private fun getTEIsForOrgUnitProgramCombination(
        trackerQueryBuilder: TrackerQuery.Builder,
        combinationLimit: Int,
        downloadedTEIs: Int,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): TEIsWithPagingResult {
        var result = TEIsWithPagingResult(0, successfulSync = true, emptyProgram = false)

        try {
            result = getTEIsWithPaging(trackerQueryBuilder, combinationLimit, downloadedTEIs, overwrite, relatives)
        } catch (ignored: D2Error) {
            result.successfulSync = false
        }

        return result
    }

    @Throws(D2Error::class)
    private fun getTEIsWithPaging(
        teiQueryBuilder: TrackerQuery.Builder,
        combinationLimit: Int,
        downloadedTEIs: Int,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): TEIsWithPagingResult {

        var downloadedTEIsForCombination = 0
        var emptyProgram = false
        val baseQuery = teiQueryBuilder.build()

        val pagingList = ApiPagingEngine.getPaginationList(baseQuery.pageSize(), combinationLimit, downloadedTEIs)

        for (paging in pagingList) {
            teiQueryBuilder.pageSize(paging.pageSize())
            teiQueryBuilder.page(paging.page())

            val pageTEIs = apiCallExecutor.wrapSingle(
                endpointCallFactory.getCall(teiQueryBuilder.build()), true
            ).blockingGet().items()

            val teisToPersist = getTEIsToPersist(paging, pageTEIs)

            val isFullUpdate = baseQuery.commonParams().program == null
            persistenceCallFactory.persistTEIs(teisToPersist, isFullUpdate, overwrite, relatives).blockingAwait()

            downloadedTEIsForCombination += teisToPersist.size

            if (pageTEIs.size < paging.pageSize()) {
                emptyProgram = true
                break
            }
        }

        return TEIsWithPagingResult(downloadedTEIsForCombination, true, emptyProgram)
    }

    private fun getTEIsToPersist(paging: Paging, pageTEIs: List<TrackedEntityInstance>): List<TrackedEntityInstance> {

        return if (paging.isFullPage && pageTEIs.size > paging.previousItemsToSkipCount()) {
            val toIndex = min(
                pageTEIs.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageTEIs.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageTEIs
        }
    }

    private class TEIsWithPagingResult(var teiCount: Int, var successfulSync: Boolean, var emptyProgram: Boolean)

    private class TEIsByProgramCount(val program: String?, var teiCount: Int)

    private class BundleIterables(
        var teisCount: Int,
        var successfulSync: Boolean,
        var bundleLimit: Int,
        var bundleOrgUnitPrograms: MutableMap<String?, MutableList<TEIsByProgramCount>>,
        var orgUnitsBundleToDownload: MutableList<String?>,
        var emptyOrCorruptedPrograms: MutableList<String?>
    )

    companion object {
        const val BUNDLE_ITERATION_LIMIT = 1000
        const val BUNDLE_SECURITY_FACTOR = 2
    }
}
