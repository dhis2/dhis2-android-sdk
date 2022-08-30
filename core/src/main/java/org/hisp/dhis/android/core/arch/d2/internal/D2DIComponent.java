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

package org.hisp.dhis.android.core.arch.d2.internal;

import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.D2Configuration;
import org.hisp.dhis.android.core.analytics.AnalyticsPackageDIModule;
import org.hisp.dhis.android.core.arch.api.internal.APIClientDIModule;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCallFactory;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseDIModule;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.json.internal.JSONSerializationDIModule;
import org.hisp.dhis.android.core.arch.repositories.di.internal.RepositoriesDIModule;
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.KeyValueStorageDIModule;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore;
import org.hisp.dhis.android.core.attribute.internal.AttributePackageDIModule;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.internal.CategoryPackageDIModule;
import org.hisp.dhis.android.core.common.internal.CommonPackageDIModule;
import org.hisp.dhis.android.core.configuration.internal.ConfigurationPackageDIModule;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager;
import org.hisp.dhis.android.core.constant.internal.ConstantPackageDIModule;
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalPackageDIModule;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.internal.DataElementPackageDIModule;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.internal.DataSetPackageDIModule;
import org.hisp.dhis.android.core.datastore.internal.DataStorePackageDIModule;
import org.hisp.dhis.android.core.datavalue.internal.DataValueConflictDIModule;
import org.hisp.dhis.android.core.datavalue.internal.DataValuePackageDIModule;
import org.hisp.dhis.android.core.domain.aggregated.data.internal.AggregatedDataPackageDIModule;
import org.hisp.dhis.android.core.domain.aggregated.internal.AggregatedModuleImpl;
import org.hisp.dhis.android.core.domain.metadata.internal.MetadataModuleImpl;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPackageDIModule;
import org.hisp.dhis.android.core.event.internal.EventPackageDIModule;
import org.hisp.dhis.android.core.event.internal.EventPostPayloadGenerator;
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePackageDIModule;
import org.hisp.dhis.android.core.imports.internal.ImportPackageDIModule;
import org.hisp.dhis.android.core.indicator.internal.IndicatorPackageDIModule;
import org.hisp.dhis.android.core.legendset.internal.LegendPackageDIModule;
import org.hisp.dhis.android.core.maintenance.internal.MaintenancePackageDIModule;
import org.hisp.dhis.android.core.note.internal.NotePackageDIModule;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.internal.OptionPackageDIModule;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitPackageDIModule;
import org.hisp.dhis.android.core.period.internal.PeriodHandler;
import org.hisp.dhis.android.core.period.internal.PeriodPackageDIModule;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.internal.ProgramPackageDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.internal.RelationshipPackageDIModule;
import org.hisp.dhis.android.core.resource.internal.ResourcePackageDIModule;
import org.hisp.dhis.android.core.settings.internal.SettingPackageDIModule;
import org.hisp.dhis.android.core.sms.internal.SmsDIModule;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoPackageDIModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityPackageDIModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.internal.OldTrackerImporterPayloadGenerator;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeLegendSetDIModule;
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterPackageDIModule;
import org.hisp.dhis.android.core.tracker.importer.internal.interpreters.InterpreterSelector;
import org.hisp.dhis.android.core.user.internal.UserPackageDIModule;
import org.hisp.dhis.android.core.validation.internal.ValidationPackageDIModule;
import org.hisp.dhis.android.core.visualization.internal.VisualizationPackageDIModule;
import org.hisp.dhis.android.core.wipe.internal.WipeDIModule;
import org.hisp.dhis.android.core.wipe.internal.WipeModule;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.ExcessivePublicCount", "PMD.GodClass"})
@Singleton
@Component(modules = {
        AppContextDIModule.class,
        APIClientDIModule.class,
        AttributePackageDIModule.class,
        DatabaseDIModule.class,
        JSONSerializationDIModule.class,
        KeyValueStorageDIModule.class,
        WipeDIModule.class,
        RepositoriesDIModule.class,

        AggregatedDataPackageDIModule.class,
        AnalyticsPackageDIModule.class,
        CategoryPackageDIModule.class,
        CommonPackageDIModule.class,
        ConfigurationPackageDIModule.class,
        ConstantPackageDIModule.class,
        DataElementPackageDIModule.class,
        DataSetPackageDIModule.class,
        DataApprovalPackageDIModule.class,
        DataValuePackageDIModule.class,
        EnrollmentPackageDIModule.class,
        EventPackageDIModule.class,
        FileResourcePackageDIModule.class,
        ImportPackageDIModule.class,
        IndicatorPackageDIModule.class,
        LegendPackageDIModule.class,
        DataStorePackageDIModule.class,
        MaintenancePackageDIModule.class,
        MaintenancePackageDIModule.class,
        NotePackageDIModule.class,
        OptionPackageDIModule.class,
        OrganisationUnitPackageDIModule.class,
        PeriodPackageDIModule.class,
        ProgramPackageDIModule.class,
        TrackedEntityAttributeLegendSetDIModule.class,
        RelationshipPackageDIModule.class,
        ResourcePackageDIModule.class,
        SystemInfoPackageDIModule.class,
        SettingPackageDIModule.class,
        TrackedEntityPackageDIModule.class,
        TrackerImporterPackageDIModule.class,
        SmsDIModule.class,
        UserPackageDIModule.class,
        ValidationPackageDIModule.class,
        VisualizationPackageDIModule.class,
        DataValueConflictDIModule.class}
)

