/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.core.network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

import static android.text.TextUtils.isEmpty;
import static com.squareup.okhttp.Credentials.basic;

import org.hisp.dhis.android.sdk.core.api.Dhis2;
import org.hisp.dhis.android.sdk.core.providers.ObjectMapperProvider;


public final class RepositoryManager {
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s

    private static ServerEndpoint serverEndpoint;
    private static AuthInterceptor interceptor;
    private static Converter converter;
    private static OkHttpClient okHttpClient;
    private static OkClient okClient;

    static {
        serverEndpoint = new ServerEndpoint();
        interceptor = new AuthInterceptor();
        converter = new JacksonConverter(ObjectMapperProvider.getInstance());

        // configuring OkHttpClient
        okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(interceptor);
        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        okClient = new OkClient(okHttpClient);
    }

    private RepositoryManager() {
        // no instances
    }

    public static DhisApi createService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(serverEndpoint)
                .setConverter(converter)
                .setClient(okClient)
                .setErrorHandler(new RetrofitErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        IDhisApi dhisApi = restAdapter.create(IDhisApi.class);

        return new DhisApi(serverEndpoint, interceptor, dhisApi);
    }

    public static OkHttpClient provideOkHttpClient() {
        return okHttpClient;
    }

    static class AuthInterceptor implements Interceptor {
        private String mUsername;
        private String mPassword;

        public AuthInterceptor() {
            // Empty constructor
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            if (isEmpty(mUsername) || isEmpty(mPassword)) {
                return chain.proceed(chain.request());
            }

            String base64Credentials = basic(mUsername, mPassword);
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED &&
                    Dhis2.isUserLoggedIn()) {
                Dhis2.invalidateSession();
            }
            return response;
        }

        public void setUsername(String username) {
            mUsername = username;
        }

        public void setPassword(String password) {
            mPassword = password;
        }
    }

    static class ServerEndpoint implements Endpoint {
        private static final String DHIS2 = "dhis2";
        private String mServerUrl;

        @Override
        public String getUrl() {
            return mServerUrl;
        }

        @Override
        public String getName() {
            return DHIS2;
        }

        public void setServerUrl(String serverUrl) {
            mServerUrl = serverUrl;
        }
    }

    private static class RetrofitErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            return APIException.fromRetrofitError(cause);
        }
    }
}