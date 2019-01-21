package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.binders.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.SQLStatementWrapper;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class CategoryOptionComboStoreImpl extends IdentifiableObjectStoreImpl<CategoryOptionCombo>
        implements CategoryOptionComboStore {

    private CategoryOptionComboStoreImpl(DatabaseAdapter databaseAdapter,
                         SQLStatementWrapper statementWrapper,
                         SQLStatementBuilder statementBuilder) {
        super(databaseAdapter, statementWrapper, statementBuilder, BINDER, FACTORY);
    }

    @Override
    public List<CategoryOptionCombo> getForCategoryCombo(String categoryComboUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(CategoryOptionComboFields.CATEGORY_COMBO, categoryComboUid)
                .build();
        return selectWhereClause(whereClause);
    }

    private static StatementBinder<CategoryOptionCombo> BINDER
            = new IdentifiableStatementBinder<CategoryOptionCombo>() {

        @Override
        public void bindToStatement(@NonNull CategoryOptionCombo o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, UidsHelper.getUidOrNull(o.categoryCombo()));
        }
    };

    private static final CursorModelFactory<CategoryOptionCombo> FACTORY
            = new CursorModelFactory<CategoryOptionCombo>() {
        @Override
        public CategoryOptionCombo fromCursor(Cursor cursor) {
            return CategoryOptionCombo.create(cursor);
        }
    };

    static CategoryOptionComboStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(CategoryOptionComboTableInfo.TABLE_INFO);
        SQLStatementWrapper statementWrapper = new SQLStatementWrapper(statementBuilder, databaseAdapter);
        return new CategoryOptionComboStoreImpl(databaseAdapter, statementWrapper, statementBuilder);
    }
}
