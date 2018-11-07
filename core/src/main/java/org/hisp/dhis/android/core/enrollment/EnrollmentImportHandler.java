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
