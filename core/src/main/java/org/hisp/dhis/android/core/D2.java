/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.BuildConfig;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.common.SSLContextInitializer;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.constant.ConstantModule;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.dataset.DataSetModule;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.imports.ImportModule;
import org.hisp.dhis.android.core.indicator.IndicatorModule;
import org.hisp.dhis.android.core.legendset.LegendSetModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceModule;
import org.hisp.dhis.android.core.option.OptionModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule;
import org.hisp.dhis.android.core.period.PeriodModule;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.settings.SystemSettingModule;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.android.core.wipe.WipeModule;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public final class D2 {
    private final Retrofit retrofit;
    private final DatabaseAdapter databaseAdapter;
    private final D2Modules modules;
    private final D2DIComponent d2DIComponent;

    D2(@NonNull Retrofit retrofit, @NonNull DatabaseAdapter databaseAdapter, @NonNull Context context) {

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
        this.d2DIComponent = D2DIComponent.create(context, retrofit, databaseAdapter);
        this.modules = d2DIComponent.modules();
    }

    @VisibleForTesting
    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    public DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }

    @NonNull
    public Callable<Unit> syncMetaData() {
        return d2DIComponent.metadataCall();
    }

    @NonNull
    public AggregatedModule aggregatedModule() {
        return d2DIComponent.aggregatedModule();
    }

    public SystemInfoModule systemInfoModule() {
        return this.modules.systemInfo;
    }

    public SystemSettingModule systemSettingModule() {
        return this.modules.systemSetting;
    }

    public PeriodModule periodModule() {
        return this.modules.periodModule;
    }

    public RelationshipModule relationshipModule() {
        return this.modules.relationship;
    }

    public CategoryModule categoryModule() {
        return this.modules.category;
    }

    public ConstantModule constantModule() {
        return this.modules.constant;
    }

    public DataElementModule dataElementModule() {
        return this.modules.dataElement;
    }

    public DataSetModule dataSetModule() {
        return this.modules.dataSet;
    }

    public OptionModule optionModule() {
        return this.modules.option;
    }

    public DataValueModule dataValueModule() {
        return this.modules.dataValue;
    }

    public EnrollmentModule enrollmentModule() {
        return this.modules.enrollment;
    }

    public EventModule eventModule() {
        return this.modules.event;
    }

    public ImportModule importModule() {
        return this.modules.importModule;
    }

    public IndicatorModule indicatorModule() {
        return this.modules.indicator;
    }

    public LegendSetModule legendSetModule() {
        return this.modules.legendSet;
    }

    public MaintenanceModule maintenanceModule() {
        return this.modules.maintenance;
    }

    public ProgramModule programModule() {
        return this.modules.program;
    }

    public OrganisationUnitModule organisationUnitModule() {
        return this.modules.organisationUnit;
    }

    public TrackedEntityModule trackedEntityModule() {
        return modules.trackedEntity;
    }

    public UserModule userModule() {
        return modules.user;
    }

    public WipeModule wipeModule() {
        return this.d2DIComponent.wipeModule();
    }

    public SmsModule smsModule() {
        return modules.sms;
    }

    public static class Builder {
        private Configuration configuration;
        private DatabaseAdapter databaseAdapter;
        private OkHttpClient okHttpClient;
        private Context context;

        public Builder() {
            // empty constructor
        }

        @NonNull
        public Builder configuration(@NonNull Configuration configuration) {
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
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .validateEagerly(true)
                    .build();

            return new D2(retrofit, databaseAdapter, context);
        }
    }
}