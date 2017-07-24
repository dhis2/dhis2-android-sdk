package org.hisp.dhis.android.core.enrollment;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.ImportEvent;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

public class EnrollmentImportHandler {
    private final EnrollmentStore enrollmentStore;
    private final EventImportHandler eventImportHandler;

    public EnrollmentImportHandler(@NonNull EnrollmentStore enrollmentStore,
                                   @NonNull EventImportHandler eventImportHandler) {
        this.enrollmentStore = enrollmentStore;
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

            if (importSummary.importEvent() != null) {
                ImportEvent importEvent = importSummary.importEvent();

                eventImportHandler.handleEventImportSummaries(importEvent.importSummaries());

            }
        }
    }
}
