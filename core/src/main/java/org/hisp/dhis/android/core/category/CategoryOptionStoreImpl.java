package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionStoreImpl extends BaseStore<CategoryOption> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionModel.TABLE + " (" +
                    CategoryModel.Columns.UID + ", " +
                    CategoryModel.Columns.CODE + ", " +
                    CategoryModel.Columns.NAME + ", " +
                    CategoryModel.Columns.DISPLAY_NAME + ", " +
                    CategoryModel.Columns.CREATED + ", " +
                    CategoryModel.Columns.LAST_UPDATED + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?);";

    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryOptionModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " =?;";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryOptionModel.TABLE + " SET " +
            CategoryModel.Columns.UID + " =?, " +
            CategoryModel.Columns.CODE + " =?, " +
            CategoryModel.Columns.NAME + " =?, " +
            CategoryModel.Columns.DISPLAY_NAME + " =?, " +
            CategoryModel.Columns.CREATED + " =?, " +
            CategoryModel.Columns.LAST_UPDATED + " =? WHERE " +
            CategoryModel.Columns.UID + " =?;";

    public CategoryOptionStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                databaseAdapter.compileStatement(UPDATE_STATEMENT),
                databaseAdapter.compileStatement(DELETE_STATEMENT),
                CategoryModel.TABLE);
    }


    @Override
    public void validate(@NonNull CategoryOption category) {
        isNull(category.uid());
    }

    @Override
    public void bindForDelete(@NonNull CategoryOption option) {
        final int UID_INDEX = 1;

        sqLiteBind(deleteStatement, UID_INDEX, option.uid());
    }

    @Override
    public void bindUpdate(@NonNull CategoryOption oldOption, @NonNull CategoryOption newOption) {
        final int WHERE_UID_INDEX = 7;
        bind(updateStatement, newOption);

        sqLiteBind(updateStatement, WHERE_UID_INDEX, oldOption.uid());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryOption option) {
        sqLiteBind(sqLiteStatement, 1, option.uid());
        sqLiteBind(sqLiteStatement, 2, option.code());
        sqLiteBind(sqLiteStatement, 3, option.name());
        sqLiteBind(sqLiteStatement, 4, option.displayName());
        sqLiteBind(sqLiteStatement, 5, option.created());
        sqLiteBind(sqLiteStatement, 6, option.lastUpdated());
    }

}

