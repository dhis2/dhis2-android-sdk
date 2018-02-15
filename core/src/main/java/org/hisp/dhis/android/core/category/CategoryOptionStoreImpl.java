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


@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class CategoryOptionStoreImpl extends Store implements CategoryOptionStore {

    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLiteStatement updateStatement;
    protected final SQLiteStatement deleteStatement;

    private static final String QUERY_BY_UID_STATEMENT = "SELECT " +
            CategoryOptionModel.Columns.UID + "," +
            CategoryOptionModel.Columns.CODE + "," +
            CategoryOptionModel.Columns.NAME + "," +
            CategoryOptionModel.Columns.DISPLAY_NAME + "," +
            CategoryOptionModel.Columns.CREATED + "," +
            CategoryOptionModel.Columns.LAST_UPDATED +
            "  FROM " + CategoryOptionModel.TABLE +
            " WHERE "+CategoryOptionModel.Columns.UID+" =?;";

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionModel.TABLE + " (" +
                    CategoryOptionModel.Columns.UID + ", " +
                    CategoryOptionModel.Columns.CODE + ", " +
                    CategoryOptionModel.Columns.NAME + ", " +
                    CategoryOptionModel.Columns.DISPLAY_NAME + ", " +
                    CategoryOptionModel.Columns.CREATED + ", " +
                    CategoryOptionModel.Columns.LAST_UPDATED + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?);";

    private static final String EXIST_BY_UID_STATEMENT = "SELECT " +
            CategoryOptionModel.Columns.UID +
            " FROM " + CategoryOptionModel.TABLE +
            " WHERE " + CategoryOptionModel.Columns.UID + " =?;";

    private static final String EQUAL_QUESTION_MARK = "=?";

    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryOptionModel.TABLE +
            " WHERE " + CategoryOptionModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryOptionModel.TABLE + " SET " +
            CategoryOptionModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryOptionModel.Columns.CODE + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryOptionModel.Columns.NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryOptionModel.Columns.DISPLAY_NAME + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryOptionModel.Columns.CREATED + " " + EQUAL_QUESTION_MARK + ", " +
            CategoryOptionModel.Columns.LAST_UPDATED + " " + EQUAL_QUESTION_MARK + " WHERE " +
            CategoryOptionModel.Columns.UID + " " + EQUAL_QUESTION_MARK + ";";

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
    public int delete(@NonNull String uid) {

        isNull(uid);

        bindForDelete(uid);

        return execute(deleteStatement);
    }

    @Override
    public int update(@NonNull CategoryOption categoryOption) {

        validate(categoryOption);

        bindUpdate(categoryOption);

        return execute(updateStatement);
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

    private void validate(@NonNull CategoryOption categoryOption) {
        isNull(categoryOption.uid());
    }

    private void bindForDelete(@NonNull String uid) {
        final int whereUidIndex = 1;

        sqLiteBind(deleteStatement, whereUidIndex, uid);
    }

    private void bindUpdate(@NonNull CategoryOption categoryOption) {
        final int whereUidIndex = 7;
        bind(updateStatement, categoryOption);

        sqLiteBind(updateStatement, whereUidIndex, categoryOption.uid());
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

        String uid = getStringFromCursor(cursor, 0);
        String code = getStringFromCursor(cursor, 1);
        String name = getStringFromCursor(cursor, 2);
        String displayName = getStringFromCursor(cursor, 3);
        Date created = getDateFromCursor(cursor, 4);
        Date lastUpdated = getDateFromCursor(cursor, 5);

        categoryOption = CategoryOption.builder().uid(uid).code(code).name(name)
                .displayName(displayName).created(created).lastUpdated(lastUpdated).build();

        return categoryOption;
    }

    @Override
    public Boolean exists(String uId) {
        Cursor cursor = databaseAdapter.query(EXIST_BY_UID_STATEMENT, uId);
        return cursor.getCount() > 0;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryOptionModel.TABLE);
    }

    @Override
    public CategoryOption queryByUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_UID_STATEMENT, uid);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else {
            return null;
        }

        return mapCategoryOptionFromCursor(cursor);
    }
}

