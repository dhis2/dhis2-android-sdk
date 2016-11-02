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

package org.hisp.dhis.client.sdk.core;

import android.app.Application;
import android.content.ContentResolver;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.BasicAuthenticator;
import org.hisp.dhis.client.sdk.core.commons.ServerUrlPreferences;
import org.hisp.dhis.client.sdk.core.commons.ServerUrlPreferencesImpl;
import org.hisp.dhis.client.sdk.core.event.EventFactory;
import org.hisp.dhis.client.sdk.core.event.EventInteractor;
import org.hisp.dhis.client.sdk.core.option.OptionFactory;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractor;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramFactory;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueInteractor;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractor;
import org.hisp.dhis.client.sdk.core.user.UserFactory;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.core.user.UserPreferences;
import org.hisp.dhis.client.sdk.utils.StringUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class D2 {
    // context
    private final Application application;

    // persistence
    private final UserPreferences userPreferences;
    private final ServerUrlPreferences serverUrlPreferences;
    private final ContentResolver contentResolver;

    // retrofit dependencies
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    // interactors which will be exposed to client applications
    private final UserInteractor userInteractor;
    private final EventInteractor eventInteractor;
    private final ProgramInteractor programInteractor;
    private final OptionSetInteractor optionSetInteractor;
    private final TrackedEntityInteractor trackedEntityInteractor;
    private final OrganisationUnitInteractor organisationUnitInteractor;
    private final TrackedEntityDataValueInteractor trackedEntityDataValueInteractor;

    public static D2.Builder builder(Application application) {
        isNull(application, "application must not be null");
        return new D2.Builder(application);
    }

    // injecting dependencies through constructor by hand, in order to make code testable
    D2(Application application, ContentResolver contentResolver, UserPreferences userPreferences,
       ServerUrlPreferences serverUrlPreferences, ObjectMapper objectMapper,
       OkHttpClient okHttpClient, Retrofit retrofit, UserInteractor userInteractor,
       ProgramInteractor programInteractor, OptionSetInteractor optionSetInteractor,
       TrackedEntityInteractor trackedEntityInteractor, EventInteractor eventInteractor,
       OrganisationUnitInteractor organisationUnitInteractor,
       TrackedEntityDataValueInteractor trackedEntityDataValueInteractor) {
        this.application = application;

        // persistence
        this.userPreferences = userPreferences;
        this.serverUrlPreferences = serverUrlPreferences;
        this.contentResolver = contentResolver;

        // retrofit
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
        this.retrofit = retrofit;

        // interactors
        this.userInteractor = userInteractor;
        this.programInteractor = programInteractor;
        this.optionSetInteractor = optionSetInteractor;
        this.trackedEntityInteractor = trackedEntityInteractor;
        this.eventInteractor = eventInteractor;
        this.organisationUnitInteractor = organisationUnitInteractor;
        this.trackedEntityDataValueInteractor = trackedEntityDataValueInteractor;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Getters for internal dependencies
    ////////////////////////////////////////////////////////////////////////////////

    public Application application() {
        return application;
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public Retrofit retrofit() {
        return retrofit;
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public ContentResolver contentResolver() {
        return contentResolver;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Getters for properties like server url, username, etc
    ////////////////////////////////////////////////////////////////////////////////

    public String serverUrl() {
        return serverUrlPreferences.get();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Getters for interactors
    ////////////////////////////////////////////////////////////////////////////////

    public UserInteractor me() {
        return userInteractor;
    }

    public EventInteractor events() {
        return eventInteractor;
    }

    public ProgramInteractor programs() {
        return programInteractor;
    }

    public OptionSetInteractor optionSets() {
        return optionSetInteractor;
    }

    public TrackedEntityInteractor trackedEntities() {
        return trackedEntityInteractor;
    }

    public OrganisationUnitInteractor organisationUnits() {
        return organisationUnitInteractor;
    }

    public TrackedEntityDataValueInteractor trackedEntityDataValues() {
        return trackedEntityDataValueInteractor;
    }

    public static class Builder {
        private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
        private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
        private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

        private final Application application;
        private final ServerUrlPreferences serverUrlPreferences;

        Builder(Application application) {
            this.application = isNull(application, "Application must not be null");
            this.serverUrlPreferences = new ServerUrlPreferencesImpl(application);
        }

        public Builder baseUrl(String baseUrl) {
            isNull(baseUrl, "Base URL must not be null");

//            String existingServerUrl = serverUrlPreferences.get();
//            if (!StringUtils.isEmpty(existingServerUrl)) {
//                throw new IllegalStateException("D2 has been already configured");
//            }

            String url = baseUrl.endsWith("/") ? baseUrl.concat("api/") : baseUrl.concat("/api/");
            serverUrlPreferences.save(url);

            return this;
        }

        public D2 build() {
            ContentResolver contentResolver = application.getContentResolver();

            // user preferences
            UserPreferences userPreferences = UserFactory.create(application);

            // basic authentication handler
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userPreferences);

            // jackson's object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
                    MapperFeature.AUTO_DETECT_FIELDS,
                    MapperFeature.AUTO_DETECT_GETTERS,
                    MapperFeature.AUTO_DETECT_IS_GETTERS,
                    MapperFeature.AUTO_DETECT_SETTERS);

            // default OkHttp client implementation
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(basicAuthenticator)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .build();

            // if server url is absent, we cannot construct retrofit
            Retrofit retrofit = null;

            // extracting server url
            String serverUrl = serverUrlPreferences.get();
            if (!StringUtils.isEmpty(serverUrl)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(serverUrl)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .client(okHttpClient)
                        .build();
            }

            // interactors
            UserInteractor userInteractor = retrofit != null ? UserFactory.create(
                    userPreferences, retrofit, contentResolver, objectMapper) : null;
            ProgramInteractor programInteractor = retrofit != null ? ProgramFactory.create(
                    retrofit, contentResolver, objectMapper) : null;
            OptionSetInteractor optionSetInteractor = retrofit != null ? OptionFactory.create(
                    retrofit, contentResolver, objectMapper) : null;
            TrackedEntityInteractor trackedEntityInteractor = retrofit != null ?
                    TrackedEntityFactory.create(retrofit, contentResolver) : null;
            EventInteractor eventInteractor = retrofit != null ? EventFactory.create(
                    retrofit, contentResolver) : null;
            OrganisationUnitInteractor organisationUnitInteractor = retrofit != null ?
                    OrganisationUnitFactory.create(retrofit, contentResolver, objectMapper) : null;
            TrackedEntityDataValueInteractor dataValueInteractor = retrofit != null ?
                    TrackedEntityFactory.create(contentResolver) : null;

            return new D2(application, contentResolver, userPreferences, serverUrlPreferences,
                    objectMapper, okHttpClient, retrofit, userInteractor, programInteractor,
                    optionSetInteractor, trackedEntityInteractor, eventInteractor,
                    organisationUnitInteractor, dataValueInteractor);
        }
    }
}
