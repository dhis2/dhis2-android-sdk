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
import android.text.TextUtils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.BasicAuthenticator;
import org.hisp.dhis.client.sdk.core.commons.DbHelper;
import org.hisp.dhis.client.sdk.core.commons.ServerUrlPreferences;
import org.hisp.dhis.client.sdk.core.option.OptionSetApi;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractor;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractorImpl;
import org.hisp.dhis.client.sdk.core.option.OptionSetStore;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractorImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.core.program.ProgramsApi;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityApi;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractor;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractorImpl;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.core.user.UserInteractorImpl;
import org.hisp.dhis.client.sdk.core.user.UserPreferences;
import org.hisp.dhis.client.sdk.core.user.UserStore;
import org.hisp.dhis.client.sdk.core.user.UsersApi;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class D2 {
    // singleton instance
    private static D2 instance;

    // context
    private final Application application;

    // persistence
    private final ServerUrlPreferences serverUrlPreferences;
    private final DbHelper dbHelper;

    // retrofit dependencies
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final Executor executor;
    private final Retrofit retrofit;

    /* holds true if D2 is configured */
    private final boolean isConfigured;
    private static String serverUrl;

    private ProgramInteractor programInteractor;
    private UserInteractor userInteractor;
    private OptionSetInteractor optionSetInteractor;
    private TrackedEntityInteractor trackedEntityInteractor;

    public static void init(Application application) {
        isNull(application, "Application must not be null");

        /* using default version of builder */
        with(new D2.Builder(application).build());
    }

    public static void with(D2 d2) {
        instance = isNull(d2, "D2 instance must not be null");
    }

    public static Builder builder(Application application) {
        return new Builder(application);
    }

    public static void configure(String baseUrl) {
        isNull(baseUrl, "Base URL must not be null");

        if (isConfigured()) {
            throw new IllegalStateException("D2 has been already configured");
        }

        String url = baseUrl.endsWith("/") ? baseUrl.concat("api/") : baseUrl.concat("/api/");
        instance().serverUrlPreferences.save(url);

        // re-instantiate D2 with new URL set
        instance = new D2(instance().application, instance().objectMapper,
                instance().okHttpClient, instance().executor);
    }

    public static void configure(HttpUrl okBaseUrl) {
        isNull(okBaseUrl, "Base URL must not be null");

        if (isConfigured()) {
            throw new IllegalStateException("D2 has been already configured");
        }

        String baseUrl = okBaseUrl.url().toString();
        String url = baseUrl.endsWith("/") ? baseUrl.concat("api/") : baseUrl.concat("/api/");
        instance().serverUrlPreferences.save(url);

        // re-instantiate D2 with new URL set
        instance = new D2(instance().application, instance().objectMapper,
                instance().okHttpClient, instance().executor);
    }

    /**
     * @return true if D2 is configured with valid URL pointing to DHIS2 instance
     */
    public static boolean isConfigured() {
        return instance().isConfigured;
    }

    /*
    * Helper method which returns singleton instance and
    * makes sure that everything is instantiated
    */
    private static D2 instance() {
        return isNull(instance, "D2 is null. Call init first");
    }

    private D2(Application app, ObjectMapper mapper, OkHttpClient client, Executor executor) {
        ServerUrlPreferences urlPreferences = new ServerUrlPreferences(app);
        serverUrl = urlPreferences.get();

        this.isConfigured = !TextUtils.isEmpty(serverUrl);
        this.application = app;

        // persistence
        this.dbHelper = new DbHelper(app);
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
    }

    public static ProgramInteractor programs() {
        if (isConfigured() && instance().programInteractor == null) {
            ProgramsApi programsApi = instance().retrofit.create(ProgramsApi.class);
            MetadataApi metadataApi = instance().retrofit.create(MetadataApi.class);
            ProgramStore programStore = new ProgramStore(
                    instance().dbHelper, instance().objectMapper);

            instance().programInteractor = new ProgramInteractorImpl(
                    programsApi, programStore, metadataApi);
        }

        return instance().programInteractor;
    }

    public static UserInteractor me() {
        if (isConfigured() && instance().userInteractor == null) {
            UsersApi usersApi = instance().retrofit.create(UsersApi.class);
            UserStore userStore = new UserStore(instance().dbHelper, instance().objectMapper);

            instance().userInteractor = new UserInteractorImpl(null, usersApi, userStore, null);
        }
        return instance().userInteractor;
    }

    public static OptionSetInteractor optionSets() {
        if (isConfigured() && instance().optionSetInteractor == null) {
            OptionSetStore optionSetStore =
                    new OptionSetStore(instance().dbHelper, instance().objectMapper);
            OptionSetApi optionSetApi =
                    instance().retrofit.create(OptionSetApi.class);
            instance().optionSetInteractor =
                    new OptionSetInteractorImpl(optionSetStore, optionSetApi);
        }

        return instance().optionSetInteractor;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static TrackedEntityInteractor trackedEntities() {
        if (isConfigured() && instance().trackedEntityInteractor == null) {
            TrackedEntityApi trackedEntityApi = instance().retrofit.create(TrackedEntityApi.class);
            TrackedEntityStore trackedEntityStore =
                    new TrackedEntityStore(instance().dbHelper, instance().objectMapper);
            instance().trackedEntityInteractor =
                    new TrackedEntityInteractorImpl(trackedEntityStore, trackedEntityApi);
        }

        return instance().trackedEntityInteractor;

    }

    public static class Builder {
        private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
        private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
        private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

        private final Application application;
        private final BasicAuthenticator basicAuthenticator;

        // customizable dependencies
        private ObjectMapper objectMapper;
        private OkHttpClient okHttpClient;
        private Executor executor;

        private Builder(Application application) {
            this.application = application;

            // Constructing default jackson's object mapper
            this.objectMapper = new ObjectMapper();
            this.objectMapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
                    MapperFeature.AUTO_DETECT_FIELDS,
                    MapperFeature.AUTO_DETECT_GETTERS,
                    MapperFeature.AUTO_DETECT_IS_GETTERS,
                    MapperFeature.AUTO_DETECT_SETTERS);

            UserPreferences userPreferences = new UserPreferences(application);

            // basic authentication handler
            this.basicAuthenticator = new BasicAuthenticator(userPreferences);

            // constructing default version of OkHttp client
            this.okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(basicAuthenticator)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .build();
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = isNull(objectMapper, "ObjectMapper must not be null");
            return this;
        }

        public Builder client(OkHttpClient okHttpClient) {
            this.okHttpClient = isNull(okHttpClient, "OkHttpClient must not be null");

            // even if user sets custom instance of OkHttpClient,
            // we need to supply authentication mechanism
            this.okHttpClient = this.okHttpClient.newBuilder()
                    .addInterceptor(basicAuthenticator)
                    .build();
            return this;
        }

        public Builder callbackExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public D2 build() {
            return new D2(application, objectMapper, okHttpClient, executor);
        }
    }
}
