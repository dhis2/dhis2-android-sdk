package org.hisp.dhis.android.core.category;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class CategoryOptionStore {

    private CategoryOptionStore() {
    }

    private static StatementBinder<CategoryOptionModel> BINDER = new NameableStatementBinder<CategoryOptionModel>() {
        @Override
        public void bindToStatement(@NonNull CategoryOptionModel o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 11, o.startDate());
            sqLiteBind(sqLiteStatement, 12, o.endDate());
        }
    };

    public static IdentifiableObjectStore<CategoryOptionModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, CategoryOptionModel.TABLE,
                new CategoryOptionModel.Columns().all(), BINDER);
    }
}