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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isNull;

public class ObjectStoreImpl<M extends CoreObject> extends ReadableStoreImpl<M> implements ObjectStore<M> {
    private StatementWrapper insertStatement;
    protected final SQLStatementBuilder builder;
    protected final StatementBinder<M> binder;
    private Integer adapterHashCode;

    ObjectStoreImpl(DatabaseAdapter databaseAdapter, SQLStatementBuilder builder, StatementBinder<M> binder,
                    ObjectFactory<M> objectFactory) {
        super(databaseAdapter, builder, objectFactory);
        this.builder = builder;
        this.binder = binder;
    }

    @Override
    public long insert(@NonNull M m) throws RuntimeException {
        isNull(m);
        compileStatements();
        binder.bindToStatement(m, insertStatement);
        long insertedRowId = databaseAdapter.executeInsert(insertStatement);
        insertStatement.clearBindings();
        if (insertedRowId == -1) {
            throw new RuntimeException("Nothing was inserted.");
        }
        return insertedRowId;
    }

    @Override
    public void insert(@NonNull Collection<M> objects) throws RuntimeException {
        for (M m : objects) {
            insert(m);
        }
    }

    private void compileStatements() {
        resetStatementsIfDbChanged();
        if (insertStatement == null) {
            insertStatement = databaseAdapter.compileStatement(builder.insert());
        }
    }

    private void resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            insertStatement.close();
            insertStatement = null;
        }
    }

    private boolean hasAdapterChanged() {
        Integer oldCode = adapterHashCode;
        adapterHashCode = databaseAdapter.hashCode();
        return oldCode != null && databaseAdapter.hashCode() != oldCode;
    }

    @Override
    public List<String> selectStringColumnsWhereClause(String column, String clause) {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectColumnWhere(column, clause));
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public final int delete() {
        return databaseAdapter.delete(builder.getTableName());
    }

    void executeUpdateDelete(StatementWrapper statement) throws RuntimeException {
        int numberOfAffectedRows = databaseAdapter.executeUpdateDelete(statement);
        statement.clearBindings();

        if (numberOfAffectedRows == 0) {
            throw new RuntimeException("No rows affected");
        } else if (numberOfAffectedRows > 1) {
            throw new RuntimeException("Unexpected number of affected rows: " + numberOfAffectedRows);
        }
    }


    @Override
    public boolean deleteById(@NonNull M m) {
        return deleteWhere(CoreColumns.ID + "='" + m.id() + "';");
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
        return databaseAdapter.delete(builder.getTableName(), clause, null) > 0;
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

    @Override
    public boolean isReady() {
        return databaseAdapter.isReady();
    }
}