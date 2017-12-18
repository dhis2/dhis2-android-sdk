package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;


abstract class BaseStore<E extends BaseIdentifiableObject> implements Store<E> {


    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLiteStatement updateStatement;
    protected final SQLiteStatement deleteStatement;
    private final String tableName;


    BaseStore(DatabaseAdapter databaseAdapter,
            SQLiteStatement insertStatement, SQLiteStatement updateStatement,
            SQLiteStatement deleteStatement, String tableName) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = insertStatement;
        this.updateStatement = updateStatement;
        this.deleteStatement = deleteStatement;
        this.tableName = tableName;
    }

    @Override
    public long insert(E entity) {

        validate(entity);

        bind(insertStatement, entity);

        return executeInsert();
    }


    @Override
    public boolean delete(E entity) {

        validate(entity);

        bindForDelete(entity);

        return execute(deleteStatement);
    }

    private boolean wasExecuted(int numberOfRows) {
        return numberOfRows >= 1;
    }

    @Override
    public boolean update(@NonNull E oldEntity,
            @NonNull E newEntity) {

        validateForUpdate(oldEntity, newEntity);

        bindUpdate(oldEntity, newEntity);

        return execute(updateStatement);
    }

    private boolean execute(SQLiteStatement statement) {
        int rowsAffected = databaseAdapter.executeUpdateDelete(tableName, statement);
        statement.clearBindings();

        return wasExecuted(rowsAffected);
    }

    private long executeInsert() {
        long lastId = databaseAdapter.executeInsert(tableName, insertStatement);
        insertStatement.clearBindings();

        return lastId;
    }

    private void validateForUpdate(@NonNull E oldEntity,
            @NonNull E newEntity) {
        isNull(oldEntity.uid());
        isNull(newEntity.uid());
    }

    public abstract void validate(@NonNull E entity);

    public abstract void bindForDelete(@NonNull E entity);

    public abstract void bindUpdate(@NonNull E oldEntity,
            @NonNull E newEntity);

    public abstract void bind(SQLiteStatement sqLiteStatement, @NonNull E entity);
}
