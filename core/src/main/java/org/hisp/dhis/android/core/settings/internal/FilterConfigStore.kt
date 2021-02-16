package org.hisp.dhis.android.core.settings.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithoutUidStore
import org.hisp.dhis.android.core.settings.FilterConfig
import org.hisp.dhis.android.core.settings.FilterConfigTableInfo

internal object FilterConfigStore {

    private val BINDER = StatementBinder { o: FilterConfig, w: StatementWrapper ->
        w.bind(1, o.scope())
        w.bind(2, o.filterType())
        w.bind(3, o.uid())
        w.bind(4, o.filter())
        w.bind(5, o.sort())
    }

    private val WHERE_UPDATE_BINDER = WhereStatementBinder {
            _: FilterConfig, _: StatementWrapper ->
    }

    private val WHERE_DELETE_BINDER = WhereStatementBinder {
            _: FilterConfig, _: StatementWrapper ->
    }

    fun create(databaseAdapter: DatabaseAdapter?): ObjectWithoutUidStore<FilterConfig> {
        return objectWithoutUidStore(
            databaseAdapter!!, FilterConfigTableInfo.TABLE_INFO,
            BINDER,
            WHERE_UPDATE_BINDER,
            WHERE_DELETE_BINDER
        ) { cursor: Cursor? -> FilterConfig.create(cursor) }
    }
}