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
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;

import java.util.ArrayList;
import java.util.Collections;
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
public class TrackedEntityDataValueStoreImpl implements TrackedEntityDataValueStore {

    private static final String SELECT_FIELDS =
            TrackedEntityDataValueModel.TABLE + "." + TrackedEntityDataValueModel.Columns.CREATED
                    + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.LAST_UPDATED + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.DATA_ELEMENT + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.EVENT + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.STORED_BY + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.VALUE + ", " +
                    TrackedEntityDataValueModel.TABLE + "."
                    + TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE;

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityDataValueModel.TABLE + " (" +
            TrackedEntityDataValueModel.Columns.EVENT + ", " +
            TrackedEntityDataValueModel.Columns.CREATED + ", " +
            TrackedEntityDataValueModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT + ", " +
            TrackedEntityDataValueModel.Columns.STORED_BY + ", " +
            TrackedEntityDataValueModel.Columns.VALUE + ", " +
            TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE +
            ") " + "VALUES (?,?,?,?,?,?,?)";

    private static final String UPDATE_STATEMENT =
            "UPDATE " + TrackedEntityDataValueModel.TABLE + " SET " +
                    TrackedEntityDataValueModel.Columns.EVENT + " =?, " +
                    TrackedEntityDataValueModel.Columns.CREATED + " =?, " +
                    TrackedEntityDataValueModel.Columns.LAST_UPDATED + " =?, " +
                    TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " =?, " +
                    TrackedEntityDataValueModel.Columns.STORED_BY + " =?, " +
                    TrackedEntityDataValueModel.Columns.VALUE + " =?, " +
                    TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE + " =? " +
                    " WHERE " + TrackedEntityDataValueModel.Columns.EVENT + " =? AND " +
                    TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " =?;";

    private static final String QUERY_TRACKED_ENTITY_DATA_VALUES_ATTACHED_TO_TRACKER_EVENTS =
            "SELECT " +
                    SELECT_FIELDS +
                    " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue"
                    + ".event = Event.uid "
                    +
                    "  INNER JOIN Enrollment ON Event.enrollment = Enrollment.uid " +
                    "  INNER JOIN TrackedEntityInstance ON Enrollment.trackedEntityInstance = "
                    + "TrackedEntityInstance.uid) "
                    +
                    "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state"
                    + " = 'TO_UPDATE' "
                    +
                    "      OR Enrollment.state = 'TO_POST' OR Enrollment.state = 'TO_UPDATE' OR "
                    + "Event.state = 'TO_POST' "
                    +
                    " OR Event.state = 'TO_POST';";

    private static final String QUERY_TRACKED_ENTITY_DATA_VALUES_ATTACHED_TO_SINGLE_EVENTS =
            "SELECT " +
                    SELECT_FIELDS +
                    " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue"
                    + ".event = Event.uid) "
                    +
                    "WHERE Event.enrollment ISNULL AND (Event.state = 'TO_POST' OR Event.state = "
                    + "'TO_UPDATE');";

    private static final String QUERY_TRACKED_ENTITY_DATA_VALUES_BY_EVENT = "SELECT " +
            SELECT_FIELDS + " FROM TrackedEntityDataValue WHERE event = '?' ";

    private static final String QUERY_TRACKED_ENTITY_DATA_VALUES = "SELECT " +
            SELECT_FIELDS + " FROM TrackedEntityDataValue";

