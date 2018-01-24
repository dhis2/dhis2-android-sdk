package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;


public class CategoryCategoryOptionLinkStoreImpl implements CategoryCategoryOptionLinkStore {

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryCategoryOptionLinkModel.TABLE + " (" +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY + ", " +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + ") " +
                    "VALUES(?, ?);";

    private static final String QUERY_BY_CATEGORY_UID_STATEMENT =
            "SELECT " + CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION +
                    " FROM " + CategoryCategoryOptionLinkModel.TABLE +
                    " WHERE " + CategoryCategoryOptionLinkModel.Columns.CATEGORY + " =?;";
    private static final String QUERY_BY_CATEGORY_OPTION_UID_STATEMENT =
            "SELECT " + CategoryCategoryOptionLinkModel.Columns.CATEGORY + " " +
                    " FROM " + CategoryCategoryOptionLinkModel.TABLE +
                    " WHERE " + CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + " =?;";

    public CategoryCategoryOptionLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryCategoryOptionLinkModel entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    private void validate(@NonNull CategoryCategoryOptionLinkModel link) {
        isNull(link.category());
        isNull(link.option());
    }

    private void bind(@NonNull SQLiteStatement sqLiteStatement, @NonNull CategoryCategoryOptionLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.option());
    }

    private int executeInsert() {
        int lastId = databaseAdapter.executeUpdateDelete(CategoryModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryCategoryOptionLinkModel.TABLE);
    }

    @Override
    public List<String> queryCategoryOptionUidListFromCategoryUid(String optionSetUid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_CATEGORY_UID_STATEMENT, optionSetUid);

        List<String> uIds = mapFromCursor(cursor);

        return uIds;
    }

    @Override
    public List<String> queryCategoryUidListFromCategoryOptionUid(String categoryOptionUid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_CATEGORY_OPTION_UID_STATEMENT, categoryOptionUid);

        List<String> uIds = mapFromCursor(cursor);

        return uIds;
    }

    private List<String> mapFromCursor(Cursor cursor) {

        List<String> uIds = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0) == null ? null : cursor.getString(
                            0);
                    uIds.add(uid);
                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return uIds;
    }
}

