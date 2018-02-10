package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
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

        return new EventEndPointCall(eventService, databaseAdapter,
                resourceHandler,
                eventHandler, new Date(), eventQuery);

    }

    public static EventEndPointCall create(Retrofit retrofit,
            DatabaseAdapter databaseAdapter, String orgUnit, int pageLimit, String categoryComboUID,
            String categoryOptionUID) {

        EventService eventService = retrofit.create(EventService.class);

        EventHandler eventHandler = HandlerFactory.createEventHandler(databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        CategoryCombo categoryCombo = CategoryCombo
                .builder()
                .uid(categoryComboUID)
                .build();

        CategoryOption categoryOption = CategoryOption
                .builder()
                .uid(categoryOptionUID)
                .build();

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageLimit(pageLimit)
                .withCategoryComboAndCategoryOption(categoryCombo, categoryOption)
                .build();

        return new EventEndPointCall(eventService, databaseAdapter,
                resourceHandler,
                eventHandler, new Date(), eventQuery);

    }
}
