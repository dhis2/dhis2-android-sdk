package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeQuery;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeService;

import java.util.Date;
import java.util.Set;

import retrofit2.Retrofit;

public class TrackedEntityAttributeEnPointCallFactory {

    public static TrackedEntityAttributeEndPointCall create(Retrofit retrofit,
            DatabaseAdapter databaseAdapter, Set<String> uids) {
        TrackedEntityAttributeService trackedEntityAttributeService = retrofit.create(
                TrackedEntityAttributeService.class);
        TrackedEntityAttributeHandler trackedEntityAttributeHandler =
                HandlerFactory.createTrackedEntityAttributeHandler(databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        TrackedEntityAttributeQuery trackedEntityAttributeQuery =
                TrackedEntityAttributeQuery.Builder
                        .create()
                        .withUIds(uids)
                        .build();

        TrackedEntityAttributeEndPointCall trackedEntityAttributeEndPointCall =
                new TrackedEntityAttributeEndPointCall(trackedEntityAttributeService,
                        databaseAdapter, trackedEntityAttributeQuery, new Date(),
                        trackedEntityAttributeHandler, resourceHandler);
        return trackedEntityAttributeEndPointCall;
    }
}
