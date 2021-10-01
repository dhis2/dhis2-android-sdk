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
import kotlin.math.min
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
            var successfulSync = true
            val bundles: List<TrackerQueryBundle> = queryFactory.getQueries(params)

            for (bundle in bundles) {

                var teisCount = 0

                val bundleOrgUnitPrograms = mutableMapOf<String?, MutableList<TEIsByProgramCount>>()
                bundle.orgUnits().forEach { orgUnit ->
                    bundleOrgUnitPrograms[orgUnit] = bundle.commonParams().programs
                        .map { TEIsByProgramCount(it, 0) }
                        .ifEmpty { emptyList() }
                        .toMutableList()
                }
                val orgUnitsBundleToDownload = bundle.orgUnits().toMutableList()

                do {
                    for (orgUnitUid in bundleOrgUnitPrograms.keys) {
                        val bundlePrograms: MutableList<TEIsByProgramCount> = bundleOrgUnitPrograms[orgUnitUid]!!
                        val emptyOrCorruptedPrograms = emptyList<String>().toMutableList()

                        if (bundlePrograms.size <= 0) {
                            orgUnitsBundleToDownload -= orgUnitUid
                            break
                        }

                        val bundleLimit: Int = if (params.limitByProgram() != true) {
                            (bundle.commonParams().limit - teisCount)
                                .div(bundleOrgUnitPrograms.keys.size * bundlePrograms.size)
                        } else bundle.commonParams().limit - teisCount

                        if (teisCount >= bundle.commonParams().limit || bundleLimit <= 0) {
                            orgUnitsBundleToDownload -= orgUnitUid
                            break
                        }

                        for (bundleProgram in bundlePrograms) {
                            if (teisCount >= bundle.commonParams().limit) {
                                break
                            }
                            val trackerQueryBuilder = TrackerQuery.builder()
                                .commonParams(
                                    bundle.commonParams().copy(
                                        program = bundleProgram.program,
                                        limit = bundleLimit
                                    )
                                )
                                .lastUpdatedStr(lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()))
                                .orgUnit(orgUnitUid)
                                .uids(params.uids())

                            val result = getTEIsForOrgUnitProgramCombination(
                                trackerQueryBuilder,
                                bundleLimit,
                                bundleProgram.teiCount,
                                params.overwrite(),
                                relatives
                            )

                            teisCount += result.teiCount
                            bundleProgram.teiCount += result.teiCount
                            successfulSync = successfulSync && result.successfulSync

                            if (result.emptyProgram || !result.successfulSync) {
                                emptyOrCorruptedPrograms += bundleProgram.program
                            }
                        }

                        bundleOrgUnitPrograms[orgUnitUid] = bundleOrgUnitPrograms[orgUnitUid]!!.filter {
                            !emptyOrCorruptedPrograms.contains(it.program)
                        }.toMutableList()
                    }
                } while (params.limitByProgram() != true &&
                    teisCount < bundle.commonParams().limit &&
                    orgUnitsBundleToDownload.isNotEmpty()
                )

                if (params.uids().isEmpty()) {
                    lastUpdatedManager.update(bundle)
                }
            }
            emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance::class.java, true))
            emitter.onComplete()
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
                endpointCallFactory.getCall(teiQueryBuilder.build()), true).blockingGet().items()

            val teisToPersist = getTEIsToPersist(paging, pageTEIs)

            apiCallExecutor.run {
                val isFullUpdate = baseQuery.commonParams().program == null
                wrapCompletableTransactionally(
                    persistenceCallFactory.persistTEIs(teisToPersist, isFullUpdate, overwrite, relatives), true
                )
                    .blockingGet()
            }

            downloadedTEIsForCombination += teisToPersist.size

            if (pageTEIs.size < paging.pageSize()) {
                emptyProgram = true
                break
            }
        }

        return TEIsWithPagingResult(downloadedTEIsForCombination, true, emptyProgram)
    }

    private fun getTEIsToPersist(paging: Paging, pageTEIs: List<TrackedEntityInstance>): List<TrackedEntityInstance> {

        return if (fullPage(paging) && pageTEIs.size > paging.previousItemsToSkipCount()) {
            val toIndex = min(
                pageTEIs.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageTEIs.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageTEIs
        }
    }

    private fun fullPage(paging: Paging): Boolean {
        return paging.isLastPage || paging.previousItemsToSkipCount() > 0 || paging.posteriorItemsToSkipCount() > 0
    }

    private class TEIsWithPagingResult(var teiCount: Int, var successfulSync: Boolean, var emptyProgram: Boolean)

    private class TEIsByProgramCount(val program: String, var teiCount: Int)
}
