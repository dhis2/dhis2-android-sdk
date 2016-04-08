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

import org.hisp.dhis.client.sdk.android.dataelement.DataElementApiClientImpl;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.event.EventApiClientImpl;
import org.hisp.dhis.client.sdk.android.event.EventApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetApiClientImpl;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitApiClientImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramStageApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionApiClientImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.systeminfo.SystemInfoApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeApiClientImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeApiClientRetrofit;
import org.hisp.dhis.client.sdk.android.user.UserAccountApiClientImpl;
import org.hisp.dhis.client.sdk.android.user.UserApiClientRetrofit;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.NetworkModule;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.UserPreferences;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementApiClient;
import org.hisp.dhis.client.sdk.core.event.EventApiClient;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetApiClient;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionApiClient;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeApiClient;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;

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
public class NetworkModuleImpl implements NetworkModule {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

    private final OrganisationUnitApiClient organisationUnitApiClient;
    private final SystemInfoApiClient systemInfoApiClient;
    private final ProgramApiClient programApiClient;
    private final ProgramStageApiClient programStageApiClient;
    private final ProgramStageSectionApiClient programStageSectionApiClient;
    private final ProgramRuleApiClient programRuleApiClient;
    private final ProgramRuleActionApiClient programRuleActionApiClient;
    private final ProgramRuleVariableApiClient programRuleVariableApiClient;
    private final ProgramIndicatorApiClient programIndicatorApiClient;
    private final UserApiClient userApiClient;
    private final EventApiClient eventApiClient;
    private final DataElementApiClient dataElementApiClient;
    private final ProgramStageDataElementApiClient programStageDataElementApiClient;
    private final OptionSetApiClient optionSetApiClient;
    private final TrackedEntityAttributeApiClient trackedEntityAttributeApiClient;

    public NetworkModuleImpl(PreferencesModule preferencesModule, OkHttpClient okClient) {
        AuthInterceptor authInterceptor = new AuthInterceptor(
                preferencesModule.getUserPreferences());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = okClient.newBuilder()
                .addInterceptor(authInterceptor)
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

        programApiClient = new ProgramApiClientImpl(retrofit.create(
                ProgramApiClientRetrofit.class));
        programStageApiClient = new ProgramStageApiClientImpl(retrofit.create(
                ProgramStageApiClientRetrofit.class));
        programStageSectionApiClient = new ProgramStageSectionApiClientImpl(
                retrofit.create(ProgramStageSectionApiClientRetrofit.class));
        programRuleApiClient = new ProgramRuleApiClientImpl(retrofit.create(
                ProgramRuleApiClientRetrofit.class));
        programRuleActionApiClient = new ProgramRuleActionApiClientImpl(
                retrofit.create(ProgramRuleActionApiClientRetrofit.class));
        programRuleVariableApiClient = new ProgramRuleVariableApiClientImpl(retrofit.create(
                ProgramRuleVariableApiClientRetrofit.class));
        programIndicatorApiClient = new ProgramIndicatorApiClientImpl(
                retrofit.create(ProgramIndicatorApiClientRetrofit.class));
        systemInfoApiClient = new org.hisp.dhis.client.sdk.android.systeminfo.SystemInfoApiClientImpl(retrofit.create(
                SystemInfoApiClientRetrofit.class));
        userApiClient = new UserAccountApiClientImpl(retrofit.create(
                UserApiClientRetrofit.class));
        organisationUnitApiClient = new OrganisationUnitApiClientImpl(retrofit.create(
                OrganisationUnitApiClientRetrofit.class));
        eventApiClient = new EventApiClientImpl(retrofit.create(
                EventApiClientRetrofit.class));
        dataElementApiClient = new DataElementApiClientImpl(retrofit.create(
                DataElementApiClientRetrofit.class));
        programStageDataElementApiClient = new ProgramStageDataElementApiClientImpl(retrofit.create(
                ProgramStageDataElementApiClientRetrofit.class));
        optionSetApiClient = new OptionSetApiClientImpl(retrofit.create(
                OptionSetApiClientRetrofit.class));
        trackedEntityAttributeApiClient = new TrackedEntityAttributeApiClientImpl(retrofit.create(
                TrackedEntityAttributeApiClientRetrofit.class));
    }

    @Override
    public SystemInfoApiClient getSystemInfoApiClient() {
        return systemInfoApiClient;
    }

    @Override
    public ProgramApiClient getProgramApiClient() {
        return programApiClient;
    }

    @Override
    public ProgramStageApiClient getProgramStageApiClient() {
        return programStageApiClient;
    }

    @Override
    public ProgramStageSectionApiClient getProgramStageSectionApiClient() {
        return programStageSectionApiClient;
    }

    @Override
    public OrganisationUnitApiClient getOrganisationUnitApiClient() {
        return organisationUnitApiClient;
    }

    @Override
    public UserApiClient getUserApiClient() {
        return userApiClient;
    }

    @Override
    public EventApiClient getEventApiClient() {
        return eventApiClient;
    }

    @Override
    public DataElementApiClient getDataElementApiClient() {
        return dataElementApiClient;
    }

    @Override
    public ProgramStageDataElementApiClient getProgramStageDataElementApiClient() {
        return programStageDataElementApiClient;
    }

    @Override
    public OptionSetApiClient getOptionSetApiClient() {
        return optionSetApiClient;
    }

    @Override
    public TrackedEntityAttributeApiClient getTrackedEntityAttributeApiClient() {
        return trackedEntityAttributeApiClient;
    }

    @Override
    public ProgramRuleApiClient getProgramRuleApiClient() {
        return programRuleApiClient;
    }

    @Override
    public ProgramRuleActionApiClient getProgramRuleActionApiClient() {
        return programRuleActionApiClient;
    }

    @Override
    public ProgramRuleVariableApiClient getProgramRuleVariableApiClient() {
        return programRuleVariableApiClient;
    }

    @Override
    public ProgramIndicatorApiClient getProgramIndicatorApiClient() {
        return programIndicatorApiClient;
    }

    private static class AuthInterceptor implements Interceptor {
        private final UserPreferences mUserPreferences;

        public AuthInterceptor(UserPreferences preferences) {
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
