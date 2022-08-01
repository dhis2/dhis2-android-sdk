/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.analytics.AnalyticsModule;
import org.hisp.dhis.android.core.arch.d2.internal.D2DIComponent;
import org.hisp.dhis.android.core.arch.d2.internal.D2Modules;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.modules.internal.WithProgressDownloader;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.constant.ConstantModule;
import org.hisp.dhis.android.core.dataelement.DataElementModule;
import org.hisp.dhis.android.core.dataset.DataSetModule;
import org.hisp.dhis.android.core.datastore.DataStoreModule;
import org.hisp.dhis.android.core.datavalue.DataValueModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.fileresource.FileResourceModule;
import org.hisp.dhis.android.core.imports.internal.ImportModule;
import org.hisp.dhis.android.core.indicator.IndicatorModule;
import org.hisp.dhis.android.core.legendset.LegendSetModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceModule;
import org.hisp.dhis.android.core.note.NoteModule;
import org.hisp.dhis.android.core.option.OptionModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModule;
import org.hisp.dhis.android.core.period.PeriodModule;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.relationship.RelationshipModule;
import org.hisp.dhis.android.core.settings.SettingModule;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.android.core.validation.ValidationModule;
import org.hisp.dhis.android.core.visualization.VisualizationModule;
import org.hisp.dhis.android.core.wipe.internal.WipeModule;

import retrofit2.Retrofit;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public final class D2 {
    private final D2Modules modules;
    final D2DIComponent d2DIComponent;

    D2(@NonNull D2DIComponent d2DIComponent) {
        this.d2DIComponent = d2DIComponent;
        this.modules = d2DIComponent.modules();
    }

    @VisibleForTesting
    @NonNull
    public Retrofit retrofit() {
        return d2DIComponent.retrofit();
    }

    @NonNull
    public DatabaseAdapter databaseAdapter() {
        return d2DIComponent.databaseAdapter();
    }

    public WithProgressDownloader metadataModule() {
        return d2DIComponent.metadataModule();
    }

    @NonNull
    public AggregatedModule aggregatedModule() {
        return d2DIComponent.aggregatedModule();
    }

    public AnalyticsModule analyticsModule() {
        return this.modules.analytics;
    }

    public SystemInfoModule systemInfoModule() {
        return this.modules.systemInfo;
    }

    /**
     * @deprecated Use {@link #settingModule()} instead.
     */
    @Deprecated
    public SettingModule systemSettingModule() {
        return this.modules.settingModule;
    }

    public SettingModule settingModule() {
        return this.modules.settingModule;
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

    public FileResourceModule fileResourceModule() {
        return this.modules.fileResource;
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

    public DataStoreModule dataStoreModule() {
        return this.modules.dataStore;
    }

    public MaintenanceModule maintenanceModule() {
        return this.modules.maintenance;
    }

    public NoteModule noteModule() {
        return this.modules.note;
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

    public ValidationModule validationModule() {
        return modules.validation;
    }

    public VisualizationModule visualizationModule() {
        return modules.visualization;
    }

    public WipeModule wipeModule() {
        return this.d2DIComponent.wipeModule();
    }

    public SmsModule smsModule() {
        return modules.sms;
    }
}