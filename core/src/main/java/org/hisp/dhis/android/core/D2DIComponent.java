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

import android.support.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeDIModule;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.category.CategoryPackageDIModule;
import org.hisp.dhis.android.core.common.CommonPackageDIModule;
import org.hisp.dhis.android.core.constant.ConstantPackageDIModule;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementPackageDIModule;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetPackageDIModule;
import org.hisp.dhis.android.core.datavalue.DataValuePackageDIModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentPackageDIModule;
import org.hisp.dhis.android.core.event.EventPackageDIModule;
import org.hisp.dhis.android.core.indicator.IndicatorPackageDIModule;
import org.hisp.dhis.android.core.legendset.LegendPackageDIModule;
import org.hisp.dhis.android.core.maintenance.MaintenancePackageDIModule;
import org.hisp.dhis.android.core.option.OptionPackageDIModule;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitPackageDIModule;
import org.hisp.dhis.android.core.period.PeriodPackageDIModule;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramPackageDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipPackageDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.resource.ResourcePackageDIModule;
import org.hisp.dhis.android.core.settings.SystemSettingPackageDIModule;
import org.hisp.dhis.android.core.sms.SmsDIModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoPackageDIModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityPackageDIModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.user.UserPackageDIModule;
import org.hisp.dhis.android.core.wipe.WipeDIModule;
import org.hisp.dhis.android.core.wipe.WipeModule;

import javax.inject.Singleton;

import dagger.Component;

@SuppressWarnings("PMD.ExcessiveImports")
@Singleton
@Component(modules = {
        AppContextDIModule.class,
        APIClientDIModule.class,
        DatabaseDIModule.class,
        WipeDIModule.class,
        RepositoryScopeDIModule.class,

        CategoryPackageDIModule.class,
        CommonPackageDIModule.class,
        ConstantPackageDIModule.class,
        DataElementPackageDIModule.class,
        DataSetPackageDIModule.class,
        DataValuePackageDIModule.class,
        EnrollmentPackageDIModule.class,
        EventPackageDIModule.class,
        IndicatorPackageDIModule.class,
        LegendPackageDIModule.class,
        MaintenancePackageDIModule.class,
        OptionPackageDIModule.class,
        OrganisationUnitPackageDIModule.class,
        PeriodPackageDIModule.class,
        ProgramPackageDIModule.class,
        RelationshipPackageDIModule.class,
        ResourcePackageDIModule.class,
        SystemInfoPackageDIModule.class,
        SystemSettingPackageDIModule.class,
        TrackedEntityPackageDIModule.class,
        SmsDIModule.class,
        UserPackageDIModule.class}
)

public interface D2DIComponent {

    D2InternalModules internalModules();
    D2Modules modules();
    AggregatedModule aggregatedModule();
    MetadataCall metadataCall();
    WipeModule wipeModule();

    @VisibleForTesting
    ListCallFactory<Program> programCallFactory();
    @VisibleForTesting
    UidsCallFactory<OptionSet> optionSetCallFactory();
    @VisibleForTesting
    UidsCallFactory<DataElement> dataElementCallFactory();
    @VisibleForTesting
    ListCallFactory<DataSet> dataSetCallFactory();
    @VisibleForTesting
    UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory();
    @VisibleForTesting
    SyncHandler<RelationshipType> relationshipTypeHandler();

    @Component.Builder
    interface Builder {
        Builder appContextDIModule(AppContextDIModule appContextDIModule);
        Builder apiClientDIModule(APIClientDIModule apiClientDIModule);
        Builder databaseDIModule(DatabaseDIModule databaseDIModule);
        Builder wipeDIModule(WipeDIModule wipeDIModule);
        Builder repositoryScopeDIModule(RepositoryScopeDIModule repositoryScopeDIModule);

        Builder categoryPackageDIModule(CategoryPackageDIModule categoryPackageDIModule);
        Builder commonPackageDIModule(CommonPackageDIModule commonPackageDIModule);
        Builder constantPackageDIModule(ConstantPackageDIModule constantPackageDIModule);
        Builder dataElementPackageDIModule(DataElementPackageDIModule dataElementPackageDIModule);
        Builder dataSetPackageDIModule(DataSetPackageDIModule dataSetPackageDIModule);
        Builder dataValuePackageDIModule(DataValuePackageDIModule dataValuePackageDIModule);
        Builder enrollmentPackageDIModule(EnrollmentPackageDIModule enrollmentPackageDIModule);
        Builder eventPackageDIModule(EventPackageDIModule eventPackageDIModule);
        Builder indicatorPackageDIModule(IndicatorPackageDIModule indicatorPackageDIModule);
        Builder legendPackageDIModule(LegendPackageDIModule legendPackageDIModule);
        Builder maintenancePackageDIModule(MaintenancePackageDIModule maintenancePackageDIModule);
        Builder optionPackageDIModule(OptionPackageDIModule optionPackageDIModule);
        Builder organisationUnitPackageDIModule(OrganisationUnitPackageDIModule organisationUnitPackageDIModule);
        Builder periodPackageDIModule(PeriodPackageDIModule periodPackageDIModule);
        Builder programPackageDIModule(ProgramPackageDIModule programPackageDIModule);
        Builder relationshipDIModule(RelationshipPackageDIModule relationshipPackageDIModule);
        Builder resourcePackageDIModule(ResourcePackageDIModule resourcePackageDIModule);
        Builder systemInfoPackageDIModule(SystemInfoPackageDIModule systemInfoPackageDIModule);
        Builder systemSettingPackageDIModule(SystemSettingPackageDIModule systemSettingPackageDIModule);
        Builder trackedEntityPackageDIModule(TrackedEntityPackageDIModule trackedEntityPackageDIModule);
        Builder userPackageDIModule(UserPackageDIModule userPackageDIModule);
        D2DIComponent build();
    }
}