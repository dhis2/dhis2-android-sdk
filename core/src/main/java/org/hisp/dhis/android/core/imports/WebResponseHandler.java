package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceImportHandler;

public class WebResponseHandler {
    private final TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler;

    public WebResponseHandler(@NonNull TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler) {
        this.trackedEntityInstanceImportHandler = trackedEntityInstanceImportHandler;
    }

    public void handleWebResponse(@NonNull WebResponse webResponse) {
        if (webResponse == null || webResponse.importSummaries() == null) {
            return;
        }

        ImportSummaries importSummaries = webResponse.importSummaries();

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                importSummaries.importSummaries()
        );

    }

}
