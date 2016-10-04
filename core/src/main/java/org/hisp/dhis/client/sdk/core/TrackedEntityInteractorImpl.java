package org.hisp.dhis.client.sdk.core;

public class TrackedEntityInteractorImpl implements TrackedEntityInteractor {
    private final TrackedEntityStore trackedEntityStore;
    private final TrackedEntityApi trackedEntityApi;

    public TrackedEntityInteractorImpl(TrackedEntityStore trackedEntityStore, TrackedEntityApi trackedEntityApi) {
        this.trackedEntityStore = trackedEntityStore;
        this.trackedEntityApi = trackedEntityApi;
    }

    @Override
    public TrackedEntityStore store() {
        return trackedEntityStore;
    }

    @Override
    public TrackedEntityApi api() {
        return trackedEntityApi;
    }
}
