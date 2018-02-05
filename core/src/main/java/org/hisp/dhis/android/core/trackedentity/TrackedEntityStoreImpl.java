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

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.NPathComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class TrackedEntityStoreImpl implements TrackedEntityStore {
    private static final String FIELDS = TrackedEntityModel.Columns.UID + ", " +
            TrackedEntityModel.Columns.CODE + ", " +
            TrackedEntityModel.Columns.NAME + ", " +
            TrackedEntityModel.Columns.DISPLAY_NAME + ", " +
            TrackedEntityModel.Columns.CREATED + ", " +
            TrackedEntityModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityModel.Columns.SHORT_NAME + ", " +
            TrackedEntityModel.Columns.DISPLAY_SHORT_NAME + ", " +
            TrackedEntityModel.Columns.DESCRIPTION + ", " +
            TrackedEntityModel.Columns.DISPLAY_DESCRIPTION;

    private static final String QUERY_STATEMENT = "SELECT " +
            FIELDS + " FROM " + TrackedEntityModel.TABLE;

    private static final String INSERT_STATEMENT =
            "INSERT INTO " + TrackedEntityModel.TABLE + " (" + FIELDS + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT = "UPDATE " + TrackedEntityModel.TABLE + " SET " +
            TrackedEntityModel.Columns.UID + "=?, " +
            TrackedEntityModel.Columns.CODE + "=?, " +
            TrackedEntityModel.Columns.NAME + "=?, " +
            TrackedEntityModel.Columns.DISPLAY_NAME + "=?, " +
            TrackedEntityModel.Columns.CREATED + "=?, " +
            TrackedEntityModel.Columns.LAST_UPDATED + "=?, " +
            TrackedEntityModel.Columns.SHORT_NAME + "=?, " +
            TrackedEntityModel.Columns.DISPLAY_SHORT_NAME + "=?, " +
            TrackedEntityModel.Columns.DESCRIPTION + "=?, " +
            TrackedEntityModel.Columns.DISPLAY_DESCRIPTION + "=? " +
            " WHERE " +
            TrackedEntityModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + TrackedEntityModel.TABLE +
            " WHERE " + TrackedEntityModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityStoreImpl(DatabaseAdapter database) {
        this.databaseAdapter = database;
        this.insertStatement = database.compileStatement(INSERT_STATEMENT);
        this.updateStatement = database.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = database.compileStatement(DELETE_STATEMENT);

    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @Nullable Date created,
            @Nullable Date lastUpdated, @Nullable String shortName,
            @Nullable String displayShortName, @Nullable String description,
            @Nullable String displayDescription
    ) {
        isNull(uid);
        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, code);
        sqLiteBind(insertStatement, 3, name);
        sqLiteBind(insertStatement, 4, displayName);
        sqLiteBind(insertStatement, 5, created);
        sqLiteBind(insertStatement, 6, lastUpdated);
        sqLiteBind(insertStatement, 7, shortName);
        sqLiteBind(insertStatement, 8, displayShortName);
        sqLiteBind(insertStatement, 9, description);
        sqLiteBind(insertStatement, 10, displayDescription);

        long rowId = databaseAdapter.executeInsert(TrackedEntityModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return rowId;

    }

    @Override
    public int update(@NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String shortName, @Nullable String displayShortName,
            @Nullable String description,
            @Nullable String displayDescription, @NonNull String whereUid
    ) {
        isNull(uid);
        isNull(whereUid);
        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, code);
        sqLiteBind(updateStatement, 3, name);
        sqLiteBind(updateStatement, 4, displayName);
        sqLiteBind(updateStatement, 5, created);
        sqLiteBind(updateStatement, 6, lastUpdated);
        sqLiteBind(updateStatement, 7, shortName);
        sqLiteBind(updateStatement, 8, displayShortName);
        sqLiteBind(updateStatement, 9, description);
        sqLiteBind(updateStatement, 10, displayDescription);
        sqLiteBind(updateStatement, 11, whereUid);

        int rowId = databaseAdapter.executeUpdateDelete(TrackedEntityModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return rowId;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        sqLiteBind(deleteStatement, 1, uid);
        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();
        return rowId;
    }

    @Override
    public List<TrackedEntity> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);

        Map<String, TrackedEntity> trackedEntityInstanceMap = mapFromCursor(cursor);

        return new ArrayList<>(trackedEntityInstanceMap.values());
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityModel.TABLE);
    }

    private Map<String, TrackedEntity> mapFromCursor(Cursor cursor) {

        Map<String, TrackedEntity> trackedEntityMap = new HashMap<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0) == null ? null : cursor.getString(
                            0);
                    String code = cursor.getString(1) == null ? null : cursor.getString(
                            1);
                    String name = cursor.getString(2) == null ? null : cursor.getString(
                            2);
                    String displayName = cursor.getString(3) == null ? null : cursor.getString(
                            3);
                    Date created = cursor.getString(4) == null ? null : parse(cursor.getString(4));
                    Date lastUpdated = cursor.getString(5) == null ? null : parse(
                            cursor.getString(5));

                    String shorName = cursor.getString(6) == null ? null : cursor.getString(
                            6);

                    String displayShorName = cursor.getString(7) == null ? null : cursor.getString(
                            7);

                    String description = cursor.getString(8) == null ? null : cursor.getString(
                            8);

                    String displayDescription = cursor.getString(9) == null ? null
                            : cursor.getString(
                                    9);

                    trackedEntityMap.put(uid, TrackedEntity.builder()
                            .uid(uid)
                            .code(code)
                            .name(name)
                            .displayName(displayName)
                            .created(created)
                            .lastUpdated(lastUpdated)
                            .shortName(shorName)
                            .displayShortName(displayShorName)
                            .description(description)
                            .displayDescription(displayDescription)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return trackedEntityMap;
    }
}
