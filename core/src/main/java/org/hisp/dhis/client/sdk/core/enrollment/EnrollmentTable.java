package org.hisp.dhis.client.sdk.core.enrollment;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.core.program.ProgramTable.ProgramColumns;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;

public interface EnrollmentTable {
    interface EnrollmentColumns extends DbContract.IdColumn, DbContract.UidColumn, DbContract.TimeStampColumns, DbContract.StateColumn {
        String TABLE_NAME = "enrollments";

        String COLUMN_PROGRAM = "program";
        String COLUMN_ORGANISATION_UNIT = "organisationUnit";
        String COLUMN_ENROLLMENT_STATUS = "enrollmentStatus";
        String COLUMN_ENROLLMENT_DATE = "enrollmentDate";
        String COLUMN_INCIDENT_DATE = "incidentDate";
        String COLUMN_TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
        String COLUMN_FOLLOWUP = "followUp";
    }

    String CREATE_TABLE_ENROLLMENTS = "CREATE TABLE IF NOT EXISTS " +
            EnrollmentColumns.TABLE_NAME + " (" +
            EnrollmentColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EnrollmentColumns.COLUMN_UID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE," +
            EnrollmentColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            EnrollmentColumns.COLUMN_LAST_UPDATED + " TEXT," +
            EnrollmentColumns.COLUMN_ENROLLMENT_STATUS + " TEXT NOT NULL," +
            EnrollmentColumns.COLUMN_FOLLOWUP + " TEXT NOT NULL," +
            EnrollmentColumns.COLUMN_ORGANISATION_UNIT + " TEXT NOT NULL," +
            EnrollmentColumns.COLUMN_ENROLLMENT_DATE + " TEXT," +
            EnrollmentColumns.COLUMN_INCIDENT_DATE + " TEXT," +
            EnrollmentColumns.COLUMN_TRACKED_ENTITY_INSTANCE + " TEXT," +
            EnrollmentColumns.COLUMN_PROGRAM + " TEXT," +
            EnrollmentColumns.COLUMN_STATE + " TEXT," +
            "FOREIGN KEY " + "(" + EnrollmentColumns.COLUMN_PROGRAM + ")" +
            "REFERENCES " + ProgramColumns.TABLE_NAME + "(" + ProgramColumns.COLUMN_UID + ")" +
            " ON DELETE CASCADE )";

    String DROP_TABLE_ENROLLMENTS = "DROP TABLE IF EXISTS " +
            EnrollmentColumns.TABLE_NAME;


    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(EnrollmentColumns.TABLE_NAME).build();
    String ENROLLMENTS = EnrollmentColumns.TABLE_NAME;
    String ENROLLMENT_ID = EnrollmentColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(Enrollment.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(Enrollment.class);

    String[] PROJECTION = new String[]{
            EnrollmentColumns.COLUMN_ID,
            EnrollmentColumns.COLUMN_UID,
            EnrollmentColumns.COLUMN_CREATED,
            EnrollmentColumns.COLUMN_LAST_UPDATED,
            EnrollmentColumns.COLUMN_ENROLLMENT_STATUS,
            EnrollmentColumns.COLUMN_FOLLOWUP,
            EnrollmentColumns.COLUMN_ORGANISATION_UNIT,
            EnrollmentColumns.COLUMN_ENROLLMENT_DATE,
            EnrollmentColumns.COLUMN_INCIDENT_DATE,
            EnrollmentColumns.COLUMN_TRACKED_ENTITY_INSTANCE,
            EnrollmentColumns.COLUMN_PROGRAM,
            EnrollmentColumns.COLUMN_STATE
    };
}
