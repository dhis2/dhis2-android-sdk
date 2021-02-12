package org.hisp.dhis.android.core.settings.internal

import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.settings.AppearanceSettings
import org.hisp.dhis.android.core.settings.FilterConfig

class FilterConfigStore {

    private val binder = StatementBinder { o: FilterConfig, w: StatementWrapper ->
        w.bind(1, o.scope())
        w.bind(2, o.filterType())
        w.bind(3, o.uid())
        w.bind(4, o.filter())
        w.bind(5, o.sort())
    }


}