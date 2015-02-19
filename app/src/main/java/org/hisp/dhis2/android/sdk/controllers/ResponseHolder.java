package org.hisp.dhis2.android.sdk.controllers;

import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.utils.APIException;

public class ResponseHolder<T> {
    private T item;
    private Response response;
    private APIException apiException;

    public APIException getApiException() {
        return apiException;
    }

    public void setApiException(APIException apiException) {
        this.apiException = apiException;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
