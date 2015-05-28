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

package org.hisp.dhis.android.sdk.network.managers;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.http.RestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;


public final class HttpManager implements IHttpManager {
    private static final String CLASS_TAG = "HttpManager";
    private static final String JSON_TYPE = "application/json";
    private static final String TEXT_TYPE = "text/plain";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final MediaType JSON =
            MediaType.parse(JSON_TYPE + ";" + "charset=utf-8");
    private static final MediaType TEXT =
            MediaType.parse(TEXT_TYPE + ";" + "charset=utf-8");

    public static final long TIME_OUT = 1500;

    private OkHttpClient okClient;

    public HttpManager(OkHttpClient okHttpClient) {
        okClient = isNull(okHttpClient, "OkHttpClient must not be null");
    }

    private static com.squareup.okhttp.Request buildOkRequest(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request object cannot be null");
        }

        com.squareup.okhttp.Request.Builder okRequestBuilder = new com.squareup.okhttp.Request.Builder();
        if (request.getHeaders() != null) {
            for (Header header : request.getHeaders()) {
                okRequestBuilder.addHeader(header.getName(), header.getValue());
            }
        }

        String url = request.getUrl();
        RestMethod method = request.getMethod();

        Log.e(CLASS_TAG, "requestUrl: " + url);

        String body = new String();
        if (request.getBody() != null) {
            body = new String(request.getBody());
        }

        com.squareup.okhttp.RequestBody requestBody;
        if (isPlainText(request.getHeaders())) {
            requestBody = com.squareup.okhttp.RequestBody.create(TEXT, body);
        } else {
            requestBody = com.squareup.okhttp.RequestBody.create(JSON, body);
        }


        if (RestMethod.PUT.equals(method)) {
            return okRequestBuilder.put(requestBody).url(url).build();
        } else if (RestMethod.PATCH.equals(method)) {
            return okRequestBuilder.patch(requestBody).url(url).build();
        } else if (RestMethod.POST.equals(method)) {
            return okRequestBuilder.post(requestBody).url(url).build();
        } else if (RestMethod.DELETE.equals(method)) {
            return okRequestBuilder.delete().url(url).build();
        } else if (RestMethod.HEAD.equals(method)) {
            return okRequestBuilder.head().url(url).build();
        } else {
            return okRequestBuilder.get().url(url).build();
        }
    }

    private static Response buildResponse(com.squareup.okhttp.Response okResponse) throws IOException {
        if (okResponse == null) {
            throw new IllegalArgumentException("Response object cannot be null");
        }

        com.squareup.okhttp.Headers okHeaders = okResponse.headers();
        ArrayList<Header> headers = new ArrayList<Header>();
        if (okHeaders != null) {
            for (String headerName : okHeaders.names()) {
                headers.add(new Header(headerName, okHeaders.get(headerName)));
            }
        }

        return new Response(
                okResponse.request().urlString(),
                okResponse.code(), okResponse.message(),
                headers, okResponse.body().bytes()
        );
    }

    @Override
    public Response request(Request request) throws IOException {
        com.squareup.okhttp.Request okRequest = buildOkRequest(request);
        com.squareup.okhttp.Response okResponse = okClient.newCall(okRequest).execute();

        return buildResponse(okResponse);
    }

    private static boolean isPlainText(List<Header> headers) {
        Header contentType = null;
        if (headers != null && headers.size() > 0) {
            for (Header header : headers) {
                if (CONTENT_TYPE_HEADER.equals(header.getName())) {
                    contentType = header;
                }
            }
        }

        return contentType != null && TEXT_TYPE.equals(contentType.getValue());
    }
}
