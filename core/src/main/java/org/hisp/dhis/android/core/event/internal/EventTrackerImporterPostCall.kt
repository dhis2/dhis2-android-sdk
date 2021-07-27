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
import io.reactivex.Single
import java.util.Date
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.tracker.importer.internal.*
import org.hisp.dhis.android.core.tracker.importer.internal.JobQueryCall
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.EVENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterService

@Reusable
internal class EventTrackerImporterPostCall @Inject internal constructor(
    private val payloadGenerator: NewTrackerImporterEventPostPayloadGenerator,
    private val stateManager: EventPostStateManager,
    private val service: TrackerImporterService,
    private val apiCallExecutor: APICallExecutor,
    private val jobQueryCall: JobQueryCall,
    private val jobObjectHandler: Handler<TrackerJobObject>
) {
    fun uploadEvents(
        events: List<Event>
    ): Observable<D2Progress> {
        return Observable.defer {
            val eventsToPost = payloadGenerator.getEvents(events)
            val partition = eventsToPost.partition { it.deleted()!! }
            Observable.concat(
                doPostCall(partition.first, IMPORT_STRATEGY_DELETE),
                doPostCall(partition.second, IMPORT_STRATEGY_CREATE_AND_UPDATE)
            )
        }
    }

    private fun generateJobObjects(events: List<NewTrackerImporterEvent>, jobUid: String): List<TrackerJobObject> {
        val lastUpdated = Date()
        return events.map {
            TrackerJobObject
                .builder()
                .trackerType(EVENT)
                .objectUid(it.uid())
                .jobUid(jobUid)
                .lastUpdated(lastUpdated)
                .build()
        }
    }

    private fun doPostCall(events: List<NewTrackerImporterEvent>, importStrategy: String): Observable<D2Progress> {
        return if (events.isEmpty()) {
            Observable.empty<D2Progress>()
        } else {
            stateManager.markObjectsAs(events, State.UPLOADING)
            Single.fromCallable {
                doPostCallInternal(events, importStrategy)
            }.doOnError {
                stateManager.markObjectsAs(events, DataStateHelper.errorIfOnline(it))
            }.flatMapObservable {
                jobQueryCall.queryJob(it)
            }
        }
    }

    private fun doPostCallInternal(events: List<NewTrackerImporterEvent>, importStrategy: String): String {
        val eventPayload = NewTrackerImporterPayload(events = events.toMutableList())
        val res = apiCallExecutor.executeObjectCall(
            service.postEvents(eventPayload, ATOMIC_MODE_OBJECT, importStrategy)
        )
        val jobId = res.response().uid()
        jobObjectHandler.handleMany(generateJobObjects(events, jobId))
        return jobId
    }
}
