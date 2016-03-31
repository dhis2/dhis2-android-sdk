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

package org.hisp.dhis.client.sdk.android.api.network;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.hisp.dhis.client.sdk.android.dataelement.DataElementApiClient;
import org.hisp.dhis.client.sdk.android.dataelement.IDataElementApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.event.EventApiClient;
import org.hisp.dhis.client.sdk.android.event.EventApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.optionset.IOptionSetApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetApiClient;
import org.hisp.dhis.client.sdk.android.organisationunit.IOrganisationUnitApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.android.program.IProgramApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.IProgramStageApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.IProgramStageDataElementApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.IProgramStageSectionApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramApiClient2;
import org.hisp.dhis.client.sdk.android.program.ProgramStageApiClient;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementApiClient;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionApiClient;
import org.hisp.dhis.client.sdk.android.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.android.systeminfo.SystemInfoApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.user.IUserApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.user.UserAccountApiClient;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementApiClient;
import org.hisp.dhis.client.sdk.core.event.IEventApiClient;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetApiClient;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.core.program.IProgramApiClient;
import org.hisp.dhis.client.sdk.core.program.IProgramStageApiClient;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementApiClient;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionApiClient;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.user.IUserApiClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

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


// Find a way to organize session
public class NetworkModule implements INetworkModule {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

    private final IOrganisationUnitApiClient organisationUnitApiClient;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IProgramApiClient programApiClient;
    private final IProgramStageApiClient programStageApiClient;
    private final IProgramStageSectionApiClient programStageSectionApiClient;
    private final IUserApiClient userApiClient;
    private final IEventApiClient eventApiClient;
    private final IDataElementApiClient dataElementApiClient;
    private final IProgramStageDataElementApiClient programStageDataElementApiClient;
    private final IOptionSetApiClient optionSetApiClient;

    public NetworkModule(IPreferencesModule preferencesModule) {
        AuthInterceptor authInterceptor = new AuthInterceptor(
                preferencesModule.getUserPreferences());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor) // TODO Consider replacing with Authenticator
                .addInterceptor(loggingInterceptor)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        // Constructing jackson's object mapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(
                MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS,
                MapperFeature.AUTO_DETECT_SETTERS);

        // Extracting base url
        Configuration configuration = preferencesModule
                .getConfigurationPreferences().get();
        HttpUrl url = HttpUrl.parse(configuration.getServerUrl())
                .newBuilder()
                .addPathSegment("api")
                .build();
        HttpUrl modifiedUrl = HttpUrl.parse(url.toString() + "/"); // TODO EW!!!

        // Creating retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(modifiedUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        programApiClient = new ProgramApiClient2(retrofit.create(
                IProgramApiClientRetrofit.class));
        programStageApiClient = new ProgramStageApiClient(retrofit.create(
                IProgramStageApiClientRetrofit.class));
        programStageSectionApiClient = new ProgramStageSectionApiClient(
                retrofit.create(IProgramStageSectionApiClientRetrofit.class));
        systemInfoApiClient = new SystemInfoApiClient(retrofit.create(
                SystemInfoApiClientRetrofit.class));
        userApiClient = new UserAccountApiClient(retrofit.create(
                IUserApiClientRetrofit.class));
        organisationUnitApiClient = new OrganisationUnitApiClient(retrofit.create(
                IOrganisationUnitApiClientRetrofit.class));
        eventApiClient = new EventApiClient(retrofit.create(
                EventApiClientRetrofit.class));
        dataElementApiClient = new DataElementApiClient(retrofit.create(
                IDataElementApiClientRetrofit.class));
        programStageDataElementApiClient = new ProgramStageDataElementApiClient(retrofit.create(
                IProgramStageDataElementApiClientRetrofit.class));
        optionSetApiClient = new OptionSetApiClient(retrofit.create(
                IOptionSetApiClientRetrofit.class));
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
    public IProgramStageApiClient getProgramStageApiClient() {
        return programStageApiClient;
    }

    @Override
    public IProgramStageSectionApiClient getProgramStageSectionApiClient() {
        return programStageSectionApiClient;
    }

    @Override
    public IOrganisationUnitApiClient getOrganisationUnitApiClient() {
        return organisationUnitApiClient;
    }

    @Override
    public IUserApiClient getUserApiClient() {
        return userApiClient;
    }

    @Override
    public IEventApiClient getEventApiClient() {
        return eventApiClient;
    }

    @Override
    public IDataElementApiClient getDataElementApiClient() {
        return dataElementApiClient;
    }

    @Override
    public IProgramStageDataElementApiClient getProgramStageDataElementApiClient() {
        return programStageDataElementApiClient;
    }

    @Override
    public IOptionSetApiClient getOptionSetApiClient() {
        return optionSetApiClient;
    }

    private static class AuthInterceptor implements Interceptor {
        private final IUserPreferences mUserPreferences;

        public AuthInterceptor(IUserPreferences preferences) {
            mUserPreferences = preferences;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            UserCredentials userCredentials = mUserPreferences.get();
            if (isEmpty(userCredentials.getUsername()) ||
                    isEmpty(userCredentials.getPassword())) {
                return chain.proceed(chain.request());
            }

            String base64Credentials = basic(
                    userCredentials.getUsername(),
                    userCredentials.getPassword());
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            Response response = chain.proceed(request);
            if (!response.isSuccessful() &&
                    response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

                if (mUserPreferences.isUserConfirmed()) {
                    // invalidate existing user
                    mUserPreferences.invalidateUser();
                } else {
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
