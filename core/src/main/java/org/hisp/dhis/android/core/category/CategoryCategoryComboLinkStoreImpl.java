package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
})
public class CategoryCategoryComboLinkStoreImpl implements CategoryCategoryComboLinkStore {
    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement updateStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryCategoryComboLinkModel.TABLE + " (" +
                    CategoryCategoryComboLinkModel.Columns.CATEGORY + ", " +
                    CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + ") " +
                    "VALUES(?, ?);";

    private static final String DELETE_STATEMENT =
            "DELETE FROM " + CategoryCategoryComboLinkModel.TABLE +
                    " WHERE " + CategoryCategoryComboLinkModel.Columns.CATEGORY + " =?" + " AND "
                    + CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + "=?;";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + CategoryCategoryComboLinkModel.TABLE + " SET " +
                    CategoryCategoryComboLinkModel.Columns.CATEGORY + " =?, " +
                    CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + " =?" +
                    " WHERE " + CategoryCategoryComboLinkModel.Columns.CATEGORY + " =? AND " +
                    CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + " =?;";

    private static final String FIELDS =
            CategoryCategoryComboLinkModel.TABLE + "."
                    + CategoryCategoryComboLinkModel.Columns.CATEGORY + "," +
                    CategoryCategoryComboLinkModel.TABLE + "."
                    + CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO;

    private static final String QUERY_CATEGORY_COMBO_LINKS_BY_CATEGORY_COMBO = "SELECT " + FIELDS + " FROM "
            + CategoryCategoryComboLinkModel.TABLE
            + " WHERE "+ CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO+"=?;";

    private static final String QUERY_DELETE_CATEGORY_COMBO_RELATIONS = "DELETE FROM "
            + CategoryCategoryComboLinkModel.TABLE
            + " WHERE "+ CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO+"=?;";

    private static final String QUERY_ALL_CATEGORY_COMBO_LINKS = "SELECT " +
            FIELDS + " FROM " + CategoryCategoryComboLinkModel.TABLE;

    public CategoryCategoryComboLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);

    }

    @Override
    public long insert(CategoryCategoryComboLinkModel categoryCategoryComboLinkModel) {

        validate(categoryCategoryComboLinkModel);

        bind(insertStatement, categoryCategoryComboLinkModel);

        return executeInsert();
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryCategoryComboLinkModel.TABLE);
    }

    @Override
    public int delete(@NonNull CategoryCategoryComboLinkModel entity) {

        validate(entity);

        bind(deleteStatement, entity);

        return execute(deleteStatement);
    }

    @Override
    public int update(@NonNull CategoryCategoryComboLinkModel oldCategoryCategoryComboLinkMode,
            @NonNull CategoryCategoryComboLinkModel newCategoryCategoryComboLinkMode) {

        validateForUpdate(oldCategoryCategoryComboLinkMode, newCategoryCategoryComboLinkMode);

        bindUpdate(oldCategoryCategoryComboLinkMode, newCategoryCategoryComboLinkMode);

        return execute(updateStatement);
    }

    @Override
    public List<CategoryCategoryComboLink> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_COMBO_LINKS);

        return mapFromCursor(cursor);
    }

    private void validate(@NonNull CategoryCategoryComboLinkModel link) {
        isNull(link.category());
        isNull(link.categoryCombo());
    }

    private void bind(SQLiteStatement sqLiteStatement,
            @NonNull CategoryCategoryComboLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.categoryCombo());
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(CategoryCategoryComboLinkModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    @Override
    public List<CategoryCategoryComboLink> queryByCategoryComboUId(String categoryComboUId) {
        Cursor cursor = databaseAdapter.query(QUERY_CATEGORY_COMBO_LINKS_BY_CATEGORY_COMBO, categoryComboUId);

        return mapFromCursor(cursor);
    }

    @Override
    public int removeCategoryComboRelations(String categoryComboUId) {

        isNull(categoryComboUId);

        Cursor cursor = databaseAdapter.query(QUERY_DELETE_CATEGORY_COMBO_RELATIONS, categoryComboUId);

        return cursor.getCount();
    }


    private List<CategoryCategoryComboLink> mapFromCursor(Cursor cursor) {
        List<CategoryCategoryComboLink> categoryCategoryComboLinks = new ArrayList<>(
                cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryCategoryComboLink categoryCategoryComboLink =
                            mapCategoryCategoryComboLinkFromCursor(cursor);

                    categoryCategoryComboLinks.add(categoryCategoryComboLink);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryCategoryComboLinks;
    }

    private CategoryCategoryComboLink mapCategoryCategoryComboLinkFromCursor(Cursor cursor) {
        CategoryCategoryComboLink categoryCategoryComboLink;

        String category = cursor.getString(0);
        String combo = cursor.getString(1);

        categoryCategoryComboLink = CategoryCategoryComboLink.create(category, combo);

        return categoryCategoryComboLink;
    }

    private int execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, statement);
        statement.clearBindings();

        return rowsAffected;
    }

    private void validateForUpdate(
            @NonNull CategoryCategoryComboLinkModel oldCategoryCategoryComboLinkMode,
            @NonNull CategoryCategoryComboLinkModel newCategoryCategoryComboLinkMode) {

        validate(oldCategoryCategoryComboLinkMode);
        validate(newCategoryCategoryComboLinkMode);
    }


    private void bindUpdate(
            @NonNull CategoryCategoryComboLinkModel oldCategoryCategoryComboLinkMode,
            @NonNull CategoryCategoryComboLinkModel newCategoryCategoryComboLinkMode) {
        bind(updateStatement, newCategoryCategoryComboLinkMode);

        sqLiteBind(updateStatement, 3, oldCategoryCategoryComboLinkMode.category());
        sqLiteBind(updateStatement, 4, oldCategoryCategoryComboLinkMode.categoryCombo());
    }
}
