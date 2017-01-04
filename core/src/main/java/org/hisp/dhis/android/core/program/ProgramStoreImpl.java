package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.relationship.RelationshipType;

import java.util.Date;

public class ProgramStoreImpl implements ProgramStore {

    private static final String INSERT_STATEMENT = "CREATE TABLE " + Tables.PROGRAM + " (" +
            ProgramContract.Columns.ID + ", " +
            ProgramContract.Columns.UID + ", " +
            ProgramContract.Columns.CODE + ", " +
            ProgramContract.Columns.NAME + ", " +
            ProgramContract.Columns.DISPLAY_NAME + ", " +
            ProgramContract.Columns.CREATED + ", " +
            ProgramContract.Columns.LAST_UPDATED + ", " +
            ProgramContract.Columns.SHORT_NAME + ", " +
            ProgramContract.Columns.DISPLAY_SHORT_NAME + ", " +
            ProgramContract.Columns.DESCRIPTION + ", " +
            ProgramContract.Columns.DISPLAY_DESCRIPTION + ", " +
            ProgramContract.Columns.VERSION + ", " +
            ProgramContract.Columns.ONLY_ENROLL_ONCE + ", " +
            ProgramContract.Columns.ENROLLMENT_DATE_LABEL + ", " +
            ProgramContract.Columns.DISPLAY_INCIDENT_DATE + ", " +
            ProgramContract.Columns.INCIDENT_DATE_LABEL + ", " +
            ProgramContract.Columns.REGISTRATION + ", " +
            ProgramContract.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE + ", " +
            ProgramContract.Columns.DATA_ENTRY_METHOD + ", " +
            ProgramContract.Columns.IGNORE_OVERDUE_EVENTS + ", " +
            ProgramContract.Columns.RELATIONSHIP_FROM_A + ", " +
            ProgramContract.Columns.SELECT_INCIDENT_DATES_IN_FUTURE + ", " +
            ProgramContract.Columns.CAPTURE_COORDINATES + ", " +
            ProgramContract.Columns.USE_FIRST_STAGE_DURING_REGISTRATION + ", " +
            ProgramContract.Columns.DISPLAY_FRONT_PAGE_LIST + ", " +
            ProgramContract.Columns.PROGRAM_TYPE + ", " +
            ProgramContract.Columns.RELATIONSHIP_TYPE + ", " +
            ProgramContract.Columns.RELATIONSHIP_TEXT + ", " +
            ProgramContract.Columns.RELATED_PROGRAM + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(
            @NonNull String uid,
            @NonNull String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
            @NonNull Date lastUpdated,
            @NonNull String shortName,
            @NonNull String displayShortName,
            @NonNull String description,
            @NonNull String displayDescription,
            @NonNull Integer version,
            @NonNull Boolean onlyEnrollOnce,
            @NonNull String enrollmentDateLabel,
            @NonNull Boolean displayIncidentDate,
            @NonNull String incidentDateLabel,
            @NonNull Boolean registration,
            @NonNull Boolean selectEnrollmentDatesInFuture,
            @NonNull Boolean dataEntryMethod,
            @NonNull Boolean ignoreOverdueEvents,
            @NonNull Boolean relationshipFromA,
            @NonNull Boolean selectIncidentDatesInFuture,
            @NonNull Boolean captureCoordinates,
            @NonNull Boolean useFirstStageDuringRegistration,
            @NonNull Boolean displayInFrontPageList,
            @NonNull ProgramType programType,
            @NonNull RelationshipType relationshipType,
            @NonNull String relationshipText,
            Program relatedProgram) {

        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, uid);
        sqLiteStatement.bindString(2, code);
        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        sqLiteStatement.bindString(7, shortName);
        sqLiteStatement.bindString(8, displayShortName);
        sqLiteStatement.bindString(9, displayDescription);
        sqLiteStatement.bindLong(10, version);
        sqLiteStatement.bindString(11, onlyEnrollOnce.toString());
        sqLiteStatement.bindString(12, enrollmentDateLabel);
        sqLiteStatement.bindLong(13, boolToInt(displayIncidentDate));
        sqLiteStatement.bindString(14, incidentDateLabel);
        sqLiteStatement.bindLong(15, boolToInt(registration));
        sqLiteStatement.bindLong(16, boolToInt(selectEnrollmentDatesInFuture));
        sqLiteStatement.bindLong(17, boolToInt(dataEntryMethod));
        sqLiteStatement.bindLong(18, boolToInt(ignoreOverdueEvents));
        sqLiteStatement.bindLong(19, boolToInt(relationshipFromA));
        sqLiteStatement.bindLong(20, boolToInt(selectIncidentDatesInFuture));
        sqLiteStatement.bindLong(21, boolToInt(captureCoordinates));
        sqLiteStatement.bindLong(22, boolToInt(useFirstStageDuringRegistration));
        sqLiteStatement.bindLong(23, boolToInt(displayInFrontPageList));
        sqLiteStatement.bindString(24, programType.name());
        sqLiteStatement.bindString(25, relationshipType.uid()); //TODO: store the relationship in it's table.
        sqLiteStatement.bindString(26, relationshipText);
        sqLiteStatement.bindString(27, relatedProgram.uid());

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }

    private int boolToInt(Boolean value) {
        if (value) return 1;
        return 0;
    }
}
