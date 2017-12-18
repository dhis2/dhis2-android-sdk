package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionLinkStoreImpl extends BaseLinkStore<CategoryOptionLinkModel> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionLinkModel.TABLE + " (" +
                    CategoryOptionLinkModel.Columns.CATEGORY + ", " +
                    CategoryOptionLinkModel.Columns.OPTION + ") " +
                    "VALUES(?, ?);";


    public CategoryOptionLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                CategoryModel.TABLE);
    }


    @Override
    public void validate(@NonNull CategoryOptionLinkModel link) {
        isNull(link.category());
        isNull(link.option());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryOptionLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.option());
    }

    @Override
    public boolean delete(CategoryOptionLinkModel element) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }

    @Override
    public boolean update(CategoryOptionLinkModel oldElement, CategoryOptionLinkModel newElement) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }
}

