package org.hisp.dhis.android.core.category;


import net.sqlcipher.database.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public final class CategoryCategoryOptionLinkStore {

    private CategoryCategoryOptionLinkStore() {}

    private static final StatementBinder<CategoryCategoryOptionLinkModel> BINDER
            = new StatementBinder<CategoryCategoryOptionLinkModel>() {
        @Override
        public void bindToStatement(@NonNull CategoryCategoryOptionLinkModel o,
                                    @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 1, o.category());
            sqLiteBind(sqLiteStatement, 2, o.option());
        }
    };

    public static LinkModelStore<CategoryCategoryOptionLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryCategoryOptionLinkModel.TABLE,
                new CategoryCategoryOptionLinkModel.Columns(),
                CategoryCategoryOptionLinkModel.Columns.CATEGORY, BINDER);
    }
}
