package org.hisp.dhis.android.core.calls;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;

public class DataPostCall implements Call {
    // service
    private TrackedEntityInstanceService trackedEntityInstanceService;


    private boolean isExecuted;

    public DataPostCall(TrackedEntityInstanceService trackedEntityInstanceService) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Object call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already isExecuted");
            }

            isExecuted = true;

        }

        return null;
    }
}
