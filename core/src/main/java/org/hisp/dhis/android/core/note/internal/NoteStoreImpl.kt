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
package org.hisp.dhis.android.core.note.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class NoteStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : NoteStore,
    IdentifiableObjectStoreImpl<Note>(
        databaseAdapter,
        NoteTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> Note.create(cursor) },
    ) {

    companion object {
        private val BINDER = StatementBinder { o: Note, w: StatementWrapper ->
            w.bind(1, o.noteType())
            w.bind(2, o.event())
            w.bind(3, o.enrollment())
            w.bind(4, o.value())
            w.bind(5, o.storedBy())
            w.bind(6, o.storedDate())
            w.bind(7, o.uid())
            w.bind(8, o.syncState())
            w.bind(9, o.deleted())
        }
        val ENROLLMENT_CHILD_PROJECTION = SingleParentChildProjection(
            NoteTableInfo.TABLE_INFO,
            NoteTableInfo.Columns.ENROLLMENT,
        )
        val EVENT_CHILD_PROJECTION = SingleParentChildProjection(
            NoteTableInfo.TABLE_INFO,
            NoteTableInfo.Columns.EVENT,
        )
    }

    override fun getNotesForEvent(eventUid: String): List<Note> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(NoteTableInfo.Columns.EVENT, eventUid)
            .build()
        val selectStatement = builder.selectWhere(whereClause)
        return selectRawQuery(selectStatement)
    }

    override fun getNotesForEnrollment(enrollmentUid: String): List<Note> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(NoteTableInfo.Columns.ENROLLMENT, enrollmentUid)
            .build()
        val selectStatement = builder.selectWhere(whereClause)
        return selectRawQuery(selectStatement)
    }
}
