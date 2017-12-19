package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;

import java.util.Date;

import retrofit2.Retrofit;

public class TrackedEntityInstanceCallFactory {
    public static TrackedEntityInstanceEndPointCall create(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, String trackedEntityInstanceUid) {

        TrackedEntityInstanceService trackedEntityInstanceService =
                retrofit.create(TrackedEntityInstanceService.class);

        TrackedEntityInstanceHandler trackedEntityInstanceHandler =
                HandlerFactory.createTrackedEntityInstanceHandler(databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                new TrackedEntityInstanceEndPointCall(
                        trackedEntityInstanceService, databaseAdapter, trackedEntityInstanceHandler,
                        resourceHandler, new Date(), trackedEntityInstanceUid);

        return trackedEntityInstanceEndPointCall;
    }
}
