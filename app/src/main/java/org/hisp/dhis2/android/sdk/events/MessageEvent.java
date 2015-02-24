package org.hisp.dhis2.android.sdk.events;

import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
public class MessageEvent<T> extends BaseEvent{

    public MessageEvent(BaseEvent.EventType eventType) {
        super(eventType);
    }

}