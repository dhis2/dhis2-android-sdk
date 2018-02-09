package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
})
public class CategoryOptionComboCategoryLinkStoreImpl extends Store implements
        CategoryOptionComboCategoryLinkStore {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryOptionComboCategoryLinkModel.TABLE + " (" +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + ", " +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + ") " +
                    "VALUES(?, ?);";

    private static final String REMOVE_CATEGORY_OPTION_RELATIONS = "DELETE FROM "
            + CategoryOptionComboCategoryLinkModel.TABLE + " WHERE "
            + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + "=?;";

    private static final String QUERY_BY_CATEGORY_OPTION_UID = "SELECT "
            + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + " FROM "
            + CategoryOptionComboCategoryLinkModel.TABLE + " WHERE "
            + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + "=?;";


    private static final String DELETE_STATEMENT =
            "DELETE FROM " + CategoryOptionComboCategoryLinkModel.TABLE +
                    " WHERE " + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO
                    + " =?" + " AND "
                    + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + "=?;";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + CategoryOptionComboCategoryLinkModel.TABLE + " SET " +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + " =?," +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + " =? " +
                    " WHERE " + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO
                    + " =? AND " +
                    CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + " =?;";

    private static final String FIELDS =
            CategoryOptionComboCategoryLinkModel.TABLE + "."
                    + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + "," +
                    CategoryOptionComboCategoryLinkModel.TABLE + "."
                    + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO;


    private static final String QUERY_ALL_CATEGORY_OPTION_COMBO_LINKS = "SELECT " +
            FIELDS + " FROM " + CategoryOptionComboCategoryLinkModel.TABLE;

    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement updateStatement;

    public CategoryOptionComboCategoryLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull CategoryOptionComboCategoryLinkModel entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryOptionComboCategoryLinkModel.TABLE);
    }

    @Override
    public int delete(@NonNull CategoryOptionComboCategoryLinkModel element) {
        validate(element);

        bind(deleteStatement, element);

        return execute(deleteStatement);
    }

    @Override
    public int update(
            @NonNull CategoryOptionComboCategoryLinkModel oldCategoryOptionComboCategoryLinkModel,
            @NonNull CategoryOptionComboCategoryLinkModel newCategoryOptionComboCategoryLinkModel) {

        validateForUpdate(oldCategoryOptionComboCategoryLinkModel,
                newCategoryOptionComboCategoryLinkModel);

        bindUpdate(oldCategoryOptionComboCategoryLinkModel,
                newCategoryOptionComboCategoryLinkModel);


        return execute(updateStatement);
    }

    @NonNull
    @Override
    public List<CategoryOptionComboCategoryLinkModel> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_OPTION_COMBO_LINKS);

        return mapFromCursor(cursor);
    }

    @Override
    public int removeCategoryComboOptionRelationsByCategoryOptionCombo(
            String categoryOptionComboUid) {
        Cursor cursor = databaseAdapter.query(REMOVE_CATEGORY_OPTION_RELATIONS,
                categoryOptionComboUid);

        return cursor.getCount();
    }

    @Override
    public List<String> queryByOptionComboUId(String uid) {
        List<String> categoryOptions = null;
        Cursor cursor = databaseAdapter.query(QUERY_BY_CATEGORY_OPTION_UID, uid);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                categoryOptions = listUidFromCursor(cursor);
            }
        } finally {
            cursor.close();
        }
        return categoryOptions;
    }

    private void validate(@NonNull CategoryOptionComboCategoryLinkModel link) {
        isNull(link.categoryOptionCombo());
        isNull(link.category());
    }

    private void bind(SQLiteStatement sqLiteStatement,
            @NonNull CategoryOptionComboCategoryLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.categoryOptionCombo());
        sqLiteBind(sqLiteStatement, 2, link.category());
    }

    private int executeInsert() {
        int lastId = (int) databaseAdapter.executeInsert(CategoryOptionComboCategoryLinkModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private int execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(CategoryComboModel.TABLE, statement);
        statement.clearBindings();

        return rowsAffected;
    }

    private List<CategoryOptionComboCategoryLinkModel> mapFromCursor(Cursor cursor) {
        List<CategoryOptionComboCategoryLinkModel> categoryOptionComboCategoryLinks =
                new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryOptionComboCategoryLinkModel categoryCategoryOptionLink =
                            CategoryOptionComboCategoryLinkModel.create(cursor);

                    categoryOptionComboCategoryLinks.add(categoryCategoryOptionLink);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryOptionComboCategoryLinks;
    }

    private void validateForUpdate(
            @NonNull CategoryOptionComboCategoryLinkModel oldCategoryOptionComboCategoryLinkModel,
            @NonNull CategoryOptionComboCategoryLinkModel newCategoryOptionComboCategoryLinkModel) {

        validate(oldCategoryOptionComboCategoryLinkModel);
        validate(newCategoryOptionComboCategoryLinkModel);
    }

    private void bindUpdate(
            @NonNull CategoryOptionComboCategoryLinkModel oldCategoryOptionComboCategoryLinkModel,
            @NonNull CategoryOptionComboCategoryLinkModel newCategoryOptionComboCategoryLinkModel) {
        bind(updateStatement, newCategoryOptionComboCategoryLinkModel);

        sqLiteBind(updateStatement, 3,
                oldCategoryOptionComboCategoryLinkModel.categoryOptionCombo());
        sqLiteBind(updateStatement, 4, oldCategoryOptionComboCategoryLinkModel.category());
    }

    private List<String> listUidFromCursor(Cursor cursor) {
        List<String> categoryOptions = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    categoryOptions.add(getStringFromCursor(cursor, 0));
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryOptions;
    }
}

