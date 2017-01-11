package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramStoreImpl implements ProgramStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM + " (" +
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
            ProgramModel.Columns.TRACKED_ENTITY + ") " +
            "VALUES (" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
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
            @NonNull ProgramType programType,
            @Nullable String relationshipType,
            @Nullable String relationshipText,
            @Nullable String relatedProgram,
            @Nullable String trackedEntity
            //TODO: add category combo when implemented.
//            @NonNull CategoryCombo categoryCombo
    ) {

        sqLiteStatement.clearBindings();

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

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
