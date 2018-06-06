/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.AggregatedDataCall;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.TrackedEntityInstancePostCall;
import org.hisp.dhis.android.core.calls.TrackerDataCall;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventPostCall;
import org.hisp.dhis.android.core.event.EventWithLimitCall;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceListDownloadAndPersistCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceWithLimitCall;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCall;
import org.hisp.dhis.android.core.user.IsUserLoggedInCallable;
import org.hisp.dhis.android.core.user.LogOutUserCallable;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserAuthenticateCall;
import org.hisp.dhis.android.core.utils.services.ProgramIndicatorEngine;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// ToDo: handle corner cases when user initially has been signed in, but later was locked (or
// password has changed)
@SuppressWarnings({"PMD.ExcessiveImports"})
public final class D2 {
    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;

    @VisibleForTesting
    D2(@NonNull Retrofit retrofit, @NonNull DatabaseAdapter databaseAdapter) {
        this.retrofit = retrofit;
        this.databaseAdapter = databaseAdapter;
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    public DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    @NonNull
    public Call<User> logIn(@NonNull String username, @NonNull String password) {
        return UserAuthenticateCall.create(databaseAdapter, retrofit, username, password);
    }

    @NonNull
    public Callable<Boolean> isUserLoggedIn() {
       return IsUserLoggedInCallable.create(databaseAdapter);
    }

    @NonNull
    public Callable<Void> logout() {
        return LogOutUserCallable.createToLogOut(databaseAdapter);

    }

    @NonNull
    public Callable<Void> wipeDB() {
        return LogOutUserCallable.createToWipe(databaseAdapter);
    }

    @NonNull
    public Call<Response> syncMetaData() {
        return MetadataCall.create(databaseAdapter, retrofit);
    }

    @NonNull
    public Call<Response> syncAggregatedData() {
        return AggregatedDataCall.create(databaseAdapter, retrofit);
    }

    @NonNull
    public Call<List<Event>> downloadSingleEvents(int eventLimit, boolean limitByOrgUnit) {
        return EventWithLimitCall.create(databaseAdapter, retrofit, eventLimit, limitByOrgUnit);
    }

    @NonNull
    public Call<List<TrackedEntityInstance>> syncTrackerData() {
        return TrackerDataCall.create(databaseAdapter, retrofit);
    }

    @NonNull
    public Call<List<TrackedEntityInstance>> downloadTrackedEntityInstancesByUid(Collection<String> uids) {
        return TrackedEntityInstanceListDownloadAndPersistCall.create(databaseAdapter, retrofit, uids);
    }

    @NonNull
    public Call<List<TrackedEntityInstance>> downloadTrackedEntityInstances(int teiLimit, boolean limitByOrgUnit) {
        return TrackedEntityInstanceWithLimitCall.create(databaseAdapter, retrofit, teiLimit, limitByOrgUnit);
    }

    @NonNull
    public String popTrackedEntityAttributeReservedValue(String attributeUid, String organisationUnitUid) {
        return TrackedEntityAttributeReservedValueManager.create(databaseAdapter, retrofit)
                .getValue(attributeUid, organisationUnitUid);
    }

    public void syncTrackedEntityAttributeReservedValue(String attributeUid, String organisationUnitUid) {
        TrackedEntityAttributeReservedValueManager.create(databaseAdapter, retrofit)
                .forceSyncReservedValues(attributeUid, organisationUnitUid);
    }

    @NonNull
    public Call<Response<WebResponse>> syncTrackedEntityInstances() {
        return TrackedEntityInstancePostCall.create(databaseAdapter, retrofit);
    }

    @NonNull
    public Call<List<TrackedEntityInstance>> queryTrackedEntityInstances(TrackedEntityInstanceQuery query) {
        return TrackedEntityInstanceQueryCall.create(retrofit, query);
    }

    public Call<Response<WebResponse>> syncSingleEvents() {
        return EventPostCall.create(databaseAdapter, retrofit);
    }

    public String evaluateProgramIndicator(String enrollmentUid, String eventUid, String programIndicatorUid) {
        return ProgramIndicatorEngine.create(databaseAdapter)
                .getProgramIndicatorValue(enrollmentUid, eventUid, programIndicatorUid);
    }

    public static class Builder {
        private ConfigurationModel configuration;
        private DatabaseAdapter databaseAdapter;
        private OkHttpClient okHttpClient;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configuration(@NonNull ConfigurationModel configuration) {
            this.configuration = configuration;
            return this;
        }

        @NonNull
        public Builder databaseAdapter(@NonNull DatabaseAdapter databaseAdapter) {
            this.databaseAdapter = databaseAdapter;
            return this;
        }

        @NonNull
        public Builder okHttpClient(@NonNull OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public D2 build() {
            if (databaseAdapter == null) {
                throw new IllegalArgumentException("databaseAdapter == null");
            }

            if (configuration == null) {
                throw new IllegalStateException("configuration must be set first");
            }

            if (okHttpClient == null) {
                throw new IllegalArgumentException("okHttpClient == null");
            }

            ObjectMapper objectMapper = new ObjectMapper()
                    .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(configuration.serverUrl())
                    .client(okHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .addConverterFactory(FilterConverterFactory.create())
                    .addConverterFactory(FieldsConverterFactory.create())
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, databaseAdapter);
        }
    }
}