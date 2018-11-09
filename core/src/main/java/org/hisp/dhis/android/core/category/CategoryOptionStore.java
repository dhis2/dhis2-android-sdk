package org.hisp.dhis.android.core.category;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.AccessHelper;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class CategoryOptionStore {

    private CategoryOptionStore() {
    }

    private static StatementBinder<CategoryOption> BINDER = new NameableStatementBinder<CategoryOption>() {
        @Override
        public void bindToStatement(@NonNull CategoryOption o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 11, o.startDate());
            sqLiteBind(sqLiteStatement, 12, o.endDate());
            sqLiteBind(sqLiteStatement, 13, AccessHelper.getAccessDataWrite(o.access()));
        }
    };

    static final CursorModelFactory<CategoryOption> FACTORY = new CursorModelFactory<CategoryOption>() {
        @Override
        public CategoryOption fromCursor(Cursor cursor) {
            return CategoryOption.create(cursor);
        }
    };

    public static IdentifiableObjectStore<CategoryOption> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter,
                CategoryOptionTableInfo.TABLE_INFO, BINDER, FACTORY);
    }
}