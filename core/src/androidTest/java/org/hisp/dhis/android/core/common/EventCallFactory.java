package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Retrofit;

public class EventCallFactory {
    public static EventEndPointCall create(Retrofit retrofit,
            DatabaseAdapter databaseAdapter, String orgUnit, int pageLimit) {

        EventService eventService = retrofit.create(EventService.class);

        EventHandler eventHandler = HandlerFactory.createEventHandler(databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageLimit(pageLimit)
                .build();

        EventEndPointCall eventEndPointCall = new EventEndPointCall(eventService, databaseAdapter,
                resourceHandler,
                eventHandler, new Date(), eventQuery);

        return eventEndPointCall;
    }
}
