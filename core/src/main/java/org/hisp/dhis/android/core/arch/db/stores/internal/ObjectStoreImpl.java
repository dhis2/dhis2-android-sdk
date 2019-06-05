/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.arch.db.stores.internal;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorModelFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class ObjectStoreImpl<M extends Model> extends ReadableStoreImpl<M> implements ObjectStore<M> {
    private final SQLiteStatement insertStatement;
    protected final SQLStatementBuilder builder;
    protected final StatementBinder<M> binder;

    public ObjectStoreImpl(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement,
                           SQLStatementBuilder builder, StatementBinder<M> binder, CursorModelFactory<M> modelFactory) {
        super(databaseAdapter, builder, modelFactory);
        this.insertStatement = insertStatement;
        this.builder = builder;
        this.binder = binder;
    }

    @Override
    public long insert(@NonNull M m) throws RuntimeException {
        isNull(m);
        binder.bindToStatement(m, insertStatement);
        Long insertedRowId = databaseAdapter.executeInsert(builder.getTableName(), insertStatement);
        insertStatement.clearBindings();
        if (insertedRowId == -1) {
            throw new RuntimeException("Nothing was inserted.");
        }
        return insertedRowId;
    }

    @Override
    public List<String> selectStringColumnsWhereClause(String column, String clause) {
        Cursor cursor = databaseAdapter.query(builder.selectColumnWhere(column, clause));
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public final int delete() {
        return databaseAdapter.delete(builder.getTableName());
    }

    void executeUpdateDelete(SQLiteStatement statement) throws RuntimeException {
        int numberOfAffectedRows = databaseAdapter.executeUpdateDelete(builder.getTableName(), statement);
        statement.clearBindings();

        if (numberOfAffectedRows == 0) {
            throw new RuntimeException("No rows affected");
        } else if (numberOfAffectedRows > 1) {
            throw new RuntimeException("Unexpected number of affected rows: " + numberOfAffectedRows);
        }
    }


    @Override
    public boolean deleteById(@NonNull M m) {
        return deleteWhere(BaseModel.Columns.ID + "='" + m.id() + "';");
    }

    protected M popOneWhere(@NonNull String whereClause) {
        M m = selectOneWhere(whereClause);
        if (m != null) {
            deleteById(m);
        }
        return m;
    }

    @Override
    public boolean deleteWhere(String clause) {
        return databaseAdapter.database().delete(builder.getTableName(), clause, null) > 0;
    }

    @Override
    public void deleteWhereIfExists(@NonNull String whereClause) throws RuntimeException {
        try {
            deleteWhere(whereClause);
        } catch(RuntimeException e) {
            if (!e.getMessage().equals("No rows affected")) {
                throw e;
            }
        }
    }
}