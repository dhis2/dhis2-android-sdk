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

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.common.DataObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;

import java.util.List;

import static org.hisp.dhis.android.core.common.DataColumns.STATE;
import static org.hisp.dhis.android.core.common.IdentifiableColumns.UID;

public class IdentifiableDataObjectStoreImpl<M extends ObjectWithUidInterface & DataObject>
        extends IdentifiableObjectStoreImpl<M> implements IdentifiableDataObjectStore<M> {

    private String selectStateQuery;
    private String existsQuery;
    private StatementWrapper setStateStatement;
    final String tableName;

    private Integer adapterHashCode;

    public IdentifiableDataObjectStoreImpl(DatabaseAdapter databaseAdapter,
                                           SQLStatementBuilder builder,
                                           StatementBinder<M> binder,
                                           ObjectFactory<M> objectFactory) {
        super(databaseAdapter, builder, binder, objectFactory);
        this.tableName = builder.getTableName();
    }

    private void compileStatements() {
        resetStatementsIfDbChanged();
        if (setStateStatement == null) {
            String whereUid = " WHERE " + UID + " =?";

            String setState = "UPDATE " + tableName + " SET " +
                    STATE + " =?" + whereUid;
            this.setStateStatement = databaseAdapter.compileStatement(setState);

            this.selectStateQuery = "SELECT " + STATE + " FROM " + tableName + whereUid;
            this.existsQuery = "SELECT 1 FROM " + tableName + whereUid;
        }
    }

    private boolean hasAdapterChanged() {
        Integer oldCode = adapterHashCode;
        adapterHashCode = databaseAdapter.hashCode();
        return oldCode != null && databaseAdapter.hashCode() != oldCode;
    }

    private void resetStatementsIfDbChanged() {
        if (hasAdapterChanged()) {
            setStateStatement.close();
            setStateStatement = null;
        }
    }

    @Override
    public int setState(@NonNull String uid, @NonNull State state) {
        compileStatements();
        setStateStatement.bind(1, state);

        // bind the where argument
        setStateStatement.bind(2, uid);

        int updatedRow = databaseAdapter.executeUpdateDelete(setStateStatement);
        setStateStatement.clearBindings();

        return updatedRow;
    }

    @Override
    public int setState(@NonNull List<String> uids, @NonNull State state) {
        ContentValues updates = new ContentValues();
        updates.put(STATE, state.toString());

        String whereClause = new WhereClauseBuilder()
                .appendInKeyStringValues(UID, uids)
                .build();

        return databaseAdapter.update(tableName, updates, whereClause, null);
    }

    @Override
    public State getState(@NonNull String uid) {
        compileStatements();
        Cursor cursor = databaseAdapter.rawQuery(selectStateQuery, uid);
        State state = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            state = cursor.getString(0) == null ? null :
                    State.valueOf(State.class, cursor.getString(0));
        }
        cursor.close();
        return state;
    }

    @Override
    public Boolean exists(@NonNull String uid) {
        compileStatements();
        Cursor cursor = databaseAdapter.rawQuery(existsQuery, uid);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}