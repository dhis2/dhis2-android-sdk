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

import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorModelFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.statementwrapper.internal.SQLStatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.common.DeletableDataModel;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.common.BaseDataModel.Columns.STATE;
import static org.hisp.dhis.android.core.common.BaseDeletableDataModel.Columns.DELETED;
import static org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel.Columns.UID;

public class IdentifiableDeletableDataObjectStoreImpl<M extends ObjectWithUidInterface & DeletableDataModel>
        extends IdentifiableDataObjectStoreImpl<M> implements IdentifiableDeletableDataObjectStore<M> {

    private final static String EQ = " = ";

    private final SQLiteStatement setStateIfUploadingStatement;
    private final SQLiteStatement setDeletedStatement;

    public IdentifiableDeletableDataObjectStoreImpl(DatabaseAdapter databaseAdapter,
                                                    SQLStatementWrapper statements,
                                                    SQLStatementBuilder builder, StatementBinder<M> binder,
                                                    CursorModelFactory<M> modelFactory) {
        super(databaseAdapter, statements, builder, binder, modelFactory);
        String whereUid =  " WHERE " + UID + " =?";

        String setState = "UPDATE " + tableName + " SET " +
                STATE + " =?" + whereUid;

        String setStateIfUploading = setState + " AND " + STATE + EQ + "'" + State.UPLOADING + "'";
        this.setStateIfUploadingStatement = databaseAdapter.compileStatement(setStateIfUploading);

        String setDeleted = "UPDATE " + tableName + " SET " +
                DELETED + " = 1" + whereUid;
        this.setDeletedStatement = databaseAdapter.compileStatement(setDeleted);
    }

    @Override
    public HandleAction setStateOrDelete(@NonNull String uid, @NonNull State state) {
        boolean deleted = false;
        if (state == State.SYNCED) {
            String whereClause = new WhereClauseBuilder()
                    .appendKeyStringValue(UID, uid)
                    .appendKeyNumberValue(DELETED, 1)
                    .appendKeyStringValue(STATE, State.UPLOADING)
                    .build();

            deleted = deleteWhere(whereClause);
        }

        if (deleted) {
            return HandleAction.Delete;
        } else {
            setStateIfUploading(uid, state);
            return HandleAction.Update;
        }
    }

    private void setStateIfUploading(@NonNull String uid, @NonNull State state) {
        sqLiteBind(setStateIfUploadingStatement, 1, state);

        // bind the where argument
        sqLiteBind(setStateIfUploadingStatement, 2, uid);

        databaseAdapter.executeUpdateDelete(tableName, setStateIfUploadingStatement);
        setStateIfUploadingStatement.clearBindings();
    }

    @Override
    public int setDeleted(@NonNull String uid) {
        sqLiteBind(setDeletedStatement, 1, uid);

        int updatedRow = databaseAdapter.executeUpdateDelete(tableName, setDeletedStatement);
        setDeletedStatement.clearBindings();

        return updatedRow;
    }
}