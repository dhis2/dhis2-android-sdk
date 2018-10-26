package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class CategoryOptionComboCategoryOptionLinkStore {

    private CategoryOptionComboCategoryOptionLinkStore() {}

    private static final StatementBinder<CategoryOptionComboCategoryOptionLinkModel> BINDER
            = new StatementBinder<CategoryOptionComboCategoryOptionLinkModel>() {
        @Override
        public void bindToStatement(@NonNull CategoryOptionComboCategoryOptionLinkModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.categoryOptionCombo());
            sqLiteBind(sqLiteStatement, 2, o.categoryOption());
        }
    };

    private static final CursorModelFactory<CategoryOptionComboCategoryOptionLinkModel> FACTORY
            = new CursorModelFactory<CategoryOptionComboCategoryOptionLinkModel>() {
        @Override
        public CategoryOptionComboCategoryOptionLinkModel fromCursor(Cursor cursor) {
            return CategoryOptionComboCategoryOptionLinkModel.create(cursor);
        }
    };

    public static LinkModelStore<CategoryOptionComboCategoryOptionLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryOptionComboCategoryOptionLinkModel.TABLE,
                new CategoryOptionComboCategoryOptionLinkModel.Columns(),
                CategoryOptionComboCategoryOptionLinkModel.Columns.CATEGORY_OPTION_COMBO, BINDER, FACTORY);
    }
}