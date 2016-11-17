package org.hisp.dhis.client.sdk.core.event;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentTable.EnrollmentColumns;
import org.hisp.dhis.client.sdk.core.program.ProgramTable.ProgramColumns;
import org.hisp.dhis.client.sdk.models.event.Event;

public interface EventTable {
    interface EventColumns extends DbContract.IdColumn, DbContract.UidColumn, DbContract.TimeStampColumns, DbContract.CoordinatesColumn, DbContract.StateColumn {
        String TABLE_NAME = "events";

        String COLUMN_PROGRAM = "program";
        String COLUMN_PROGRAM_STAGE = "programStage";
        String COLUMN_ORGANISATION_UNIT = "organisationUnit";
        String COLUMN_ENROLLMENT = "enrollmentUid";
        String COLUMN_EVENT_STATUS = "eventStatus";
        String COLUMN_EVENT_DATE = "eventDate";
        String COLUMN_DUE_DATE = "dueDate";
        String COLUMN_COMPLETED_DATE = "completedDate";
    }

    String CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " +
            EventColumns.TABLE_NAME + " (" +
            EventColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EventColumns.COLUMN_UID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE," +
            EventColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            EventColumns.COLUMN_LAST_UPDATED + " TEXT," +
            EventColumns.COLUMN_EVENT_STATUS + " TEXT NOT NULL," +
            EventColumns.COLUMN_PROGRAM_STAGE + " TEXT NOT NULL," +
            EventColumns.COLUMN_ORGANISATION_UNIT + " TEXT NOT NULL," +
            EventColumns.COLUMN_ENROLLMENT + " TEXT," +
            EventColumns.COLUMN_EVENT_DATE + " TEXT," +
            EventColumns.COLUMN_DUE_DATE + " TEXT," +
            EventColumns.COLUMN_COMPLETED_DATE + " TEXT," +
            EventColumns.COLUMN_LONGITUDE + " REAL," +
            EventColumns.COLUMN_LATITUDE + " REAL," +
            EventColumns.COLUMN_PROGRAM + " TEXT," +
            EventColumns.COLUMN_STATE + " TEXT," +
            "FOREIGN KEY " + "(" +
            EventColumns.COLUMN_PROGRAM + ")" +
            "REFERENCES " + ProgramColumns.TABLE_NAME + "(" + ProgramColumns.COLUMN_UID + ")" +
            " FOREIGN KEY " + "(" + EventColumns.COLUMN_ENROLLMENT + ")" +
            "REFERENCES " + EnrollmentColumns.TABLE_NAME + "(" + EnrollmentColumns.COLUMN_UID + ")" +
            " ON DELETE CASCADE )";

    String DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " +
            EventColumns.TABLE_NAME;


    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(EventTable.EventColumns.TABLE_NAME).build();
    String EVENTS = EventColumns.TABLE_NAME;
    String EVENT_ID = EventColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(Event.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(Event.class);

    String[] PROJECTION = new String[]{
            EventColumns.COLUMN_ID,
            EventColumns.COLUMN_UID,
            EventColumns.COLUMN_CREATED,
            EventColumns.COLUMN_LAST_UPDATED,
            EventColumns.COLUMN_EVENT_STATUS,
            EventColumns.COLUMN_PROGRAM_STAGE,
            EventColumns.COLUMN_ORGANISATION_UNIT,
            EventColumns.COLUMN_EVENT_DATE,
            EventColumns.COLUMN_DUE_DATE,
            EventColumns.COLUMN_COMPLETED_DATE,
            EventColumns.COLUMN_LONGITUDE,
            EventColumns.COLUMN_LATITUDE,
            EventColumns.COLUMN_PROGRAM,
            EventColumns.COLUMN_STATE
    };
}
