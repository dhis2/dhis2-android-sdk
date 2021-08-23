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
import io.reactivex.ObservableEmitter
import java.net.HttpURLConnection.HTTP_CONFLICT
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventImportHandler
import org.hisp.dhis.android.core.event.internal.EventPayload
import org.hisp.dhis.android.core.event.internal.EventService
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.relationship.internal.RelationshipPostCall
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

@Reusable
internal class OldTrackerImporterPostCall @Inject internal constructor(
    private val trackerImporterPayloadGenerator: OldTrackerImporterPayloadGenerator,
    private val trackerStateManager: TrackerPostStateManager,
    private val trackedEntityInstanceService: TrackedEntityInstanceService,
    private val eventService: EventService,
    private val teiWebResponseHandler: TEIWebResponseHandler,
    private val eventImportHandler: EventImportHandler,
    private val apiCallExecutor: APICallExecutor,
    private val relationshipPostCall: RelationshipPostCall
) {

    fun uploadTrackedEntityInstances(
        trackedEntityInstances: List<TrackedEntityInstance>
    ): Observable<D2Progress> {
        val payload = trackerImporterPayloadGenerator.getTrackedEntityInstancePayload(trackedEntityInstances)
        return uploadPayload(payload)
    }

    fun uploadEvents(
        events: List<Event>
    ): Observable<D2Progress> {
        val payload = trackerImporterPayloadGenerator.getEventPayload(events)
        return uploadPayload(payload)
    }

    private fun uploadPayload(
        payload: OldTrackerImporterPayload
    ): Observable<D2Progress> {
        return Observable.defer {
            val partitionedRelationships = payload.relationships.partition { it.deleted()!! }

            Observable.concat(
                relationshipPostCall.deleteRelationships(partitionedRelationships.first),
                postTrackedEntityInstances(payload.trackedEntityInstances),
                postEvents(payload.events),
                relationshipPostCall.postRelationships(partitionedRelationships.second)
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun postTrackedEntityInstances(
        trackedEntityInstances: List<TrackedEntityInstance>
    ): Observable<D2Progress> {
        return Observable.create { emitter: ObservableEmitter<D2Progress> ->
            val progressManager = D2ProgressManager(null)
            val teiPartitions = trackedEntityInstances.chunked(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)

            for (partition in teiPartitions) {
                try {
                    trackerStateManager.setPayloadStates(
                        trackedEntityInstances = partition,
                        forcedState = State.UPLOADING
                    )
                    val trackedEntityInstancePayload = TrackedEntityInstancePayload.create(partition)
                    val webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                        trackedEntityInstanceService.postTrackedEntityInstances(
                            trackedEntityInstancePayload, "SYNC"
                        ),
                        @Suppress("MagicNumber")
                        listOf(HTTP_CONFLICT),
                        TEIWebResponse::class.java
                    )
                    teiWebResponseHandler.handleWebResponse(webResponse, partition)
                    emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance::class.java, false))
                } catch (e: Exception) {
                    trackerStateManager.restorePayloadStates(partition)
                    if (e is D2Error && e.isOffline) {
                        emitter.onError(e)
                        break
                    } else {
                        emitter.onNext(
                            progressManager.increaseProgress(
                                TrackedEntityInstance::class.java,
                                false
                            )
                        )
                    }
                }
            }
            emitter.onComplete()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun postEvents(
        events: List<Event>
    ): Observable<D2Progress> {
        val progressManager = D2ProgressManager(null)

        return if (events.isEmpty()) {
            Observable.just<D2Progress>(progressManager.increaseProgress(Event::class.java, true))
        } else {
            Observable.defer {
                val eventPayload = EventPayload()
                trackerStateManager.setPayloadStates(
                    events = events,
                    forcedState = State.UPLOADING
                )

                eventPayload.events = events
                val strategy = "SYNC"
                try {
                    val webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                        eventService.postEvents(eventPayload, strategy),
                        @Suppress("MagicNumber")
                        listOf(HTTP_CONFLICT),
                        EventWebResponse::class.java
                    )
                    eventImportHandler.handleEventImportSummaries(
                        eventImportSummaries = webResponse?.response()?.importSummaries(),
                        events = events,
                        // TODO Manage tracker events
                        enrollmentUid = null,
                        teiUid = null
                    )
                    Observable.just<D2Progress>(progressManager.increaseProgress(Event::class.java, true))
                } catch (e: Exception) {
                    trackerStateManager.restorePayloadStates(events = events)
                    Observable.error<D2Progress>(e)
                }
            }
        }
    }
}
