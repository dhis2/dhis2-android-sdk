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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteTableInfo

@Reusable
internal class JobReportEventHandler @Inject internal constructor(
    private val noteStore: IdentifiableObjectStore<Note>,
    private val conflictStore: TrackerImportConflictStore,
    private val eventStore: EventStore,
    private val conflictHelper: TrackerConflictHelper
) : JobReportTypeHandler() {

    fun handleEventNotes(eventUid: String, state: State) {
        val newNoteState = if (state == State.SYNCED) State.SYNCED else State.TO_POST
        val whereClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.STATE, State.uploadableStatesIncludingError().map { it.name }
            )
            .appendKeyStringValue(NoteTableInfo.Columns.EVENT, eventUid).build()
        for (note in noteStore.selectWhere(whereClause)) {
            noteStore.update(note.toBuilder().state(newNoteState).build())
        }
    }

    override fun handleObject(uid: String, state: State) {
        eventStore.setState(uid, state)
        conflictStore.deleteEventConflicts(uid)
        handleEventNotes(uid, state)
    }

    override fun storeConflict(errorReport: JobValidationError) {
        conflictStore.insert(
            conflictHelper.getConflictBuilder(errorReport)
                .tableReference(EventTableInfo.TABLE_INFO.name())
                .event(errorReport.uid).build()
        )
    }
}