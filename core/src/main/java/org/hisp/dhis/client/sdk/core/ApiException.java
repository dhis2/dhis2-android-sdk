package org.hisp.dhis.client.sdk.core;

import java.io.IOException;

public class ApiException extends RuntimeException {
    private final String url;
    private final Response response;
    private final Kind kind;

    private ApiException(String message, String url, Response response,
                         Kind kind, Throwable exception) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.kind = kind;
    }

    public static ApiException networkError(String url, IOException exception) {
        return new ApiException(exception.getMessage(), url,
                null, Kind.NETWORK, exception);
    }

    public static ApiException conversionError(String url, Response response,
                                               Throwable exception) {
        return new ApiException(exception.getMessage(), url,
                response, Kind.CONVERSION, exception);
    }

    public static ApiException httpError(String url, Response response) {
        String message = response.getStatus() + " " + response.getReason();
        return new ApiException(message, url, response, Kind.HTTP, null);
    }

    public static ApiException unexpectedError(String url, Throwable exception) {
        return new ApiException(exception.getMessage(), url, null, Kind.UNEXPECTED,
                exception);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Identifies the event kind which triggered a {@link ApiException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * An exception was thrown while (de)serializing a body.
         */
        CONVERSION,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }
}
