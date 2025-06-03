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

package org.hisp.dhis.android.core.note.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.NoteTableInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteUniquenessManager {

    private final NoteStore noteStore;

    public NoteUniquenessManager(NoteStore noteStore) {
        this.noteStore = noteStore;
    }

    public Set<Note> buildUniqueCollection(Collection<Note> notes, Note.NoteType noteType, String ownerUid) {
        if (noteType == null) {
            throw new IllegalArgumentException("Note type is null");
        }

        String ownerColumn = noteType == Note.NoteType.ENROLLMENT_NOTE ?
                NoteTableInfo.Columns.ENROLLMENT :
                NoteTableInfo.Columns.EVENT;

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataColumns.SYNC_STATE, State.TO_POST)
                .appendKeyStringValue(ownerColumn, ownerUid).build();
        List<Note> toPostNotes = noteStore.selectWhere(whereClause);

        String deleteWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(ownerColumn, ownerUid).build();
        noteStore.deleteWhere(deleteWhereClause);

        Set<Note> uniqueNotes = new HashSet<>();
        for (Note note : notes) {
            uniqueNotes.add(note.toBuilder()
                    .id(null)
                    .syncState(State.SYNCED)
                    .build());
        }

        for (Note toPostNote : toPostNotes) {
            uniqueNotes.add(toPostNote.toBuilder()
                    .id(null)
                    .build());
        }

        return uniqueNotes;
    }
}