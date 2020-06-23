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
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.List;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isNull;

public class IdentifiableObjectStoreImpl<M extends CoreObject & ObjectWithUidInterface>
        extends ObjectStoreImpl<M> implements IdentifiableObjectStore<M> {

    private StatementWrapper updateStatement;
    private StatementWrapper deleteStatement;

    private Integer adapterHashCode;

    public IdentifiableObjectStoreImpl(DatabaseAdapter databaseAdapter,
                                       SQLStatementBuilder builder, StatementBinder<M> binder,
                                       ObjectFactory<M> objectFactory) {
        super(databaseAdapter, builder, binder, objectFactory);
    }

    @Override
    public final long insert(@NonNull M m) throws RuntimeException {
        isNull(m);
        isNull(m.uid());
        return super.insert(m);
    }

    @Override
    public final void delete(@NonNull String uid) throws RuntimeException {
        isNull(uid);
        compileStatements();
        deleteStatement.bind(1, uid);
        executeUpdateDelete(deleteStatement);
    }

    private void compileStatements() {
        resetStatementsIfDbChanged();
        if (deleteStatement == null) {
            deleteStatement = databaseAdapter.compileStatement(builder.deleteById());
            updateStatement = databaseAdapter.compileStatement(builder.update());

        }
    }

    private boolean hasAdapterChanged() {
        Integer oldCode = adapterHashCode;
        adapterHashCode = databaseAdapter.hashCode();
        return oldCode != null && databaseAdapter.hashCode() != oldCode;
    }

    private void resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            updateStatement.close();
            deleteStatement.close();
            updateStatement = null;
            deleteStatement = null;
        }
    }

    @Override
    public final void deleteIfExists(@NonNull String uid) throws RuntimeException {
        try {
            delete(uid);
        } catch(RuntimeException e) {
            if (!e.getMessage().equals("No rows affected")) {
                throw e;
            }
        }
    }

    @Override
    public final void update(@NonNull M m) throws RuntimeException {
        isNull(m);
        compileStatements();
        binder.bindToStatement(m, updateStatement);
        updateStatement.bind(builder.getColumns().length + 1, m.uid());
        executeUpdateDelete(updateStatement);
    }

    @Override
    public final HandleAction updateOrInsert(@NonNull M m) throws RuntimeException {
        try {
            update(m);
            return HandleAction.Update;
        } catch (Exception e){
            insert(m);
            return HandleAction.Insert;
        }
    }

    @Override
    public List<String> selectUids() throws RuntimeException {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectUids());
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public List<String> selectUidsWhere(String whereClause) throws RuntimeException {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectUidsWhere(whereClause));
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public List<String> selectUidsWhere(String whereClause, String orderByClause) throws RuntimeException {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectUidsWhere(whereClause, orderByClause));
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public M selectByUid(String uid) throws RuntimeException {
        Cursor cursor = databaseAdapter.rawQuery(builder.selectByUid(), uid);
        return mapObjectFromCursor(cursor);
    }

    private M mapObjectFromCursor(Cursor cursor) {
        M object = null;

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                object = objectFactory.fromCursor(cursor);
            }
        } finally {
            cursor.close();
        }
        return object;
    }
}


