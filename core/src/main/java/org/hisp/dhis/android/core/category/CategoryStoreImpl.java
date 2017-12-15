package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryStoreImpl extends BaseStore<Category> {

    private static final String INSERT_STATEMENT = "INSERT INTO " + CategoryModel.TABLE + " (" +
            CategoryModel.Columns.UID + ", " +
            CategoryModel.Columns.CODE + ", " +
            CategoryModel.Columns.NAME + ", " +
            CategoryModel.Columns.DISPLAY_NAME + ", " +
            CategoryModel.Columns.CREATED + ", " +
            CategoryModel.Columns.LAST_UPDATED + ", " +
            CategoryModel.Columns.DATA_DIMENSION_TYPE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " =?;";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryModel.TABLE + " SET " +
            CategoryModel.Columns.UID + " =?, " +
            CategoryModel.Columns.CODE + " =?, " +
            CategoryModel.Columns.NAME + " =?, " +
            CategoryModel.Columns.DISPLAY_NAME + " =?, " +
            CategoryModel.Columns.CREATED + " =?, " +
            CategoryModel.Columns.LAST_UPDATED + " =?, " +
            CategoryModel.Columns.DATA_DIMENSION_TYPE + " =? " + " WHERE " +
            CategoryModel.Columns.UID + " =?;";

    public CategoryStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                databaseAdapter.compileStatement(UPDATE_STATEMENT),
                databaseAdapter.compileStatement(DELETE_STATEMENT),
                CategoryModel.TABLE);
    }


    @Override
    public void validate(@NonNull Category category) {
        isNull(category.uid());
    }

    @Override
    public void bindForDelete(@NonNull Category category) {
        final int UID_INDEX = 1;

        sqLiteBind(deleteStatement, UID_INDEX, category.uid());
    }

    @Override
    public void bindUpdate(@NonNull Category oldCategory, @NonNull Category newCategory) {
        final int WHERE_UID_INDEX = 8;
        bind(updateStatement, newCategory);

        sqLiteBind(updateStatement, WHERE_UID_INDEX, oldCategory.uid());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull Category category) {
        sqLiteBind(sqLiteStatement, 1, category.uid());
        sqLiteBind(sqLiteStatement, 2, category.code());
        sqLiteBind(sqLiteStatement, 3, category.name());
        sqLiteBind(sqLiteStatement, 4, category.displayName());
        sqLiteBind(sqLiteStatement, 5, category.created());
        sqLiteBind(sqLiteStatement, 6, category.lastUpdated());
        sqLiteBind(sqLiteStatement, 7, category.dataDimensionType());

    }

}

