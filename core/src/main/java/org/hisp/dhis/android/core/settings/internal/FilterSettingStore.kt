package org.hisp.dhis.android.core.settings.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.objectWithoutUidStore
import org.hisp.dhis.android.core.settings.FilterSetting
import org.hisp.dhis.android.core.settings.FilterSettingTableInfo

internal object FilterSettingStore {

    private val BINDER = StatementBinder { o: FilterSetting, w: StatementWrapper ->
        w.bind(1, o.scope())
        w.bind(2, o.filterType())
        w.bind(3, o.uid())
        w.bind(4, o.filter())
        w.bind(5, o.sort())
    }

    private val WHERE_UPDATE_BINDER = WhereStatementBinder {
            _: FilterSetting, _: StatementWrapper ->
    }

    private val WHERE_DELETE_BINDER = WhereStatementBinder {
            _: FilterSetting, _: StatementWrapper ->
    }

    fun create(databaseAdapter: DatabaseAdapter?): ObjectWithoutUidStore<FilterSetting> {
        return objectWithoutUidStore(
            databaseAdapter!!, FilterSettingTableInfo.TABLE_INFO,
            BINDER,
            WHERE_UPDATE_BINDER,
            WHERE_DELETE_BINDER
        ) { cursor: Cursor? -> FilterSetting.create(cursor) }
    }
}