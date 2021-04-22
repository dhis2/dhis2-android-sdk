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
import kotlin.math.min
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine
import org.hisp.dhis.android.core.arch.api.paging.internal.Paging
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader

@Reusable
class EventDownloadCall @Inject internal constructor(
    private val systemInfoModuleDownloader: SystemInfoModuleDownloader,
    private val d2CallExecutor: D2CallExecutor,
    private val rxCallExecutor: RxAPICallExecutor,
    private val eventQueryBundleFactory: EventQueryBundleFactory,
    private val endpointCallFactory: EventEndpointCallFactory,
    private val persistenceCallFactory: EventPersistenceCallFactory,
    private val lastUpdatedManager: EventLastUpdatedManager
) {

    fun downloadSingleEvents(params: ProgramDataDownloadParams): Observable<D2Progress> {

        val progressManager = D2ProgressManager(2)
        return Observable.merge(
            systemInfoModuleDownloader.downloadWithProgressManager(progressManager),
            downloadEventsInternal(params, progressManager)
        )
    }

    private fun downloadEventsInternal(
        params: ProgramDataDownloadParams,
        progressManager: D2ProgressManager
    ): Observable<D2Progress> {

        return Observable.create { emitter ->
            var successfulSync = true
            val bundles = eventQueryBundleFactory.getQueries(params)

            for (bundle in bundles) {

                var eventsCount = 0

                val bundleOrgUnits = bundle.orgUnits().ifEmpty { listOf(null) }
                val bundlePrograms = bundle.commonParams().programs.ifEmpty { listOf(null) }

                for (orgunitUid in bundleOrgUnits) {
                    if (eventsCount >= bundle.commonParams().limit) {
                        break
                    }
                    for (programUid in bundlePrograms) {
                        if (eventsCount >= bundle.commonParams().limit) {
                            break
                        }
                        val eventQueryBuilder = EventQuery.builder()
                            .commonParams(bundle.commonParams().copy(program = programUid))
                            .lastUpdatedStr(lastUpdatedManager.getLastUpdatedStr(bundle.commonParams()))
                            .orgUnit(orgunitUid)
                            .uids(params.uids())

                        val result = getEventsForOrgUnitProgramCombination(
                            eventQueryBuilder,
                            bundle.commonParams().limit - eventsCount
                        )
                        eventsCount += result.eventCount
                        successfulSync = successfulSync && result.successfulSync
                    }
                }
                if (params.uids().isEmpty()) {
                    lastUpdatedManager.update(bundle)
                }
            }
            emitter.onNext(progressManager.increaseProgress(Event::class.java, true))
            emitter.onComplete()
        }
    }

    private fun getEventsForOrgUnitProgramCombination(
        eventQueryBuilder: EventQuery.Builder,
        combinationLimit: Int
    ): EventsWithPagingResult {

        var eventsCount = 0
        var successfulSync = true

        try {
            eventsCount = getEventsWithPaging(eventQueryBuilder, combinationLimit)
        } catch (ignored: D2Error) {
            successfulSync = false
        }

        return EventsWithPagingResult(eventsCount, successfulSync)
    }

    @Throws(D2Error::class)
    private fun getEventsWithPaging(eventQueryBuilder: EventQuery.Builder, combinationLimit: Int): Int {

        var downloadedEventsForCombination = 0
        val baseQuery = eventQueryBuilder.build()

        val pagingList = ApiPagingEngine.getPaginationList(baseQuery.pageSize(), combinationLimit)

        for (paging in pagingList) {
            eventQueryBuilder.pageSize(paging.pageSize())
            eventQueryBuilder.page(paging.page())

            val pageEvents = d2CallExecutor.executeD2Call(
                endpointCallFactory.getCall(eventQueryBuilder.build()), true
            )

            val eventsToPersist = getEventsToPersist(paging, pageEvents)

            rxCallExecutor.wrapCompletableTransactionally(
                persistenceCallFactory
                    .persistEvents(eventsToPersist, null),
                true
            ).blockingGet()

            downloadedEventsForCombination += eventsToPersist.size

            if (pageEvents.size < paging.pageSize()) {
                break
            }
        }

        return downloadedEventsForCombination
    }

    private fun getEventsToPersist(paging: Paging, pageEvents: List<Event>): List<Event> {

        return if (paging.isLastPage && pageEvents.size > paging.previousItemsToSkipCount()) {
            val toIndex = min(
                pageEvents.size,
                paging.pageSize() - paging.posteriorItemsToSkipCount()
            )
            pageEvents.subList(paging.previousItemsToSkipCount(), toIndex)
        } else {
            pageEvents
        }
    }

    private class EventsWithPagingResult(var eventCount: Int, var successfulSync: Boolean)
}
