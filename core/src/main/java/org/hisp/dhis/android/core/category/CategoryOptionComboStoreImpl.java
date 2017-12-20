package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryOptionComboStoreImpl implements CategoryOptionComboStore {

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboModel.TABLE + " (" +
                    CategoryOptionComboModel.Columns.UID + ", " +
                    CategoryOptionComboModel.Columns.CODE + ", " +
                    CategoryOptionComboModel.Columns.NAME + ", " +
                    CategoryOptionComboModel.Columns.DISPLAY_NAME + ", " +
                    CategoryOptionComboModel.Columns.CREATED + ", " +
                    CategoryOptionComboModel.Columns.LAST_UPDATED + ", " +
                    CategoryOptionComboModel.Columns.CATEGORY_COMBO + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private static final String EQUAL_QUESTION_MARK = " =?";
    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryOptionComboModel.TABLE +
            " WHERE " + CategoryOptionComboModel.Columns.UID + EQUAL_QUESTION_MARK + ";";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + CategoryOptionComboModel.TABLE + " SET " +
                    CategoryOptionComboModel.Columns.UID + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.CODE + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.NAME + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.DISPLAY_NAME + EQUAL_QUESTION_MARK + ", "
                    +
                    CategoryOptionComboModel.Columns.CREATED + EQUAL_QUESTION_MARK + ", " +
                    CategoryOptionComboModel.Columns.CATEGORY_COMBO + EQUAL_QUESTION_MARK
                    + " WHERE " +
                    CategoryOptionComboModel.Columns.UID + EQUAL_QUESTION_MARK + ";";

    public CategoryOptionComboStoreImpl(DatabaseAdapter databaseAdapter) {

        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryOptionCombo entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    @Override
    public boolean delete(@NonNull CategoryOptionCombo entity) {

        validate(entity);

        bindForDelete(entity);

        return execute(deleteStatement);
    }

    @Override
    public boolean update(@NonNull CategoryOptionCombo oldEntity,
            @NonNull CategoryOptionCombo newEntity) {

        validateForUpdate(oldEntity, newEntity);

        bindUpdate(oldEntity, newEntity);

        return execute(updateStatement);
    }

    private void validate(@NonNull CategoryOptionCombo optionCombo) {
        isNull(optionCombo.uid());
    }

    private void bindForDelete(@NonNull CategoryOptionCombo optionCombo) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, optionCombo.uid());
    }

    private void bindUpdate(@NonNull CategoryOptionCombo oldOptionCombo,
            @NonNull CategoryOptionCombo newOptionCombo) {
        final int whereUidIndex = 7;
        bind(updateStatement, newOptionCombo);

        sqLiteBind(updateStatement, whereUidIndex, oldOptionCombo.uid());
    }

    private void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryOptionCombo newOptionCombo) {
        sqLiteBind(sqLiteStatement, 1, newOptionCombo.uid());
        sqLiteBind(sqLiteStatement, 2, newOptionCombo.code());
        sqLiteBind(sqLiteStatement, 3, newOptionCombo.name());
        sqLiteBind(sqLiteStatement, 4, newOptionCombo.displayName());
        sqLiteBind(sqLiteStatement, 5, newOptionCombo.created());
        sqLiteBind(sqLiteStatement, 6, newOptionCombo.lastUpdated());

        //noinspection ConstantConditions
        if (newOptionCombo.categoryCombo() != null
                && newOptionCombo.categoryCombo().uid() != null) {
            //noinspection ConstantConditions
            sqLiteBind(sqLiteStatement, 7, newOptionCombo.categoryCombo().uid());
        }

    }

    private boolean wasExecuted(int numberOfRows) {
        return numberOfRows >= 1;
    }

    private boolean execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryOptionComboModel.TABLE, statement);
        statement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryOptionComboModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validateForUpdate(@NonNull CategoryOptionCombo oldEntity,
            @NonNull CategoryOptionCombo newEntity) {
        isNull(oldEntity.uid());
        isNull(newEntity.uid());
    }


}

