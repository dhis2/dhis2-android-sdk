package org.hisp.dhis.client.sdk.core.trackedentity;

public class TrackedEntityInstanceInteractorImpl implements TrackedEntityInstanceInteractor {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceApi trackedEntityInstanceApi;

    public TrackedEntityInstanceInteractorImpl(TrackedEntityInstanceStore trackedEntityInstanceStore, TrackedEntityInstanceApi trackedEntityInstanceApi) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackedEntityInstanceApi = trackedEntityInstanceApi;
    }

    @Override
    public TrackedEntityInstanceStore store() {
        return trackedEntityInstanceStore;
    }

    @Override
    public TrackedEntityInstanceApi api() {
        return trackedEntityInstanceApi;
    }
}
