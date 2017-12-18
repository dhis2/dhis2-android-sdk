package org.hisp.dhis.android.core.category;


import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


abstract class BaseLinkStore<E extends BaseModel> implements Store<E> {

    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    private final String tableName;


    BaseLinkStore(DatabaseAdapter databaseAdapter,
            SQLiteStatement insertStatement, String tableName) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = insertStatement;
        this.tableName = tableName;
    }

    @Override
    public long insert(E entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }

    private int executeInsert() {
        int lastId = databaseAdapter.executeUpdateDelete(tableName, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    public abstract void validate(@NonNull E entity);

    public abstract void bind(SQLiteStatement sqLiteStatement, @NonNull E entity);
}
