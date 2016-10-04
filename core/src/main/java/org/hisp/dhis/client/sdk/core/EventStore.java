package org.hisp.dhis.client.sdk.core;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.DbContract.CoordinatesColumn;
import org.hisp.dhis.client.sdk.core.DbContract.IdentifiableColumns;
import org.hisp.dhis.client.sdk.core.ProgramStore.ProgramColumns;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.event.Event.EventStatus;
import org.hisp.dhis.client.sdk.models.option.OptionSet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class EventStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;
    private SimpleDateFormat simpleDateFormat;
    private String dateFormat = "yyyy-MM-dd";

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
            EventColumns.COLUMN_PROGRAM + " TEXT"  +
            "FOREIGN KEY(" + EventColumns.COLUMN_PROGRAM + ")"  +
            "REFERENCES "+ ProgramColumns.TABLE_NAME +
            "("+ ProgramColumns.COLUMN_NAME_KEY + ")"  + " )";

    public static final String DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " +
            EventColumns.TABLE_NAME;

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
        this.simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
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
                EventColumns.COLUMN_EVENT_STATUS,
                EventColumns.COLUMN_ORGANISATION_UNIT,
                EventColumns.COLUMN_PROGRAM,
                EventColumns.COLUMN_PROGRAM_STAGE,
                EventColumns.COLUMN_CODE,
                EventColumns.COLUMN_LATITUDE,
                EventColumns.COLUMN_LONGITUDE,
                EventColumns.COLUMN_COMPLETED_DATE,
                EventColumns.COLUMN_EVENT_DATE,
                EventColumns.COLUMN_CREATED,
                EventColumns.COLUMN_LAST_UPDATED
        };

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(EventColumns.TABLE_NAME, projection,
                null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String uid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String displayName = cursor.getString(2);

                    String eventStatus = cursor.getString(3);
                    EventStatus eventStatusEnum = null;
                    for (EventStatus status : EventStatus.values()) {
                        if(status.toString().equals(eventStatus)) {
                            eventStatusEnum = status;
                        }
                    }

                    String organisationUnit = cursor.getString(4);
                    String program = cursor.getString(5);
                    String programStage = cursor.getString(6);
                    String code = cursor.getString(7);
                    double latitude = cursor.getDouble(8);
                    double longitude = cursor.getDouble(9);


                    Date completedDate = null;
                    Date eventDate = null;
                    Date created = null;
                    Date lastUpdated = null;
                    try {
                        completedDate = simpleDateFormat.parse(cursor.getString(10));
                        eventDate = simpleDateFormat.parse(cursor.getString(11));
                        created = simpleDateFormat.parse(cursor.getString(12));
                        lastUpdated = simpleDateFormat.parse(cursor.getString(13));
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }
                    Event event = new Event();
                    event.setUid(uid);
                    event.setName(name);
                    event.setDisplayName(displayName);
                    event.setStatus(eventStatusEnum);
                    event.setOrgUnit(organisationUnit);
                    event.setProgram(program);
                    event.setProgramStage(programStage);
                    event.setCode(code);
                    event.setCoordinate(new Coordinates(latitude,longitude));
                    event.setCompletedDate(completedDate);
                    event.setEventDate(eventDate);
                    event.setCreated(created);
                    event.setLastUpdated(lastUpdated);

                    events.add(event);
                } while (cursor.moveToNext());
            }
        }  finally {
            cursor.close();
        }


        return events;
    }

    /**
     * Right now will only return you list of events with uid. Needs to be modified
     * @param projection
     * @return
     */
    public List<Event> listBy(String[] projection) {
        //TODO Implement proper listBy functionality
        isNull(projection, "Projection must not be null");

        List<Event> events = new ArrayList<>();
        SparseArray<String> columnIndices = new SparseArray<>();
        for (int i = 0; i < projection.length; i++) {
            columnIndices.append(i, projection[i]);
        }
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(EventColumns.TABLE_NAME, projection,
                null, null, null, null, null);


        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Event event = new Event();
                ParseException error = null;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (columnIndices.get(i)) {
                        case EventColumns.COLUMN_UID:
                            String uid = cursor.getString(i);
                            event.setUid(uid);
                            break;
                        case EventColumns.COLUMN_NAME:
                            String name = cursor.getString(i);
                            event.setName(name);
                            break;
                        case EventColumns.COLUMN_DISPLAY_NAME:
                            String displayName = cursor.getString(i);
                            event.setDisplayName(displayName);
                            break;
                        case EventColumns.COLUMN_EVENT_STATUS:
                            String eventStatus = cursor.getString(i);
                            EventStatus eventStatusEnum = null;
                            for (EventStatus status : EventStatus.values()) {
                                if(status.toString().equals(eventStatus)) {
                                    eventStatusEnum = status;
                                }
                            }
                            event.setStatus(eventStatusEnum);
                            break;
                        case EventColumns.COLUMN_ORGANISATION_UNIT:
                            String organisationUnit = cursor.getString(i);
                            event.setOrgUnit(organisationUnit);
                            break;
                        case EventColumns.COLUMN_PROGRAM:
                            String program = cursor.getString(i);
                            event.setProgram(program);
                            break;
                        case EventColumns.COLUMN_PROGRAM_STAGE:
                            String programStage = cursor.getString(i);
                            event.setProgramStage(programStage);
                            break;
                        case EventColumns.COLUMN_CODE:
                            String code = cursor.getString(i);
                            event.setCode(code);
                            break;
                        case EventColumns.COLUMN_LATITUDE:
                            double latitude = cursor.getDouble(i);
                            double lng = 0.00;
                            if(event.getCoordinate() != null) {
                                lng = event.getCoordinate().getLongitude();
                                event.setCoordinate(new Coordinates(latitude,lng));
                            }
                            else {
                                event.setCoordinate(new Coordinates(latitude, 0.00));
                            }
                            break;
                        case EventColumns.COLUMN_LONGITUDE:
                            double longitude = cursor.getDouble(i);
                            double lat = 0.00;
                            if(event.getCoordinate() != null) {
                                lat = event.getCoordinate().getLatitude();
                                event.setCoordinate(new Coordinates(lat,longitude));
                            }
                            else {
                                event.setCoordinate(new Coordinates(0.00, longitude));
                            }
                            break;
                        case EventColumns.COLUMN_COMPLETED_DATE:
                            String completedDateString = cursor.getString(i);
                            Date completedDate = null;
                            try {
                                completedDate = simpleDateFormat.parse(completedDateString);
                            } catch (ParseException e) {
                                error = e;
                                e.printStackTrace();
                            }
                            event.setCompletedDate(completedDate);
                            break;
                        case EventColumns.COLUMN_EVENT_DATE:
                            String eventDateString = cursor.getString(i);
                            Date eventDate = null;
                            try {
                                eventDate = simpleDateFormat.parse(eventDateString);
                            } catch (ParseException e) {
                                error = e;
                                e.printStackTrace();
                            }
                            event.setEventDate(eventDate);
                            break;
                        case EventColumns.COLUMN_CREATED:
                            String createdString = cursor.getString(i);
                            Date created = null;
                            try {
                                created = simpleDateFormat.parse(createdString);
                            } catch (ParseException e) {
                                error = e;
                                e.printStackTrace();
                            }
                            event.setCreated(created);
                            break;
                        case EventColumns.COLUMN_LAST_UPDATED:
                            String lastUpdatedString = cursor.getString(i);
                            Date lastUpdated = null;
                            try {
                                lastUpdated = simpleDateFormat.parse(lastUpdatedString);
                            } catch (ParseException e) {
                                error = e;
                                e.printStackTrace();
                            }
                            event.setLastUpdated(lastUpdated);
                            break;


                    }
                }
                if (error == null) {
                    events.add(event);
                }

                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return events;
    }
}
