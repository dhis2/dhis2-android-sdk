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

package org.hisp.dhis.android.sdk.core.modules;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.hisp.dhis.android.sdk.corejava.common.modules.INetworkModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.IPreferencesModule;
import org.hisp.dhis.android.sdk.corejava.common.network.Configuration;
import org.hisp.dhis.android.sdk.corejava.common.network.UserCredentials;
import org.hisp.dhis.android.sdk.corejava.common.preferences.IConfigurationPreferences;
import org.hisp.dhis.android.sdk.corejava.common.preferences.IUserPreferences;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardApiClient;
import org.hisp.dhis.android.sdk.corejava.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.android.sdk.corejava.user.IUserApiClient;
import org.hisp.dhis.android.sdk.core.clients.DashboardApiClient;
import org.hisp.dhis.android.sdk.core.clients.DashboardApiClientRetrofit;
import org.hisp.dhis.android.sdk.core.clients.SystemInfoApiClientRetrofit;
import org.hisp.dhis.android.sdk.core.clients.SystemInfoApiClient;
import org.hisp.dhis.android.sdk.core.clients.UserApiClient;
import org.hisp.dhis.android.sdk.core.clients.UserApiClientRetrofit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import retrofit.BaseUrl;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

import static com.squareup.okhttp.Credentials.basic;
import static org.hisp.dhis.android.sdk.models.utils.StringUtils.isEmpty;

public class NetworkModule implements INetworkModule {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

    private final IDashboardApiClient mDashboardApiClient;
    private final ISystemInfoApiClient mSystemInfoApiClient;
    private final IUserApiClient mUserApiClient;

    public NetworkModule(IPreferencesModule preferencesModule) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new AuthInterceptor(preferencesModule.getUserPreferences()));
        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        /* constructing jackson's object mapper */
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(
                MapperFeature.AUTO_DETECT_CREATORS,
                MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS,
                MapperFeature.AUTO_DETECT_IS_GETTERS,
                MapperFeature.AUTO_DETECT_SETTERS
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new ApiBaseUrl(preferencesModule.getConfigurationPreferences()))
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        mDashboardApiClient = new DashboardApiClient(retrofit.create(DashboardApiClientRetrofit.class));
        mSystemInfoApiClient = new SystemInfoApiClient(retrofit.create(SystemInfoApiClientRetrofit.class));
        mUserApiClient = new UserApiClient(retrofit.create(UserApiClientRetrofit.class));
    }

    @Override
    public IDashboardApiClient getDashboardApiClient() {
        return mDashboardApiClient;
    }

    @Override
    public ISystemInfoApiClient getSystemInfoApiClient() {
        return mSystemInfoApiClient;
    }

    @Override
    public IUserApiClient getUserApiClient() {
        return mUserApiClient;
    }

    private static class ApiBaseUrl implements BaseUrl {
        private final IConfigurationPreferences mConfigurationPreferences;

        public ApiBaseUrl(IConfigurationPreferences configurationPreferences) {
            mConfigurationPreferences = configurationPreferences;
        }

        @Override
        public HttpUrl url() {
            Configuration configuration = mConfigurationPreferences.get();
            return HttpUrl.parse(configuration.getServerUrl());
        }
    }

    private static class AuthInterceptor implements Interceptor {
        private final IUserPreferences mUserPreferences;

        public AuthInterceptor(IUserPreferences preferences) {
            mUserPreferences = preferences;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            UserCredentials userCredentials = mUserPreferences.get();
            if (isEmpty(userCredentials.getUsername())
                    || isEmpty(userCredentials.getPassword())) {
                return chain.proceed(chain.request());
            }

            String base64Credentials = basic(userCredentials.getUsername(),
                    userCredentials.getPassword());
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            Response response = chain.proceed(request);
            if (!response.isSuccessful() && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                mUserPreferences.invalidateUserCredentials();
            }
            return response;
        }
    }
}
