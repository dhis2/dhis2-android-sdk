/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.network;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

import static com.squareup.okhttp.Credentials.basic;


public final class RepoManager {
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 60 * 1000; // 60s
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 60 * 1000; // 60s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 60 * 1000; // 60s

    private RepoManager() {
        // no instances
    }

    public static DhisApi createService(HttpUrl serverUrl, Credentials credentials) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(provideServerUrl(serverUrl))
                .setConverter(provideJacksonConverter())
                .setClient(provideOkClient(credentials))
                .setErrorHandler(new RetrofitErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter.create(DhisApi.class);
    }

    private static String provideServerUrl(HttpUrl httpUrl) {
        return httpUrl.newBuilder()
                .addPathSegment("api")
                .build().toString();
    }

    private static Converter provideJacksonConverter() {
        return new JacksonConverter(DhisController.getInstance().getObjectMapper());
    }

    private static OkClient provideOkClient(Credentials credentials) {
        return new OkClient(provideOkHttpClient(credentials));
    }

    public static OkHttpClient provideOkHttpClient(Credentials credentials) {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        client.interceptors().add(provideInterceptor(credentials));
        client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return client;
    }

    private static Interceptor provideInterceptor(Credentials credentials) {
        return new AuthInterceptor(credentials.getUsername(), credentials.getPassword());
    }

    private static class AuthInterceptor implements Interceptor {
        private final String mUsername;
        private final String mPassword;

        public AuthInterceptor(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            String base64Credentials = basic(mUsername, mPassword);
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();
            Log.d("RepoManager", request.toString());
            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED &&
                    DhisController.getInstance().isUserLoggedIn()) {
                DhisController.getInstance().invalidateSession();
            }
            return response;
        }
    }

    private static class RetrofitErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            cause.printStackTrace();
            Log.d("RepoManager", "there was an error.." + cause.getKind().name());
            try {
                String body = new StringConverter().fromBody(cause.getResponse().getBody(), String.class);
                Log.e("RepoManager", body);
            } catch (ConversionException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            APIException apiException = APIException.fromRetrofitError(cause);
            switch (apiException.getKind()) {
                case CONVERSION:
                case UNEXPECTED: {
                    logResponseBody(cause);
                    Crashlytics.logException(cause.getCause());
                    break;
                }
                case HTTP:
                    if (apiException.getResponse().getStatus() >= 500) {
                        logResponseBody(cause);
                        Crashlytics.logException(cause.getCause());
                    } else if (apiException.getResponse().getStatus() == 409) {
                        logResponseBody(cause);
                        Crashlytics.logException(cause.getCause());
                    }
            }

            return apiException;
        }
    }

    private static void logResponseBody(RetrofitError cause) {
        if (cause.getResponse() != null && cause.getResponse().getBody() != null) {
            Crashlytics.log(cause.getResponse().getBody().toString());
        }
    }
}
