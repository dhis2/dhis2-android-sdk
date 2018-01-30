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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryComboStoreImpl implements CategoryComboStore {

    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLiteStatement updateStatement;
    protected final SQLiteStatement deleteStatement;

    private static final String FIELDS = CategoryComboModel.Columns.UID + ", " +
            CategoryComboModel.Columns.CODE + ", " +
            CategoryComboModel.Columns.NAME + ", " +
            CategoryComboModel.Columns.DISPLAY_NAME + ", " +
            CategoryComboModel.Columns.CREATED + ", " +
            CategoryComboModel.Columns.LAST_UPDATED + ", " +
            CategoryComboModel.Columns.IS_DEFAULT;

    private static final String QUERY_BY_UID_STATEMENT = "SELECT " +
            FIELDS +
            " FROM " + CategoryComboModel.TABLE +
            " WHERE "+CategoryComboModel.Columns.UID+" =?;";

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboModel.TABLE + " (" +
                    FIELDS + ") " +
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
            CategoryComboModel.Columns.LAST_UPDATED + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.IS_DEFAULT + " =? WHERE " +
            CategoryComboModel.Columns.UID + " =?;";

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

        return executeDelete();
    }

    @Override
    public boolean update(@NonNull CategoryCombo categoryCombo) {

        isNull(categoryCombo);

        bindUpdate(categoryCombo);

        return executeUpdate();
    }

    private boolean executeDelete() {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private boolean executeUpdate() {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryComboModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validate(@NonNull CategoryCombo category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull CategoryCombo combo) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, combo.uid());
    }

    private void bindUpdate(@NonNull CategoryCombo categoryCombo) {
        final int whereUidIndex = 8;
        bind(updateStatement, categoryCombo);

        sqLiteBind(updateStatement, whereUidIndex, categoryCombo.uid());
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

    @Override
    public CategoryCombo queryByUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        Map<String, CategoryCombo> categoryMap = mapFromCursor(cursor);

        return categoryMap.get(uid);
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

    private Map<String, CategoryCombo> mapFromCursor(Cursor cursor) {

        Map<String, CategoryCombo> categoryMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    mapCategory(cursor, categoryMap);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return categoryMap;
    }

    @SuppressWarnings({
            "PMD.NPathComplexity",
    })
    private void mapCategory(Cursor cursor, Map<String, CategoryCombo> categoryMap) {
        String uid = cursor.getString(0) == null ? null : cursor.getString(
                0);
        String code = cursor.getString(1) == null ? null : cursor.getString(
                1);
        String name = cursor.getString(2) == null ? null : cursor.getString(
                2);
        String displayName = cursor.getString(3) == null ? null : cursor.getString(
                3);
        Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
        Date lastUpdated = cursor.getString(5) == null ? null : parse(
                cursor.getString(5));
        Boolean isBoolean =
                cursor.getString(6) == null || cursor.getInt(6) == 0 ? Boolean.FALSE
                        : Boolean.TRUE;

        categoryMap.put(uid, CategoryCombo.builder()
                .uid(uid)
                .code(code)
                .name(name)
                .displayName(displayName)
                .created(created)
                .lastUpdated(lastUpdated)
                .isDefault(isBoolean)
                .build());
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryComboModel.TABLE);
    }

}

