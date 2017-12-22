package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class EventPostCall implements Call<Response<WebResponse>> {
    // retrofit service
    private final EventService eventService;

    // adapter and stores
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    private boolean isExecuted;

    public EventPostCall(@NonNull EventService eventService,
                         @NonNull EventStore eventStore,
                         @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore) {
        this.eventService = eventService;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }


    @Override
    public Response<WebResponse> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("EventPostCall is already executed");
            }

            isExecuted = true;

        }

        List<Event> eventsToPost = queryEventsToPost();

        // if there is nothing to send, return null
        if (eventsToPost.isEmpty()) {
            return null;
        }

        EventPayload eventPayload = new EventPayload();
        eventPayload.events = eventsToPost;

        Response<WebResponse> response = eventService.postEvents(eventPayload).execute();

        if (response.isSuccessful()) {
            handleWebResponse(response);
        }
        return response;
    }

    @NonNull
    private List<Event> queryEventsToPost() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(Boolean.TRUE);
        List<Event> events = eventStore.querySingleEventsToPost();
        int eventSize = events.size();

        List<Event> eventRecreated = new ArrayList<>(eventSize);

        for (int i = 0; i < eventSize; i++) {
            Event event = events.get(i);
            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());

            eventRecreated.add(Event.create(event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                    event.createdAtClient(), event.lastUpdatedAtClient(), event.program(), event.programStage(),
                    event.organisationUnit(), event.eventDate(), event.status(), event.coordinates(),
                    event.completedDate(), event.dueDate(), event.deleted(), dataValuesForEvent,
                    event.attributeCategoryOptions(), event.attributeOptionCombo(), event.trackedEntityInstance()));
        }

        return eventRecreated;
    }

    private void handleWebResponse(Response<WebResponse> response) {
        WebResponse webResponse = response.body();
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);
        eventImportHandler.handleEventImportSummaries(
                webResponse.importSummaries().importSummaries()
        );

    }
}
