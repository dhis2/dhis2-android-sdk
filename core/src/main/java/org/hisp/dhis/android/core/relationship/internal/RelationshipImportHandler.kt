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

import org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.getSyncState
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.internal.BaseImportSummaryHelper.getReferences
import org.hisp.dhis.android.core.imports.internal.RelationshipImportSummary
import org.hisp.dhis.android.core.imports.internal.conflicts.RelationshipNotFoundConflict
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipImportHandler internal constructor(
    private val relationshipStore: RelationshipStore,
    private val dataStatePropagator: DataStatePropagator,
    private val relationshipRepository: RelationshipCollectionRepository,
) {

    suspend fun handleRelationshipImportSummaries(
        importSummaries: List<RelationshipImportSummary?>?,
        relationships: List<Relationship>,
    ) {
        importSummaries?.filterNotNull()?.forEach { importSummary ->
            importSummary.reference()?.let { relationshipUid ->
                handleSingleRelationship(importSummary, relationshipUid)
            }
        }

        processIgnoredRelationships(importSummaries, relationships)
    }

    private suspend fun handleSingleRelationship(
        importSummary: RelationshipImportSummary,
        relationshipUid: String,
    ) {
        val relationship = relationshipRepository.withItems().uid(relationshipUid).blockingGet()
        val relationshipNotFoundOnServer = checkRelationshipNotFoundOnServer(importSummary)

        val handleAction = if (relationshipNotFoundOnServer) {
            handleRelationshipNotFound(relationship)
        } else {
            handleRelationshipUpdateState(importSummary, relationshipUid)
        }

        if (handleAction != HandleAction.Delete) {
            dataStatePropagator.propagateRelationshipUpdate(relationship)
        }
    }

    private suspend fun handleRelationshipNotFound(relationship: Relationship?): HandleAction {
        relationship?.let { relationshipStore.deleteByEntity(it) }
        return HandleAction.Delete
    }

    private suspend fun handleRelationshipUpdateState(
        importSummary: RelationshipImportSummary,
        relationshipUid: String,
    ): HandleAction {
        val state = getSyncState(importSummary.status())
        val handledState = if (state == State.ERROR || state == State.WARNING) {
            State.TO_UPDATE
        } else {
            state
        }

        relationshipStore.setSyncStateOrDelete(relationshipUid, handledState)
        return HandleAction.Update
    }

    private fun checkRelationshipNotFoundOnServer(importSummary: RelationshipImportSummary): Boolean {
        val hasConflict = importSummary.conflicts()?.any { conflict ->
            RelationshipNotFoundConflict.matches(conflict)
        } ?: false

        val hasDescriptionError = importSummary.description()?.let { description ->
            RelationshipNotFoundConflict.matchesString(description)
        } ?: false

        return hasConflict || hasDescriptionError
    }

    private suspend fun processIgnoredRelationships(
        importSummaries: List<RelationshipImportSummary?>?,
        relationships: List<Relationship>,
    ) {
        val processedRelationships = getReferences(importSummaries)

        relationships.filterNot { processedRelationships.contains(it.uid()) }.forEach { relationship ->
            relationshipStore.setSyncStateOrDelete(relationship.uid()!!, State.TO_UPDATE)
            dataStatePropagator.propagateRelationshipUpdate(relationship)
        }
    }
}
