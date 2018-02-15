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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.NPathComplexity",
        "PMD.AvoidDuplicateLiterals"
})
public class CategoryStoreImpl extends Store implements CategoryStore {

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            CategoryModel.Columns.UID +
            " FROM " + CategoryModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " =?;";

    private static final String QUERY_BY_UID_STATEMENT = "SELECT " +
            CategoryModel.Columns.UID + "," +
            CategoryModel.Columns.CODE + "," +
            CategoryModel.Columns.NAME + "," +
            CategoryModel.Columns.DISPLAY_NAME + "," +
            CategoryModel.Columns.CREATED + "," +
            CategoryModel.Columns.LAST_UPDATED + "," +
            CategoryModel.Columns.DATA_DIMENSION_TYPE +
            "  FROM " + CategoryModel.TABLE +
            " WHERE " + CategoryModel.Columns.UID + " =?;";

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
    public int update(@NonNull Category newCategory) {

        validate(newCategory);

        bindUpdate(newCategory);

        return execute(updateStatement);
    }

    @Override
    public List<Category> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);

        Map<String, Category> categoryMap = mapFromCursor(cursor);

        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public Category queryByUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        Map<String, Category> categoryMap = mapFromCursor(cursor);

        return categoryMap.get(uid);
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

    private void validate(@NonNull Category category) {
        isNull(category.uid());
    }

    private void bindForDelete(@NonNull String uid) {
        final int whereUidIndex = 1;

        sqLiteBind(deleteStatement, whereUidIndex, uid);
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

    @Override
    public Boolean exists(String categoryUId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, categoryUId);
        return cursor.getCount() > 0;
    }

    private Map<String, Category> mapFromCursor(Cursor cursor) {

        Map<String, Category> categoryMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Category category = mapCategory(cursor);

                    categoryMap.put(category.uid(), category);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return categoryMap;
    }

    private Category mapCategory(Cursor cursor) {
        String uid = getStringFromCursor(cursor, 0);
        String code = getStringFromCursor(cursor, 1);
        String name = getStringFromCursor(cursor, 2);
        String displayName = getStringFromCursor(cursor, 3);
        Date created = getDateFromCursor(cursor, 4);
        Date lastUpdated = getDateFromCursor(cursor, 5);
        String dataDimensionType = getStringFromCursor(cursor, 6);

        return Category.builder()
                .uid(uid)
                .code(code)
                .name(name)
                .displayName(displayName)
                .created(created)
                .lastUpdated(lastUpdated)
                .dataDimensionType(dataDimensionType)
                .build();
    }
}

