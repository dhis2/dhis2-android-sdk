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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
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

    private static final String UPDATE_STATEMENT = "UPDATE " + EventModel.TABLE + " SET " +
            Columns.UID + " =? , " +
            Columns.ENROLLMENT_UID + " =? , " +
            Columns.CREATED + " =? , " +
            Columns.LAST_UPDATED + " =? ," +
            Columns.STATUS + " =? ," +
            Columns.LATITUDE + " =? ," +
            Columns.LONGITUDE + " =? ," +
            Columns.PROGRAM + " =? ," +
            Columns.PROGRAM_STAGE + " =? , " +
            Columns.ORGANISATION_UNIT + " =?, " +
            Columns.EVENT_DATE + " =? , " +
            Columns.COMPLETE_DATE + " =? , " +
            Columns.DUE_DATE + " =? , " +
            Columns.STATE + " =? " +
            " WHERE " +
            Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            EventModel.TABLE + " WHERE " +
            Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;
    private final DatabaseAdapter databaseAdapter;

    public EventStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String enrollmentUid,
                       @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable EventStatus status, @Nullable String latitude,
                       @Nullable String longitude, @NonNull String program,
                       @NonNull String programStage, @NonNull String organisationUnit,
                       @Nullable Date eventDate, @Nullable Date completedDate,
                       @Nullable Date dueDate, @Nullable State state) {
        insertStatement.clearBindings();

        sqLiteBind(insertStatement, 1, uid);
        sqLiteBind(insertStatement, 2, enrollmentUid);
        sqLiteBind(insertStatement, 3, created);
        sqLiteBind(insertStatement, 4, lastUpdated);
        sqLiteBind(insertStatement, 5, status);
        sqLiteBind(insertStatement, 6, latitude);
        sqLiteBind(insertStatement, 7, longitude);
        sqLiteBind(insertStatement, 8, program);
        sqLiteBind(insertStatement, 9, programStage);
        sqLiteBind(insertStatement, 10, organisationUnit);
        sqLiteBind(insertStatement, 11, eventDate);
        sqLiteBind(insertStatement, 12, completedDate);
        sqLiteBind(insertStatement, 13, dueDate);
        sqLiteBind(insertStatement, 14, state);

        return databaseAdapter.executeInsert(EventModel.TABLE, insertStatement);
    }

    @Override
    public int update(@NonNull String uid, @Nullable String enrollmentUid,
                      @NonNull Date created, @NonNull Date lastUpdated,
                      @NonNull EventStatus eventStatus, @Nullable String latitude,
                      @Nullable String longitude, @NonNull String program,
                      @NonNull String programStage, @NonNull String organisationUnit,
                      @NonNull Date eventDate, @Nullable Date completedDate,
                      @Nullable Date dueDate, @NonNull State state,
                      @NonNull String whereEventUid) {
        updateStatement.clearBindings();

        sqLiteBind(updateStatement, 1, uid);
        sqLiteBind(updateStatement, 2, enrollmentUid);
        sqLiteBind(updateStatement, 3, created);
        sqLiteBind(updateStatement, 4, lastUpdated);
        sqLiteBind(updateStatement, 5, eventStatus);
        sqLiteBind(updateStatement, 6, latitude);
        sqLiteBind(updateStatement, 7, longitude);
        sqLiteBind(updateStatement, 8, program);
        sqLiteBind(updateStatement, 9, programStage);
        sqLiteBind(updateStatement, 10, organisationUnit);
        sqLiteBind(updateStatement, 11, eventDate);
        sqLiteBind(updateStatement, 12, completedDate);
        sqLiteBind(updateStatement, 13, dueDate);
        sqLiteBind(updateStatement, 14, state);

        // bind the where clause
        sqLiteBind(updateStatement, 15, whereEventUid);

        int rowId = databaseAdapter.executeUpdateDelete(EventModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return rowId;
    }

    @Override
    public int delete(@NonNull String uid) {
        deleteStatement.clearBindings();
        sqLiteBind(deleteStatement, 1, uid);

        int rowId = deleteStatement.executeUpdateDelete();
        deleteStatement.clearBindings();

        return rowId;
    }

}
