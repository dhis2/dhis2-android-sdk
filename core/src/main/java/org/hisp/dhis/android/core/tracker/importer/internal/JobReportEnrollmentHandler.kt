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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteTableInfo
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore

@Reusable
internal class JobReportEnrollmentHandler @Inject internal constructor(
    private val noteStore: IdentifiableObjectStore<Note>,
    private val enrollmentStore: EnrollmentStore,
    private val conflictStore: TrackerImportConflictStore,
    private val conflictHelper: TrackerConflictHelper,
    relationshipStore: RelationshipStore
) : JobReportTypeHandler(relationshipStore) {

    fun handleEnrollmentNotes(enrollmentUid: String, state: State) {
        val newNoteState = if (state == State.SYNCED) State.SYNCED else State.TO_POST
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE, State.uploadableStatesIncludingError().map { it.name }
            )
            .appendKeyStringValue(NoteTableInfo.Columns.ENROLLMENT, enrollmentUid).build()
        for (note in noteStore.selectWhere(whereClause)) {
            noteStore.update(note.toBuilder().syncState(newNoteState).build())
        }
    }

    override fun handleObject(uid: String, state: State): HandleAction {
        conflictStore.deleteEnrollmentConflicts(uid)
        val handleAction = enrollmentStore.setSyncStateOrDelete(uid, state)

        if (state == State.SYNCED && (handleAction == HandleAction.Update || handleAction == HandleAction.Insert)) {
            handleEnrollmentNotes(uid, state)
        }

        return handleAction
    }

    override fun storeConflict(errorReport: JobValidationError) {
        enrollmentStore.selectByUid(errorReport.uid)?.let { enrollment ->
            if (errorReport.errorCode == ImporterError.E1081.name && enrollment.deleted() == true) {
                enrollmentStore.delete(enrollment.uid())
            } else {
                conflictStore.insert(
                    conflictHelper.getConflictBuilder(errorReport)
                        .tableReference(EnrollmentTableInfo.TABLE_INFO.name())
                        .enrollment(errorReport.uid)
                        .trackedEntityInstance(enrollment.trackedEntityInstance())
                        .build()
                )
            }
        }
    }

    override fun getRelatedRelationships(uid: String): List<String> {
        return relationshipStore.getRelationshipsByItem(RelationshipHelper.enrollmentItem(uid)).mapNotNull { it.uid() }
    }
}
