/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.tracker.exporter

import io.reactivex.Observable
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal abstract class TrackerDownloadCall<T, Q : BaseTrackerQueryBundle> (
    private val rxCallExecutor: RxAPICallExecutor,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory
) {

    fun download(params: ProgramDataDownloadParams): Observable<D2Progress> {
        val observable = Observable.defer {
            val progressManager = D2ProgressManager(null)
            if (userOrganisationUnitLinkStore.count() == 0) {
                return@defer Observable.just(
                    progressManager.increaseProgress(TrackedEntityInstance::class.java, true)
                )
            } else {
                val relatives = RelationshipItemRelatives()
                return@defer Observable.concat(
                    systemInfoModuleDownloader.downloadWithProgressManager(progressManager),
                    downloadInternal(params, progressManager, relatives),
                    downloadRelationships(progressManager, relatives)
                )
            }
        }
        return rxCallExecutor.wrapObservableTransactionally(observable, true)
    }

    abstract fun getBundles(params: ProgramDataDownloadParams): List<Q>

    abstract fun getItems(query: TrackerAPIQuery): List<T>


    abstract fun persistItems(items: List<T>, params: IdentifiableDataHandlerParams, relatives: RelationshipItemRelatives)

    abstract fun updateLastUpdated(bundle: Q)

    protected abstract fun queryByUids(
        bundle: Q,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): ItemsWithPagingResult

    protected abstract fun getQuery(
        bundle: Q,
        bundleProgram: ItemsByProgramCount,
        orgunitUid: String?,
        iterables: BundleIterables
    ): TrackerAPIQuery

    fun downloadInternal(
        params: ProgramDataDownloadParams,
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {

        return Observable.create { emitter ->
            val iterables = BundleIterables(
                0, true, 0, mutableMapOf(), mutableListOf(), mutableListOf()
            )
            val bundles: List<Q> = getBundles(params)

            for (bundle in bundles) {
                if (bundle.commonParams().uids.isNotEmpty()) {
                    val result = queryByUids(bundle, params.overwrite(), relatives)

                    result.d2Error?.let {
                        emitter.onError(it)
                        return@create
                    }
                } else {
                    iterables.count = 0
                    iterables.bundleOrgUnitPrograms = mutableMapOf()
                    iterables.orgUnitsBundleToDownload = bundle.orgUnits().toMutableList()

                    bundle.orgUnits()
                        .ifEmpty { listOf(null) }
                        .forEach { orgUnit ->
                            iterables.bundleOrgUnitPrograms[orgUnit] = when (orgUnit) {
                                null -> listOf(ItemsByProgramCount(null, 0))
                                else ->
                                    bundle.commonParams().programs
                                        .map { ItemsByProgramCount(it, 0) }
                            }.toMutableList()
                        }
                    var iterationCount = 0
                    do {
                        iterateBundle(bundle, params, iterables, relatives)
                        iterationCount++
                    } while (iterationNotFinished(bundle, params, iterables, iterationCount))

                    updateLastUpdated(bundle)
                }
            }
            emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance::class.java, true))
            emitter.onComplete()
        }
    }

    private fun iterationNotFinished(
        bundle: Q,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        iterationCount: Int
    ): Boolean {
        return params.limitByProgram() != true &&
                iterables.count < bundle.commonParams().limit &&
                iterables.orgUnitsBundleToDownload.isNotEmpty() &&
                iterationCount < max(bundle.commonParams().limit * BUNDLE_SECURITY_FACTOR, BUNDLE_ITERATION_LIMIT)
    }

    private fun iterateBundle(
        bundle: Q,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        val limitPerCombo = getBundleLimit(bundle, params, iterables)

        for ((orgUnitUid, orgunitPrograms) in iterables.bundleOrgUnitPrograms.entries) {
            iterables.emptyOrCorruptedPrograms = emptyList<String?>().toMutableList()

            val pendingTeis = bundle.commonParams().limit - iterables.count
            iterables.bundleLimit = min(limitPerCombo, pendingTeis)

            if (iterables.bundleLimit <= 0 || orgunitPrograms.isEmpty()) {
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
        bundle: Q,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        for (bundleProgram in iterables.bundleOrgUnitPrograms[orgUnitUid]!!) {
            if (iterables.count >= bundle.commonParams().limit) {
                break
            }

            val trackerQuery = getQuery(bundle, bundleProgram, orgUnitUid, iterables)

            val result = getItemsForOrgUnitProgramCombination(
                trackerQuery,
                iterables.bundleLimit,
                bundleProgram.itemCount,
                params.overwrite(),
                relatives
            )

            iterables.count += result.count
            bundleProgram.itemCount += result.count
            iterables.successfulSync = iterables.successfulSync && result.successfulSync

            if (result.emptyProgram || !result.successfulSync) {
                iterables.emptyOrCorruptedPrograms = (iterables.emptyOrCorruptedPrograms + bundleProgram.program)
                    .toMutableList()
            }
        }
    }

    private fun getBundleLimit(
        bundle: Q,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables
    ): Int {
        return when {
            params.uids().isNotEmpty() -> params.uids().size
            params.limitByProgram() != true -> {
                val numOfCombinations = iterables.bundleOrgUnitPrograms.values.sumOf { it.size }
                val pendingTeis = bundle.commonParams().limit - iterables.count

                if (numOfCombinations == 0 || pendingTeis == 0) 0
                else ceil(pendingTeis.toDouble() / numOfCombinations.toDouble()).roundToInt()
            }
            else -> bundle.commonParams().limit - iterables.count
        }
    }

    private fun getItemsForOrgUnitProgramCombination(
        trackerQueryBuilder: TrackerAPIQuery,
        combinationLimit: Int,
        downloadedTEIs: Int,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): ItemsWithPagingResult {
        return try {
            getItemsWithPaging(trackerQueryBuilder, combinationLimit, downloadedTEIs, overwrite, relatives)
        } catch (ignored: D2Error) {
            // TODO Build result
            ItemsWithPagingResult(0, false, null, false)
        }
    }

    @Throws(D2Error::class)
    private fun getItemsWithPaging(
        query: TrackerAPIQuery,
        combinationLimit: Int,
        downloadedCount: Int,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): ItemsWithPagingResult {

        var downloadedItemsForCombination = 0
        var emptyProgram = false

        val pagingList = ApiPagingEngine.getPaginationList(query.pageSize, combinationLimit, downloadedCount)

        for (paging in pagingList) {
            val pageQuery = query.copy(
                pageSize = paging.pageSize(),
                page = paging.page()
            )

            val items = getItems(pageQuery)

            val itemsToPersist = getItemsToPersist(paging, items)

            val persistParams = IdentifiableDataHandlerParams(
                hasAllAttributes = true,
                overwrite = overwrite,
                asRelationship = false,
                program = pageQuery.commonParams.program
            )

            persistItems(itemsToPersist, persistParams, relatives)

            downloadedItemsForCombination += itemsToPersist.size

            if (items.size < paging.pageSize()) {
                emptyProgram = true
                break
            }
        }

        return ItemsWithPagingResult(downloadedItemsForCombination, true, null, emptyProgram)
    }


    private fun getItemsToPersist(paging: Paging, pageItems: List<T>): List<T> {

        return if (paging.isFullPage && pageItems.size > paging.previousItemsToSkipCount()) {
            val toIndex = min(
                pageItems.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageItems.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageItems
        }
    }

    protected class ItemsWithPagingResult(
        var count: Int,
        var successfulSync: Boolean,
        var d2Error: D2Error?,
        var emptyProgram: Boolean
    )

    protected class ItemsByProgramCount(val program: String?, var itemCount: Int)

    protected class BundleIterables(
        var count: Int,
        var successfulSync: Boolean,
        var bundleLimit: Int,
        var bundleOrgUnitPrograms: MutableMap<String?, MutableList<ItemsByProgramCount>>,
        var orgUnitsBundleToDownload: MutableList<String?>,
        var emptyOrCorruptedPrograms: MutableList<String?>
    )

    companion object {
        const val BUNDLE_ITERATION_LIMIT = 1000
        const val BUNDLE_SECURITY_FACTOR = 2
    }

    private fun downloadRelationships(
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {
        return relationshipDownloadAndPersistCallFactory.downloadAndPersist(relatives).andThen(
            Observable.just(
                progressManager.increaseProgress(
                    TrackedEntityInstance::class.java, true
                )
            )
        )
    }
}
