package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.common.Payload;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;

@Reusable
final class EventEndpointCallFactory {

    private final EventService service;
    private final APICallExecutor apiCallExecutor;

    @Inject
    EventEndpointCallFactory(@NonNull EventService service, APICallExecutor apiCallExecutor) {
        this.service = service;
        this.apiCallExecutor = apiCallExecutor;
    }

    Callable<List<Event>> getCall(final EventQuery eventQuery) {
        return new Callable<List<Event>>() {
            @Override
            public List<Event> call() throws Exception {

                String categoryComboId = eventQuery.categoryCombo() == null ? null : eventQuery.categoryCombo().uid();

                Call<Payload<Event>> call = service.getEvents(eventQuery.orgUnit(), eventQuery.program(),
                        eventQuery.trackedEntityInstance(), EventFields.allFields, Boolean.TRUE,
                        eventQuery.page(), eventQuery.pageSize(), categoryComboId, eventQuery.lastUpdatedStartDate());

                return apiCallExecutor.executePayloadCall(call);
            }
        };
    }
}