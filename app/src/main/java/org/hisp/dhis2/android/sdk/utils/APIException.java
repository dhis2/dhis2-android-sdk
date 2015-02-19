package org.hisp.dhis2.android.sdk.utils;

import org.hisp.dhis2.android.sdk.network.http.Response;

import java.io.IOException;

public class APIException extends Exception {
    private final String mUrl;
    private final Response mResponse;
    private final boolean mNetworkError;
    private final boolean mHttpError;
    private final boolean mConversionError;


    private APIException(String url, String message, Response response, Throwable exception,
                         boolean networkError, boolean httpError, boolean conversionError) {
        super(message, exception);
        mUrl = url;
        mResponse = response;
        mNetworkError = networkError;
        mHttpError = httpError;
        mConversionError = conversionError;
    }

    public static APIException networkError(String url, IOException exception) {
        return new APIException(url, exception.getMessage(), null, exception,
                true, false, false);
    }

    public static APIException httpError(String url, Response response) {
        String message = response.getStatus() + " " + response.getReason();
        return new APIException(url, message, response, null, false, true, false);
    }

    public static APIException conversionError(String url, Response response,
                                               Exception exception) {
        return new APIException(url, exception.getMessage(), response,
                exception, false, false, true);
    }

    public static APIException unexpectedError(String url, Throwable exception) {
        return new APIException(url, exception.getMessage(), null, exception, false, false, false);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return mResponse;
    }

    /**
     * Whether or not this error was the result of a network error.
     */
    public boolean isNetworkError() {
        return mNetworkError;
    }

    /**
     * Whether or not this error was the result of a http error.
     */
    public boolean isHttpError() {
        return mHttpError;
    }

    /**
     * Whether or not this error was the result of a conversion error.
     */
    public boolean isConversionError() {
        return mConversionError;
    }

    /**
     * Whether or not this error was the result of a unknown error.
     */
    public boolean isUnknownError() {
        return !mNetworkError && !mHttpError && !mConversionError;
    }
}
