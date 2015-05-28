/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.network.http;

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