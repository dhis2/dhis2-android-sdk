package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public final class EventEndpointCall extends SyncCall<List<Event>> {

    private final EventService eventService;
    private final EventQuery eventQuery;

    private EventEndpointCall(@NonNull EventService eventService,
                      @NonNull EventQuery eventQuery) {
        this.eventService = eventService;
        this.eventQuery = eventQuery;
    }

    @Override
    public List<Event> call() throws D2CallException {
        super.setExecuted();

        Call<Payload<Event>> call;
        Integer eventsToRequest = Math.min(eventQuery.getPageLimit(), eventQuery.getPageSize());

        if (eventQuery.getCategoryCombo() == null || eventQuery.getCategoryOption() == null) {
            call = eventService.getEvents(eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), Event.allFields, Event.uid.in(eventQuery.getUIds()),
                    Boolean.TRUE, eventQuery.getPage(), eventsToRequest);
        } else {
            CategoryCombo categoryCombo =  eventQuery.getCategoryCombo();
            CategoryOption categoryOption =  eventQuery.getCategoryOption();

            call = eventService.getEvents(eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), Event.allFields, Event.uid.in(eventQuery.getUIds()),
                    Boolean.TRUE, eventQuery.getPage(), eventsToRequest, categoryCombo.uid(),
                    categoryOption.uid());
        }

        return new APICallExecutor().executePayloadCall(call);
    }

    public static EventEndpointCall create(Retrofit retrofit, EventQuery eventQuery) {
        return new EventEndpointCall(
                retrofit.create(EventService.class),
                eventQuery);
    }
}