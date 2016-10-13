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
import org.hisp.dhis.client.sdk.core.option.OptionSetApi;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractor;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractorImpl;
import org.hisp.dhis.client.sdk.core.option.OptionSetStore;
import org.hisp.dhis.client.sdk.core.option.OptionSetStoreImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractorImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStoreImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramsApi;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityApi;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractor;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractorImpl;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.core.user.UserInteractorImpl;
import org.hisp.dhis.client.sdk.core.user.UserPreferences;
import org.hisp.dhis.client.sdk.core.user.UserStore;
import org.hisp.dhis.client.sdk.core.user.UserStoreImpl;
import org.hisp.dhis.client.sdk.core.user.UsersApi;
import org.hisp.dhis.client.sdk.utils.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class D2 {
    // context
    private final Application application;

    // persistence
    private final ServerUrlPreferences serverUrlPreferences;
    private final ContentResolver contentResolver;

    // retrofit dependencies
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final Executor executor;
    private final Retrofit retrofit;

    // true if valid server URL is set to D2
    private final boolean isConfigured;

    // interactors which will be exposed to client applications
    private final UserInteractor userInteractor;
    private final ProgramInteractor programInteractor;
    private final OptionSetInteractor optionSetInteractor;
    private final TrackedEntityInteractor trackedEntityInteractor;

    public static Builder builder(Application application) {
        return new Builder(application);
    }

    public D2.Builder configure(HttpUrl okBaseUrl) {
        isNull(okBaseUrl, "Base URL must not be null");
        return configure(okBaseUrl.toString());
    }

    public D2.Builder configure(String baseUrl) {
        isNull(baseUrl, "Base URL must not be null");

        if (isConfigured()) {
            throw new IllegalStateException("D2 has been already configured");
        }

        String url = baseUrl.endsWith("/") ? baseUrl.concat("api/") : baseUrl.concat("/api/");
        serverUrlPreferences.save(url);

        // re-instantiate D2 with new URL set
        return new D2.Builder(this);
    }

    private D2(Application app, ObjectMapper mapper, OkHttpClient client, Executor executor) {
        // retrieve server url
        ServerUrlPreferences urlPreferences = new ServerUrlPreferences(app);
        String serverUrl = urlPreferences.get();

        this.isConfigured = !StringUtils.isEmpty(serverUrl);
        this.application = app;

        // persistence
        this.contentResolver = app.getContentResolver();
        this.serverUrlPreferences = urlPreferences;

        // retrofit
        this.objectMapper = mapper;
        this.okHttpClient = client;
        this.executor = executor;

        Retrofit retrofit = null;
        if (isConfigured) {
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(serverUrl)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .client(client);

            if (executor != null) {
                retrofitBuilder = retrofitBuilder.callbackExecutor(executor);
            }

            retrofit = retrofitBuilder.build();
        }

        // can be null in case if D2 is not configured
        this.retrofit = retrofit;

        // constructing interactors
        if (retrofit != null) {
            // user interactor
            UserStore userStore = new UserStoreImpl(contentResolver, objectMapper);
            UsersApi usersApi = retrofit.create(UsersApi.class);
            userInteractor = new UserInteractorImpl(null, usersApi, userStore, null);

            // program interactor
            ProgramsApi programsApi = retrofit.create(ProgramsApi.class);
            MetadataApi metadataApi = retrofit.create(MetadataApi.class);
            ProgramStore programStore = new ProgramStoreImpl(contentResolver, objectMapper);
            programInteractor = new ProgramInteractorImpl(programsApi, programStore, metadataApi);

            // option set interactor
            OptionSetStore optionSetStore = new OptionSetStoreImpl(contentResolver, objectMapper);
            OptionSetApi optionSetApi = retrofit.create(OptionSetApi.class);
            optionSetInteractor = new OptionSetInteractorImpl(optionSetStore, optionSetApi);

            // tracked entities
            TrackedEntityApi trackedEntityApi = retrofit.create(TrackedEntityApi.class);
            TrackedEntityStore trackedEntityStore = new TrackedEntityStoreImpl(contentResolver);
            trackedEntityInteractor = new TrackedEntityInteractorImpl(trackedEntityStore, trackedEntityApi);
        } else {
            userInteractor = null;
            programInteractor = null;
            optionSetInteractor = null;
            trackedEntityInteractor = null;
        }
    }

    /**
     * @return true if D2 is configured with valid URL pointing to DHIS2 instance
     */
    public boolean isConfigured() {
        return isConfigured;
    }

    public Application application() {
        return application;
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public Executor executor() {
        return executor;
    }

    public Retrofit retrofit() {
        return retrofit;
    }

    public String serverUrl() {
        return serverUrlPreferences.get();
    }

    public UserInteractor me() {
        return userInteractor;
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

    public static class Builder {
        private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
        private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
        private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

        private final Application application;

        // builder which can be used to set custom interceptors
        private final OkHttpClient.Builder clientBuilder;

        private ObjectMapper objectMapper;
        private Executor executor;

        Builder(D2 d2) {
            this.application = d2.application();
            this.clientBuilder = d2.okHttpClient().newBuilder();
            this.objectMapper = d2.objectMapper();
            this.executor = d2.executor();
        }

        Builder(Application application) {
            this.application = isNull(application, "Application must not be null");

            // constructing default jackson's object mapper
            this.objectMapper = new ObjectMapper();
            this.objectMapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
                    MapperFeature.AUTO_DETECT_FIELDS,
                    MapperFeature.AUTO_DETECT_GETTERS,
                    MapperFeature.AUTO_DETECT_IS_GETTERS,
                    MapperFeature.AUTO_DETECT_SETTERS);

            // user preferences
            UserPreferences userPreferences = new UserPreferences(application);

            // basic authentication handler
            BasicAuthenticator basicAuthenticator = new BasicAuthenticator(userPreferences);

            // constructing default version of OkHttp client
            this.clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(basicAuthenticator)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = isNull(objectMapper, "ObjectMapper must not be null");
            return this;
        }

        public Builder interceptor(Interceptor interceptor) {
            isNull(interceptor, "interceptor must not be null");

            clientBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder callbackExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public D2 build() {
            OkHttpClient okHttpClient = clientBuilder.build();
            return new D2(application, objectMapper, okHttpClient, executor);
        }
    }
}
