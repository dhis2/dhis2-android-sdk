/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteTableInfo;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.ImportEvent;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

public class EnrollmentImportHandler {
    private final EnrollmentStore enrollmentStore;
    private final ObjectWithoutUidStore<Note> noteStore;
    private final EventImportHandler eventImportHandler;

    public EnrollmentImportHandler(@NonNull EnrollmentStore enrollmentStore,
                                   @NonNull ObjectWithoutUidStore<Note> noteStore,
                                   @NonNull EventImportHandler eventImportHandler) {
        this.enrollmentStore = enrollmentStore;
        this.noteStore = noteStore;
        this.eventImportHandler = eventImportHandler;
    }

    public void handleEnrollmentImportSummary(@NonNull List<ImportSummary> importSummaries) {
        if (importSummaries == null) {
            return;
        }

        int size = importSummaries.size();
        for (int i = 0; i < size; i++) {
            ImportSummary importSummary = importSummaries.get(i);

            if (importSummary == null) {
                break;
            }

            ImportStatus importStatus = importSummary.importStatus();
            State state = getState(importStatus);

            enrollmentStore.setState(importSummary.reference(), state);

            handleNoteImportSummary(importSummary.reference(), state);

            if (importSummary.importEvent() != null) {
                ImportEvent importEvent = importSummary.importEvent();

                eventImportHandler.handleEventImportSummaries(importEvent.importSummaries());

            }
        }
    }

    private void handleNoteImportSummary(String enrollmentUid, State state) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST)
                .appendKeyStringValue(NoteTableInfo.Columns.ENROLLMENT, enrollmentUid).build();
        List<Note> notes = noteStore.selectWhereClause(whereClause);
        for (Note note : notes) {
            noteStore.updateWhere(note.toBuilder().state(state).build());
        }
    }
}
