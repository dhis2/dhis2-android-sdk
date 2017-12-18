package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


public class CategoryComboStoreImpl extends BaseStore<CategoryCombo> {

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + CategoryComboModel.TABLE + " (" +
                    CategoryComboModel.Columns.UID + ", " +
                    CategoryComboModel.Columns.CODE + ", " +
                    CategoryComboModel.Columns.NAME + ", " +
                    CategoryComboModel.Columns.DISPLAY_NAME + ", " +
                    CategoryComboModel.Columns.CREATED + ", " +
                    CategoryComboModel.Columns.LAST_UPDATED + ", " +
                    CategoryComboModel.Columns.IS_DEFAULT + ") " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

    private static final String DELETE_STATEMENT = "DELETE FROM " + CategoryComboModel.TABLE +
            " WHERE " + CategoryComboModel.Columns.UID + " =?;";

    private static final String EQUAL_QUESTION_MARK = "=?,";

    private static final String UPDATE_STATEMENT = "UPDATE " + CategoryComboModel.TABLE + " SET " +
            CategoryComboModel.Columns.UID + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.CODE + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.NAME + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.DISPLAY_NAME + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.CREATED + " " + EQUAL_QUESTION_MARK + " " +
            CategoryComboModel.Columns.IS_DEFAULT + " =? WHERE " +
            CategoryComboModel.Columns.UID + " =?;";

    public CategoryComboStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter,
                databaseAdapter.compileStatement(INSERT_STATEMENT),
                databaseAdapter.compileStatement(UPDATE_STATEMENT),
                databaseAdapter.compileStatement(DELETE_STATEMENT),
                CategoryComboModel.TABLE);
    }


    @Override
    public void validate(@NonNull CategoryCombo category) {
        isNull(category.uid());
    }

    @Override
    public void bindForDelete(@NonNull CategoryCombo combo) {
        final int uidIndex = 1;

        sqLiteBind(deleteStatement, uidIndex, combo.uid());
    }

    @Override
    public void bindUpdate(@NonNull CategoryCombo oldCombo, @NonNull CategoryCombo newCombo) {
        final int whereUidIndex = 7;
        bind(updateStatement, newCombo);

        sqLiteBind(updateStatement, whereUidIndex, oldCombo.uid());
    }

    @Override
    public void bind(SQLiteStatement sqLiteStatement, @NonNull CategoryCombo combo) {
        sqLiteBind(sqLiteStatement, 1, combo.uid());
        sqLiteBind(sqLiteStatement, 2, combo.code());
        sqLiteBind(sqLiteStatement, 3, combo.name());
        sqLiteBind(sqLiteStatement, 4, combo.displayName());
        sqLiteBind(sqLiteStatement, 5, combo.created());
        sqLiteBind(sqLiteStatement, 6, combo.lastUpdated());
        sqLiteBind(sqLiteStatement, 7, fromBooleanToInt(combo));
    }


    @SuppressWarnings("ConstantConditions")
    private int fromBooleanToInt(@NonNull CategoryCombo combo) {
        if (combo == null) {
            return 0;
        }
        if (combo.isDefault() == null) {
            return 0;
        }

        return combo.isDefault() ? 1 : 0;
    }

}

