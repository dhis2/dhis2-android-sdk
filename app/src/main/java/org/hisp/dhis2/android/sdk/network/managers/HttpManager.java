package org.hisp.dhis2.android.sdk.network.managers;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import org.hisp.dhis2.android.sdk.network.http.Header;
import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.http.RestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;


public final class HttpManager implements IHttpManager {
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

        Log.e("ddd", "requestUrl: " + url);

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
