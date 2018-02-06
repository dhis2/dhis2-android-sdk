package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import java.util.List;

public class EventHandler {
    private final EventStore eventStore;
    private final TrackedEntityDataValueHandler trackedEntityDataValueHandler;

    public EventHandler(EventStore eventStore,
            TrackedEntityDataValueHandler trackedEntityDataValueHandler) {
        this.eventStore = eventStore;
        this.trackedEntityDataValueHandler = trackedEntityDataValueHandler;
    }

    public void handle(@NonNull List<Event> events) {

        if (events != null && !events.isEmpty()) {
            int size = events.size();

            for (int i = 0; i < size; i++) {
                Event event = events.get(i);
                handle(event);
            }
        }
    }

    private boolean isValid(@NonNull Event event) {
        return event.eventDate()!=null && event.organisationUnit()!=null;
    }

    public void handle(@NonNull Event event) {
        if (event == null) {
            return;
        }

        if (isDeleted(event)) {
            eventStore.delete(event.uid());
        } else if (isValid(event)) {
            String latitude = null;
            String longitude = null;

            if (event.coordinates() != null) {
                latitude = event.coordinates().latitude();
                longitude = event.coordinates().longitude();
            }


            int updatedRow = eventStore.update(event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                    event.createdAtClient(), event.lastUpdatedAtClient(),
                    event.status(), latitude, longitude, event.program(), event.programStage(),
                    event.organisationUnit(), event.eventDate(), event.completedDate(),
                    event.dueDate(), State.SYNCED, event.attributeCategoryOptions(), event.attributeOptionCombo(),
                    event.trackedEntityInstance(), event.uid());

            if (updatedRow <= 0) {
                eventStore.insert(event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                        event.createdAtClient(), event.lastUpdatedAtClient(),
                        event.status(), latitude, longitude, event.program(), event.programStage(),
                        event.organisationUnit(), event.eventDate(), event.completedDate(),
                        event.dueDate(), State.SYNCED, event.attributeCategoryOptions(), event.attributeOptionCombo(),
                        event.trackedEntityInstance());
            }

            trackedEntityDataValueHandler.handle(event.uid(),
                    event.trackedEntityDataValues());
        } else {
            Log.d(this.getClass().getSimpleName(), event.uid() + " with no org. unit or event date");
        }
    }


}
