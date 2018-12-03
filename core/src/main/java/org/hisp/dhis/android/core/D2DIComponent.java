package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.arch.api.retrofit.RetrofitDIModule;
import org.hisp.dhis.android.core.category.CategoryDIModule;
import org.hisp.dhis.android.core.data.database.DatabaseDIModule;
import org.hisp.dhis.android.core.dataelement.DataElementDIModule;
import org.hisp.dhis.android.core.datavalue.DataValueDIModule;
import org.hisp.dhis.android.core.maintenance.MaintenanceDIModule;
import org.hisp.dhis.android.core.relationship.RelationshipDIModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoDIModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { DatabaseDIModule.class, RetrofitDIModule.class, D2InternalModulesDIModule.class,
        SystemInfoDIModule.class, RelationshipDIModule.class, CategoryDIModule.class, DataElementDIModule.class,
        DataValueDIModule.class, MaintenanceDIModule.class})
public interface D2DIComponent {

    D2InternalModules internalModules();

    @Component.Builder
    interface Builder {
        Builder databaseDIModule(DatabaseDIModule databaseDIModule);
        Builder retrofitDIModule(RetrofitDIModule retrofitDIModule);
        Builder systemInfoDIModule(SystemInfoDIModule systemInfoDIModule);
        Builder d2InternalModulesDIModule(D2InternalModulesDIModule d2InternalModulesDIModule);
        Builder relationshipDIModule(RelationshipDIModule relationshipDIModule);
        Builder categoryDIModule(CategoryDIModule categoryDIModule);
        Builder dataElementDIModule(DataElementDIModule dataElementDIModule);
        Builder dataValueDIModule(DataValueDIModule dataValueDIModule);
        Builder maintenanceDIModule(MaintenanceDIModule maintenanceDIModule);
        D2DIComponent build();
    }
}