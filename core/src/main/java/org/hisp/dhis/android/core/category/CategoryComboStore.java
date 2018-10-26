package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class CategoryComboStore {

    private CategoryComboStore() {}

    private static StatementBinder<CategoryCombo> BINDER = new IdentifiableStatementBinder<CategoryCombo>() {
        @Override
        public void bindToStatement(@NonNull CategoryCombo o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, o.isDefault());
        }
    };

    private static final CursorModelFactory<CategoryCombo> FACTORY = new CursorModelFactory<CategoryCombo>() {
        @Override
        public CategoryCombo fromCursor(Cursor cursor) {
            return CategoryCombo.create(cursor);
        }
    };

    public static IdentifiableObjectStore<CategoryCombo> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, CategoryComboTableInfo.TABLE_INFO, BINDER, FACTORY);
    }
}