public interface D2DIComponent {

    D2Modules modules();
    MetadataModuleImpl metadataModule();
    AggregatedModuleImpl aggregatedModule();
    WipeModule wipeModule();

    DatabaseAdapter databaseAdapter();
    UserIdInMemoryStore userIdInMemoryStore();
    MultiUserDatabaseManagerForD2Manager multiUserDatabaseManagerForD2Manager();
    CredentialsSecureStore credentialsSecureStore();

    @VisibleForTesting
    Retrofit retrofit();
    @VisibleForTesting
    D2InternalModules internalModules();
    @VisibleForTesting
    UidsCall<Program> programCall();
    @VisibleForTesting
    UidsCall<OptionSet> optionSetCall();
    @VisibleForTesting
    UidsCall<Option> optionCall();
    @VisibleForTesting
    UidsCallFactory<DataElement> dataElementCallFactory();
    @VisibleForTesting
    UidsCallFactory<DataSet> dataSetCallFactory();
    @VisibleForTesting
    Handler<RelationshipType> relationshipTypeHandler();
    @VisibleForTesting
    Handler<TrackedEntityType> trackedEntityTypeHandler();
    @VisibleForTesting
    OldTrackerImporterPayloadGenerator oldTrackerImporterPayloadGenerator();
    @VisibleForTesting
    EventPostPayloadGenerator eventPostPayloadGenerator();
    @VisibleForTesting
    IdentifiableObjectStore<CategoryOption> categoryOptionStore();
    @VisibleForTesting
    PeriodHandler periodHandler();
    @VisibleForTesting
    InterpreterSelector interpreterSelector();

    @Component.Builder
    interface Builder {
        Builder appContextDIModule(AppContextDIModule appContextDIModule);
        Builder secureStorageDIModule(KeyValueStorageDIModule secureStoregeDIModule);
        Builder wipeDIModule(WipeDIModule wipeDIModule);
        Builder repositoriesDIModule(RepositoriesDIModule repositoriesDIModule);

        Builder analyticsPackageDIModule(AnalyticsPackageDIModule analyticsPackageDIModule);
        Builder categoryPackageDIModule(CategoryPackageDIModule categoryPackageDIModule);
        Builder commonPackageDIModule(CommonPackageDIModule commonPackageDIModule);
        Builder configurationPackageDIModule(ConfigurationPackageDIModule configurationPackageDIModule);
        Builder constantPackageDIModule(ConstantPackageDIModule constantPackageDIModule);
        Builder dataElementPackageDIModule(DataElementPackageDIModule dataElementPackageDIModule);
        Builder dataSetPackageDIModule(DataSetPackageDIModule dataSetPackageDIModule);
        Builder dataApprovalPackageDIModule(DataApprovalPackageDIModule dataApprovalPackageDIModule);
        Builder dataValuePackageDIModule(DataValuePackageDIModule dataValuePackageDIModule);
        Builder enrollmentPackageDIModule(EnrollmentPackageDIModule enrollmentPackageDIModule);
        Builder eventPackageDIModule(EventPackageDIModule eventPackageDIModule);
        Builder fileResourcePackageDIModule(FileResourcePackageDIModule fileResourcePackageDIModule);
        Builder importPackageDIModule(ImportPackageDIModule importPackageDIModule);
        Builder indicatorPackageDIModule(IndicatorPackageDIModule indicatorPackageDIModule);
        Builder legendPackageDIModule(LegendPackageDIModule legendPackageDIModule);
        Builder dataStorePackageDIModule(DataStorePackageDIModule dataStorePackageDIModule);
        Builder maintenancePackageDIModule(MaintenancePackageDIModule maintenancePackageDIModule);
        Builder optionPackageDIModule(OptionPackageDIModule optionPackageDIModule);
        Builder organisationUnitPackageDIModule(OrganisationUnitPackageDIModule organisationUnitPackageDIModule);
        Builder periodPackageDIModule(PeriodPackageDIModule periodPackageDIModule);
        Builder programPackageDIModule(ProgramPackageDIModule programPackageDIModule);
        Builder relationshipDIModule(RelationshipPackageDIModule relationshipPackageDIModule);
        Builder resourcePackageDIModule(ResourcePackageDIModule resourcePackageDIModule);
        Builder systemInfoPackageDIModule(SystemInfoPackageDIModule systemInfoPackageDIModule);
        Builder systemSettingPackageDIModule(SettingPackageDIModule settingPackageDIModule);
        Builder trackedEntityPackageDIModule(TrackedEntityPackageDIModule trackedEntityPackageDIModule);
        Builder trackerImporterPackageDIModule(TrackerImporterPackageDIModule trackerImporterPackageDIModule);
        Builder userPackageDIModule(UserPackageDIModule userPackageDIModule);
        Builder validationPackageDIModule(ValidationPackageDIModule validationPackageDIModule);
        Builder visualizationPackageDIModule(VisualizationPackageDIModule visualizationPackageDIModule);
        Builder dataValueConflictDIModule(DataValueConflictDIModule dataValueConflictDIModule);
        D2DIComponent build();
    }

    static D2DIComponent create(D2Configuration d2Configuration,
                                SecureStore secureStore,
                                InsecureStore insecureStore) {
        return DaggerD2DIComponent.builder()
                .appContextDIModule(new AppContextDIModule(d2Configuration))
                .secureStorageDIModule(new KeyValueStorageDIModule(secureStore, insecureStore))
                .build();
    }
}