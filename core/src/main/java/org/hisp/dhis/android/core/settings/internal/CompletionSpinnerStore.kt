package org.hisp.dhis.android.core.settings.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory
import org.hisp.dhis.android.core.settings.CompletionSpinner
import org.hisp.dhis.android.core.settings.CompletionSpinnerTableInfo

internal object CompletionSpinnerStore {

    private val BINDER = StatementBinder { o: CompletionSpinner, w: StatementWrapper ->
        w.bind(1, o.uid())
        w.bind(2, o.visible())
    }

    private val WHERE_UPDATE_BINDER = WhereStatementBinder { _: CompletionSpinner, _: StatementWrapper ->
    }

    private val WHERE_DELETE_BINDER = WhereStatementBinder { _: CompletionSpinner, _: StatementWrapper ->
    }

    fun create(databaseAdapter: DatabaseAdapter?): ObjectWithoutUidStore<CompletionSpinner> {
        return StoreFactory.objectWithoutUidStore(
            databaseAdapter!!, CompletionSpinnerTableInfo.TABLE_INFO,
            BINDER,
            WHERE_UPDATE_BINDER,
            WHERE_DELETE_BINDER
        ) { cursor: Cursor? -> CompletionSpinner.create(cursor) }
    }
}