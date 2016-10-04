package org.hisp.dhis.client.sdk.core;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.DbContract.CoordinatesColumn;
import org.hisp.dhis.client.sdk.core.DbContract.IdentifiableColumns;
import org.hisp.dhis.client.sdk.core.ProgramStore.ProgramColumns;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class EventStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;

    public static final String CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " +
            EventColumns.TABLE_NAME + " (" +
            EventColumns.COLUMN_UID + " TEXT PRIMARY KEY," +
            EventColumns.COLUMN_NAME + " TEXT NOT NULL," +
            EventColumns.COLUMN_DISPLAY_NAME + " TEXT NOT NULL," +
            EventColumns.COLUMN_CODE + " INTEGER," +
            EventColumns.COLUMN_CREATED + " DATE NOT NULL," +
            EventColumns.COLUMN_LAST_UPDATED + " DATE NOT NULL," +
            EventColumns.COLUMN_EVENT_STATUS + " TEXT NOT NULL," +
            EventColumns.COLUMN_PROGRAM_STAGE + " TEXT NOT NULL," +
            EventColumns.COLUMN_ORGANISATION_UNIT + " TEXT NOT NULL," +
            EventColumns.COLUMN_EVENT_DATE + " DATE," +
            EventColumns.COLUMN_COMPLETED_DATE + " DATE," +
            EventColumns.COLUMN_LONGITUDE + " REAL," +
            EventColumns.COLUMN_LATITUDE + " REAL," +
            EventColumns.COLUMN_PROGRAM + " TEXT" +
            "FOREIGN KEY(" + EventColumns.COLUMN_PROGRAM + ")" +
            "REFERENCES " + ProgramColumns.TABLE_NAME +
            "(" + ProgramColumns.COLUMN_NAME_KEY + ")" + " )";

    public static final String DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " +
            EventColumns.TABLE_NAME;

    public List<Event> list(OrganisationUnit organisationUnit, Program program) {
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        String selection = EventColumns.COLUMN_ORGANISATION_UNIT + " EQUALS ? + and " + EventColumns.COLUMN_PROGRAM + " EQUALS ?";
        String[] selectionArgs = new String[]{organisationUnit.getUid(), program.getUid()};
        Cursor cursor = database.query(EventColumns.TABLE_NAME, null,
                selection, selectionArgs, null, null, null);

        List<Event> events = new ArrayList<>();
        return events;
    }

    public interface EventColumns extends IdentifiableColumns, CoordinatesColumn {
        String TABLE_NAME = "event";
        String COLUMN_PROGRAM = "program";
        String COLUMN_PROGRAM_STAGE = "programStage";
        String COLUMN_ORGANISATION_UNIT = "organisationUnit";
        String COLUMN_EVENT_STATUS = "eventStatus";
        String COLUMN_EVENT_DATE = "eventDate";
        String COLUMN_COMPLETED_DATE = "completedDate";
    }

    public EventStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public synchronized boolean save(List<Event> events) throws JsonProcessingException {
        isNull(events, "Events cannot be null");
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = mapToContentValues(events);

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(ProgramColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();
        return true;
    }

    private List<ContentValues> mapToContentValues(List<Event> events) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (Event event : events) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(EventColumns.COLUMN_UID, event.getUid());
            contentValues.put(EventColumns.COLUMN_NAME, event.getName());
            contentValues.put(EventColumns.COLUMN_DISPLAY_NAME, event.getDisplayName());
            contentValues.put(EventColumns.COLUMN_COMPLETED_DATE, event.getCompletedDate().toString());
            contentValues.put(EventColumns.COLUMN_EVENT_DATE, event.getEventDate().toString());
            contentValues.put(EventColumns.COLUMN_EVENT_STATUS, event.getStatus().toString());
            contentValues.put(EventColumns.COLUMN_ORGANISATION_UNIT, event.getOrgUnit());
            contentValues.put(EventColumns.COLUMN_PROGRAM, event.getProgram());
            contentValues.put(EventColumns.COLUMN_PROGRAM_STAGE, event.getProgramStage());
            contentValues.put(EventColumns.COLUMN_CODE, event.getCode());
            contentValues.put(EventColumns.COLUMN_CREATED, event.getCreated().toString());
            contentValues.put(EventColumns.COLUMN_LAST_UPDATED, event.getLastUpdated().toString());
            contentValues.put(EventColumns.COLUMN_LATITUDE, event.getCoordinate().getLatitude());
            contentValues.put(EventColumns.COLUMN_LONGITUDE, event.getCoordinate().getLongitude());

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    public List<Event> list() {
        List<Event> events = new ArrayList<>();

        String[] projection = new String[]{
                EventColumns.COLUMN_UID,
                EventColumns.COLUMN_NAME,
                EventColumns.COLUMN_DISPLAY_NAME,
                EventColumns.COLUMN_COMPLETED_DATE,
                EventColumns.COLUMN_EVENT_DATE,
                EventColumns.COLUMN_EVENT_STATUS,
                EventColumns.COLUMN_ORGANISATION_UNIT,
                EventColumns.COLUMN_PROGRAM,
                EventColumns.COLUMN_PROGRAM_STAGE,
                EventColumns.COLUMN_CODE,
                EventColumns.COLUMN_CREATED,
                EventColumns.COLUMN_LAST_UPDATED,
                EventColumns.COLUMN_LATITUDE,
                EventColumns.COLUMN_LONGITUDE
        };

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(EventColumns.TABLE_NAME, projection,
                null, null, null, null, null);


        return events;
    }

    /**
     * Right now will only return you list of events with uid. Needs to be modified
     *
     * @param projection
     * @return
     */
    public List<Event> listBy(String[] projection) {
        //TODO Implement proper listBy functionality
        isNull(projection, "Projection must not be null");

        List<Event> events = new ArrayList<>();

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(EventColumns.TABLE_NAME, projection,
                null, null, null, null, null);


        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                // needs to loop through projection. (database table).
                String uid = cursor.getString(0);
                Event event = new Event();
                event.setUid(uid);

                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return events;
    }

    public Event get(String uid) {
        //TODO: get proper Event
        return new Event();
    }
}
