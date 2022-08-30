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
package org.hisp.dhis.android.core.relationship.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.net.HttpURLConnection.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.RelationshipDeleteWebResponse
import org.hisp.dhis.android.core.imports.internal.RelationshipWebResponse
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.trackedentity.internal.TrackerPostStateManager

@Reusable
internal class RelationshipPostCall @Inject internal constructor(
    private val relationshipService: RelationshipService,
    private val relationshipStore: RelationshipStore,
    private val relationshipImportHandler: RelationshipImportHandler,
    private val dataStatePropagator: DataStatePropagator,
    private val trackerStateManager: TrackerPostStateManager,
    private val apiCallExecutor: APICallExecutor
) {

    fun deleteRelationships(relationships: List<Relationship>): Observable<D2Progress> {
        return Observable.create { emitter: ObservableEmitter<D2Progress> ->
            for (relationship in relationships) {
                val httpResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                    relationshipService.deleteRelationship(relationship.uid()!!),
                    listOf(HTTP_NOT_FOUND),
                    RelationshipDeleteWebResponse::class.java
                )
                val status = httpResponse.response()?.status()

                if ((httpResponse.httpStatusCode() == HTTP_OK && ImportStatus.SUCCESS == status) ||
                    httpResponse.httpStatusCode() == HTTP_NOT_FOUND
                ) {
                    relationshipStore.delete(relationship.uid()!!)
                } else {
                    // TODO Implement better handling
                    // The relationship is marked as error, but there is no handling in the TEI. The TEI is being posted
                    relationshipStore.setSyncState(relationship.uid()!!, State.ERROR)
                }
                dataStatePropagator.propagateRelationshipUpdate(relationship)
            }
            emitter.onComplete()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun postRelationships(relationships: List<Relationship>): Observable<D2Progress> {
        val progressManager = D2ProgressManager(null)

        return if (relationships.isEmpty()) {
            Observable.just<D2Progress>(progressManager.increaseProgress(Relationship::class.java, false))
        } else {
            Observable.defer {
                try {
                    val payload = RelationshipPayload.builder().relationships(relationships).build()
                    trackerStateManager.setPayloadStates(
                        relationships = relationships,
                        forcedState = State.UPLOADING
                    )
                    val httpResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                        relationshipService.postRelationship(payload),
                        listOf(HTTP_CONFLICT),
                        RelationshipWebResponse::class.java
                    )

                    relationshipImportHandler.handleRelationshipImportSummaries(
                        importSummaries = httpResponse.response()?.importSummaries(),
                        relationships = relationships
                    )
                    Observable.just<D2Progress>(progressManager.increaseProgress(Relationship::class.java, false))
                } catch (e: Exception) {
                    trackerStateManager.restorePayloadStates(relationships = relationships)
                    relationships.forEach { dataStatePropagator.propagateRelationshipUpdate(it) }
                    Observable.error<D2Progress>(e)
                }
            }
        }
    }
}
