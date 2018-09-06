/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class ObjectStoreImpl<M extends Model> implements ObjectStore<M> {
    protected final DatabaseAdapter databaseAdapter;
    protected final SQLiteStatement insertStatement;
    protected final SQLStatementBuilder builder;
    final StatementBinder<M> binder;

    ObjectStoreImpl(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement, SQLStatementBuilder builder,
                    StatementBinder<M> binder) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = insertStatement;
        this.builder = builder;
        this.binder = binder;
    }

    @Override
    public long insert(@NonNull M m) throws RuntimeException {
        isNull(m);
        binder.bindToStatement(m, insertStatement);
        Long insertedRowId = databaseAdapter.executeInsert(builder.tableName, insertStatement);
        insertStatement.clearBindings();
        if (insertedRowId == -1) {
            throw new RuntimeException("Nothing was inserted.");
        }
        return insertedRowId;
    }

    @Override
    public final int delete() {
        return databaseAdapter.delete(builder.tableName);
    }

    protected void executeUpdateDelete(SQLiteStatement statement) throws RuntimeException {
        int numberOfAffectedRows = databaseAdapter.executeUpdateDelete(builder.tableName, statement);
        statement.clearBindings();

        if (numberOfAffectedRows == 0) {
            throw new RuntimeException("No rows affected");
        } else if (numberOfAffectedRows > 1) {
            throw new RuntimeException("Unexpected number of affected rows: " + numberOfAffectedRows);
        }
    }

    private void addAll(@NonNull CursorModelFactory<M> modelFactory,
                       @NonNull Collection<M> collection) {
        Cursor cursor = databaseAdapter.query(builder.selectAll());
        addObjectsToCollection(cursor, modelFactory, collection);
    }

    @Override
    public Set<M> selectAll(@NonNull CursorModelFactory<M> modelFactory) {
        Set<M> set = new HashSet<>();
        addAll(modelFactory, set);
        return set;
    }

    @Override
    public M selectFirst(@NonNull CursorModelFactory<M> modelFactory) {
        Cursor cursor = databaseAdapter.query(builder.selectAll());
        return getFirstFromCursor(cursor, modelFactory);
    }

    @Override
    public boolean deleteById(@NonNull M m) {
        return deleteWhereClause(BaseModel.Columns.ID + "='" + m.id() + "';");
    }

    private M selectOneWhere(@NonNull CursorModelFactory<M> modelFactory,
                               @NonNull String whereClause) {
        Cursor cursor = databaseAdapter.query(builder.selectWhereWithLimit(whereClause, 1));
        return getFirstFromCursor(cursor, modelFactory);
    }

    private M getFirstFromCursor(@NonNull Cursor cursor, @NonNull CursorModelFactory<M> modelFactory) {
        try {
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                return modelFactory.fromCursor(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    protected M popOneWhere(@NonNull CursorModelFactory<M> modelFactory,
                            @NonNull String whereClause) {
        M m = selectOneWhere(modelFactory, whereClause);
        if (m != null) {
            deleteById(m);
        }
        return m;
    }

    protected int countWhere(@NonNull String whereClause) {
        Cursor cursor = databaseAdapter.query(builder.countWhere(whereClause));
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    private void addObjectsToCollection(Cursor cursor, CursorModelFactory<M> modelFactory,
                                            Collection<M> collection) {
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    collection.add(modelFactory.fromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    protected boolean deleteWhereClause(String clause) {
        return databaseAdapter.database().delete(builder.tableName, clause, null) > 0;
    }

    @Override
    public Set<String> selectStringColumnsWhereClause(String column, String clause) {
        Cursor cursor = databaseAdapter.query(builder.selectColumnWhere(column, clause));
        return mapStringColumnSetFromCursor(cursor);
    }

    Set<String> mapStringColumnSetFromCursor(Cursor cursor) {
        Set<String> columns = new HashSet<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    columns.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return columns;
    }
}