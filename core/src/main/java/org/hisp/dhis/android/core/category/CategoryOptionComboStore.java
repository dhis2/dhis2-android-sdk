package org.hisp.dhis.android.core.category;


import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class CategoryOptionComboStore {

    private CategoryOptionComboStore() {}

    private static StatementBinder<CategoryOptionCombo> BINDER
            = new IdentifiableStatementBinder<CategoryOptionCombo>() {

        @Override
        public void bindToStatement(@NonNull CategoryOptionCombo o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, UidsHelper.getUidOrNull(o.categoryCombo()));
        }
    };

    public static IdentifiableObjectStore<CategoryOptionCombo> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, CategoryOptionComboTableInfo.TABLE_INFO, BINDER);
    }
}
