package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.category.CategoryDIModule;
import org.hisp.dhis.android.core.common.CommonDIModule;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.dataelement.DataElementPackageDIModule;
import org.hisp.dhis.android.core.dataset.DataSetPackageDIModule;
import org.hisp.dhis.android.core.datavalue.DataValuePackageDIModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.indicator.IndicatorPackageDIModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceDIModule;
import org.hisp.dhis.android.core.option.OptionPackageDIModule;
import org.hisp.dhis.android.core.period.PeriodPackageDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipDIModule;
import org.hisp.dhis.android.core.resource.ResourceDIModule;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.settings.SystemSettingDIModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoDIModule;
import org.hisp.dhis.android.core.user.UserPackageDIModule;
import org.hisp.dhis.android.core.wipe.WipeDIModule;
import org.hisp.dhis.android.core.wipe.WipeModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        DatabaseDIModule.class,
        APIClientDIModule.class,
        CommonDIModule.class,
        ResourceDIModule.class,
        SystemInfoDIModule.class,
        SystemSettingDIModule.class,
        RelationshipDIModule.class,
        CategoryDIModule.class,
        DataSetPackageDIModule.class,
        DataElementPackageDIModule.class,
        DataValuePackageDIModule.class,
        IndicatorPackageDIModule.class,
        OptionPackageDIModule.class,
        PeriodPackageDIModule.class,
        UserPackageDIModule.class,
        MaintenanceDIModule.class,
        WipeDIModule.class}
)
public interface D2DIComponent {

    D2InternalModules internalModules();
    ResourceHandler resourceHandler();
    GenericCallData genericCallData();
    AggregatedModule aggregatedModule();
    WipeModule wipeModule();

    @Component.Builder
    interface Builder {
        Builder databaseDIModule(DatabaseDIModule databaseDIModule);
        Builder apiClientDIModule(APIClientDIModule apiClientDIModule);
        Builder commonDIModule(CommonDIModule commonDIModule);
        Builder resourceDIModule(ResourceDIModule resourceDIModule);
        Builder systemInfoDIModule(SystemInfoDIModule systemInfoDIModule);
        Builder systemSettingDIModule(SystemSettingDIModule systemSettingDIModule);
        Builder relationshipDIModule(RelationshipDIModule relationshipDIModule);
        Builder categoryDIModule(CategoryDIModule categoryDIModule);
        Builder dataSetPackageDIModule(DataSetPackageDIModule dataSetPackageDIModule);
        Builder dataElementPackageDIModule(DataElementPackageDIModule dataElementPackageDIModule);
        Builder dataValuePackageDIModule(DataValuePackageDIModule dataValuePackageDIModule);
        Builder indicatorPackageDIModule(IndicatorPackageDIModule indicatorPackageDIModule);
        Builder optionPackageDIModule(OptionPackageDIModule optionPackageDIModule);
        Builder periodPackageDIModule(PeriodPackageDIModule periodPackageDIModule);
        Builder userPackageDIModule(UserPackageDIModule userPackageDIModule);
        Builder maintenanceDIModule(MaintenanceDIModule maintenanceDIModule);
        Builder wipeDIModule(WipeDIModule wipeDIModule);
        D2DIComponent build();
    }
}