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

package org.hisp.dhis.android.core.enrollment;

import static org.hisp.dhis.android.core.utils.StoreUtils.parse;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.AvoidInstantiatingObjectsInLoops"
})
public class EnrollmentStoreImpl implements EnrollmentStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + EnrollmentModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.CREATED_AT_CLIENT + ", " +
            Columns.LAST_UPDATED_AT_CLIENT + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.PROGRAM + ", " +
            Columns.DATE_OF_ENROLLMENT + ", " +
            Columns.DATE_OF_INCIDENT + ", " +
            Columns.FOLLOW_UP + ", " +
            Columns.ENROLLMENT_STATUS + ", " +
            Columns.TRACKED_ENTITY_INSTANCE + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.STATE + ") " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + EnrollmentModel.TABLE + " SET " +
            Columns.UID + " =?, " +
            Columns.CREATED + " =?, " +
            Columns.LAST_UPDATED + " =?, " +
            Columns.CREATED_AT_CLIENT + " =? , " +
            Columns.LAST_UPDATED_AT_CLIENT + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.PROGRAM + " =?, " +
            Columns.DATE_OF_ENROLLMENT + " =?, " +
            Columns.DATE_OF_INCIDENT + " =?, " +
            Columns.FOLLOW_UP + " =?, " +
            Columns.ENROLLMENT_STATUS + " =?, " +
            Columns.TRACKED_ENTITY_INSTANCE + " =?, " +
            Columns.LATITUDE + " =?, " +
            Columns.LONGITUDE + " =?, " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String UPADTE_STATE_STATEMENT =
            "UPDATE " + EnrollmentModel.TABLE + " SET " +
                    Columns.STATE + " =? " +
                    " WHERE " +
                    Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            EnrollmentModel.TABLE + " WHERE " +
            Columns.UID + " =?;";

    private static final String FIELDS =
            "  Enrollment.uid, " +
                    "  Enrollment.created, " +
                    "  Enrollment.lastUpdated, " +
                    "  Enrollment.createdAtClient, " +
                    "  Enrollment.lastUpdatedAtClient, " +
                    "  Enrollment.organisationUnit, " +
                    "  Enrollment.program, " +
                    "  Enrollment.enrollmentDate, " +
                    "  Enrollment.incidentDate, " +
                    "  Enrollment.followup, " +
                    "  Enrollment.status, " +
                    "  Enrollment.trackedEntityInstance, " +
                    "  Enrollment.latitude, " +
                    "  Enrollment.longitude ";

    private static final String QUERY_STATEMENT_TO_POST = "SELECT " +
            FIELDS +
            "FROM (Enrollment " +
            "  INNER JOIN TrackedEntityInstance ON Enrollment.trackedEntityInstance = TrackedEntityInstance.uid "

            +
            ") " +
            "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state = 'TO_UPDATE' "
            +
            " OR Enrollment.state = 'TO_POST' OR Enrollment.state = 'TO_UPDATE';";

    private static final String QUERY_STATEMENT = "SELECT " +
            FIELDS +
            " FROM Enrollment;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final SQLiteStatement setStateStatement;
    private final DatabaseAdapter databaseAdapter;

    public EnrollmentStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
        this.setStateStatement = databaseAdapter.compileStatement(UPADTE_STATE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
            @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
            @NonNull String organisationUnit, @NonNull String program,
            @Nullable Date dateOfEnrollment,
            @Nullable Date dateOfIncident, @Nullable Boolean followUp,
            @Nullable EnrollmentStatus enrollmentStatus, @NonNull String trackedEntityInstance,
            @Nullable String latitude, @Nullable String longitude, @Nullable State state) {
        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, lastUpdated);
        sqLiteBind(insertStatement, 4, createdAtClient);
        sqLiteBind(insertStatement, 5, lastUpdatedAtClient);
        sqLiteBind(insertStatement, 6, organisationUnit);
        sqLiteBind(insertStatement, 7, program);
        sqLiteBind(insertStatement, 8, dateOfEnrollment);
        sqLiteBind(insertStatement, 9, dateOfIncident);
        sqLiteBind(insertStatement, 10, followUp);
        sqLiteBind(insertStatement, 11, enrollmentStatus);
        sqLiteBind(insertStatement, 12, trackedEntityInstance);
        sqLiteBind(insertStatement, 13, latitude);
        sqLiteBind(insertStatement, 14, longitude);
        sqLiteBind(insertStatement, 15, state);

        long insert = databaseAdapter.executeInsert(EnrollmentModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;
    }

    @Override
    public int delete(@NonNull String uid) {
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }

    @Override
    public int update(@NonNull String uid, @NonNull Date created, @NonNull Date lastUpdated,
            @Nullable String createdAtClient, @Nullable String lastUpdatedAtClient,
            @NonNull String organisationUnit, @NonNull String program,
            @NonNull Date dateOfEnrollment, @Nullable Date dateOfIncident,
            @Nullable Boolean followUp, @NonNull EnrollmentStatus enrollmentStatus,
            @NonNull String trackedEntityInstance, @Nullable String latitude,
            @Nullable String longitude, @NonNull State state, @NonNull String whereEnrollmentUid) {

        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, created);
        sqLiteBind(updateStatement, 3, lastUpdated);
        sqLiteBind(updateStatement, 4, createdAtClient);
        sqLiteBind(updateStatement, 5, lastUpdatedAtClient);
        sqLiteBind(updateStatement, 6, organisationUnit);
        sqLiteBind(updateStatement, 7, program);
        sqLiteBind(updateStatement, 8, dateOfEnrollment);
        sqLiteBind(updateStatement, 9, dateOfIncident);
        sqLiteBind(updateStatement, 10, followUp);
        sqLiteBind(updateStatement, 11, enrollmentStatus);
        sqLiteBind(updateStatement, 12, trackedEntityInstance);
        sqLiteBind(updateStatement, 13, latitude);
        sqLiteBind(updateStatement, 14, longitude);
        sqLiteBind(updateStatement, 15, state);

        // bind the where clause
        sqLiteBind(updateStatement, 16, whereEnrollmentUid);

        int rowId = databaseAdapter.executeUpdateDelete(EnrollmentModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return rowId;
    }

    @Override
    public int setState(@NonNull String uid, @NonNull State state) {
        sqLiteBind(setStateStatement, 1, state);

        // bind the where clause
        sqLiteBind(setStateStatement, 2, uid);

        int update = databaseAdapter.executeUpdateDelete(EnrollmentModel.TABLE, setStateStatement);
        setStateStatement.clearBindings();

        return update;
    }

    @Override
    public Map<String, List<Enrollment>> query() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT_TO_POST);
        return mapFromCursor(cursor);
    }

    @Override
    public Map<String, List<Enrollment>> queryAll() {
        Cursor cursor = databaseAdapter.query(QUERY_STATEMENT);
        return mapFromCursor(cursor);
    }

    private Map<String, List<Enrollment>> mapFromCursor(Cursor cursor) {
        Map<String, List<Enrollment>> enrollmentMap = new HashMap<>();

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String uid = cursor.getString(0);
                    Date created = cursor.getString(1) == null ? null : parse(cursor.getString(1));
                    Date lastUpdated = cursor.getString(2) == null ? null : parse(
                            cursor.getString(2));
                    String createdAtClient = cursor.getString(3) == null ? null : cursor.getString(
                            3);
                    String lastUpdatedAtClient = cursor.getString(4) == null ? null
                            : cursor.getString(4);
                    String organisationUnit = cursor.getString(5) == null ? null : cursor.getString(
                            5);
                    String program = cursor.getString(6) == null ? null : cursor.getString(6);
                    Date enrollmentDate = cursor.getString(7) == null ? null : parse(
                            cursor.getString(7));
                    Date incidentDate = cursor.getString(8) == null ? null : parse(
                            cursor.getString(8));
                    Boolean followUp =
                            cursor.getString(9) == null || cursor.getInt(9) == 0 ? Boolean.FALSE
                                    : Boolean.TRUE;
                    EnrollmentStatus status =
                            cursor.getString(10) == null ? null : EnrollmentStatus.valueOf(
                                    cursor.getString(10));
                    String trackedEntityInstance = cursor.getString(11) == null ? null
                            : cursor.getString(11);
                    String latitude = cursor.getString(12) == null ? null : cursor.getString(12);
                    String longitude = cursor.getString(13) == null ? null : cursor.getString(13);

                    if (enrollmentMap.get(trackedEntityInstance) == null) {
                        enrollmentMap.put(trackedEntityInstance, new ArrayList<Enrollment>());
                    }

                    Coordinates coordinates = null;

                    if (latitude != null && longitude != null) {
                        coordinates = Coordinates.create(latitude, longitude);
                    }

                    enrollmentMap.get(trackedEntityInstance).add(Enrollment.create(
                            uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient,
                            organisationUnit, program, enrollmentDate, incidentDate, followUp,
                            status, trackedEntityInstance, coordinates, false, null
                    ));

                }
                while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }

        return enrollmentMap;
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(EnrollmentModel.TABLE);
    }
}
