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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.CoreObject;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.isNull;

public class ObjectWithoutUidStoreImpl<M extends CoreObject>
        extends ObjectStoreImpl<M> implements ObjectWithoutUidStore<M> {
    private StatementWrapper updateWhereStatement;
    private StatementWrapper deleteWhereStatement;
    private final WhereStatementBinder<M> whereUpdateBinder;
    private final WhereStatementBinder<M> whereDeleteBinder;

    private Integer adapterHashCode;

    public ObjectWithoutUidStoreImpl(DatabaseAdapter databaseAdapter,
                                     SQLStatementBuilder builder,
                                     StatementBinder<M> binder,
                                     WhereStatementBinder<M> whereUpdateBinder,
                                     WhereStatementBinder<M> whereDeleteBinder,
                                     ObjectFactory<M> objectFactory) {
        super(databaseAdapter, builder, binder, objectFactory);
        this.whereUpdateBinder = whereUpdateBinder;
        this.whereDeleteBinder = whereDeleteBinder;
    }

    @Override
    public void updateWhere(@NonNull M m) throws RuntimeException {
        isNull(m);
        compileStatements();
        binder.bindToStatement(m, updateWhereStatement);
        whereUpdateBinder.bindWhereStatement(m, updateWhereStatement);
        executeUpdateDelete(updateWhereStatement);
    }

    private void compileStatements() {
        resetStatementsIfDbChanged();
        if (updateWhereStatement == null) {
            updateWhereStatement = databaseAdapter.compileStatement(builder.updateWhere());
            deleteWhereStatement = databaseAdapter.compileStatement(builder.deleteWhere());
        }
    }

    private boolean hasAdapterChanged() {
        Integer oldCode = adapterHashCode;
        adapterHashCode = databaseAdapter.hashCode();
        return oldCode != null && databaseAdapter.hashCode() != oldCode;
    }

    private void resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            updateWhereStatement.close();
            deleteWhereStatement.close();
            updateWhereStatement = null;
            deleteWhereStatement = null;
        }
    }

    @Override
    public void deleteWhere(@NonNull M m) throws RuntimeException {
        isNull(m);
        compileStatements();
        whereDeleteBinder.bindWhereStatement(m, deleteWhereStatement);
        executeUpdateDelete(deleteWhereStatement);
    }

    @Override
    public void deleteWhereIfExists(@NonNull M m) throws RuntimeException {
        try {
            deleteWhere(m);
        } catch(RuntimeException e) {
            if (!e.getMessage().equals("No rows affected")) {
                throw e;
            }
        }
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