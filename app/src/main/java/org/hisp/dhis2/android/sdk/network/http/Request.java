package org.hisp.dhis2.android.sdk.network.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates all of the information necessary to make an HTTP request.
 */
public final class Request {
    private final RestMethod mMethod;
    private final String mUrl;
    private final List<Header> mHeaders;
    private final byte[] mBody;

    public Request(RestMethod method, String url, List<Header> headers, byte[] body) {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        if (url == null) {
            throw new IllegalArgumentException("URL must not be null.");
        }
        mMethod = method;
        mUrl = url;

        if (headers == null) {
            mHeaders = Collections.emptyList();
        } else {
            mHeaders = Collections.unmodifiableList(new ArrayList<Header>(headers));
        }

        mBody = body;
    }

    /**
     * HTTP method verb.
     */
    public RestMethod getMethod() {
        return mMethod;
    }

    /**
     * Target URL.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns an unmodifiable list of headers, never {@code null}.
     */
    public List<Header> getHeaders() {
        return mHeaders;
    }

    /**
     * Returns the request body or {@code null}.
     */
    public byte[] getBody() {
        return mBody;
    }
}