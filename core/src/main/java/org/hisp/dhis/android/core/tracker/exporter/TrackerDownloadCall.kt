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

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging
import org.hisp.dhis.android.core.arch.call.D2ProgressSyncStatus
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Suppress("TooManyFunctions")
internal abstract class TrackerDownloadCall<T, Q : BaseTrackerQueryBundle>(
    private val rxCallExecutor: RxAPICallExecutor,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory
) {

    fun download(params: ProgramDataDownloadParams): Observable<TrackerD2Progress> {
        return Observable.defer {
            val progressManager = TrackerD2ProgressManager(null)
            if (userOrganisationUnitLinkStore.count() == 0) {
                return@defer Observable.fromCallable {
                    progressManager.setTotalCalls(1)
                    progressManager.increaseProgress(TrackedEntityInstance::class.java, true)
                }
            } else {
                val relatives = RelationshipItemRelatives()
                return@defer systemInfoModuleDownloader.downloadWithProgressManager(progressManager)
                    .switchMap {
                        Observable.merge(
                            Observable.defer { downloadInternal(params, progressManager, relatives) },
                            Observable.defer { downloadRelationships(progressManager, relatives) },
                            Observable.fromCallable { progressManager.complete() }
                        )
                    }
            }
        }
    }

    private fun downloadInternal(
        params: ProgramDataDownloadParams,
        progressManager: TrackerD2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<TrackerD2Progress> {
        return Observable.create { emitter ->
            val bundles: List<Q> = getBundles(params)
            val programs = bundles.flatMap { it.commonParams().programs }

            progressManager.setTotalCalls(programs.size + 2)
            progressManager.setPrograms(programs)
            emitter.onNext(progressManager.getProgress())

            for (bundle in bundles) {
                if (bundle.commonParams().uids.isNotEmpty()) {
                    val observable = Completable.fromCallable {
                        val result = queryByUids(bundle, params.overwrite(), relatives)

                        result.d2Error?.let {
                            emitter.onError(it)
                        }
                    }
                    rxCallExecutor.wrapCompletableTransactionally(observable, cleanForeignKeys = true).blockingAwait()
                } else {
                    val orgunitPrograms = bundle.orgUnits()
                        .associateWith {
                            bundle.commonParams().programs
                                .map { ItemsByProgramCount(it, 0) }
                                .toMutableList()
                        }.toMutableMap()

                    val bundleResult = BundleResult(0, orgunitPrograms, bundle.orgUnits().toMutableList())

                    var iterationCount = 0
                    var successfulSync = true

                    do {
                        val result = iterateBundle(bundle, params, bundleResult, relatives, emitter, progressManager)
                        successfulSync = successfulSync && result.successfulSync
                        iterationCount++
                    } while (iterationNotFinished(bundle, params, bundleResult, iterationCount))

                    if (successfulSync) {
                        updateLastUpdated(bundle)
                    }
                }
            }

            if (progressManager.getProgress().programs().any { !it.value.isComplete }) {
                emitter.onNext(progressManager.completePrograms())
            }

            emitter.onComplete()
        }
    }

    private fun iterationNotFinished(
        bundle: Q,
        params: ProgramDataDownloadParams,
        bundleResult: BundleResult,
        iterationCount: Int
    ): Boolean {
        return params.limitByProgram() != true &&
            bundleResult.bundleCount < bundle.commonParams().limit &&
            bundleResult.bundleOrgUnitsToDownload.isNotEmpty() &&
            iterationCount < max(bundle.commonParams().limit * BUNDLE_SECURITY_FACTOR, BUNDLE_ITERATION_LIMIT)
    }

    @Suppress("LongParameterList")
    private fun iterateBundle(
        bundle: Q,
        params: ProgramDataDownloadParams,
        bundleResult: BundleResult,
        relatives: RelationshipItemRelatives,
        emitter: ObservableEmitter<TrackerD2Progress>,
        progressManager: TrackerD2ProgressManager
    ): IterationResult {
        val iterationResult = IterationResult()
        val limitPerCombo = getBundleLimit(bundle, params, bundleResult)

        for ((orgUnitUid, orgunitPrograms) in bundleResult.bundleOrgUnitPrograms.entries) {
            val pendingTeis = bundle.commonParams().limit - bundleResult.bundleCount
            val bundleLimit = min(limitPerCombo, pendingTeis)

            if (bundleLimit <= 0 || orgunitPrograms.isEmpty()) {
                bundleResult.bundleOrgUnitsToDownload -= orgUnitUid
                continue
            }

            val result = iterateBundleOrgunit(
                orgUnitUid,
                bundle,
                params,
                bundleResult,
                bundleLimit,
                relatives,
                emitter,
                progressManager
            )
            iterationResult.successfulSync = iterationResult.successfulSync && result.successfulSync
        }

        return iterationResult
    }

    @Suppress("LongParameterList")
    private fun iterateBundleOrgunit(
        orgUnitUid: String,
        bundle: Q,
        params: ProgramDataDownloadParams,
        bundleResult: BundleResult,
        limit: Int,
        relatives: RelationshipItemRelatives,
        emitter: ObservableEmitter<TrackerD2Progress>,
        progressManager: TrackerD2ProgressManager
    ): IterationResult {
        val iterationResult = IterationResult()

        for (bundleProgram in bundleResult.bundleOrgUnitPrograms[orgUnitUid]!!) {
            val observable = Completable.fromCallable {
                if (bundleResult.bundleCount < bundle.commonParams().limit) {
                    val trackerQuery = getQuery(bundle, bundleProgram.program, orgUnitUid, limit)

                    val result = getItemsForOrgUnitProgramCombination(
                        trackerQuery,
                        limit,
                        bundleProgram.itemCount,
                        params.overwrite(),
                        relatives
                    )

                    bundleResult.bundleCount += result.count
                    bundleProgram.itemCount += result.count
                    iterationResult.successfulSync = iterationResult.successfulSync && result.successfulSync

                    val syncStatus =
                        if (result.successfulSync) D2ProgressSyncStatus.SUCCESS
                        else D2ProgressSyncStatus.ERROR

                    progressManager.updateProgramSyncStatus(bundleProgram.program, syncStatus)

                    if (result.emptyProgram || !result.successfulSync) {
                        bundleResult.bundleOrgUnitPrograms[orgUnitUid] =
                            bundleResult.bundleOrgUnitPrograms[orgUnitUid]!!
                                .filter { it.program != bundleProgram.program }.toMutableList()

                        val hasOtherOrgunits = bundleResult.bundleOrgUnitPrograms.values.any { list ->
                            list.any { it.program == bundleProgram.program }
                        }

                        if (!hasOtherOrgunits) {
                            progressManager.increaseProgress(TrackedEntityInstance::class.java, false)
                            progressManager.completeProgram(bundleProgram.program)
                            emitter.onNext(progressManager.getProgress())
                        }
                    }
                }
            }

            rxCallExecutor.wrapCompletableTransactionally(observable, cleanForeignKeys = true).blockingAwait()
        }
        return iterationResult
    }

    private fun getBundleLimit(
        bundle: Q,
        params: ProgramDataDownloadParams,
        bundleResult: BundleResult
    ): Int {
        return when {
            params.uids().isNotEmpty() -> params.uids().size
            params.limitByProgram() != true -> {
                val numOfCombinations = bundleResult.bundleOrgUnitPrograms.values.sumOf { it.size }
                val pendingTeis = bundle.commonParams().limit - bundleResult.bundleCount

                if (numOfCombinations == 0 || pendingTeis == 0) 0
                else ceil(pendingTeis.toDouble() / numOfCombinations.toDouble()).roundToInt()
            }
            else -> bundle.commonParams().limit - bundleResult.bundleCount
        }
    }

    private fun getItemsForOrgUnitProgramCombination(
        trackerQueryBuilder: TrackerAPIQuery,
        combinationLimit: Int,
        downloadedCount: Int,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): ItemsWithPagingResult {
        return try {
            getItemsWithPaging(trackerQueryBuilder, combinationLimit, downloadedCount, overwrite, relatives)
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

    private fun downloadRelationships(
        progressManager: TrackerD2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<TrackerD2Progress> {
        return rxCallExecutor.wrapObservableTransactionally(
            relationshipDownloadAndPersistCallFactory.downloadAndPersist(relatives).andThen(
                Observable.fromCallable {
                    progressManager.increaseProgress(
                        TrackedEntityInstance::class.java, false
                    )
                }
            ),
            true
        )
    }

    protected class ItemsWithPagingResult(
        var count: Int,
        var successfulSync: Boolean,
        var d2Error: D2Error?,
        var emptyProgram: Boolean
    )

    protected class ItemsByProgramCount(val program: String, var itemCount: Int)

    protected class BundleResult(
        var bundleCount: Int,
        var bundleOrgUnitPrograms: MutableMap<String, MutableList<ItemsByProgramCount>>,
        var bundleOrgUnitsToDownload: MutableList<String?>
    )

    protected class IterationResult(
        var successfulSync: Boolean = true
    )

    companion object {
        const val BUNDLE_ITERATION_LIMIT = 1000
        const val BUNDLE_SECURITY_FACTOR = 2
    }

    protected abstract fun getBundles(params: ProgramDataDownloadParams): List<Q>

    protected abstract fun getItems(query: TrackerAPIQuery): List<T>

    protected abstract fun persistItems(
        items: List<T>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives
    )

    protected abstract fun updateLastUpdated(bundle: Q)

    protected abstract fun queryByUids(
        bundle: Q,
        overwrite: Boolean,
        relatives: RelationshipItemRelatives
    ): ItemsWithPagingResult

    protected abstract fun getQuery(
        bundle: Q,
        program: String?,
        orgunitUid: String?,
        limit: Int
    ): TrackerAPIQuery
}
