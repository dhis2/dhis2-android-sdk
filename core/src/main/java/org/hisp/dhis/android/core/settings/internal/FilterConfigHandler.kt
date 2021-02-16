package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl
import org.hisp.dhis.android.core.settings.FilterConfig

internal class FilterConfigHandler(store: ObjectWithoutUidStore<FilterConfig>) :
    ObjectWithoutUidHandlerImpl<FilterConfig>(store) {

    override fun beforeCollectionHandled(oCollection: Collection<FilterConfig>): Collection<FilterConfig> {
        store.delete()
        return oCollection
    }
}