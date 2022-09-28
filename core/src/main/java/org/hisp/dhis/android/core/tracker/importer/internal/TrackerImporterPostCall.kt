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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayloadWrapper
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostPayloadGenerator
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostStateManager
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.ENROLLMENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.EVENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.RELATIONSHIP
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.TRACKED_ENTITY

@Reusable
internal class TrackerImporterPostCall @Inject internal constructor(
    private val payloadGenerator: NewTrackerImporterTrackedEntityPostPayloadGenerator,
    private val stateManager: NewTrackerImporterTrackedEntityPostStateManager,
    private val service: TrackerImporterService,
    private val fileResourcesPostCall: TrackerImporterFileResourcesPostCall,
    private val programOwnerPostCall: TrackerImporterProgramOwnerPostCall,
    private val apiCallExecutor: APICallExecutor,
    private val jobQueryCall: JobQueryCall,
    private val jobObjectHandler: Handler<TrackerJobObject>,
    private val breakTheGlassHelper: TrackerImporterBreakTheGlassHelper
) {
    fun uploadTrackedEntityInstances(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>
    ): Observable<D2Progress> {
        return Observable.defer {
            postPayloadWrapper(payloadGenerator.getTrackedEntityPayload(filteredTrackedEntityInstances))
        }
    }

    fun uploadEvents(
        filteredEvents: List<Event>
    ): Observable<D2Progress> {
        return Observable.defer {
            postPayloadWrapper(payloadGenerator.getEventPayload(filteredEvents))
        }
    }

    private fun postPayloadWrapper(
        payloadWrapper: NewTrackerImporterPayloadWrapper
    ): Observable<D2Progress> {
        return fileResourcesPostCall.uploadFileResources(payloadWrapper).flatMapObservable { payload ->
            Observable.concat(
                programOwnerPostCall.uploadProgramOwners(payload.programOwners, onlyExistingTeis = true),
                doPostCall(payload.deleted, IMPORT_STRATEGY_DELETE),
                doPostCall(payload.updated, IMPORT_STRATEGY_CREATE_AND_UPDATE),
                programOwnerPostCall.uploadProgramOwners(payload.programOwners, onlyExistingTeis = false)
            )
        }
    }

    private fun doPostCall(
        payload: NewTrackerImporterPayload,
        importStrategy: String
    ): Observable<D2Progress> {
        return if (payload.isEmpty()) {
            Observable.empty<D2Progress>()
        } else {
            doPost(payload, importStrategy).flatMap { d2Progress ->
                val glassErrors = breakTheGlassHelper.getGlassErrors(payload)

                if (glassErrors.isEmpty()) {
                    Observable.just(d2Progress)
                } else {
                    breakTheGlassHelper.fakeBreakGlass(payload)
                    doPost(payload, importStrategy)
                }
            }
        }
    }

    private fun doPost(
        payload: NewTrackerImporterPayload,
        importStrategy: String
    ): Observable<D2Progress> {
        return Observable.defer {
            stateManager.setStates(payload, State.UPLOADING)

            Single.fromCallable {
                doPostCallInternal(payload, importStrategy)
            }.doOnError {
                stateManager.restoreStates(payload)
            }.flatMapObservable {
                jobQueryCall.queryJob(it)
            }
        }
    }

    private fun doPostCallInternal(
        payload: NewTrackerImporterPayload,
        importStrategy: String
    ): String {
        val res = apiCallExecutor.executeObjectCall(
            service.postTrackedEntityInstances(payload, ATOMIC_MODE_OBJECT, importStrategy)
        )
        val jobId = res.response().uid()
        jobObjectHandler.handleMany(generateJobObjects(payload, jobId))
        return jobId
    }

    private fun generateJobObjects(
        payload: NewTrackerImporterPayload,
        jobUid: String
    ): List<TrackerJobObject> {
        val builder = TrackerJobObject
            .builder()
            .jobUid(jobUid)
            .lastUpdated(Date())

        val enrollments = payload.trackedEntities.flatMap { it.enrollments() ?: emptyList() } + payload.enrollments
        val events = enrollments.flatMap { it.events() ?: emptyList() } + payload.events

        return generateTypeObjects(builder, TRACKED_ENTITY, payload.trackedEntities, payload.fileResourcesMap) +
            generateTypeObjects(builder, ENROLLMENT, enrollments, payload.fileResourcesMap) +
            generateTypeObjects(builder, EVENT, events, payload.fileResourcesMap) +
            generateTypeObjects(builder, RELATIONSHIP, payload.relationships, payload.fileResourcesMap)
    }

    private fun generateTypeObjects(
        builder: TrackerJobObject.Builder,
        objectType: TrackerImporterObjectType,
        objects: List<ObjectWithUidInterface>,
        fileResourcesMap: Map<String, List<String>>
    ): List<TrackerJobObject> {
        return objects.map {
            builder
                .trackerType(objectType)
                .objectUid(it.uid())
                .fileResources(fileResourcesMap[it.uid()] ?: emptyList())
                .build()
        }
    }
}
