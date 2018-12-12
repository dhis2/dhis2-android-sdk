package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.arch.api.retrofit.APIClientDIModule;
import org.hisp.dhis.android.core.category.CategoryDIModule;
import org.hisp.dhis.android.core.common.CommonDIModule;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.dataelement.DataElementDIModule;
import org.hisp.dhis.android.core.dataset.DataSetDIModule;
import org.hisp.dhis.android.core.datavalue.DataValueDIModule;
import org.hisp.dhis.android.core.domain.aggregated.AggregatedModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceDIModule;
import org.hisp.dhis.android.core.period.PeriodDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipDIModule;
import org.hisp.dhis.android.core.resource.ResourceDIModule;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.settings.SystemSettingDIModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoDIModule;
import org.hisp.dhis.android.core.user.UserDIModule;

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
        DataSetDIModule.class,
        DataElementDIModule.class,
        DataValueDIModule.class,
        PeriodDIModule.class,
        UserDIModule.class,
        MaintenanceDIModule.class}
)
public interface D2DIComponent {

    D2InternalModules internalModules();
    ResourceHandler resourceHandler();
    GenericCallData genericCallData();
    AggregatedModule aggregatedModule();

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
        Builder dataSetDIModule(DataSetDIModule dataSetDIModule);
        Builder dataElementDIModule(DataElementDIModule dataElementDIModule);
        Builder dataValueDIModule(DataValueDIModule dataValueDIModule);
        Builder periodDIModule(PeriodDIModule periodDIModule);
        Builder userDIModule(UserDIModule userDIModule);
        Builder maintenanceDIModule(MaintenanceDIModule maintenanceDIModule);
        D2DIComponent build();
    }
}