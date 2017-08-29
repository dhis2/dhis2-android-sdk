package org.hisp.dhis.android.sdk.synchronization.data.event;

import org.hisp.dhis.android.sdk.persistence.models.Event;

public class EventLocalDataSource {
    public void save(Event event) {
        event.save();
    }
}
