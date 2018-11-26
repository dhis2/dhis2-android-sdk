package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventEndpointCall;
import org.hisp.dhis.android.core.event.EventQuery;

import retrofit2.Retrofit;

public class EventCallFactory {
    public static EventEndpointCall create(Retrofit retrofit,
                                           DatabaseAdapter databaseAdapter,
                                           String orgUnit,
                                           int pageSize) {

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageSize(pageSize)
                .build();

        return EventEndpointCall.create(retrofit, databaseAdapter, eventQuery);
    }

    public static EventEndpointCall create(Retrofit retrofit,
                                           DatabaseAdapter databaseAdapter,
                                           String orgUnit,
                                           int pageSize,
                                           String categoryComboUID) {

        CategoryCombo categoryCombo = CategoryCombo
                .builder()
                .uid(categoryComboUID)
                .build();

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageSize(pageSize)
                .withCategoryCombo(categoryCombo)
                .build();

        return EventEndpointCall.create(retrofit, databaseAdapter, eventQuery);
    }
}
