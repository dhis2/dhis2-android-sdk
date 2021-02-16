package org.hisp.dhis.android.core.settings.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.settings.FilterSetting

@Module
internal class FilterSettingEntityDIModule {

    @Provides
    @Reusable
    fun store(databaseAdapter: DatabaseAdapter): ObjectWithoutUidStore<FilterSetting> {
        return FilterConfigStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun handler(store: ObjectWithoutUidStore<FilterSetting>): Handler<FilterSetting> {
        return FilterConfigHandler(store)
    }
}