package org.hisp.dhis2.android.sdk.events;

import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.network.http.Response;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public class ResponseEvent<T> extends BaseEvent {

    public ResponseEvent(EventType eventType) {
        super(eventType);
    }

    private ResponseHolder<T> responseHolder;
    public ResponseHolder<T> getResponseHolder() {
        return responseHolder;
    }
    public void setResponseHolder(ResponseHolder<T> responseHolder) {
        this.responseHolder = responseHolder;
    }
}