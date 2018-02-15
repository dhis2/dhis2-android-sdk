package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class TrackedEntityFactory {
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityService trackedEntityService;
    private final ResourceHandler resourceHandler;
    private final TrackedEntityHandler trackedEntityHandler;

    private final List<DeletableStore> deletableStores;

    public TrackedEntityFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityService = retrofit.create(TrackedEntityService.class);
        this.resourceHandler = resourceHandler;

        TrackedEntityStore trackedEntityStore = new TrackedEntityStoreImpl(databaseAdapter);

        this.deletableStores = new ArrayList<>();
        this.deletableStores.add(trackedEntityStore);


        this.trackedEntityHandler = new TrackedEntityHandler(trackedEntityStore);
    }

    public TrackedEntityCall newEndPointCall(TrackedEntityQuery trackedEntityQuery,
            Date serverDate) {
        return new TrackedEntityCall(databaseAdapter, getHandler(), resourceHandler,
                trackedEntityService, serverDate, trackedEntityQuery);
    }

    public TrackedEntityHandler getHandler() {
        return trackedEntityHandler;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }
}
