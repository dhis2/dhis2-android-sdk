package org.hisp.dhis.android.core.trackedentity;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.ImportEnrollment;
import org.hisp.dhis.android.core.imports.ImportEvent;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

public class TrackedEntityInstanceImportHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentImportHandler enrollmentImportHandler;
    private final EventImportHandler eventImportHandler;

    public TrackedEntityInstanceImportHandler(@NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                              @NonNull EnrollmentImportHandler enrollmentImportHandler,
                                              @NonNull EventImportHandler eventImportHandler) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentImportHandler = enrollmentImportHandler;
        this.eventImportHandler = eventImportHandler;
    }

    public void handleTrackedEntityInstanceImportSummaries(@NonNull List<ImportSummary> importSummaries) {
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

            // store the state in database
            trackedEntityInstanceStore.setState(importSummary.reference(), state);

            if (importSummary.importEnrollment() != null) {
                ImportEnrollment importEnrollment = importSummary.importEnrollment();

                enrollmentImportHandler.handleEnrollmentImportSummary(importEnrollment.importSummaries());
            }


            if (importSummary.importEvent() != null) {
                ImportEvent importEvent = importSummary.importEvent();

                eventImportHandler.handleEventImportSummaries(importEvent.importSummaries());
            }
        }


    }
}

