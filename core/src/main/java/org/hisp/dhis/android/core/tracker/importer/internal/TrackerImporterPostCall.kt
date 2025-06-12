/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayloadWrapper
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostPayloadGenerator
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostStateManager
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.ENROLLMENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.EVENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.RELATIONSHIP
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType.TRACKED_ENTITY
import org.hisp.dhis.android.network.tracker.IMPORT_STRATEGY_CREATE_AND_UPDATE
import org.hisp.dhis.android.network.tracker.IMPORT_STRATEGY_DELETE
import org.koin.core.annotation.Singleton
import java.util.Date

@Singleton
internal class TrackerImporterPostCall internal constructor(
    private val payloadGenerator: NewTrackerImporterTrackedEntityPostPayloadGenerator,
    private val stateManager: NewTrackerImporterTrackedEntityPostStateManager,
    private val networkHandler: TrackerImporterNetworkHandler,
    private val fileResourcesPostCall: TrackerImporterFileResourcesPostCall,
    private val programOwnerPostCall: TrackerImporterProgramOwnerPostCall,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val jobQueryCall: JobQueryCall,
    private val jobObjectHandler: TrackerJobObjectHandler,
    private val breakTheGlassHelper: TrackerImporterBreakTheGlassHelper,
) {
    fun uploadTrackedEntityInstances(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>,
    ): Flow<D2Progress> = flow {
        emitAll(postPayloadWrapper(payloadGenerator.getTrackedEntityPayload(filteredTrackedEntityInstances)))
    }

    fun uploadEvents(
        filteredEvents: List<Event>,
    ): Flow<D2Progress> = flow {
        emitAll(postPayloadWrapper(payloadGenerator.getEventPayload(filteredEvents)))
    }

    private fun postPayloadWrapper(
        payloadWrapper: NewTrackerImporterPayloadWrapper,
    ): Flow<D2Progress> = flow {
        val payload = fileResourcesPostCall.uploadFileResources(payloadWrapper)

        emitAll(doPostCall(payload.deleted, IMPORT_STRATEGY_DELETE))
        emitAll(doPostCall(payload.updated, IMPORT_STRATEGY_CREATE_AND_UPDATE))
        emitAll(programOwnerPostCall.uploadProgramOwners(payload.programOwners))
    }

    private fun doPostCall(
        payload: NewTrackerImporterPayload,
        importStrategy: String,
    ): Flow<D2Progress> = flow {
        if (payload.isEmpty()) {
            emit(D2ProgressManager(null).increaseProgress(NewTrackerImporterPayload::class.java, true))
        } else {
            emitAll(doPost(payload, importStrategy))

            val glassErrors = breakTheGlassHelper.getGlassErrors(payload)

            if (!glassErrors.isEmpty()) {
                breakTheGlassHelper.fakeBreakGlass(glassErrors)
                emitAll(doPost(payload, importStrategy))
            }
        }
    }

    private fun doPost(
        payload: NewTrackerImporterPayload,
        importStrategy: String,
    ): Flow<D2Progress> = flow {
        stateManager.setStates(payload, State.UPLOADING)

        doPostCallInternal(payload, importStrategy).fold(
            onSuccess = { jobId ->
                emitAll(jobQueryCall.queryJob(jobId))
            },
            onFailure = {
                stateManager.restoreStates(payload)
                throw it
            },
        )
    }

    private suspend fun doPostCallInternal(
        payload: NewTrackerImporterPayload,
        importStrategy: String,
    ): Result<String, D2Error> {
        return coroutineAPICallExecutor.wrap(storeError = true) {
            networkHandler.postTrackerPayload(payload, importStrategy)
        }.map { res ->
            val jobId = res.response().uid()
            jobObjectHandler.handleMany(generateJobObjects(payload, jobId))
            jobId
        }
    }

    private fun generateJobObjects(
        payload: NewTrackerImporterPayload,
        jobUid: String,
    ): List<TrackerJobObject> {
        val builder = TrackerJobObject
            .builder()
            .jobUid(jobUid)
            .lastUpdated(Date())

        val enrollments = payload.trackedEntities.flatMap { it.enrollments ?: emptyList() } + payload.enrollments
        val events = enrollments.flatMap { it.events ?: emptyList() } + payload.events

        return generateTypeObjects(builder, TRACKED_ENTITY, payload.trackedEntities, payload.fileResourcesMap) +
            generateTypeObjects(builder, ENROLLMENT, enrollments, payload.fileResourcesMap) +
            generateTypeObjects(builder, EVENT, events, payload.fileResourcesMap) +
            generateTypeObjects(builder, RELATIONSHIP, payload.relationships, payload.fileResourcesMap)
    }

    private fun generateTypeObjects(
        builder: TrackerJobObject.Builder,
        objectType: TrackerImporterObjectType,
        objects: List<ObjectWithUidInterface>,
        fileResourcesMap: Map<String, List<String>>,
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
