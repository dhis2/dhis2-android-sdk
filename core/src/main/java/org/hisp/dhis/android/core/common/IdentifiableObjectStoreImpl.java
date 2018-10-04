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
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Set;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class IdentifiableObjectStoreImpl<M extends Model & ObjectWithUidInterface>
        extends ObjectStoreImpl<M> implements IdentifiableObjectStore<M> {

    private final SQLStatementWrapper statements;

    public IdentifiableObjectStoreImpl(DatabaseAdapter databaseAdapter,
                                       SQLStatementWrapper statements,
                                       SQLStatementBuilder builder, StatementBinder<M> binder,
                                       CursorModelFactory<M> modelFactory) {
        super(databaseAdapter, statements.insert, builder, binder, modelFactory);
        this.statements = statements;
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
        sqLiteBind(statements.deleteById, 1, uid);
        executeUpdateDelete(statements.deleteById);
    }

    @Override
    public final void deleteIfExists(@NonNull String uid) throws RuntimeException {
        try {
            delete(uid);
        } catch(RuntimeException e) {
            if(!e.getMessage().equals("No rows affected")) {
                throw e;
            }
        }
    }

    @Override
    public final void update(@NonNull M m) throws RuntimeException {
        isNull(m);
        binder.bindToStatement(m, statements.update);
        sqLiteBind(statements.update, builder.columns.length + 1, m.uid());
        executeUpdateDelete(statements.update);
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
    public Set<String> selectUids() throws RuntimeException {
        Cursor cursor = databaseAdapter.query(statements.selectUids);
        return mapStringColumnSetFromCursor(cursor);
    }

    public Set<String> selectUidsWhere(String whereClause) throws RuntimeException {
        Cursor cursor = databaseAdapter.query(builder.selectUidsWhere(whereClause));
        return mapStringColumnSetFromCursor(cursor);
    }

    @Override
    public M selectByUid(String uid) throws RuntimeException {
        Cursor cursor = databaseAdapter.query(builder.selectByUid(), uid);
        return mapObjectFromCursor(cursor);
    }

    private M mapObjectFromCursor(Cursor cursor) {
        M object = null;

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                object = modelFactory.fromCursor(cursor);
            }
        } finally {
            cursor.close();
        }
        return object;
    }
}


