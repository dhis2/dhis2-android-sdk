package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionComboLinkCategoryStoreImpl extends
        BaseLinkStore<CategoryOptionComboLinkCategoryModel> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboLinkCategoryModel.TABLE + " (" +
                    CategoryOptionComboLinkCategoryModel.Columns.OPTION_COMBO + ", " +
                    CategoryOptionComboLinkCategoryModel.Columns.CATEGORY + ") " +
                    "VALUES(?, ?);";


    public CategoryOptionComboLinkCategoryStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                CategoryOptionComboLinkCategoryModel.TABLE);
    }


    @Override
    public void validate(@NonNull CategoryOptionComboLinkCategoryModel link) {
        isNull(link.optionCombo());
        isNull(link.category());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement,
            @NonNull CategoryOptionComboLinkCategoryModel link) {
        sqLiteBind(sqLiteStatement, 1, link.optionCombo());
        sqLiteBind(sqLiteStatement, 2, link.category());
    }

    @Override
    public boolean delete(CategoryOptionComboLinkCategoryModel element) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }

    @Override
    public boolean update(CategoryOptionComboLinkCategoryModel oldElement,
            CategoryOptionComboLinkCategoryModel newElement) {
        throw new UnsupportedOperationException(
                "CategoryOptionLinkStoreImpl doesn't support delete");
    }
}

