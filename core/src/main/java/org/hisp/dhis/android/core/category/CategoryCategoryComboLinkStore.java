package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class CategoryCategoryComboLinkStore {

    private CategoryCategoryComboLinkStore() {}

    private static final StatementBinder<CategoryCategoryComboLinkModel> BINDER
            = new StatementBinder<CategoryCategoryComboLinkModel>() {
        @Override
        public void bindToStatement(@NonNull CategoryCategoryComboLinkModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.category());
            sqLiteBind(sqLiteStatement, 2, o.categoryCombo());
            sqLiteBind(sqLiteStatement, 3, o.sortOrder());
        }
    };

    private static final CursorModelFactory<CategoryCategoryComboLinkModel> FACTORY
            = new CursorModelFactory<CategoryCategoryComboLinkModel>() {
        @Override
        public CategoryCategoryComboLinkModel fromCursor(Cursor cursor) {
            return CategoryCategoryComboLinkModel.create(cursor);
        }
    };

    public static LinkModelStore<CategoryCategoryComboLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryCategoryComboLinkTableInfo.TABLE_INFO,
                CategoryCategoryComboLinkTableInfo.Columns.CATEGORY_COMBO, BINDER, FACTORY);
    }
}