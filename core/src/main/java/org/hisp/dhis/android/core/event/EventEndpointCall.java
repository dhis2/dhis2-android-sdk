package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.APICallExecutorImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public final class EventEndpointCall extends SyncCall<List<Event>> {

    private final EventService eventService;
    private final EventQuery eventQuery;
    private final APICallExecutor apiCallExecutor;

    private EventEndpointCall(@NonNull EventService eventService,
                              @NonNull EventQuery eventQuery,
                              @NonNull APICallExecutor apiCallExecutor) {
        this.eventService = eventService;
        this.eventQuery = eventQuery;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public List<Event> call() throws D2Error {
        setExecuted();

        Call<Payload<Event>> call;

        if (eventQuery.getCategoryCombo() == null) {
            call = eventService.getEvents(eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), Event.allFields, Boolean.TRUE,
                    eventQuery.getPage(), eventQuery.getPageSize());
        } else {
            CategoryCombo categoryCombo =  eventQuery.getCategoryCombo();

            call = eventService.getEvents(eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), Event.allFields, Boolean.TRUE,
                    eventQuery.getPage(), eventQuery.getPageSize(), categoryCombo.uid());
        }

        return apiCallExecutor.executePayloadCall(call);
    }

    public static EventEndpointCall create(Retrofit retrofit, DatabaseAdapter databaseAdapter, EventQuery eventQuery) {
        return new EventEndpointCall(
                retrofit.create(EventService.class),
                eventQuery,
                APICallExecutorImpl.create(databaseAdapter));
    }
}