/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment.note;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NoteUniquenessManager {

    public static Set<Note> buildUniqueCollection(Collection<Note> notes,
                                                  String enrollmentUid,
                                                  ObjectWithoutUidStore<Note> noteStore) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST)
                .appendKeyStringValue(NoteTableInfo.Columns.ENROLLMENT, enrollmentUid).build();
        List<Note> toPostNotes = noteStore.selectWhereClause(whereClause);
        noteStore.delete();

        Set<Note> uniqueNotes = new HashSet<>();
        for (Note note : notes) {
            uniqueNotes.add(note.toBuilder()
                    .id(null)
                    .state(null)
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