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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.implementations.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class ObjectWithoutUidStoreImpl<M extends Model>
        extends ObjectStoreImpl<M> implements ObjectWithoutUidStore<M> {
    private final SQLiteStatement updateWhereStatement;
    private final WhereStatementBinder<M> whereBinder;

    public ObjectWithoutUidStoreImpl(DatabaseAdapter databaseAdapter,
                                     SQLiteStatement insertStatement,
                                     SQLiteStatement updateWhereStatement,
                                     SQLStatementBuilder builder,
                                     StatementBinder<M> binder,
                                     WhereStatementBinder<M> whereBinder,
                                     CursorModelFactory<M> modelFactory) {
        super(databaseAdapter, insertStatement, builder, binder, modelFactory);
        this.updateWhereStatement = updateWhereStatement;
        this.whereBinder = whereBinder;
    }

    @Override
    public void updateWhere(@NonNull M m) throws RuntimeException {
        isNull(m);
        binder.bindToStatement(m, updateWhereStatement);
        whereBinder.bindToUpdateWhereStatement(m, updateWhereStatement);
        executeUpdateDelete(updateWhereStatement);
    }

    @Override
    public HandleAction updateOrInsertWhere(@NonNull M m) throws RuntimeException {
        try {
            updateWhere(m);
            return HandleAction.Update;
        } catch (Exception e){
            insert(m);
            return HandleAction.Insert;
        }
    }
}