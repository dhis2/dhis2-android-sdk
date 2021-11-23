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
package org.hisp.dhis.android.core.event.internal

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
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.relationship.internal.RelationshipDownloadAndPersistCallFactory
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader

@Reusable
class EventDownloadCall @Inject internal constructor(
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val d2CallExecutor: D2CallExecutor,
    private val rxCallExecutor: RxAPICallExecutor,
    private val eventQueryBundleFactory: EventQueryBundleFactory,
    private val endpointCallFactory: EventEndpointCallFactory,
    private val persistenceCallFactory: EventPersistenceCallFactory,
    private val relationshipDownloadAndPersistCallFactory: RelationshipDownloadAndPersistCallFactory,
    private val lastUpdatedManager: EventLastUpdatedManager
) {

    fun downloadSingleEvents(params: ProgramDataDownloadParams): Observable<D2Progress> {
        val observable = Observable.defer {
            val progressManager = D2ProgressManager(null)
            val relatives = RelationshipItemRelatives()
            return@defer Observable.concat(
                systemInfoModuleDownloader.downloadWithProgressManager(progressManager),
                downloadEventsInternal(params, progressManager, relatives),
                downloadRelationships(progressManager, relatives)
            )
        }

        return rxCallExecutor.wrapObservableTransactionally(observable, true)
    }

    private fun downloadEventsInternal(
        params: ProgramDataDownloadParams,
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {

        return Observable.create { emitter ->
            val iterables = BundleIterables(
                0, true, 0, mutableMapOf(), mutableListOf(), mutableListOf()
            )
            val bundles: List<EventQueryBundle> = eventQueryBundleFactory.getQueries(params)

            for (bundle in bundles) {
                iterables.eventsCount = 0
                iterables.bundleOrgUnitPrograms = mutableMapOf()
                iterables.orgUnitsBundleToDownload = bundle.orgUnits().toMutableList()
                bundle.orgUnits()
                    .ifEmpty { listOf(null) }
                    .forEach { orgUnit ->
                        iterables.bundleOrgUnitPrograms[orgUnit] = when (orgUnit) {
                            null -> listOf(EventsByProgramCount(null, 0))
                            else ->
                                bundle.commonParams().programs
                                    .map { EventsByProgramCount(it, 0) }
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
            emitter.onNext(progressManager.increaseProgress(Event::class.java, false))
            emitter.onComplete()
        }
    }

    private fun iterationNotFinished(
        bundle: EventQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        iterationCount: Int
    ): Boolean {
        return params.limitByProgram() != true &&
            iterables.eventsCount < bundle.commonParams().limit &&
            iterables.orgUnitsBundleToDownload.isNotEmpty() &&
            iterationCount < max(bundle.commonParams().limit * BUNDLE_SECURITY_FACTOR, BUNDLE_ITERATION_LIMIT)
    }

    private fun iterateBundle(
        bundle: EventQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        val limitPerCombo = getBundleLimit(bundle, params, iterables)

        for (orgUnitUid in iterables.bundleOrgUnitPrograms.keys) {
            iterables.emptyOrCorruptedPrograms = emptyList<String?>().toMutableList()
            val orgunitPrograms = iterables.bundleOrgUnitPrograms[orgUnitUid]

            val pendingEvents = bundle.commonParams().limit - iterables.eventsCount
            iterables.bundleLimit = min(limitPerCombo, pendingEvents)

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
        bundle: EventQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables,
        relatives: RelationshipItemRelatives
    ) {
        for (bundleProgram in iterables.bundleOrgUnitPrograms[orgUnitUid]!!) {
            if (iterables.eventsCount >= bundle.commonParams().limit) {
                break
            }
            val eventQueryBuilder = EventQuery.builder()
                .commonParams(
                    bundle.commonParams().copy(
                        program = bundleProgram.program,
                        limit = iterables.bundleLimit
                    )
                )
                .lastUpdatedStr(lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()))
                .orgUnit(orgUnitUid)
                .uids(params.uids())

            val result = getEventsForOrgUnitProgramCombination(
                eventQueryBuilder,
                iterables.bundleLimit,
                bundleProgram.eventCount,
                relatives
            )

            iterables.eventsCount += result.eventCount
            bundleProgram.eventCount += result.eventCount
            iterables.successfulSync = iterables.successfulSync && result.successfulSync

            if (result.emptyProgram || !result.successfulSync) {
                iterables.emptyOrCorruptedPrograms = (iterables.emptyOrCorruptedPrograms + bundleProgram.program)
                    .toMutableList()
            }
        }
    }

    private fun getBundleLimit(
        bundle: EventQueryBundle,
        params: ProgramDataDownloadParams,
        iterables: BundleIterables
    ): Int {
        return when {
            params.uids().isNotEmpty() -> params.uids().size
            params.limitByProgram() != true -> {
                val numOfCombinations = iterables.bundleOrgUnitPrograms.values.map { it.size }.sum()
                val pendingEvents = bundle.commonParams().limit - iterables.eventsCount

                if (numOfCombinations == 0) 0
                else ceil(pendingEvents.toDouble() / numOfCombinations.toDouble()).roundToInt()
            }
            else -> bundle.commonParams().limit - iterables.eventsCount
        }
    }

    private fun getEventsForOrgUnitProgramCombination(
        eventQueryBuilder: EventQuery.Builder,
        combinationLimit: Int,
        downloadedEvents: Int,
        relatives: RelationshipItemRelatives
    ): EventsWithPagingResult {

        var result = EventsWithPagingResult(0, successfulSync = true, emptyProgram = false)

        try {
            result = getEventsWithPaging(eventQueryBuilder, combinationLimit, downloadedEvents, relatives)
        } catch (ignored: D2Error) {
            result.successfulSync = false
        }

        return result
    }

    @Throws(D2Error::class)
    private fun getEventsWithPaging(
        eventQueryBuilder: EventQuery.Builder,
        combinationLimit: Int,
        downloadedEvents: Int,
        relatives: RelationshipItemRelatives
    ): EventsWithPagingResult {

        var downloadedEventsForCombination = 0
        var emptyProgram = false
        val baseQuery = eventQueryBuilder.build()

        val pagingList = ApiPagingEngine.getPaginationList(baseQuery.pageSize(), combinationLimit, downloadedEvents)

        for (paging in pagingList) {
            eventQueryBuilder.pageSize(paging.pageSize())
            eventQueryBuilder.page(paging.page())

            val pageEvents = d2CallExecutor.executeD2Call(
                endpointCallFactory.getCall(eventQueryBuilder.build()), true
            )

            val eventsToPersist = getEventsToPersist(paging, pageEvents)

            persistenceCallFactory.persistEvents(eventsToPersist, relatives).blockingAwait()

            downloadedEventsForCombination += eventsToPersist.size

            if (pageEvents.size < paging.pageSize()) {
                emptyProgram = true
                break
            }
        }

        return EventsWithPagingResult(downloadedEventsForCombination, true, emptyProgram)
    }

    private fun getEventsToPersist(paging: Paging, pageEvents: List<Event>): List<Event> {

        return if (paging.isFullPage && pageEvents.size > paging.previousItemsToSkipCount()) {
            val toIndex = min(
                pageEvents.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageEvents.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageEvents
        }
    }

    private fun downloadRelationships(
        progressManager: D2ProgressManager,
        relatives: RelationshipItemRelatives
    ): Observable<D2Progress> {
        return relationshipDownloadAndPersistCallFactory.downloadAndPersist(relatives).andThen(
            Observable.just(
                progressManager.increaseProgress(
                    Event::class.java, true
                )
            )
        )
    }

    private class EventsWithPagingResult(var eventCount: Int, var successfulSync: Boolean, var emptyProgram: Boolean)

    private class EventsByProgramCount(val program: String?, var eventCount: Int)

    private class BundleIterables(
        var eventsCount: Int,
        var successfulSync: Boolean,
        var bundleLimit: Int,
        var bundleOrgUnitPrograms: MutableMap<String?, MutableList<EventsByProgramCount>>,
        var orgUnitsBundleToDownload: MutableList<String?>,
        var emptyOrCorruptedPrograms: MutableList<String?>
    )

    companion object {
        const val BUNDLE_ITERATION_LIMIT = 1000
        const val BUNDLE_SECURITY_FACTOR = 2
    }
}
