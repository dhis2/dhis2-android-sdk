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
import org.hisp.dhis.android.core.common.StoreWithStateImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.period.FeatureType;
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
public class TrackedEntityInstanceStoreImpl extends StoreWithStateImpl implements TrackedEntityInstanceStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityInstanceModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.CREATED_AT_CLIENT + ", " +
            Columns.LAST_UPDATED_AT_CLIENT + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.TRACKED_ENTITY_TYPE + ", " +
            Columns.COORDINATES + ", " +
            Columns.FEATURE_TYPE + ", " +
            Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + TrackedEntityInstanceModel.TABLE + " SET " +
            Columns.UID + " =?, " +
            Columns.CREATED + " =?, " +
            Columns.LAST_UPDATED + " =?, " +
            Columns.CREATED_AT_CLIENT + " =? , " +
            Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.TRACKED_ENTITY_TYPE + " =?, " +
            Columns.COORDINATES + " =?, " +
            Columns.FEATURE_TYPE + " =?, " +
            Columns.STATE + " =? " +
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
            "  TrackedEntityInstance.trackedEntityType," +
            "  TrackedEntityInstance.coordinates," +
            "  TrackedEntityInstance.featureType, " +
            "  TrackedEntityInstance.state " +
            "FROM TrackedEntityInstance ";

    private static final String QUERY_STATEMENT_TO_POST =
            QUERY_STATEMENT +
                    "WHERE state = 'TO_POST' " +
                    "OR state = 'TO_UPDATE' " +
                    "OR state = 'TO_DELETE'";

    private static final String QUERY_STATEMENT_SYNCED =
            QUERY_STATEMENT +
                    " WHERE state = 'SYNCED'";

    private static final String QUERY_STATEMENT_RELATIONSHIPS =
            QUERY_STATEMENT +
                    " WHERE state = 'RELATIONSHIP'";

    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final SQLiteStatement insertStatement;

    public TrackedEntityInstanceStoreImpl(DatabaseAdapter databaseAdapter) {
        super(databaseAdapter, TrackedEntityInstanceModel.TABLE);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                       @NonNull String organisationUnit, @NonNull String trackedEntityType,
                       @Nullable String coordinates, @Nullable FeatureType featureType,
                       @Nullable State state) {

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, lastUpdated);
        sqLiteBind(insertStatement, 4, createdAtClient);
        sqLiteBind(insertStatement, 5, lastUpdatedAtClient);
        sqLiteBind(insertStatement, 6, organisationUnit);
        sqLiteBind(insertStatement, 7, trackedEntityType);
        sqLiteBind(insertStatement, 8, coordinates);
        sqLiteBind(insertStatement, 9, featureType);
        sqLiteBind(insertStatement, 10, state);

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
                      @NonNull String organisationUnit, @NonNull String trackedEntityType,
                      @Nullable String coordinates, @Nullable FeatureType featureType,
                      @NonNull State state, @NonNull String whereTrackedEntityInstanceUid) {
        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, created);
        sqLiteBind(updateStatement, 3, lastUpdated);
        sqLiteBind(updateStatement, 4, createdAtClient);
        sqLiteBind(updateStatement, 5, lastUpdatedAtClient);
        sqLiteBind(updateStatement, 6, organisationUnit);
        sqLiteBind(updateStatement, 7, trackedEntityType);
        sqLiteBind(updateStatement, 8, coordinates);
        sqLiteBind(updateStatement, 9, featureType);
        sqLiteBind(updateStatement, 10, state);

        // bind the where clause
        sqLiteBind(updateStatement, 11, whereTrackedEntityInstanceUid);

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
    public Map<String, TrackedEntityInstance> queryToPost() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT_TO_POST);
        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = mapFromCursor(cursor);

        return trackedEntityInstanceMap;
    }

    @Override
    public Map<String, TrackedEntityInstance> querySynced() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT_SYNCED);
        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = mapFromCursor(cursor);

        return trackedEntityInstanceMap;
    }

    @Override
    public Map<String, TrackedEntityInstance> queryRelationships() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT_RELATIONSHIPS);
        return mapFromCursor(cursor);
    }

    @Override
    public Map<String, TrackedEntityInstance> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);

        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = mapFromCursor(cursor);

        return trackedEntityInstanceMap;
    }

    @NonNull
    private Map<String, TrackedEntityInstance> mapFromCursor(Cursor cursor) {
        Map<String, TrackedEntityInstance> trackedEntityInstanceMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(cursor.getString(2));
                    String createdAtClient = cursor.getString(3);
                    String lastUpdatedAtClient = cursor.getString(4);
                    String organisationUnit = cursor.getString(5);
                    String trackedEntityType = cursor.getString(6);
                    String coordinates = cursor.getString(7);
                    FeatureType featureType = cursor.getString(8) == null ? null :
                            FeatureType.valueOf(FeatureType.class, cursor.getString(8));
                    String stateStr = cursor.getString(9);

                    Boolean deleted = stateStr != null && stateStr.equals(State.TO_DELETE.toString());

                    trackedEntityInstanceMap.put(uid, TrackedEntityInstance.create(
                            uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                            organisationUnit, trackedEntityType, coordinates, featureType, deleted,
                            null, null, null));

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return trackedEntityInstanceMap;
    }
}
