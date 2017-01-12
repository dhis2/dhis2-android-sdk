package org.hisp.dhis.android.core.data.database;

import org.hisp.dhis.android.core.event.EventStatus;

public class DbEventStatusColumnAdapter extends EnumColumnAdapter<EventStatus> {
    @Override
    protected Class<EventStatus> getEnumClass() {
        return EventStatus.class;
    }
}
