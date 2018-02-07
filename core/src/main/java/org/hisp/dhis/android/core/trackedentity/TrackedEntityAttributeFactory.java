package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class TrackedEntityAttributeFactory {
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityAttributeService trackedEntityAttributeService;
    private final ResourceHandler resourceHandler;
    private final TrackedEntityAttributeHandler trackedEntityAttributeHandler;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final List<DeletableStore> deletableStores;

    public TrackedEntityAttributeFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityAttributeService = retrofit.create(TrackedEntityAttributeService.class);
        this.resourceHandler = resourceHandler;
        this.trackedEntityAttributeStore = new TrackedEntityAttributeStoreImpl(databaseAdapter);
        this.trackedEntityAttributeHandler = new TrackedEntityAttributeHandler(
                trackedEntityAttributeStore);
        this.deletableStores = new ArrayList<>();
        this.deletableStores.add(trackedEntityAttributeStore);
    }

    public TrackedEntityAttributeEndPointCall newEndPointCall(
            TrackedEntityAttributeQuery trackedEntityAttributeQuery, Date serverDate)
            throws Exception {
        return new TrackedEntityAttributeEndPointCall(trackedEntityAttributeService,
                trackedEntityAttributeQuery,
                trackedEntityAttributeHandler, resourceHandler, databaseAdapter, serverDate);
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public TrackedEntityAttributeStore getTrackedEntityAttributeStore() {
        return trackedEntityAttributeStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }

    public TrackedEntityAttributeHandler getTrackedEntityAttributeHandler() {
        return trackedEntityAttributeHandler;
    }
}
