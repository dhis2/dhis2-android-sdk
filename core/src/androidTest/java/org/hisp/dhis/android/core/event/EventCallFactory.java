package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public class EventCallFactory {
    public static Callable<List<Event>> create(Retrofit retrofit,
                                               DatabaseAdapter databaseAdapter,
                                               String orgUnit,
                                               int pageSize) {

        EventQuery eventQuery = EventQuery.builder()
                .orgUnit(orgUnit)
                .pageSize(pageSize)
                .build();

        return new EventEndpointCallFactory(retrofit.create(EventService.class),
                APICallExecutorImpl.create(databaseAdapter)).getCall(eventQuery);
    }
}
