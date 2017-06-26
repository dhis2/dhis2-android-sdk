package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.getState;

public class EventImportHandler {
    private final EventStore eventStore;

    public EventImportHandler(@NonNull EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void handleEventImportSummaries(@NonNull List<ImportSummary> importSummaries) {
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

            eventStore.setState(importSummary.reference(), state);
        }
    }
}
