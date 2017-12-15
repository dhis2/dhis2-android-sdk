package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class CategoryComboLinkStoreImpl extends BaseLinkStore<CategoryComboLinkModel> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboLinkModel.TABLE + " (" +
                    CategoryComboLinkModel.Columns.CATEGORY + ", " +
                    CategoryComboLinkModel.Columns.COMBO + ") " +
                    "VALUES(?, ?);";

    public CategoryComboLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                CategoryComboLinkModel.TABLE);
    }

    @Override
    public void validate(@NonNull CategoryComboLinkModel link) {
        isNull(link.category());
        isNull(link.combo());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryComboLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.combo());
    }

    @Override
    public boolean delete(CategoryComboLinkModel element) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }

    @Override
    public boolean update(CategoryComboLinkModel oldElement, CategoryComboLinkModel newElement) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }
}

