package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventEndpointCall;
import org.hisp.dhis.android.core.event.EventQuery;

import java.util.Date;

import retrofit2.Retrofit;

public class EventCallFactory {
    public static EventEndpointCall create(Retrofit retrofit,
                                           DatabaseAdapter databaseAdapter, String orgUnit, int pageLimit) {

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withPageLimit(pageLimit)
                .build();

        GenericCallData data = GenericCallData.create(databaseAdapter, retrofit, new Date());
        return EventEndpointCall.create(data.retrofit(), eventQuery);
    }

    public static EventEndpointCall create(Retrofit retrofit,
                                           DatabaseAdapter databaseAdapter, String orgUnit, int pageLimit, String categoryComboUID,
                                           String categoryOptionUID) {

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

        GenericCallData data = GenericCallData.create(databaseAdapter, retrofit, new Date());
        return EventEndpointCall.create(data.retrofit(), eventQuery);
    }
}
