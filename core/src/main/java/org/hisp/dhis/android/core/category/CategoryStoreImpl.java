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

public class CategoryStoreImpl implements CategoryStore {

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            CategoryModel.Columns.UID +
            " FROM " + CategoryModel.TABLE +
            " WHERE "+CategoryModel.Columns.UID+" =?;";

    private static final String INSERT_STATEMENT = "INSERT INTO " + CategoryModel.TABLE + " (" +
            CategoryModel.Columns.UID + ", " +
            CategoryModel.Columns.CODE + ", " +
            CategoryModel.Columns.NAME + ", " +
            CategoryModel.Columns.DISPLAY_NAME + ", " +
            CategoryModel.Columns.CREATED + ", " +
            CategoryModel.Columns.LAST_UPDATED + ", " +
            CategoryModel.Columns.DATA_DIMENSION_TYPE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?);";
    private static final String QUERY_STATEMENT = "SELECT " +
            CategoryModel.Columns.UID + "," +
            CategoryModel.Columns.CODE + "," +
            CategoryModel.Columns.NAME + "," +
            CategoryModel.Columns.DISPLAY_NAME + "," +
            CategoryModel.Columns.CREATED + "," +
            CategoryModel.Columns.LAST_UPDATED + "," +
            CategoryModel.Columns.DATA_DIMENSION_TYPE +
            "  FROM " + CategoryModel.TABLE;

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
    public int delete(@NonNull String uid) {

        isNull(uid);

        bindForDelete(uid);

        return execute(deleteStatement);
    }

    @Override
    public boolean update(@NonNull Category oldCategory,
            @NonNull Category newCategory) {

        validateForUpdate(oldCategory, newCategory);

        bindUpdate(oldCategory, newCategory);

        return wasExecuted(execute(updateStatement));
    }

    private boolean wasExecuted(int numberOfRows) {
        return numberOfRows >= 1;
    }

    private int execute(@NonNull SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryModel.TABLE, statement);
        statement.clearBindings();
        return rowsAffected;
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validateForUpdate(@NonNull Category oldCategory,
            @NonNull Category newCategory) {
        isNull(oldCategory.uid());
        isNull(newCategory.uid());
    }

    private void validate(@NonNull Category category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull String uid) {
        final int whereUidIndex = 1;

        sqLiteBind(deleteStatement, whereUidIndex, uid);
    }

    private void bindUpdate(@NonNull Category oldCategory, @NonNull Category newCategory) {
        final int whereUidIndex = 8;
        bind(updateStatement, newCategory);

        sqLiteBind(updateStatement, whereUidIndex, oldCategory.uid());
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

    @Override
    public List<Category> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);

        return mapOrgUnitsFromCursor(cursor);
    }

    @Override
    public Boolean exists(String categoryUId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, categoryUId);
        return cursor.getCount()>0;
    }

    private List<Category> mapOrgUnitsFromCursor(Cursor cursor) {
        List<Category> categories = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    Category category = mapCategoryFromCursor(cursor);

                    categories.add(category);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categories;
    }

    @NonNull
    private Category mapCategoryFromCursor(Cursor cursor) {
        String uid = cursor.getString(0);
        String code = cursor.getString(1);
        String name = cursor.getString(2);
        String displayName = cursor.getString(3);
        Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
        Date lastUpdated = cursor.getString(5) == null ? null : parse(
                cursor.getString(5));
        String dataDimensionType = cursor.getString(6);

        return Category.builder()
                .uid(uid)
                .code(code)
                .created(created)
                .name(name)
                .lastUpdated(lastUpdated)
                .displayName(displayName)
                .categoryOptions(new ArrayList<CategoryOption>())
                .dataDimensionType(dataDimensionType).build();
    }
}

