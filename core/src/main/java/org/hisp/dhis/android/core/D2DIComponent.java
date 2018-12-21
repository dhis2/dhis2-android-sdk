package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.category.CategoryPackageDIModule;
import org.hisp.dhis.android.core.common.CommonPackageDIModule;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.dataelement.DataElementPackageDIModule;
import org.hisp.dhis.android.core.dataset.DataSetPackageDIModule;
import org.hisp.dhis.android.core.datavalue.DataValuePackageDIModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.indicator.IndicatorPackageDIModule;
import org.hisp.dhis.android.core.maintenance.MaintenancePackageDIModule;
import org.hisp.dhis.android.core.option.OptionPackageDIModule;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitPackageDIModule;
import org.hisp.dhis.android.core.period.PeriodPackageDIModule;
import org.hisp.dhis.android.core.program.ProgramPackageDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipPackageDIModule;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourcePackageDIModule;
import org.hisp.dhis.android.core.settings.SystemSettingPackageDIModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoPackageDIModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityPackageDIModule;
import org.hisp.dhis.android.core.user.UserPackageDIModule;
import org.hisp.dhis.android.core.wipe.WipeDIModule;
import org.hisp.dhis.android.core.wipe.WipeModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        APIClientDIModule.class,
        DatabaseDIModule.class,
        WipeDIModule.class,

        CategoryPackageDIModule.class,
        CommonPackageDIModule.class,
        DataElementPackageDIModule.class,
        DataSetPackageDIModule.class,
        DataValuePackageDIModule.class,
        IndicatorPackageDIModule.class,
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
        UserPackageDIModule.class}
)
public interface D2DIComponent {

    D2InternalModules internalModules();
    ResourceHandler resourceHandler();
    GenericCallData genericCallData();
    AggregatedModule aggregatedModule();
    WipeModule wipeModule();

    @Component.Builder
    interface Builder {
        Builder apiClientDIModule(APIClientDIModule apiClientDIModule);
        Builder databaseDIModule(DatabaseDIModule databaseDIModule);
        Builder wipeDIModule(WipeDIModule wipeDIModule);

        Builder categoryPackageDIModule(CategoryPackageDIModule categoryPackageDIModule);
        Builder commonPackageDIModule(CommonPackageDIModule commonPackageDIModule);
        Builder dataElementPackageDIModule(DataElementPackageDIModule dataElementPackageDIModule);
        Builder dataSetPackageDIModule(DataSetPackageDIModule dataSetPackageDIModule);
        Builder dataValuePackageDIModule(DataValuePackageDIModule dataValuePackageDIModule);
        Builder indicatorPackageDIModule(IndicatorPackageDIModule indicatorPackageDIModule);
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