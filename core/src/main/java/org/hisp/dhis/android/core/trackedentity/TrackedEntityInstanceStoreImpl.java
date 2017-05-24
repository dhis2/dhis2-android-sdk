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

package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

class TrackedEntityInstanceStoreImpl implements TrackedEntityInstanceStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityInstanceModel.TABLE + " (" +
            TrackedEntityInstanceModel.Columns.UID + ", " +
            TrackedEntityInstanceModel.Columns.CREATED + ", " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityInstanceModel.Columns.CREATED_AT_CLIENT + ", " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED_AT_CLIENT + ", " +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + ", " +
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY + ", " +
            TrackedEntityInstanceModel.Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + TrackedEntityInstanceModel.TABLE + " SET " +
            TrackedEntityInstanceModel.Columns.UID + " =?, " +
            TrackedEntityInstanceModel.Columns.CREATED + " =?, " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + " =?, " +
            TrackedEntityInstanceModel.Columns.CREATED_AT_CLIENT + " =? , " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + " =?, " +
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY + " =?, " +
            TrackedEntityInstanceModel.Columns.STATE + " =? " +
            " WHERE " +
            TrackedEntityInstanceModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            TrackedEntityInstanceModel.TABLE +
            " WHERE " +
            TrackedEntityInstanceModel.Columns.UID + " =?;";

    private final SQLiteStatement insertRowStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final DatabaseAdapter databaseAdapter;

    TrackedEntityInstanceStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                       @NonNull String organisationUnit, @NonNull String trackedEntity, @Nullable State state) {
        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, created);
        sqLiteBind(insertRowStatement, 3, lastUpdated);
        sqLiteBind(insertRowStatement, 4, createdAtClient);
        sqLiteBind(insertRowStatement, 5, lastUpdatedAtClient);
        sqLiteBind(insertRowStatement, 6, organisationUnit);
        sqLiteBind(insertRowStatement, 7, trackedEntity);
        sqLiteBind(insertRowStatement, 8, state);

        return databaseAdapter.executeInsert(TrackedEntityInstanceModel.TABLE, insertRowStatement);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityInstanceModel.TABLE);
    }

    @Override
    public int update(@NonNull String uid, @NonNull Date created, @NonNull Date lastUpdated,
                      @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                      @NonNull String organisationUnit, @NonNull String trackedEntity,
                      @NonNull State state, @NonNull String whereTrackedEntityInstanceUid) {
        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, created);
        sqLiteBind(updateStatement, 3, lastUpdated);
        sqLiteBind(updateStatement, 4, createdAtClient);
        sqLiteBind(updateStatement, 5, lastUpdatedAtClient);
        sqLiteBind(updateStatement, 6, organisationUnit);
        sqLiteBind(updateStatement, 7, trackedEntity);
        sqLiteBind(updateStatement, 8, state);

        // bind the where clause
        sqLiteBind(updateStatement, 9, whereTrackedEntityInstanceUid);

        int rowId = databaseAdapter.executeUpdateDelete(TrackedEntityInstanceModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return rowId;
    }

    @Override
    public int delete(@NonNull String uid) {
        deleteStatement.clearBindings();
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }
}
