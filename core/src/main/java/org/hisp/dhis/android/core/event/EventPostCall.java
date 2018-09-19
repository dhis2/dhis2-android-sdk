package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

public final class EventPostCall extends SyncCall<WebResponse> {
    // retrofit service
    private final EventService eventService;

    // adapter and stores
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    private EventPostCall(@NonNull EventService eventService,
                         @NonNull EventStore eventStore,
                         @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore) {
        this.eventService = eventService;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    @Override
    public WebResponse call() throws Exception {
        setExecuted();

        List<Event> eventsToPost = queryEventsToPost();

        // if there is nothing to send, return null
        if (eventsToPost.isEmpty()) {
            return WebResponse.EMPTY;
        }

        EventPayload eventPayload = new EventPayload();
        eventPayload.events = eventsToPost;

        WebResponse webResponse = new APICallExecutor().executeObjectCall(eventService.postEvents(eventPayload));
        handleWebResponse(webResponse);
        return webResponse;
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

    private void handleWebResponse(WebResponse webResponse) {
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);
        eventImportHandler.handleEventImportSummaries(
                webResponse.importSummaries().importSummaries()
        );
    }

    public static EventPostCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
        return new EventPostCall(
                retrofit.create(EventService.class),
                new EventStoreImpl(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter)
        );
    }
}
