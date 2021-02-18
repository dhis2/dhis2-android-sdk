package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.settings.FilterSetting

internal class FilterSettingHandler(store: ObjectWithoutUidStore<FilterSetting>) :
    ObjectWithoutUidHandlerImpl<FilterSetting>(store) {

    override fun beforeCollectionHandled(oCollection: Collection<FilterSetting>): Collection<FilterSetting> {
        store.delete()
        return oCollection
    }
}
