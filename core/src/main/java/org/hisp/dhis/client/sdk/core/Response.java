package org.hisp.dhis.client.sdk.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An HTTP response.
 */
public final class Response {
    private final String mUrl;
    private final int mStatus;
    private final String mReason;
    private final List<Header> mHeaders;
    private final byte[] mBody;

    public Response(String url, int status, String reason, List<Header> headers, byte[] body) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        if (status < 200) {
            throw new IllegalArgumentException("Invalid status code: " + status);
        }
        if (reason == null) {
            throw new IllegalArgumentException("reason == null");
        }
        if (headers == null) {
            throw new IllegalArgumentException("headers == null");
        }

        mUrl = url;
        mStatus = status;
        mReason = reason;
        mHeaders = Collections.unmodifiableList(new ArrayList<>(headers));
        mBody = body;
    }

    /**
     * Request URL.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Status line code.
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Status line reason phrase.
     */
    public String getReason() {
        return mReason;
    }

    /**
     * An unmodifiable collection of headers.
     */
    public List<Header> getHeaders() {
        return mHeaders;
    }

    /**
     * Response body. May be {@code null}.
     */
    public byte[] getBody() {
        return mBody;
    }
}
