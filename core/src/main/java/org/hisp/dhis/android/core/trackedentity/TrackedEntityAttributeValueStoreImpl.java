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
import android.text.TextUtils;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.NPathComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.AvoidDuplicateLiterals"
})
public class TrackedEntityAttributeValueStoreImpl implements TrackedEntityAttributeValueStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityAttributeValueModel.TABLE + " (" +
            TrackedEntityAttributeValueModel.Columns.VALUE + ", " +
            TrackedEntityAttributeValueModel.Columns.CREATED + ", " +
            TrackedEntityAttributeValueModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + ") " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + TrackedEntityAttributeValueModel.TABLE + " SET " +
                    TrackedEntityAttributeValueModel.Columns.VALUE + " =?, " +
                    TrackedEntityAttributeValueModel.Columns.CREATED + " =?, " +
                    TrackedEntityAttributeValueModel.Columns.LAST_UPDATED + " =? " +
                    " WHERE " + TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE
                    + " =? AND " +
                    TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + " =?;";

    private static final String FIELDS =
            "  TrackedEntityAttributeValue.value, " +
                    "  TrackedEntityAttributeValue.created, " +
                    "  TrackedEntityAttributeValue.lastUpdated, " +
                    "  TrackedEntityAttributeValue.trackedEntityAttribute, " +
                    "  TrackedEntityAttributeValue.trackedEntityInstance ";

    private static final String QUERY_STATEMENT_TO_POST =
            "SELECT " + FIELDS +
                    "FROM (TrackedEntityAttributeValue " +
                    "  INNER JOIN TrackedEntityInstance " +
                    "    ON TrackedEntityAttributeValue.trackedEntityInstance = "
                    + "TrackedEntityInstance.uid) "
                    +
                    "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state"
                    + " = "
                    + "'TO_UPDATE';";

    private static final String QUERY_STATEMENT =
            "SELECT " + FIELDS +
                    "FROM TrackedEntityAttributeValue";

    private static final String QUERY_BY_TRACKED_ENTITY_INSTANCE_STATEMENT =
            "SELECT " + FIELDS +
                    "FROM TrackedEntityAttributeValue where trackedEntityInstance = '?'";

    private final SQLiteStatement insertRowStatement;
    private final SQLiteStatement updateRowStatement;
    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityAttributeValueStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateRowStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public long insert(@Nullable String value, @Nullable Date created,
            @Nullable Date lastUpdated, @NonNull String trackedEntityAttribute,
            @NonNull String trackedEntityInstance) {
        isNull(trackedEntityAttribute);
        isNull(trackedEntityInstance);

        bindArguments(insertRowStatement, value, created, lastUpdated,
                trackedEntityAttribute, trackedEntityInstance);

        long returnValue = databaseAdapter.executeInsert(
                TrackedEntityAttributeValueModel.TABLE, insertRowStatement);

        insertRowStatement.clearBindings();
        return returnValue;

    }

    @Override
    public int update(@Nullable String value, @Nullable Date created,
            @Nullable Date lastUpdated, @NonNull String trackedEntityAttribute,
            @NonNull String trackedEntityInstance) {
        isNull(trackedEntityAttribute);
        isNull(trackedEntityInstance);

        bindArguments(updateRowStatement, value, created, lastUpdated,
                trackedEntityAttribute, trackedEntityInstance);

        int update = databaseAdapter.executeUpdateDelete(
                TrackedEntityAttributeValueModel.TABLE, updateRowStatement);

        updateRowStatement.clearBindings();

        return update;
    }

    @Override
    public int deleteByInstanceAndAttributes(
            @NonNull String trackedEntityInstanceUId,
            @NonNull List<String> trackedEntityAttributeUIds) {

        isNull(trackedEntityInstanceUId);
        isNull(trackedEntityAttributeUIds);

        List<String> argumentValues = new ArrayList<>();
        argumentValues.add(trackedEntityInstanceUId);
        argumentValues.addAll(trackedEntityAttributeUIds);
        String[] argumentValuesArray = argumentValues.toArray(new String[argumentValues.size()]);

        String inArguments = TextUtils.join(
                ",", Collections.nCopies(trackedEntityAttributeUIds.size(), "?"));

        int delete = databaseAdapter.delete(TrackedEntityAttributeValueModel.TABLE,
                TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + " = ? AND " +
                        TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " in ("
                        +
                        inArguments + ");", argumentValuesArray);

        return delete;
    }

    @Override
    public List<TrackedEntityAttributeValue> queryByTrackedEntityInstance(
            String trackedEntityInstanceUid) {
        String queryStatement = QUERY_BY_TRACKED_ENTITY_INSTANCE_STATEMENT;

        queryStatement = queryStatement.replace("?", trackedEntityInstanceUid);

        Cursor cursor = databaseAdapter.query(queryStatement);

        Map<String, List<TrackedEntityAttributeValue>> attributeValuesMap =
                mapFromCursor(cursor);

        List<TrackedEntityAttributeValue> attributeValues =
                attributeValuesMap.get(trackedEntityInstanceUid);

        if (attributeValues == null) {
            return new ArrayList<>();
        } else {
            return attributeValues;
        }
    }

    @Override
    public Map<String, List<TrackedEntityAttributeValue>> query() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT_TO_POST);
        Map<String, List<TrackedEntityAttributeValue>> attributeValues = mapFromCursor(cursor);

        return attributeValues;
    }

    @Override
    public Map<String, List<TrackedEntityAttributeValue>> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);
        Map<String, List<TrackedEntityAttributeValue>> attributeValues = mapFromCursor(cursor);

        return attributeValues;
    }

    @NonNull
    private Map<String, List<TrackedEntityAttributeValue>> mapFromCursor(Cursor cursor) {
        Map<String, List<TrackedEntityAttributeValue>> attributeValues = new HashMap<>(cursor
                .getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    String value = cursor.getString(0) == null ? null : cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(
                            cursor.getString(2));
                    String attribute = cursor.getString(3) == null ? null : cursor.getString(3);
                    String trackedEntityInstance = cursor.getString(4) == null ? null
                            : cursor.getString(4);


                    if (attributeValues.get(trackedEntityInstance) == null) {
                        attributeValues.put(trackedEntityInstance, new
                                ArrayList<TrackedEntityAttributeValue>());
                    }

                    attributeValues.get(trackedEntityInstance)
                            .add(TrackedEntityAttributeValue.create(
                                    attribute, value, created, lastUpdated));

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return attributeValues;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
            @Nullable String value, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String trackedEntityAttribute, @Nullable String trackedEntityInstance) {
        sqLiteBind(sqLiteStatement, 1, value);
        sqLiteBind(sqLiteStatement, 2, created);
        sqLiteBind(sqLiteStatement, 3, lastUpdated);
        sqLiteBind(sqLiteStatement, 4, trackedEntityAttribute);
        sqLiteBind(sqLiteStatement, 5, trackedEntityInstance);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityAttributeValueModel.TABLE);
    }

}
