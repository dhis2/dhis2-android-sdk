package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class CategoryComboLinkStoreImpl implements CategoryComboLinkStore {
    private final DatabaseAdapter databaseAdapter;
    private final SQLiteStatement insertStatement;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboLinkModel.TABLE + " (" +
                    CategoryComboLinkModel.Columns.CATEGORY + ", " +
                    CategoryComboLinkModel.Columns.COMBO + ") " +
                    "VALUES(?, ?);";

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
    public int delete() {
        return databaseAdapter.delete(CategoryComboLinkModel.TABLE);
    }
}

