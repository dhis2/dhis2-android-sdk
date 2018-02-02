package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class CategoryStoreImpl implements CategoryStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + CategoryModel.TABLE + " (" +
            CategoryModel.Columns.UID + ", " +
            CategoryModel.Columns.CODE + ", " +
            CategoryModel.Columns.NAME + ", " +
            CategoryModel.Columns.DISPLAY_NAME + ", " +
            CategoryModel.Columns.CREATED + ", " +
            CategoryModel.Columns.LAST_UPDATED + ", " +
            CategoryModel.Columns.DATA_DIMENSION_TYPE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private static final String EQUAL_QUESTION_MARK = "=?";
    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryModel.TABLE + " SET " +
            CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.CODE + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.DISPLAY_NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.CREATED + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.LAST_UPDATED + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryModel.Columns.DATA_DIMENSION_TYPE + " " + EQUAL_QUESTION_MARK + " " + " WHERE "
            +
            CategoryModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

    public CategoryStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull Category category) {

        validate(category);

        bind(insertStatement, category);

        return executeInsert();
    }

    @Override
    public boolean delete(@NonNull Category category) {

        validate(category);

        bindForDelete(category);

        return execute(deleteStatement);
    }

    @Override
    public boolean update(@NonNull Category newCategory) {

        validateForUpdate(newCategory);

        bindUpdate(newCategory);

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

    private void validateForUpdate(@NonNull Category newCategory) {
        isNull(newCategory.uid());
    }

    private void validate(@NonNull Category category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull Category category) {
        final int whereUidIndex = 1;

        sqLiteBind(deleteStatement, whereUidIndex, category.uid());
    }

    private void bindUpdate(@NonNull Category category) {
        final int whereUidIndex = 8;
        bind(updateStatement, category);

        sqLiteBind(updateStatement, whereUidIndex, category.uid());
    }

    private void bind(@NonNull SQLiteStatement sqLiteStatement, @NonNull Category category) {
        sqLiteBind(sqLiteStatement, 1, category.uid());
        sqLiteBind(sqLiteStatement, 2, category.code());
        sqLiteBind(sqLiteStatement, 3, category.name());
        sqLiteBind(sqLiteStatement, 4, category.displayName());
        sqLiteBind(sqLiteStatement, 5, category.created());
        sqLiteBind(sqLiteStatement, 6, category.lastUpdated());
        sqLiteBind(sqLiteStatement, 7, category.dataDimensionType());

    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryModel.TABLE);
    }
}

