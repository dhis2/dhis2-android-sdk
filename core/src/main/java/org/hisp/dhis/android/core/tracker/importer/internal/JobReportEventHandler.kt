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

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.note.internal.NoteStore
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.note.NoteTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class JobReportEventHandler internal constructor(
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val noteStore: NoteStore,
    private val conflictStore: TrackerImportConflictStore,
    private val eventStore: EventStore,
    private val enrollmentStore: EnrollmentStore,
    private val conflictHelper: TrackerConflictHelper,
    relationshipStore: RelationshipStore,
) : JobReportTypeHandler(relationshipStore) {

    override suspend fun handleObject(uid: String, state: State): HandleAction {
        conflictStore.deleteEventConflicts(uid)
        val handleAction = eventStore.setSyncStateOrDelete(uid, state)

        if (state == State.SYNCED && (handleAction == HandleAction.Update || handleAction == HandleAction.Insert)) {
            handleSyncedEvent(uid)
        }

        return handleAction
    }

    override suspend fun storeConflict(errorReport: JobValidationError) {
        eventStore.selectByUid(errorReport.uid)?.let { event ->
            val trackedEntityInstanceUid = event.enrollment()?.let {
                enrollmentStore.selectByUid(it)?.trackedEntityInstance()
            }
            if (errorReport.errorCode == ImporterError.E1032.name && event.deleted() == true) {
                eventStore.delete(event.uid())
            } else {
                conflictStore.updateOrInsertWhere(
                    conflictHelper.getConflictBuilder(errorReport)
                        .tableReference(EventTableInfo.TABLE_INFO.name())
                        .trackedEntityInstance(trackedEntityInstanceUid)
                        .enrollment(event.enrollment())
                        .event(errorReport.uid)
                        .build(),
                )
            }
        }
    }

    override suspend fun getRelatedRelationships(uid: String): List<String> {
        return relationshipStore.getRelationshipsByItem(RelationshipHelper.eventItem(uid)).mapNotNull { it.uid() }
    }

    suspend fun handleSyncedEvent(eventUid: String) {
        handleEventNotes(eventUid, State.SYNCED)
        trackedEntityDataValueStore.setSyncStateByEvent(eventUid, State.SYNCED)
        trackedEntityDataValueStore.removeDeletedDataValuesByEvent(eventUid)
        trackedEntityDataValueStore.removeUnassignedDataValuesByEvent(eventUid)
    }

    private suspend fun handleEventNotes(eventUid: String, state: State) {
        val newNoteState = if (state == State.SYNCED) State.SYNCED else State.TO_POST
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE,
                State.uploadableStatesIncludingError().map { it.name },
            )
            .appendKeyStringValue(NoteTableInfo.Columns.EVENT, eventUid).build()
        for (note in noteStore.selectWhere(whereClause)) {
            noteStore.update(note.toBuilder().syncState(newNoteState).build())
        }
    }
}
