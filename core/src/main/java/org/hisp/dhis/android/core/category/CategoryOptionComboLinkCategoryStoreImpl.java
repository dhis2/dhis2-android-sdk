package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionComboLinkCategoryStoreImpl implements
        CategoryOptionComboLinkCategoryStore {

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboLinkCategoryModel.TABLE + " (" +
                    CategoryOptionComboLinkCategoryModel.Columns.OPTION_COMBO + ", " +
                    CategoryOptionComboLinkCategoryModel.Columns.CATEGORY + ") " +
                    "VALUES(?, ?);";


    public CategoryOptionComboLinkCategoryStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryOptionComboLinkCategoryModel entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    private void validate(@NonNull CategoryOptionComboLinkCategoryModel link) {
        isNull(link.optionCombo());
        isNull(link.category());
    }

    private void bind(SQLiteStatement sqLiteStatement,
            @NonNull CategoryOptionComboLinkCategoryModel link) {
        sqLiteBind(sqLiteStatement, 1, link.optionCombo());
        sqLiteBind(sqLiteStatement, 2, link.category());
    }

    private int executeInsert() {
        int lastId = databaseAdapter.executeUpdateDelete(CategoryOptionComboLinkCategoryModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }
}

