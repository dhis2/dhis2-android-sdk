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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class EventStoreImpl implements EventStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + EventModel.TABLE + " (" +
            Columns.UID + ", " +
            Columns.ENROLLMENT_UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.STATUS + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.EVENT_DATE + ", " +
            Columns.COMPLETE_DATE + ", " +
            Columns.DUE_DATE + ", " +
            Columns.STATE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public EventStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String enrollmentUid,
                       @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable EventStatus status, @Nullable String latitude,
                       @Nullable String longitude, @NonNull String program,
                       @NonNull String programStage, @NonNull String organisationUnit,
                       @Nullable Date eventDate, @Nullable Date completedDate,
                       @Nullable Date dueDate, @Nullable State state) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, enrollmentUid);
        sqLiteBind(sqLiteStatement, 3, created);
        sqLiteBind(sqLiteStatement, 4, lastUpdated);
        sqLiteBind(sqLiteStatement, 5, status);
        sqLiteBind(sqLiteStatement, 6, latitude);
        sqLiteBind(sqLiteStatement, 7, longitude);
        sqLiteBind(sqLiteStatement, 8, program);
        sqLiteBind(sqLiteStatement, 9, programStage);
        sqLiteBind(sqLiteStatement, 10, organisationUnit);
        sqLiteBind(sqLiteStatement, 11, eventDate);
        sqLiteBind(sqLiteStatement, 12, completedDate);
        sqLiteBind(sqLiteStatement, 13, dueDate);
        sqLiteBind(sqLiteStatement, 14, state);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
