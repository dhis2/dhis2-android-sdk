package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CategoryOptionStoreImpl implements CategoryOptionStore {

    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLiteStatement updateStatement;
    protected final SQLiteStatement deleteStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionModel.TABLE + " (" +
                    CategoryModel.Columns.UID + ", " +
                    CategoryModel.Columns.CODE + ", " +
                    CategoryModel.Columns.NAME + ", " +
                    CategoryModel.Columns.DISPLAY_NAME + ", " +
                    CategoryModel.Columns.CREATED + ", " +
                    CategoryModel.Columns.LAST_UPDATED + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?);";

    private static final String EQUAL_QUESTION_MARK = "=?";
    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryOptionModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryOptionModel.TABLE + " SET " +
            CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.CODE + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.DISPLAY_NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.CREATED + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.LAST_UPDATED + " " + EQUAL_QUESTION_MARK + " WHERE " +
            CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

    private static final String FIELDS = CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.UID + "," +
            CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.CODE + "," +
            CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.NAME + "," +
            CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.DISPLAY_NAME + "," +
            CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.CREATED + "," +
            CategoryOptionModel.TABLE +"."+ CategoryOptionModel.Columns.LAST_UPDATED;

    private static final String QUERY_ALL_CATEGORY_OPTIONS = "SELECT " +
            FIELDS + " FROM " + CategoryOptionModel.TABLE;

    public CategoryOptionStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);

    }

    @Override
    public long insert(@NonNull CategoryOption categoryOption) {

        validate(categoryOption);

        bind(insertStatement, categoryOption);

        return executeInsert();
    }

    @Override
    public boolean delete(@NonNull CategoryOption categoryOption) {

        validate(categoryOption);

        bindForDelete(categoryOption);

        return execute(deleteStatement);
    }

    @Override
    public boolean update(@NonNull CategoryOption oldCategoryOption,
            @NonNull CategoryOption newCategoryOption) {

        validateForUpdate(oldCategoryOption, newCategoryOption);

        bindUpdate(oldCategoryOption, newCategoryOption);

        return execute(updateStatement);
    }

    private boolean wasExecuted(int numberOfRows) {
        return numberOfRows >= 1;
    }

    private boolean execute(@NonNull SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryModel.TABLE, statement);
        statement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validateForUpdate(@NonNull CategoryOption oldCategoryOption,
            @NonNull CategoryOption newCategoryOption) {
        isNull(oldCategoryOption.uid());
        isNull(newCategoryOption.uid());
    }

    private void validate(@NonNull CategoryOption category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull CategoryOption option) {
        final int whereUidIndex = 1;

        sqLiteBind(deleteStatement, whereUidIndex, option.uid());
    }

    private void bindUpdate(@NonNull CategoryOption oldOption, @NonNull CategoryOption newOption) {
        final int whereUidIndex = 7;
        bind(updateStatement, newOption);

        sqLiteBind(updateStatement, whereUidIndex, oldOption.uid());
    }

    private void bind(@NonNull SQLiteStatement sqLiteStatement, @NonNull CategoryOption option) {
        sqLiteBind(sqLiteStatement, 1, option.uid());
        sqLiteBind(sqLiteStatement, 2, option.code());
        sqLiteBind(sqLiteStatement, 3, option.name());
        sqLiteBind(sqLiteStatement, 4, option.displayName());
        sqLiteBind(sqLiteStatement, 5, option.created());
        sqLiteBind(sqLiteStatement, 6, option.lastUpdated());
    }

    @Override
    public List<CategoryOption> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_OPTIONS);

        return mapCategoryOptionsFromCursor(cursor);
    }

    private List<CategoryOption> mapCategoryOptionsFromCursor(Cursor cursor) {
        List<CategoryOption> categoryOptions = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryOption categoryOption = mapCategoryOptionFromCursor(cursor);

                    categoryOptions.add(categoryOption);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryOptions;
    }

    private CategoryOption mapCategoryOptionFromCursor(Cursor cursor) {
        CategoryOption categoryOption;

        String uid = cursor.getString(0);
        String code = cursor.getString(1);
        String name = cursor.getString(2);
        String displayName = cursor.getString(3);
        Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
        Date lastUpdated = cursor.getString(5) == null ? null : parse(cursor.getString(5));

        categoryOption = CategoryOption.create(
                uid, code, name, displayName, created, lastUpdated);

        return categoryOption;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryOptionModel.TABLE);
    }
}

