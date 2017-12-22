package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryComboLinkStoreImpl implements CategoryComboLinkStore {
    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboLinkModel.TABLE + " (" +
                    CategoryComboLinkModel.Columns.CATEGORY + ", " +
                    CategoryComboLinkModel.Columns.COMBO + ") " +
                    "VALUES(?, ?);";

    private static final String FIELDS =
            CategoryComboLinkModel.TABLE + "." + CategoryComboLinkModel.Columns.CATEGORY + "," +
                    CategoryComboLinkModel.TABLE + "." + CategoryComboLinkModel.Columns.COMBO;

    private static final String QUERY_ALL_CATEGORY_COMBO_LINKS = "SELECT " +
            FIELDS + " FROM " + CategoryComboLinkModel.TABLE;

    public CategoryComboLinkStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);

    }

    @Override
    public long insert(CategoryComboLinkModel entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    private void validate(@NonNull CategoryComboLinkModel link) {
        isNull(link.category());
        isNull(link.combo());
    }

    private void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryComboLinkModel link) {
        sqLiteBind(sqLiteStatement, 1, link.category());
        sqLiteBind(sqLiteStatement, 2, link.combo());
    }

    private int executeInsert() {
        int lastId = databaseAdapter.executeUpdateDelete(CategoryComboLinkModel.TABLE,
                insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    @Override
    public List<CategoryComboLink> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_CATEGORY_COMBO_LINKS);

        return mapCategoryComboLinksFromCursor(cursor);
    }

    private List<CategoryComboLink> mapCategoryComboLinksFromCursor(Cursor cursor) {
        List<CategoryComboLink> categoryComboLinks = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    CategoryComboLink categoryComboLink = mapCategoryComboLinkFromCursor(cursor);

                    categoryComboLinks.add(categoryComboLink);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return categoryComboLinks;
    }

    private CategoryComboLink mapCategoryComboLinkFromCursor(Cursor cursor) {
        CategoryComboLink categoryComboLink;

        String category = cursor.getString(0);
        String combo = cursor.getString(1);

        categoryComboLink = CategoryComboLink.create(category, combo);

        return categoryComboLink;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(CategoryComboLinkModel.TABLE);
    }
}

