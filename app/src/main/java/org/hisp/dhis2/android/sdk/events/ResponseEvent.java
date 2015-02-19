package org.hisp.dhis2.android.sdk.events;

import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public class ResponseEvent<T> {
    private ResponseHolder<T> responseHolder;
    public ResponseHolder<T> getResponseHolder() {
        return responseHolder;
    }
    public void setResponseHolder(ResponseHolder<T> responseHolder) {
        this.responseHolder = responseHolder;
    }
}