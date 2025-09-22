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
package org.hisp.dhis.android.core.relationship.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.trackedentity.internal.TrackerPostStateManager
import org.koin.core.annotation.Singleton
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

@Singleton
internal class RelationshipPostCall(
    private val relationshipNetworkHandler: RelationshipNetworkHandler,
    private val relationshipStore: RelationshipStore,
    private val relationshipImportHandler: RelationshipImportHandler,
    private val dataStatePropagator: DataStatePropagator,
    private val trackerStateManager: TrackerPostStateManager,
) {

    fun deleteRelationships(relationships: List<Relationship>): Flow<D2Progress> = flow {
        val progressManager = D2ProgressManager(null)
        for (relationship in relationships) {
            val response = relationshipNetworkHandler.deleteRelationship(relationship.uid()!!)

            response.fold(
                onSuccess = { webResponse ->
                    val httpCode = webResponse.httpStatusCode()
                    val status = webResponse.response()?.status()

                    if ((httpCode == HTTP_OK && status == ImportStatus.SUCCESS) || httpCode == HTTP_NOT_FOUND) {
                        relationshipStore.deleteByEntity(relationship)
                    } else {
                        handleDeleteRelationshipError(relationship.uid()!!)
                    }
                },
                onFailure = {
                    handleDeleteRelationshipError(relationship.uid()!!)
                },
            )

            dataStatePropagator.propagateRelationshipUpdate(relationship)
        }

        emit(progressManager.increaseProgress(Relationship::class.java, false))
    }

    @Suppress("TooGenericExceptionCaught")
    fun postRelationships(relationships: List<Relationship>): Flow<D2Progress> = flow {
        val progressManager = D2ProgressManager(null)

        if (relationships.isEmpty()) {
            emit(progressManager.increaseProgress(Relationship::class.java, false))
        } else {
            try {
                trackerStateManager.setPayloadStates(
                    relationships = relationships,
                    forcedState = State.UPLOADING,
                )
                val httpResponse = relationshipNetworkHandler.postRelationship(relationships).getOrThrow()

                relationshipImportHandler.handleRelationshipImportSummaries(
                    importSummaries = httpResponse.response()?.importSummaries(),
                    relationships = relationships,
                )
                emit(progressManager.increaseProgress(Relationship::class.java, false))
            } catch (e: Exception) {
                trackerStateManager.restorePayloadStates(relationships = relationships)
                relationships.forEach { dataStatePropagator.propagateRelationshipUpdate(it) }
                throw e
            }
        }
    }

    private suspend fun handleDeleteRelationshipError(relationshipUid: String) {
        // TODO Implement better handling
        // The relationship is marked as error, but there is no handling in the TEI. The TEI is being posted
        relationshipStore.setSyncState(relationshipUid, State.ERROR)
    }
}
