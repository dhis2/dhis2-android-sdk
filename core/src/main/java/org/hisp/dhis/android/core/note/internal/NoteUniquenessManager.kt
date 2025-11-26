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

import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.Note.NoteType
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.note.NoteTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class NoteUniquenessManager(private val noteStore: NoteStore) {
    suspend fun buildUniqueCollection(notes: Collection<Note>, noteType: NoteType, ownerUid: String): Set<Note> {
        val ownerColumn = when (noteType) {
            NoteType.ENROLLMENT_NOTE -> NoteTableInfo.Columns.ENROLLMENT
            NoteType.EVENT_NOTE -> NoteTableInfo.Columns.EVENT
        }

        val toPostWhere = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.SYNC_STATE, State.TO_POST)
            .appendKeyStringValue(ownerColumn, ownerUid)
            .build()

        val toPostNotes = noteStore.selectWhere(toPostWhere)

        noteStore.deleteByOwner(ownerColumn, ownerUid)

        val newNotes = notes
            .map { it.toBuilder().syncState(State.SYNCED).build() }
            .toSet()

        val pendingNotes = toPostNotes.toSet()

        return newNotes + pendingNotes
    }
}
