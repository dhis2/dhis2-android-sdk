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

package org.hisp.dhis.android.core.program;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class ProgramStoreImpl implements ProgramStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + ProgramModel.TABLE + " (" +
            ProgramModel.Columns.UID + ", " +
            ProgramModel.Columns.CODE + ", " +
            ProgramModel.Columns.NAME + ", " +
            ProgramModel.Columns.DISPLAY_NAME + ", " +
            ProgramModel.Columns.CREATED + ", " +
            ProgramModel.Columns.LAST_UPDATED + ", " +
            ProgramModel.Columns.SHORT_NAME + ", " +
            ProgramModel.Columns.DISPLAY_SHORT_NAME + ", " +
            ProgramModel.Columns.DESCRIPTION + ", " +
            ProgramModel.Columns.DISPLAY_DESCRIPTION + ", " +
            ProgramModel.Columns.VERSION + ", " +
            ProgramModel.Columns.ONLY_ENROLL_ONCE + ", " +
            ProgramModel.Columns.ENROLLMENT_DATE_LABEL + ", " +
            ProgramModel.Columns.DISPLAY_INCIDENT_DATE + ", " +
            ProgramModel.Columns.INCIDENT_DATE_LABEL + ", " +
            ProgramModel.Columns.REGISTRATION + ", " +
            ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE + ", " +
            ProgramModel.Columns.DATA_ENTRY_METHOD + ", " +
            ProgramModel.Columns.IGNORE_OVERDUE_EVENTS + ", " +
            ProgramModel.Columns.RELATIONSHIP_FROM_A + ", " +
            ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE + ", " +
            ProgramModel.Columns.CAPTURE_COORDINATES + ", " +
            ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION + ", " +
            ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST + ", " +
            ProgramModel.Columns.PROGRAM_TYPE + ", " +
            ProgramModel.Columns.RELATIONSHIP_TYPE + ", " +
            ProgramModel.Columns.RELATIONSHIP_TEXT + ", " +
            ProgramModel.Columns.RELATED_PROGRAM + ", " +
            ProgramModel.Columns.TRACKED_ENTITY + ", " +
            ProgramModel.Columns.CATEGORY_COMBO + ", " +
            ProgramModel.Columns.ACCESS_DATA_WRITE + ") " +
            "VALUES (" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + ProgramModel.TABLE + " SET " +
            ProgramModel.Columns.UID + " =?, " +
            ProgramModel.Columns.CODE + " =?, " +
            ProgramModel.Columns.NAME + " =?, " +
            ProgramModel.Columns.DISPLAY_NAME + " =?, " +
            ProgramModel.Columns.CREATED + " =?, " +
            ProgramModel.Columns.LAST_UPDATED + " =?, " +
            ProgramModel.Columns.SHORT_NAME + " =?, " +
            ProgramModel.Columns.DISPLAY_SHORT_NAME + " =?, " +
            ProgramModel.Columns.DESCRIPTION + " =?, " +
            ProgramModel.Columns.DISPLAY_DESCRIPTION + " =?, " +
            ProgramModel.Columns.VERSION + " =?, " +
            ProgramModel.Columns.ONLY_ENROLL_ONCE + " =?, " +
            ProgramModel.Columns.ENROLLMENT_DATE_LABEL + " =?, " +
            ProgramModel.Columns.DISPLAY_INCIDENT_DATE + " =?, " +
            ProgramModel.Columns.INCIDENT_DATE_LABEL + " =?, " +
            ProgramModel.Columns.REGISTRATION + " =?, " +
            ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE + " =?, " +
            ProgramModel.Columns.DATA_ENTRY_METHOD + " =?, " +
            ProgramModel.Columns.IGNORE_OVERDUE_EVENTS + " =?, " +
            ProgramModel.Columns.RELATIONSHIP_FROM_A + " =?, " +
            ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE + " =?, " +
            ProgramModel.Columns.CAPTURE_COORDINATES + " =?, " +
            ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION + " =?, " +
            ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST + " =?, " +
            ProgramModel.Columns.PROGRAM_TYPE + " =?, " +
            ProgramModel.Columns.RELATIONSHIP_TYPE + " =?, " +
            ProgramModel.Columns.RELATIONSHIP_TEXT + " =?, " +
            ProgramModel.Columns.RELATED_PROGRAM + " =?, " +
            ProgramModel.Columns.TRACKED_ENTITY + " =?, " +
            ProgramModel.Columns.CATEGORY_COMBO + " =?, " +
            ProgramModel.Columns.ACCESS_DATA_WRITE + " =? " +
            " WHERE " +
            ProgramModel.Columns.UID + " = ?;";


    private static final String DELETE_STATEMENT = "DELETE FROM " + ProgramModel.TABLE + " WHERE " +
            ProgramModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public ProgramStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @Nullable String shortName,
            @Nullable String displayShortName,
            @Nullable String description,
            @Nullable String displayDescription,
            @Nullable Integer version,
            @Nullable Boolean onlyEnrollOnce,
            @Nullable String enrollmentDateLabel,
            @Nullable Boolean displayIncidentDate,
            @Nullable String incidentDateLabel,
            @Nullable Boolean registration,
            @Nullable Boolean selectEnrollmentDatesInFuture,
            @Nullable Boolean dataEntryMethod,
            @Nullable Boolean ignoreOverdueEvents,
            @Nullable Boolean relationshipFromA,
            @Nullable Boolean selectIncidentDatesInFuture,
            @Nullable Boolean captureCoordinates,
            @Nullable Boolean useFirstStageDuringRegistration,
            @Nullable Boolean displayInFrontPageList,
            @Nullable ProgramType programType,
            @Nullable String relationshipType,
            @Nullable String relationshipText,
            @Nullable String relatedProgram,
            @Nullable String trackedEntity,
            @Nullable String categoryCombo,
            @Nullable Boolean accessDataWrite
    ) {

        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
                description, displayDescription, version, onlyEnrollOnce, enrollmentDateLabel, displayIncidentDate,
                incidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod,
                ignoreOverdueEvents, relationshipFromA, selectIncidentDatesInFuture, captureCoordinates,
                useFirstStageDuringRegistration, displayInFrontPageList, programType,
                relationshipType, relationshipText, relatedProgram, trackedEntity, categoryCombo, accessDataWrite);

        Long insert = databaseAdapter.executeInsert(ProgramModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return insert;
    }

    @Override
    public int update(@NonNull String uid,
                      @Nullable String code,
                      @NonNull String name,
                      @Nullable String displayName,
                      @Nullable Date created,
                      @Nullable Date lastUpdated,
                      @Nullable String shortName,
                      @Nullable String displayShortName,
                      @Nullable String description,
                      @Nullable String displayDescription,
                      @Nullable Integer version,
                      @Nullable Boolean onlyEnrollOnce,
                      @Nullable String enrollmentDateLabel,
                      @Nullable Boolean displayIncidentDate,
                      @Nullable String incidentDateLabel,
                      @Nullable Boolean registration,
                      @Nullable Boolean selectEnrollmentDatesInFuture,
                      @Nullable Boolean dataEntryMethod,
                      @Nullable Boolean ignoreOverdueEvents,
                      @Nullable Boolean relationshipFromA,
                      @Nullable Boolean selectIncidentDatesInFuture,
                      @Nullable Boolean captureCoordinates,
                      @Nullable Boolean useFirstStageDuringRegistration,
                      @Nullable Boolean displayInFrontPageList,
                      @Nullable ProgramType programType,
                      @Nullable String relationshipType,
                      @Nullable String relationshipText,
                      @Nullable String relatedProgram,
                      @Nullable String trackedEntity,
                      @Nullable String categoryCombo,
                      @Nullable Boolean accessDataWrite,
                      @NonNull String whereProgramUid) {
        isNull(uid);
        isNull(whereProgramUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
                description, displayDescription, version, onlyEnrollOnce, enrollmentDateLabel, displayIncidentDate,
                incidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod,
                ignoreOverdueEvents, relationshipFromA, selectIncidentDatesInFuture, captureCoordinates,
                useFirstStageDuringRegistration, displayInFrontPageList, programType,
                relationshipType, relationshipText, relatedProgram, trackedEntity, categoryCombo,
                accessDataWrite);

        // bind the where argument
        sqLiteBind(updateStatement, 32, whereProgramUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(ProgramModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return update;
    }


    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(ProgramModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();

        return delete;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
                               @NonNull String uid,
                               @Nullable String code,
                               @NonNull String name,
                               @Nullable String displayName,
                               @Nullable Date created,
                               @Nullable Date lastUpdated,
                               @Nullable String shortName,
                               @Nullable String displayShortName,
                               @Nullable String description,
                               @Nullable String displayDescription,
                               @Nullable Integer version,
                               @Nullable Boolean onlyEnrollOnce,
                               @Nullable String enrollmentDateLabel,
                               @Nullable Boolean displayIncidentDate,
                               @Nullable String incidentDateLabel,
                               @Nullable Boolean registration,
                               @Nullable Boolean selectEnrollmentDatesInFuture,
                               @Nullable Boolean dataEntryMethod,
                               @Nullable Boolean ignoreOverdueEvents,
                               @Nullable Boolean relationshipFromA,
                               @Nullable Boolean selectIncidentDatesInFuture,
                               @Nullable Boolean captureCoordinates,
                               @Nullable Boolean useFirstStageDuringRegistration,
                               @Nullable Boolean displayInFrontPageList,
                               @Nullable ProgramType programType,
                               @Nullable String relationshipType,
                               @Nullable String relationshipText,
                               @Nullable String relatedProgram,
                               @Nullable String trackedEntity,
                               @Nullable String categoryCombo,
                               @Nullable boolean accessDataWrite) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, shortName);
        sqLiteBind(sqLiteStatement, 8, displayShortName);
        sqLiteBind(sqLiteStatement, 9, description);
        sqLiteBind(sqLiteStatement, 10, displayDescription);
        sqLiteBind(sqLiteStatement, 11, version);
        sqLiteBind(sqLiteStatement, 12, onlyEnrollOnce);
        sqLiteBind(sqLiteStatement, 13, enrollmentDateLabel);
        sqLiteBind(sqLiteStatement, 14, displayIncidentDate);
        sqLiteBind(sqLiteStatement, 15, incidentDateLabel);
        sqLiteBind(sqLiteStatement, 16, registration);
        sqLiteBind(sqLiteStatement, 17, selectEnrollmentDatesInFuture);
        sqLiteBind(sqLiteStatement, 18, dataEntryMethod);
        sqLiteBind(sqLiteStatement, 19, ignoreOverdueEvents);
        sqLiteBind(sqLiteStatement, 20, relationshipFromA);
        sqLiteBind(sqLiteStatement, 21, selectIncidentDatesInFuture);
        sqLiteBind(sqLiteStatement, 22, captureCoordinates);
        sqLiteBind(sqLiteStatement, 23, useFirstStageDuringRegistration);
        sqLiteBind(sqLiteStatement, 24, displayInFrontPageList);
        sqLiteBind(sqLiteStatement, 25, programType.name());
        sqLiteBind(sqLiteStatement, 26, relationshipType);
        sqLiteBind(sqLiteStatement, 27, relationshipText);
        sqLiteBind(sqLiteStatement, 28, relatedProgram);
        sqLiteBind(sqLiteStatement, 29, trackedEntity);
        sqLiteBind(sqLiteStatement, 30, categoryCombo);
        sqLiteBind(sqLiteStatement, 31, accessDataWrite);
    }

    @Override
    public int delete() {
        return databaseAdapter.delete(ProgramModel.TABLE);
    }
}
