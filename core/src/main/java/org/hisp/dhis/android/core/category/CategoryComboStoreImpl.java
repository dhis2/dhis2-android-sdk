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


public class CategoryComboStoreImpl implements CategoryComboStore {

    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLiteStatement updateStatement;
    protected final SQLiteStatement deleteStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboModel.TABLE + " (" +
                    CategoryComboModel.Columns.UID + ", " +
                    CategoryComboModel.Columns.CODE + ", " +
                    CategoryComboModel.Columns.NAME + ", " +
                    CategoryComboModel.Columns.DISPLAY_NAME + ", " +
                    CategoryComboModel.Columns.CREATED + ", " +
                    CategoryComboModel.Columns.LAST_UPDATED + ", " +
                    CategoryComboModel.Columns.IS_DEFAULT + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryComboModel.TABLE +
            " WHERE " + CategoryComboModel.Columns.UID + " =?;";

    private static final String EQUAL_QUESTION_MARK = "=?,";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryComboModel.TABLE + " SET " +
            CategoryComboModel.Columns.UID + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.CODE + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.NAME + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.DISPLAY_NAME + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.CREATED + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.IS_DEFAULT + " =? WHERE " +
            CategoryComboModel.Columns.UID + " =?;";

    private static final String FIELDS = CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.UID + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.CODE + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.NAME + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.DISPLAY_NAME + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.CREATED + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.LAST_UPDATED + "," +
            CategoryComboModel.TABLE +"."+ CategoryComboModel.Columns.IS_DEFAULT;

    private static final String QUERY_ALL_CATEGORY_COMBOS = "SELECT " +
            FIELDS + " FROM " + CategoryComboModel.TABLE;

    public CategoryComboStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryCombo categoryCombo) {

        validate(categoryCombo);

        bind(insertStatement, categoryCombo);

        return executeInsert();
    }

    @Override
    public boolean delete(@NonNull CategoryCombo combo) {

        validate(combo);

        bindForDelete(combo);

        return execute(deleteStatement);
    }

    @Override
    public boolean update(@NonNull CategoryCombo oldCombo,
            @NonNull CategoryCombo newCombo) {

        validateForUpdate(oldCombo, newCombo);

        bindUpdate(oldCombo, newCombo);

        return execute(updateStatement);
    }

    private boolean execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, statement);
        statement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryComboModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validateForUpdate(@NonNull CategoryCombo oldCombo,
            @NonNull CategoryCombo newCombo) {
        isNull(oldCombo.uid());
        isNull(newCombo.uid());
    }

    private void validate(@NonNull CategoryCombo category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull CategoryCombo combo) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, combo.uid());
    }

    private void bindUpdate(@NonNull CategoryCombo oldCombo, @NonNull CategoryCombo newCombo) {
        final int whereUidIndex = 7;
        bind(updateStatement, newCombo);

        sqLiteBind(updateStatement, whereUidIndex, oldCombo.uid());
    }

    private void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryCombo categoryCombo) {
        sqLiteBind(sqLiteStatement, 1, categoryCombo.uid());
        sqLiteBind(sqLiteStatement, 2, categoryCombo.code());
        sqLiteBind(sqLiteStatement, 3, categoryCombo.name());
        sqLiteBind(sqLiteStatement, 4, categoryCombo.displayName());
        sqLiteBind(sqLiteStatement, 5, categoryCombo.created());
        sqLiteBind(sqLiteStatement, 6, categoryCombo.lastUpdated());
        sqLiteBind(sqLiteStatement, 7, fromBooleanToInt(categoryCombo));
    }

    @SuppressWarnings("ConstantConditions")
    private int fromBooleanToInt(@NonNull CategoryCombo categoryCombo) {
        if (categoryCombo == null) {
            return 0;
        }
        if (categoryCombo.isDefault() == null) {
            return 0;
        }

        return categoryCombo.isDefault() ? 1 : 0;
    }

    private boolean wasExecuted(int numberOfRows) {
        return numberOfRows >= 1;
    }

    @Override
    public List<CategoryCombo> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_COMBOS);

        return mapCategoryCombosFromCursor(cursor);
    }

    private List<CategoryCombo> mapCategoryCombosFromCursor(Cursor cursor) {
        List<CategoryCombo> categoryCombos = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryCombo categoryCombo = mapCategoryComboFromCursor(cursor);

                    categoryCombos.add(categoryCombo);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryCombos;
    }

    private CategoryCombo mapCategoryComboFromCursor(Cursor cursor) {
        CategoryCombo categoryCombo;

        String uid = cursor.getString(0);
        String code = cursor.getString(1);
        String name = cursor.getString(2);
        String displayName = cursor.getString(3);
        Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
        Date lastUpdated = cursor.getString(5) == null ? null : parse(cursor.getString(5));
        Boolean isDefault = cursor.getInt(6) > 0;

        categoryCombo = CategoryCombo.create(
                uid, code, name, displayName, created, lastUpdated, isDefault, null, null);

        return categoryCombo;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryComboModel.TABLE);
    }

}

