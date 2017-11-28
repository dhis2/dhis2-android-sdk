package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.util.Date;

import retrofit2.Response;

public class EventCall implements Call<Response<Payload<Program>>> {

    private final EventService eventService;
    private final DatabaseAdapter databaseAdapter;
    private final EventQuery eventQuery;
    private final Date serverDate;
    private final ResourceStore resourceStore;
    private final EventStore eventStore;

    private boolean isExecuted;

    private final EventHandler eventHandler;

    public EventCall(EventService eventService,
            DatabaseAdapter databaseAdapter,
            ResourceStore resourceStore,
            EventStore eventStore,
            Date serverDate,
            EventQuery eventQuery) {
        this.eventService = eventService;
        this.databaseAdapter = databaseAdapter;
        this.resourceStore = resourceStore;
        this.eventStore = eventStore;
        this.eventQuery = eventQuery;
        this.serverDate = new Date(serverDate.getTime());
        this.eventHandler = new EventHandler(eventStore);

        if (eventQuery != null && eventQuery.getUIds() != null &&
                eventQuery.getUIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of events: " + eventQuery.getUIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<Program>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }



        return null;
    }
}
