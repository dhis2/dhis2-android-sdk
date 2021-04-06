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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.relationship.internal.RelationshipDeleteCall
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPayload
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostPayloadGenerator
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostStateManager

@Reusable
internal class TrackedEntityInstanceTrackerImporterPostCall @Inject internal constructor(
    private val payloadGenerator: NewTrackerImporterTrackedEntityPostPayloadGenerator,
    private val stateManager: NewTrackerImporterTrackedEntityPostStateManager,
    private val service: TrackerImporterService,
    private val apiCallExecutor: APICallExecutor,
    private val jobQueryCall: JobQueryCall,
    private val relationshipDeleteCall: RelationshipDeleteCall
) {
    fun uploadTrackedEntityInstances(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>
    ): Observable<D2Progress> {
        return Observable.defer {
            val trackedEntitiesToPost = payloadGenerator.getTrackedEntities(filteredTrackedEntityInstances)
            Single.fromCallable {
                // TODO HANDLE DELETIONS
                // TODO HANDLE RELATIONSHIPS
                // TODO HANDLE DELETED RELATIONSHIPS
                //  relationshipDeleteCall.postDeletedRelationships(partition)
                val trackedEntityInstancePayload = NewTrackerImporterTrackedEntityPayload(trackedEntitiesToPost)
                val res = apiCallExecutor.executeObjectCall(
                    service.postTrackedEntityInstances(trackedEntityInstancePayload)
                )
                val jobId = res.response().uid()
                jobQueryCall.storeJob(jobId)
                jobId
            }.doOnError {
                stateManager.restoreStates(trackedEntitiesToPost)
            }.flatMapObservable {
                jobQueryCall.queryJob(it)
            }
        }
    }
}
