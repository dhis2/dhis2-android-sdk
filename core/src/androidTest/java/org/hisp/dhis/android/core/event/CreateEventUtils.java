package org.hisp.dhis.android.core.event;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.State;

import java.text.ParseException;

public class CreateEventUtils {

    private static final Long ID = 3L;
    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";
    private static final State STATE = State.TO_POST;

    // timestamp
    private static final String DATE = "2017-01-12T11:31:00.000";


    public static ContentValues create(String uid, String program, String programStage, String orgUnit) throws ParseException {
        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, uid);
        event.put(EventModel.Columns.ENROLLMENT_UID, ENROLLMENT_UID);
        event.put(EventModel.Columns.CREATED, DATE);
        event.put(EventModel.Columns.LAST_UPDATED, DATE);
        event.put(EventModel.Columns.STATUS, STATUS.name());
        event.put(EventModel.Columns.LATITUDE, LATITUDE);
        event.put(EventModel.Columns.LONGITUDE, LONGITUDE);
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        event.put(EventModel.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventModel.Columns.EVENT_DATE, DATE);
        event.put(EventModel.Columns.COMPLETE_DATE, DATE);
        event.put(EventModel.Columns.DUE_DATE, DATE);
        return event;
    }
}
