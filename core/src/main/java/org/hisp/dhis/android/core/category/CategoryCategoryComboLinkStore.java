package org.hisp.dhis.android.core.category;


import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class CategoryCategoryComboLinkStore {

    private CategoryCategoryComboLinkStore() {}

    private static final StatementBinder<CategoryCategoryComboLinkModel> BINDER
            = new StatementBinder<CategoryCategoryComboLinkModel>() {
        @Override
        public void bindToStatement(@NonNull CategoryCategoryComboLinkModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.category());
            sqLiteBind(sqLiteStatement, 2, o.combo());
        }
    };

    private static final WhereStatementBinder<CategoryCategoryComboLinkModel> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<CategoryCategoryComboLinkModel>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull CategoryCategoryComboLinkModel o,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 3, o.category());
            sqLiteBind(sqLiteStatement, 4, o.combo());
        }
    };

    public static LinkModelStore<CategoryCategoryComboLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryCategoryComboLinkModel.TABLE,
                new CategoryCategoryComboLinkModel.Columns(),
                CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO, BINDER, WHERE_UPDATE_BINDER);
    }
}

