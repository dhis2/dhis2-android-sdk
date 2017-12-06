package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventCall;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;

import java.util.Date;

import retrofit2.Retrofit;

public class EventCallFactory {
    public static EventCall create(Retrofit retrofit,
            DatabaseAdapter databaseAdapter, String orgUnit, int pageLimit) {

        EventService eventService = retrofit.create(EventService.class);

        EventStore eventStore = new EventStoreImpl(databaseAdapter);

        TrackedEntityDataValueStore trackedEntityDataValueStore =
                new TrackedEntityDataValueStoreImpl(databaseAdapter);

        TrackedEntityDataValueHandler trackedEntityDataValueHandler =
                new TrackedEntityDataValueHandler(trackedEntityDataValueStore);

        EventHandler eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter);
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageLimit(pageLimit)
                .build();

        EventCall eventCall = new EventCall(eventService, databaseAdapter, resourceHandler,
                eventHandler, new Date(), eventQuery);

        return eventCall;
    }
}
