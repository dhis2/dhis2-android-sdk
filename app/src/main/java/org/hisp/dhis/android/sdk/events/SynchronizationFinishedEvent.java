package org.hisp.dhis.android.sdk.events;

/**
 * Created by erling on 6/12/15.
 */
public class SynchronizationFinishedEvent<T> extends BaseEvent
{
    public boolean success;

    public SynchronizationFinishedEvent(EventType eventType) {
        super(eventType);
    }
}
