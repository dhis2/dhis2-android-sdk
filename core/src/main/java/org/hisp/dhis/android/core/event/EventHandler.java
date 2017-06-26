package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class EventHandler {
    private final EventStore eventStore;

    public EventHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void handle(@NonNull Event event) {
        if (event == null) {
            return;
        }

        if (isDeleted(event)) {
            eventStore.delete(event.uid());
        } else {
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
                    event.dueDate(), State.SYNCED, event.uid());

            if (updatedRow <= 0) {
                eventStore.insert(event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                        event.createdAtClient(), event.lastUpdatedAtClient(),
                        event.status(), latitude, longitude, event.program(), event.programStage(),
                        event.organisationUnit(), event.eventDate(), event.completedDate(),
                        event.dueDate(), State.SYNCED);
            }
        }
    }
}