    private final SQLiteStatement insertRowStatement;
    private final SQLiteStatement updateRowStatement;
    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityDataValueStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateRowStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String event, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String dataElement, @Nullable String storedBy,
            @Nullable String value, @Nullable Boolean providedElsewhere) {

        isNull(event);
        isNull(dataElement);

        bindArguments(insertRowStatement,
                event, created, lastUpdated, dataElement, storedBy, value, providedElsewhere);

        long ret = databaseAdapter.executeInsert(TrackedEntityDataValueModel.TABLE,
                insertRowStatement);
        insertRowStatement.clearBindings();
        return ret;
    }

    @Override
    public int update(@NonNull String event, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String dataElement, @Nullable String storedBy, @Nullable String value,
            @Nullable Boolean providedElsewhere) {
        isNull(event);

        bindArguments(updateRowStatement,
                event, created, lastUpdated, dataElement, storedBy, value, providedElsewhere);

        // bind the where argument
        sqLiteBind(updateRowStatement, 8, event);
        sqLiteBind(updateRowStatement, 9, dataElement);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(
                ProgramStageDataElementModel.TABLE, updateRowStatement);

        updateRowStatement.clearBindings();

        return update;
    }

    @Override
    public int deleteByEventAndDataElementUIds(@NonNull String eventUid,
            @NonNull List<String> dataElementUids) {

        //SQLiteStatement deleteRowStatement = new SQLiteStatement();

        //String dataElementUidsWhere = "'" +  TextUtils.join("','", dataElementUids) + "'";
        //sqLiteBind(deleteRowStatement, 1,  eventUid);
        //sqLiteBind(deleteRowStatement, 2, dataElementUidsWhere);

        List<String> argumentValues = new ArrayList<>();
        argumentValues.add(eventUid);
        argumentValues.addAll(dataElementUids);
        String[] argumentValuesArray = argumentValues.toArray(new String[argumentValues.size()]);

        // execute and clear bindings
        //int delete = databaseAdapter.delete("","","")..executeUpdateDelete(
        //      TrackedEntityDataValueModel.TABLE, deleteRowStatement);

        String inArguments = TextUtils.join(
                ",", Collections.nCopies(dataElementUids.size(), "?"));

        int delete = databaseAdapter.delete(TrackedEntityDataValueModel.TABLE,
                TrackedEntityDataValueModel.Columns.EVENT + " = ? AND " +
                        TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " in (" +
                        inArguments + ");", argumentValuesArray);

        return delete;
    }

    @Override
    public List<TrackedEntityDataValue> queryTrackedEntityDataValues(String eventFilter) {
        String queryStatement = QUERY_TRACKED_ENTITY_DATA_VALUES_BY_EVENT;

        queryStatement = queryStatement.replace("?", eventFilter);

        Cursor cursor = databaseAdapter.query(queryStatement);
        Map<String, List<TrackedEntityDataValue>> dataValuesByEvent = mapFromCursor(cursor);

        List<TrackedEntityDataValue> dataValues = dataValuesByEvent.get(eventFilter);

        if (dataValues == null) {
            return new ArrayList<>();
        } else {
            return dataValues;
        }
    }

    /**
     * If singleEvents is true, then we query for Events which is NOT
     * attached to any Enrollment and TrackedEntityInstance
     * If singleEvents is false, then we query for Events which IS attached to Enrollments and
     * TrackedEntityInstances.
     * Example: singleEvents = true, then we query for TrackedEntityDataValues to Events
     * where Event.program.programType = 'WITHOUT_REGISTRATION'
     */

    //TODO TESTS!!
    @Override
    public Map<String, List<TrackedEntityDataValue>> queryTrackedEntityDataValues(
            Boolean singleEvents) {
        String queryStatement;
        if (singleEvents) {
            queryStatement = QUERY_TRACKED_ENTITY_DATA_VALUES_ATTACHED_TO_SINGLE_EVENTS;
        } else {
            queryStatement = QUERY_TRACKED_ENTITY_DATA_VALUES_ATTACHED_TO_TRACKER_EVENTS;
        }

        Cursor cursor = databaseAdapter.query(queryStatement);

        Map<String, List<TrackedEntityDataValue>> dataValues = mapFromCursor(cursor);

        return dataValues;
    }

    @Override
    public Map<String, List<TrackedEntityDataValue>> queryTrackedEntityDataValues() {
        String queryStatement = QUERY_TRACKED_ENTITY_DATA_VALUES;

        Cursor cursor = databaseAdapter.query(queryStatement);


        Map<String, List<TrackedEntityDataValue>> dataValues = mapFromCursor(cursor);

        return dataValues;
    }

    @NonNull
    private Map<String, List<TrackedEntityDataValue>> mapFromCursor(Cursor cursor) {
        Map<String, List<TrackedEntityDataValue>> dataValues = new HashMap<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Date created = cursor.getString(0) == null ? null : parse(cursor.getString(0));
                    Date lastUpdated = cursor.getString(1) == null ? null : parse(
                            cursor.getString(1));
                    String dataElement = cursor.getString(2) == null ? null : cursor.getString(2);
                    String event = cursor.getString(3) == null ? null : cursor.getString(3);
                    String storedBy = cursor.getString(4) == null ? null : cursor.getString(4);
                    String value = cursor.getString(5) == null ? null : cursor.getString(5);
                    Boolean providedElsewhere =
                            cursor.getString(6) == null || cursor.getInt(6) == 0 ? Boolean.FALSE
                                    : Boolean.TRUE;

                    if (dataValues.get(event) == null) {
                        dataValues.put(event, new ArrayList<TrackedEntityDataValue>());
                    }


                    dataValues.get(event).add(TrackedEntityDataValue.create(
                            created, lastUpdated, dataElement, storedBy, value, providedElsewhere
                    ));

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return dataValues;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
            @NonNull String event, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String dataElement, @Nullable String storedBy,
            @Nullable String value, @Nullable Boolean providedElsewhere) {
        sqLiteBind(sqLiteStatement, 1, event);
        sqLiteBind(sqLiteStatement, 2, created);
        sqLiteBind(sqLiteStatement, 3, lastUpdated);
        sqLiteBind(sqLiteStatement, 4, dataElement);
        sqLiteBind(sqLiteStatement, 5, storedBy);
        sqLiteBind(sqLiteStatement, 6, value);
        sqLiteBind(sqLiteStatement, 7, providedElsewhere);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(TrackedEntityDataValueModel.TABLE);
    }

    @Override
    public int countAll() {
        String queryStatement = QUERY_TRACKED_ENTITY_DATA_VALUES;

        Cursor cursor = databaseAdapter.query(queryStatement);
        return cursor.getCount();
    }
}
