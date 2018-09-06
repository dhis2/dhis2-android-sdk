package org.hisp.dhis.android.core.category;


import net.sqlcipher.database.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class CategoryOptionComboCategoryOptionLinkStore {

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

    public static LinkModelStore<CategoryOptionComboCategoryOptionLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryOptionComboCategoryOptionLinkModel.TABLE,
                new CategoryOptionComboCategoryOptionLinkModel.Columns(),
                CategoryOptionComboCategoryOptionLinkModel.Columns.CATEGORY_OPTION_COMBO, BINDER);
    }
}