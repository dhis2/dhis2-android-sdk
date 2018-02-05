package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;
import java.util.Set;

import retrofit2.Retrofit;

public class TrackedEntityFactory {
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityService trackedEntityService;
    private final ResourceHandler resourceHandler;
    private final TrackedEntityHandler trackedEntityHandler;

    public TrackedEntityFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityService = retrofit.create(TrackedEntityService.class);
        this.resourceHandler = resourceHandler;
        this.trackedEntityHandler = new TrackedEntityHandler(
                new TrackedEntityStoreImpl(databaseAdapter));
    }

    public TrackedEntityCall newEndPointCall(Set<String> trackedEntityUids, Date serverDate) {
        return new TrackedEntityCall(
                trackedEntityUids, databaseAdapter, getHandler(), resourceHandler,
                trackedEntityService, serverDate);
    }

    public TrackedEntityHandler getHandler() {
        return trackedEntityHandler;
    }
}
