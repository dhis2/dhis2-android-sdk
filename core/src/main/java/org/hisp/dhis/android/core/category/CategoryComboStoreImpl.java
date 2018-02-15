package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CategoryComboStoreImpl extends Store implements CategoryComboStore {

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

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboModel.TABLE + " (" +
                    FIELDS + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            CategoryComboModel.Columns.UID +
            " FROM " + CategoryComboModel.TABLE +
            " WHERE " + CategoryComboModel.Columns.UID + " =?;";

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

    private static final String QUERY_BY_UID_STATEMENT = "SELECT " +
            FIELDS + " FROM " + CategoryComboModel.TABLE +
            " WHERE " + CategoryComboModel.Columns.UID + "=?";

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
    public int delete(@NonNull String uid) {

        isNull(uid);

        bindForDelete(uid);

        return execute(deleteStatement);

    }

    @Override
    public int update(@NonNull CategoryCombo categoryCombo) {

        validate(categoryCombo);

        bindUpdate(categoryCombo);

        return execute(updateStatement);
    }

    private int execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, statement);
        statement.clearBindings();

        return rowsAffected;
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryComboModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validate(@NonNull CategoryCombo category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull String uid) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, uid);
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

    @Override
    public List<CategoryCombo> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_COMBOS);

        return mapCategoryCombosFromCursor(cursor);
    }

    @Override
    public CategoryCombo queryByUid(String uid) {
        CategoryCombo categoryCombo = null;

        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            categoryCombo = mapCategoryCombosFromCursor(cursor).get(0);
        }

        return categoryCombo;
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

        String uid = getStringFromCursor(cursor,  0);
        String code = getStringFromCursor(cursor, 1);
        String name = getStringFromCursor(cursor, 2);
        String displayName = getStringFromCursor(cursor, 3);
        Date created = getDateFromCursor(cursor, 4);
        Date lastUpdated = getDateFromCursor(cursor, 5);
        Boolean isDefault = getBooleanFromCursor(cursor, 6);

        categoryCombo = CategoryCombo.builder().uid(uid).code(code).name(name).displayName(displayName)
                .created(created).lastUpdated(lastUpdated).isDefault(isDefault).build();

        return categoryCombo;
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }
    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryComboModel.TABLE);
    }

}

