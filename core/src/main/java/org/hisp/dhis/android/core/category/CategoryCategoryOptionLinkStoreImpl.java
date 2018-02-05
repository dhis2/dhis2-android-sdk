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
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement updateStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryCategoryOptionLinkModel.TABLE + " (" +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY + ", " +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + ") " +
                    "VALUES(?, ?);";

    private static final String DELETE_STATEMENT =
            "DELETE FROM " + CategoryCategoryOptionLinkModel.TABLE +
                    " WHERE " + CategoryCategoryOptionLinkModel.Columns.CATEGORY + " =?" + " AND "
                    + CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + "=?;";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + CategoryCategoryOptionLinkModel.TABLE + " SET " +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY + " =?, " +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + " =?" +
                    " WHERE " + CategoryCategoryOptionLinkModel.Columns.CATEGORY + " =? AND " +
                    CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + " =?;";

    private static final String FIELDS =
            CategoryCategoryOptionLinkModel.TABLE + "."
                    + CategoryCategoryOptionLinkModel.Columns.CATEGORY + "," +
                    CategoryCategoryOptionLinkModel.TABLE + "."
                    + CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION;


    private static final String QUERY_ALL_CATEGORY_OPTION_LINKS = "SELECT " +
            FIELDS + " FROM " + CategoryCategoryOptionLinkModel.TABLE;

    public CategoryCategoryOptionLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryCategoryOptionLinkModel categoryOptionLinkModel) {

        validate(categoryOptionLinkModel);

        bind(insertStatement, categoryOptionLinkModel);

        return executeInsert();
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryCategoryOptionLinkModel.TABLE);
    }

    @Override
    public int delete(@NonNull CategoryCategoryOptionLinkModel categoryOptionLinkModel) {
        validate(categoryOptionLinkModel);

        bind(deleteStatement, categoryOptionLinkModel);

        return execute(deleteStatement);
    }

    @Override
    public int update(
            @NonNull CategoryCategoryOptionLinkModel oldCategoryCategoryOptionLinkModel,
            @NonNull CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel) {

        validateForUpdate(oldCategoryCategoryOptionLinkModel, newCategoryCategoryOptionLinkModel);
        bindUpdate(oldCategoryCategoryOptionLinkModel, newCategoryCategoryOptionLinkModel);

        return execute(updateStatement);
    }

    @Override
    public List<CategoryCategoryOptionLinkModel> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_OPTION_LINKS);

        return mapFromCursor(cursor);
    }

    private void validate(@NonNull CategoryCategoryOptionLinkModel link) {
        isNull(link.category());
        isNull(link.categoryOption());
    }

    private void validateForUpdate(
            @NonNull CategoryCategoryOptionLinkModel oldCategoryCategoryOptionLinkModel,
            @NonNull CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel) {

        validate(oldCategoryCategoryOptionLinkModel);
        validate(newCategoryCategoryOptionLinkModel);
    }

    private void bind(@NonNull SQLiteStatement sqLiteStatement,
            @NonNull CategoryCategoryOptionLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.categoryOption());
    }

    private void bindUpdate(
            @NonNull CategoryCategoryOptionLinkModel oldCategoryCategoryOptionLinkModel,
            @NonNull CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel) {
        bind(updateStatement, newCategoryCategoryOptionLinkModel);

        sqLiteBind(updateStatement, 3, oldCategoryCategoryOptionLinkModel.category());
        sqLiteBind(updateStatement, 4, oldCategoryCategoryOptionLinkModel.categoryOption());
    }

    private int executeInsert() {
        int lastId = databaseAdapter.executeUpdateDelete(CategoryModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private int execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, statement);
        statement.clearBindings();

        return rowsAffected;
    }

    private List<CategoryCategoryOptionLinkModel> mapFromCursor(
            Cursor cursor) {
        List<CategoryCategoryOptionLinkModel> categoryCategoryOptionLinks = new ArrayList<>(
                cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryCategoryOptionLinkModel categoryCategoryOptionLink =
                            CategoryCategoryOptionLinkModel.create(cursor);

                    categoryCategoryOptionLinks.add(categoryCategoryOptionLink);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryCategoryOptionLinks;
    }

}