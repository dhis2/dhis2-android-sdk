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
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.statementwrapper.internal.SQLStatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.common.DataModel;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;

import java.util.List;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.common.BaseDataModel.Columns.STATE;
import static org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel.Columns.UID;

public class IdentifiableDataObjectStoreImpl<M extends ObjectWithUidInterface & DataModel>
        extends IdentifiableObjectStoreImpl<M> implements IdentifiableDataObjectStore<M> {

    private final static String EQ = " = ";
    private final static String NE = " != ";
    private final static String AND = " AND ";

    private final String selectStateQuery;
    private final String existsQuery;
    private final SQLiteStatement setStateStatement;
    private final SQLiteStatement setStateForUpdateStatement;
    final String tableName;

    public IdentifiableDataObjectStoreImpl(DatabaseAdapter databaseAdapter,
                                           SQLStatementWrapper statements,
                                           SQLStatementBuilder builder, StatementBinder<M> binder,
                                           ObjectFactory<M> objectFactory) {
        super(databaseAdapter, statements, builder, binder, objectFactory);
        this.tableName = builder.getTableName();
        String whereUid =  " WHERE " + UID + " =?";

        String setState = "UPDATE " + tableName + " SET " +
                STATE + " =?" + whereUid;
        this.setStateStatement = databaseAdapter.compileStatement(setState);

        String setStateForUpdate = "UPDATE " + tableName + " SET " +
                STATE + " = (case " +
                    "when " + STATE + EQ + "'" + State.TO_POST + "' then '" + State.TO_POST + "' " +
                    "when " + STATE + NE + "'" + State.TO_POST + "'" + AND +
                        STATE + NE + "'" + State.RELATIONSHIP + "' then '" + State.TO_UPDATE + "'" +
                        " END)" +
                    " where "  +
                        UID + " =? ;";
        this.setStateForUpdateStatement = databaseAdapter.compileStatement(setStateForUpdate);

        this.selectStateQuery = "SELECT " + STATE + " FROM " + tableName + whereUid;
        this.existsQuery = "SELECT 1 FROM " + tableName + whereUid;

    }

    @Override
    public int setState(@NonNull String uid, @NonNull State state) {
        sqLiteBind(setStateStatement, 1, state);

        // bind the where argument
        sqLiteBind(setStateStatement, 2, uid);

        int updatedRow = databaseAdapter.executeUpdateDelete(tableName, setStateStatement);
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

        return databaseAdapter.database().update(tableName, updates, whereClause, null);
    }

    @Override
    public int setStateForUpdate(@NonNull String uid) {
        sqLiteBind(setStateForUpdateStatement, 1, uid);

        int updatedRow = databaseAdapter.executeUpdateDelete(tableName, setStateForUpdateStatement);
        setStateForUpdateStatement.clearBindings();

        return updatedRow;
    }

    @Override
    public State getState(@NonNull String uid) {
        Cursor cursor = databaseAdapter.query(selectStateQuery, uid);
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
        Cursor cursor = databaseAdapter.query(existsQuery, uid);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}