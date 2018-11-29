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

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.hisp.dhis.android.BuildConfig;
import org.hisp.dhis.android.core.calls.AggregatedDataCall;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.TrackedEntityInstancePostCall;
import org.hisp.dhis.android.core.calls.TrackedEntityInstanceSyncDownCall;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.SSLContextInitializer;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationPostCall;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.datavalue.DataValuePostCall;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventPostCall;
import org.hisp.dhis.android.core.event.EventWithLimitCall;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.maintenance.MaintenanceModule;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule;
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
import org.hisp.dhis.android.core.wipe.WipeModule;
import org.hisp.dhis.android.core.wipe.WipeModuleImpl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

// ToDo: handle corner cases when user initially has been signed in, but later was locked (or
// password has changed)
@SuppressWarnings({"PMD.ExcessiveImports"})
public final class D2 {
    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;
    private final D2InternalModules internalModules;
    private final WipeModule wipeModule;

    @VisibleForTesting
    D2(@NonNull Retrofit retrofit, @NonNull DatabaseAdapter databaseAdapter,
       @NonNull Context context) {

        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        } else {
            /* SSLContextInitializer, necessary to ensure everything works in Android 4.4 crashes
            when running the StrictMode above. That's why it's in the else clause */
            SSLContextInitializer.initializeSSLContext(context);
        }

        this.retrofit = retrofit;
        this.databaseAdapter = databaseAdapter;
        this.internalModules = D2InternalModules.create(databaseAdapter, retrofit);
        this.wipeModule = WipeModuleImpl.create(databaseAdapter, internalModules);
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
    public Callable<User> logIn(@NonNull String username, @NonNull String password) {
        return UserAuthenticateCall.create(databaseAdapter, retrofit, internalModules, username, password);
    }

    @NonNull
    public Callable<Boolean> isUserLoggedIn() {
       return IsUserLoggedInCallable.create(databaseAdapter);
    }

    @NonNull
    public Callable<Unit> logout() {
        return LogOutUserCallable.create(databaseAdapter);
    }

    @NonNull
    public Callable<Unit> syncMetaData() {
        return MetadataCall.create(databaseAdapter, retrofit, internalModules);
    }

    @NonNull
    public Callable<Unit> syncAggregatedData() {
        return AggregatedDataCall.create(databaseAdapter, retrofit, internalModules);
    }

    /**
     * Allows uploading to DHIS2 server all DataSetCompleteRegistration with TO_POST or TO_UPDATE state
     *
     * @return A Callable instace ready to perform the data upload and retrieve the results
     *         in form of {@link ImportSummary}
     */
    @NonNull
    public Callable<ImportSummary> syncDataSetCompleteRegistrations() {
        return DataSetCompleteRegistrationPostCall.create(databaseAdapter, retrofit);
    }

    /**
     * Allows uploading to DHIS2 server all DataValues with TO_POST or TO_UPDATE state
     *
     * @return A Callable instace ready to perform the data upload and retrieve the results
     *         in form of {@link ImportSummary}
     */
    @NonNull
    public Callable<ImportSummary> syncDataValues() {
        return DataValuePostCall.create(databaseAdapter, retrofit);
    }

    @NonNull
    public Callable<List<Event>> downloadSingleEvents(int eventLimit, boolean limitByOrgUnit) {
        return EventWithLimitCall.create(databaseAdapter, retrofit, eventLimit, limitByOrgUnit);
    }

    @NonNull
    public Callable<List<TrackedEntityInstance>> syncDownSyncedTrackedEntityInstances() {
        return TrackedEntityInstanceSyncDownCall.create(databaseAdapter, retrofit, internalModules);
    }

    @NonNull
    public Callable<List<TrackedEntityInstance>> downloadTrackedEntityInstancesByUid(Collection<String> uids) {
        return TrackedEntityInstanceListDownloadAndPersistCall.create(databaseAdapter, retrofit, internalModules, uids);
    }

    @NonNull
    public Callable<List<TrackedEntityInstance>> downloadTrackedEntityInstances(int teiLimit, boolean limitByOrgUnit) {
        return TrackedEntityInstanceWithLimitCall.create(databaseAdapter, retrofit, internalModules, teiLimit,
                limitByOrgUnit);
    }

    @NonNull
    public String popTrackedEntityAttributeReservedValue(String attributeUid, String organisationUnitUid)
            throws D2Error {
        return TrackedEntityAttributeReservedValueManager.create(databaseAdapter, retrofit, internalModules)
                .getValue(attributeUid, organisationUnitUid);
    }

    public void syncTrackedEntityAttributeReservedValues(String attributeUid, String organisationUnitUid,
                                                         Integer numberOfValuesToFillUp) {
        TrackedEntityAttributeReservedValueManager.create(databaseAdapter, retrofit, internalModules)
                .syncReservedValues(attributeUid, organisationUnitUid, numberOfValuesToFillUp);
    }

    @NonNull
    public Callable<WebResponse> syncTrackedEntityInstances() {
        return TrackedEntityInstancePostCall.create(databaseAdapter, retrofit, internalModules);
    }

    @NonNull
    public Callable<List<TrackedEntityInstance>> queryTrackedEntityInstances(TrackedEntityInstanceQuery query) {
        return TrackedEntityInstanceQueryCall.create(retrofit, databaseAdapter, query);
    }

    public Callable<WebResponse> syncSingleEvents() {
        return EventPostCall.create(databaseAdapter, retrofit);
    }

    public String evaluateProgramIndicator(String enrollmentUid, String eventUid, String programIndicatorUid) {
        return ProgramIndicatorEngine.create(databaseAdapter)
                .getProgramIndicatorValue(enrollmentUid, eventUid, programIndicatorUid);
    }

    public SystemInfoModule systemInfoModule() {
        return this.internalModules.systemInfo.publicModule;
    }

    public RelationshipModule relationshipModule() {
        return this.internalModules.relationshipModule.publicModule;
    }

    public CategoryModule categoryModule() {
        return this.internalModules.categoryModule.publicModule;
    }

    public DataElementModule dataElementModule() {
        return this.internalModules.dataElementModule.publicModule;
    }

    public DataValueModule dataValueModule() {
        return this.internalModules.dataValueModule.publicModule;
    }

    public MaintenanceModule maintenanceModule() {
        return this.internalModules.maintenanceModule.publicModule;
    }

    public WipeModule wipeModule() {
        return wipeModule;
    }

    public static class Builder {
        private ConfigurationModel configuration;
        private DatabaseAdapter databaseAdapter;
        private OkHttpClient okHttpClient;
        private Context context;

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

        @NonNull
        public Builder context(@NonNull Context context) {
            this.context = context;
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

            if (context == null) {
                throw new IllegalArgumentException("context == null");
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(configuration.serverUrl())
                    .client(okHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create(ObjectMapperFactory.objectMapper()))
                    .addConverterFactory(FilterConverterFactory.create())
                    .addConverterFactory(FieldsConverterFactory.create())
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, databaseAdapter, context);
        }
    }
}