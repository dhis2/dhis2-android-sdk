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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

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

    private final SQLiteStatement sqLiteStatement;

    public EnrollmentStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }


    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @NonNull String organisationUnit, @NonNull String program, @Nullable Date dateOfEnrollment,
                       @Nullable Date dateOfIncident, @Nullable Boolean followUp,
                       @Nullable EnrollmentStatus enrollmentStatus, @NonNull String trackedEntityInstance,
                       @Nullable String latitude, @Nullable String longitude, @Nullable State state) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, created);
        sqLiteBind(sqLiteStatement, 3, lastUpdated);
        sqLiteBind(sqLiteStatement, 4, organisationUnit);
        sqLiteBind(sqLiteStatement, 5, program);
        sqLiteBind(sqLiteStatement, 6, dateOfEnrollment);
        sqLiteBind(sqLiteStatement, 7, dateOfIncident);
        sqLiteBind(sqLiteStatement, 8, followUp);
        sqLiteBind(sqLiteStatement, 9, enrollmentStatus);
        sqLiteBind(sqLiteStatement, 10, trackedEntityInstance);
        sqLiteBind(sqLiteStatement, 11, latitude);
        sqLiteBind(sqLiteStatement, 12, longitude);
        sqLiteBind(sqLiteStatement, 13, state);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
