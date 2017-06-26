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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel.Columns;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity"
})
public class TrackedEntityInstanceStoreImpl implements TrackedEntityInstanceStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityInstanceModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.CREATED_AT_CLIENT + ", " +
            Columns.LAST_UPDATED_AT_CLIENT + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.TRACKED_ENTITY + ", " +
            Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + TrackedEntityInstanceModel.TABLE + " SET " +
            Columns.UID + " =?, " +
            Columns.CREATED + " =?, " +
            Columns.LAST_UPDATED + " =?, " +
            Columns.CREATED_AT_CLIENT + " =? , " +
            Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.TRACKED_ENTITY + " =?, " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String SET_STATE_STATEMENT = "UPDATE " + TrackedEntityInstanceModel.TABLE + " SET " +
            Columns.STATE + " =?" +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            TrackedEntityInstanceModel.TABLE +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String QUERY_STATEMENT = "SELECT " +
            "  TrackedEntityInstance.uid, " +
            "  TrackedEntityInstance.created, " +
            "  TrackedEntityInstance.lastUpdated, " +
            "  TrackedEntityInstance.createdAtClient, " +
            "  TrackedEntityInstance.lastUpdatedAtClient, " +
            "  TrackedEntityInstance.organisationUnit, " +
            "  TrackedEntityInstance.trackedEntity " +
            "FROM TrackedEntityInstance " +
            "WHERE state = 'TO_POST' OR state = 'TO_UPDATE'";


    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement setStateStatement;

    private final SQLiteStatement insertStatement;

    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityInstanceStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.setStateStatement = databaseAdapter.compileStatement(SET_STATE_STATEMENT);
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                       @NonNull String organisationUnit, @NonNull String trackedEntity, @Nullable State state) {

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, lastUpdated);
        sqLiteBind(insertStatement, 4, createdAtClient);
        sqLiteBind(insertStatement, 5, lastUpdatedAtClient);
        sqLiteBind(insertStatement, 6, organisationUnit);
        sqLiteBind(insertStatement, 7, trackedEntity);
        sqLiteBind(insertStatement, 8, state);

        long returnValue = databaseAdapter.executeInsert(TrackedEntityInstanceModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return returnValue;
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
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }

    @Override
    public int setState(@NonNull String uid, @NonNull State state) {
        sqLiteBind(setStateStatement, 1, state);

        // bind the where argument
        sqLiteBind(setStateStatement, 2, uid);

        int updatedRow = databaseAdapter.executeUpdateDelete(TrackedEntityInstanceModel.TABLE, setStateStatement);
        setStateStatement.clearBindings();

        return updatedRow;
    }

    @Override
    public Map<String, TrackedEntityInstance> query() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);
        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(cursor.getString(2));
                    String createdAtClient = cursor.getString(3) == null ? null : cursor.getString(3);
                    String lastUpdatedAtClient = cursor.getString(4) == null ? null : cursor.getString(4);
                    String organisationUnit = cursor.getString(5) == null ? null : cursor.getString(5);
                    String trackedEntity = cursor.getString(6) == null ? null : cursor.getString(6);

                    trackedEntityInstanceMap.put(uid, TrackedEntityInstance.create(
                            uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                            organisationUnit, trackedEntity, false, null, null, null));

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }

        return trackedEntityInstanceMap;
    }
}
