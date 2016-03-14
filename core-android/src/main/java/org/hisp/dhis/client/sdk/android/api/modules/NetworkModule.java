/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.api.modules;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.hisp.dhis.client.sdk.android.common.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.android.common.SystemInfoApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.organisationunit.IOrganisationUnitApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.android.program.IProgramApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramApiClient2;
import org.hisp.dhis.client.sdk.android.user.IUserApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.user.UserAccountApiClient;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.IConfigurationPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.core.program.IProgramApiClient;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.user.IUserApiClient;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static android.text.TextUtils.isEmpty;
import static okhttp3.Credentials.basic;

// import retrofit2.BaseUrl;
// import retrofit2.JacksonConverterFactory;

public class NetworkModule implements INetworkModule {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

    private final IOrganisationUnitApiClient organisationUnitApiClient;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IProgramApiClient programApiClient;
    private final IUserApiClient userApiClient;

    public NetworkModule(IPreferencesModule preferencesModule) {
        AuthInterceptor authInterceptor = new AuthInterceptor(
                preferencesModule.getUserPreferences(),
                preferencesModule.getConfigurationPreferences());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(authInterceptor);
        okHttpClient.interceptors().add(loggingInterceptor);

//        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        /* constructing jackson's object mapper */
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(
                MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS,
                MapperFeature.AUTO_DETECT_SETTERS);

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(new ApiBaseUrl(preferencesModule.getConfigurationPreferences()))
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        programApiClient = new ProgramApiClient2(retrofit.create(IProgramApiClientRetrofit.class));


        systemInfoApiClient = new SystemInfoApiClient(retrofit.create(
                SystemInfoApiClientRetrofit.class));
        userApiClient = new UserAccountApiClient(retrofit.create(
                IUserApiClientRetrofit.class));
        organisationUnitApiClient = new OrganisationUnitApiClient(retrofit.create(
                IOrganisationUnitApiClientRetrofit.class));

        // LEGACY
//        mDashboardApiClient = new DashboardApiClient(retrofit.create(
//                DashboardApiClientRetrofit.class));
//        mEventApiClient = new EventApiClient(retrofit.create(EventApiClientRetrofit.class));
//        mEnrollmentApiClient = null;
//        mTrackedEntityAttributeApiClient = null;
//        mTrackedEntityApiClient = null;
    }

    @Override
    public ISystemInfoApiClient getSystemInfoApiClient() {
        return systemInfoApiClient;
    }

    @Override
    public IProgramApiClient getProgramApiClient() {
        return programApiClient;
    }

    @Override
    public IOrganisationUnitApiClient getOrganisationUnitApiClient() {
        return organisationUnitApiClient;
    }

    @Override
    public IUserApiClient getUserApiClient() {
        return userApiClient;
    }

    private static class ApiBaseUrl {
        private final IConfigurationPreferences mConfigurationPreferences;

        public ApiBaseUrl(IConfigurationPreferences configurationPreferences) {
            mConfigurationPreferences = configurationPreferences;
        }

        public HttpUrl url() {
            Configuration configuration = mConfigurationPreferences.get();
            HttpUrl url = HttpUrl.parse(configuration.getServerUrl())
                    .newBuilder()
                    .addPathSegment("api")
                    .build();
            HttpUrl modifiedUrl = HttpUrl.parse(url.toString() + "/");
            //System.out.println("ApiBaseUrl.url: " + modifiedUrl);
            return modifiedUrl;
        }
    }

    private static class AuthInterceptor implements Interceptor {
        private final IUserPreferences mUserPreferences;
        private final IConfigurationPreferences mConfigurationPreferences;

        public AuthInterceptor(IUserPreferences preferences,
                               IConfigurationPreferences configurationPreferences) {
            mUserPreferences = preferences;
            mConfigurationPreferences = configurationPreferences;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            UserCredentials userCredentials = mUserPreferences.get();
            if (isEmpty(userCredentials.getUsername()) || isEmpty(userCredentials.getPassword())) {
                return chain.proceed(chain.request());
            }

            String base64Credentials = basic(userCredentials.getUsername(),
                    userCredentials.getPassword());
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            //System.out.println("Request: " + request.urlString());

            Response response = chain.proceed(request);
            if (!response.isSuccessful() &&
                    response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

                if (mUserPreferences.isUserConfirmed()) {
                    // invalidate existing user
                    mUserPreferences.invalidateUser();
                } else {
                    if (!mUserPreferences.isUserInvalidated()) {
                        // remove server URL address
                        mConfigurationPreferences.clear();
                    }

                    // remove username/password
                    mUserPreferences.clear();
                }
            } else {
                if (!mUserPreferences.isUserConfirmed()) {
                    mUserPreferences.confirmUser();
                }
            }
            return response;
        }
    }
}
