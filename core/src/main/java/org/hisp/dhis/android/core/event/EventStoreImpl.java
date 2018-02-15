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

package org.hisp.dhis.android.core.event;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.EventModel.Columns;

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
public class EventStoreImpl implements EventStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + EventModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.ENROLLMENT_UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.CREATED_AT_CLIENT + ", " +
            Columns.LAST_UPDATED_AT_CLIENT + ", " +
            Columns.STATUS + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.EVENT_DATE + ", " +
            Columns.COMPLETE_DATE + ", " +
            Columns.DUE_DATE + ", " +
            Columns.STATE + ", " +
            Columns.ATTRIBUTE_CATEGORY_OPTIONS + ", " +
            Columns.ATTRIBUTE_OPTION_COMBO + ", " +
            Columns.TRACKED_ENTITY_INSTANCE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + EventModel.TABLE + " SET " +
            Columns.UID + " =? , " +
            Columns.ENROLLMENT_UID + " =? , " +
            Columns.CREATED + " =? , " +
            Columns.LAST_UPDATED + " =? ," +
            Columns.CREATED_AT_CLIENT + " =? , " +
            Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            Columns.STATUS + " =? ," +
            Columns.LATITUDE + " =? ," +
            Columns.LONGITUDE + " =? ," +
            Columns.PROGRAM + " =? ," +
            Columns.PROGRAM_STAGE + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.EVENT_DATE + " =? , " +
            Columns.COMPLETE_DATE + " =? , " +
            Columns.DUE_DATE + " =? , " +
            Columns.STATE + " =?, " +
            Columns.ATTRIBUTE_CATEGORY_OPTIONS + " =?, " +
            Columns.ATTRIBUTE_OPTION_COMBO + " =?, " +
            Columns.TRACKED_ENTITY_INSTANCE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String UPDATE_STATE_STATEMENT = "UPDATE " + EventModel.TABLE + " SET " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            EventModel.TABLE + " WHERE " +
            Columns.UID + " =?;";

    private static final String FIELDS =
            "  Event.uid, " +
            "  Event.created, " +
            "  Event.lastUpdated, " +
            "  Event.createdAtClient, " +
            "  Event.lastUpdatedAtClient, " +
            "  Event.status, " +
            "  Event.latitude, " +
            "  Event.longitude, " +
            "  Event.program, " +
            "  Event.programStage, " +
            "  Event.organisationUnit, " +
            "  Event.enrollment, " +
            "  Event.eventDate, " +
            "  Event.completedDate, " +
            "  Event.dueDate, "  +
            "  Event.attributeCategoryOptions, "  +
            "  Event.attributeOptionCombo, "  +
            "  Event.trackedEntityInstance ";

    private static final String QUERY_EVENTS_ATTACHED_TO_ENROLLMENTS = "SELECT " +
            FIELDS +
            " FROM (Event INNER JOIN Enrollment ON Event.enrollment = Enrollment.uid " +
            "  INNER JOIN TrackedEntityInstance ON Enrollment.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state = 'TO_UPDATE' " +
            "      OR Enrollment.state = 'TO_POST' OR Enrollment.state = 'TO_UPDATE' OR Event.state = 'TO_POST' " +
            "OR Event.state = 'TO_UPDATE';";

    private static final String QUERY_SINGLE_EVENTS = "SELECT " +
            FIELDS +
            "FROM Event WHERE Event.enrollment ISNULL";

    private static final String QUERY_ALL_EVENTS = "SELECT " +
            FIELDS + " FROM Event ";

    private static final String QUERY_SINGLE_EVENTS_TO_POST =
            QUERY_SINGLE_EVENTS + "  AND (Event.state = 'TO_POST' OR Event.state = 'TO_UPDATE')";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement setStateStatement;
    private final DatabaseAdapter databaseAdapter;

    public EventStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.setStateStatement = databaseAdapter.compileStatement(UPDATE_STATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String enrollmentUid,
                       @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                       @Nullable EventStatus status, @Nullable String latitude,
                       @Nullable String longitude, @NonNull String program,
                       @NonNull String programStage, @NonNull String organisationUnit,
                       @Nullable Date eventDate, @Nullable Date completedDate,
                       @Nullable Date dueDate, @Nullable State state,
                       @Nullable String attributeCategoryOptions, @Nullable String attributeOptionCombo,
                       @Nullable String trackedEntityInstance) {
        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, enrollmentUid);
        sqLiteBind(insertStatement, 3, created);
        sqLiteBind(insertStatement, 4, lastUpdated);
        sqLiteBind(insertStatement, 5, createdAtClient);
        sqLiteBind(insertStatement, 6, lastUpdatedAtClient);
        sqLiteBind(insertStatement, 7, status);
        sqLiteBind(insertStatement, 8, latitude);
        sqLiteBind(insertStatement, 9, longitude);
        sqLiteBind(insertStatement, 10, program);
        sqLiteBind(insertStatement, 11, programStage);
        sqLiteBind(insertStatement, 12, organisationUnit);
        sqLiteBind(insertStatement, 13, eventDate);
        sqLiteBind(insertStatement, 14, completedDate);
        sqLiteBind(insertStatement, 15, dueDate);
        sqLiteBind(insertStatement, 16, state);
        sqLiteBind(insertStatement, 17, attributeCategoryOptions);
        sqLiteBind(insertStatement, 18, attributeOptionCombo);
        sqLiteBind(insertStatement, 19, trackedEntityInstance);

        long insert = databaseAdapter.executeInsert(EventModel.TABLE, insertStatement);

        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int update(@NonNull String uid, @Nullable String enrollmentUid,
                      @NonNull Date created, @NonNull Date lastUpdated,
                      @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
                      @NonNull EventStatus eventStatus, @Nullable String latitude,
                      @Nullable String longitude, @NonNull String program,
                      @NonNull String programStage, @NonNull String organisationUnit,
                      @NonNull Date eventDate, @Nullable Date completedDate,
                      @Nullable Date dueDate, @NonNull State state,
                      @Nullable String attributeCategoryOptions, @Nullable String attributeOptionCombo,
                      @Nullable String trackedEntityInstance, @NonNull String whereEventUid) {

        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, enrollmentUid);
        sqLiteBind(updateStatement, 3, created);
        sqLiteBind(updateStatement, 4, lastUpdated);
        sqLiteBind(updateStatement, 5, createdAtClient);
        sqLiteBind(updateStatement, 6, lastUpdatedAtClient);
        sqLiteBind(updateStatement, 7, eventStatus);
        sqLiteBind(updateStatement, 8, latitude);
        sqLiteBind(updateStatement, 9, longitude);
        sqLiteBind(updateStatement, 10, program);
        sqLiteBind(updateStatement, 11, programStage);
        sqLiteBind(updateStatement, 12, organisationUnit);
        sqLiteBind(updateStatement, 13, eventDate);
        sqLiteBind(updateStatement, 14, completedDate);
        sqLiteBind(updateStatement, 15, dueDate);
        sqLiteBind(updateStatement, 16, state);
        sqLiteBind(updateStatement, 17, attributeCategoryOptions);
        sqLiteBind(updateStatement, 18, attributeOptionCombo);
        sqLiteBind(updateStatement, 19, trackedEntityInstance);

        // bind the where clause
        sqLiteBind(updateStatement, 20, whereEventUid);

        int rowId = databaseAdapter.executeUpdateDelete(EventModel.TABLE, updateStatement);
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
        sqLiteBind(setStateStatement, 2, uid);

        int update = databaseAdapter.executeUpdateDelete(EventModel.TABLE, setStateStatement);
        setStateStatement.clearBindings();

        return update;
    }

    @Override
    public Map<String, List<Event>> queryEventsAttachedToEnrollmentToPost() {
        Cursor cursor = databaseAdapter.query(QUERY_EVENTS_ATTACHED_TO_ENROLLMENTS);
        Map<String, List<Event>> events = new HashMap<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Event event = mapEventFromCursor(cursor);

                    if (events.get(event.enrollmentUid()) == null) {
                        events.put(event.enrollmentUid(), new ArrayList<Event>());
                    }

                    events.get(event.enrollmentUid()).add(event);

                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return events;
    }

    @Override
    public List<Event> querySingleEventsToPost() {
        Cursor cursor = databaseAdapter.query(QUERY_SINGLE_EVENTS_TO_POST);

        return mapEventsFromCursor(cursor);
    }

    @Override
    public List<Event> querySingleEvents() {
        Cursor cursor = databaseAdapter.query(QUERY_SINGLE_EVENTS);

        return mapEventsFromCursor(cursor);
    }

    @Override
    public List<Event> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_ALL_EVENTS);

        return mapEventsFromCursor(cursor);
    }

    private List<Event> mapEventsFromCursor(Cursor cursor) {
        List<Event> events = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    Event event = mapEventFromCursor(cursor);

                    events.add(event);
                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return events;
    }

    private Event mapEventFromCursor(Cursor cursor) {
        Event event;

        String uid = cursor.getString(0);
        Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
        Date lastUpdated = cursor.getString(2) == null ? null : parse(cursor.getString(2));
        String createdAtClient = cursor.getString(3) == null ? null : cursor.getString(3);
        String lastUpdatedAtClient = cursor.getString(4) == null ? null : cursor.getString(4);
        EventStatus eventStatus =
                cursor.getString(5) == null ? null : EventStatus.valueOf(cursor.getString(5));
        String latitude = cursor.getString(6) == null ? null : cursor.getString(6);
        String longitude = cursor.getString(7) == null ? null : cursor.getString(7);
        String program = cursor.getString(8) == null ? null : cursor.getString(8);
        String programStage = cursor.getString(9) == null ? null : cursor.getString(9);
        String organisationUnit = cursor.getString(10) == null ? null : cursor.getString(10);
        String enrollment = cursor.getString(11) == null ? null : cursor.getString(11);
        Date eventDate = cursor.getString(12) == null ? null : parse(cursor.getString(12));
        Date completedDate = cursor.getString(13) == null ? null : parse(cursor.getString(13));
        Date dueDate = cursor.getString(14) == null ? null : parse(cursor.getString(14));
        String categoryCombo = cursor.getString(15) == null ? null : cursor.getString(15);
        String optionCombo = cursor.getString(16) == null ? null : cursor.getString(16);
        String trackedEntityInstance = cursor.getString(17) == null ? null : cursor.getString(17);

        Coordinates coordinates = null;

        if (latitude != null && longitude != null) {
            coordinates = Coordinates.create(latitude, longitude);
        }

        event = Event.builder()
                .uid(uid)
                .enrollmentUid(enrollment)
                .created(created)
                .lastUpdated(lastUpdated)
                .createdAtClient(createdAtClient)
                .lastUpdatedAtClient(lastUpdatedAtClient)
                .program(program)
                .programStage(programStage)
                .organisationUnit(organisationUnit)
                .eventDate(eventDate)
                .status(eventStatus)
                .coordinates(coordinates)
                .completedDate(completedDate)
                .dueDate(dueDate)
                .deleted(false)
                .attributeOptionCombo(optionCombo)
                .attributeCategoryOptions(categoryCombo)
                .trackedEntityInstance(trackedEntityInstance)
                .build();

        return event;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(EventModel.TABLE);
    }
}
