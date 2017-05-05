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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class EnrollmentStoreImpl implements EnrollmentStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + EnrollmentModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
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
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + EnrollmentModel.TABLE + " SET " +
            Columns.UID + " =?, " +
            Columns.CREATED + " =?, " +
            Columns.LAST_UPDATED + " =?, " +
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

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            EnrollmentModel.TABLE + " WHERE " +
            Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final DatabaseAdapter databaseAdapter;

    public EnrollmentStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }


    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @NonNull String organisationUnit, @NonNull String program, @Nullable Date dateOfEnrollment,
                       @Nullable Date dateOfIncident, @Nullable Boolean followUp,
                       @Nullable EnrollmentStatus enrollmentStatus, @NonNull String trackedEntityInstance,
                       @Nullable String latitude, @Nullable String longitude, @Nullable State state) {
        insertStatement.clearBindings();

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, lastUpdated);
        sqLiteBind(insertStatement, 4, organisationUnit);
        sqLiteBind(insertStatement, 5, program);
        sqLiteBind(insertStatement, 6, dateOfEnrollment);
        sqLiteBind(insertStatement, 7, dateOfIncident);
        sqLiteBind(insertStatement, 8, followUp);
        sqLiteBind(insertStatement, 9, enrollmentStatus);
        sqLiteBind(insertStatement, 10, trackedEntityInstance);
        sqLiteBind(insertStatement, 11, latitude);
        sqLiteBind(insertStatement, 12, longitude);
        sqLiteBind(insertStatement, 13, state);

        return databaseAdapter.executeInsert(EnrollmentModel.TABLE, insertStatement);
    }

    @Override
    public int delete(@NonNull String uid) {
        deleteStatement.clearBindings();
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }

    @Override
    public int update(@NonNull String uid, @NonNull Date created, @NonNull Date lastUpdated,
                      @NonNull String organisationUnit, @NonNull String program,
                      @NonNull Date dateOfEnrollment, @Nullable Date dateOfIncident,
                      @Nullable Boolean followUp, @NonNull EnrollmentStatus enrollmentStatus,
                      @NonNull String trackedEntityInstance, @Nullable String latitude,
                      @Nullable String longitude, @NonNull State state, @NonNull String whereEnrollmentUid) {
        updateStatement.clearBindings();

        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, created);
        sqLiteBind(updateStatement, 3, lastUpdated);
        sqLiteBind(updateStatement, 4, organisationUnit);
        sqLiteBind(updateStatement, 5, program);
        sqLiteBind(updateStatement, 6, dateOfEnrollment);
        sqLiteBind(updateStatement, 7, dateOfIncident);
        sqLiteBind(updateStatement, 8, followUp);
        sqLiteBind(updateStatement, 9, enrollmentStatus);
        sqLiteBind(updateStatement, 10, trackedEntityInstance);
        sqLiteBind(updateStatement, 11, latitude);
        sqLiteBind(updateStatement, 12, longitude);
        sqLiteBind(updateStatement, 13, state);

        // bind the where clause
        sqLiteBind(updateStatement, 14, whereEnrollmentUid);

        int rowId = databaseAdapter.executeUpdateDelete(EnrollmentModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return rowId;
    }
}
