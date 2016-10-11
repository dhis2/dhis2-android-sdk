/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.event;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.event.EventStatus;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getDouble;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getInt;
import static org.hisp.dhis.client.sdk.core.commons.DbUtils.getString;

public class EventMapper implements Mapper<Event> {
    public interface EventColumns extends DbContract.IdentifiableColumns, DbContract.CoordinatesColumn, DbContract.StateColumn {
        String TABLE_NAME = "events";

        String COLUMN_PROGRAM = "program";
        String COLUMN_PROGRAM_STAGE = "programStage";
        String COLUMN_ORGANISATION_UNIT = "organisationUnit";
        String COLUMN_EVENT_STATUS = "eventStatus";
        String COLUMN_EVENT_DATE = "eventDate";
        String COLUMN_COMPLETED_DATE = "completedDate";
    }

    public static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(EventColumns.TABLE_NAME).build();

    public static final String EVENTS = EventColumns.TABLE_NAME;
    public static final String EVENTS_ID = EventColumns.TABLE_NAME + "/#";

    public static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/org.hisp.dhis.models.Event";
    public static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/org.hisp.dhis.models.Event";

    private static final String[] PROJECTION = new String[]{
            EventColumns.COLUMN_ID,
            EventColumns.COLUMN_UID,
            EventColumns.COLUMN_CODE,
            EventColumns.COLUMN_CREATED,
            EventColumns.COLUMN_LAST_UPDATED,
            EventColumns.COLUMN_NAME,
            EventColumns.COLUMN_DISPLAY_NAME,
            EventColumns.COLUMN_COMPLETED_DATE,
            EventColumns.COLUMN_EVENT_DATE,
            EventColumns.COLUMN_EVENT_STATUS,
            EventColumns.COLUMN_ORGANISATION_UNIT,
            EventColumns.COLUMN_PROGRAM,
            EventColumns.COLUMN_PROGRAM_STAGE,
            EventColumns.COLUMN_LATITUDE,
            EventColumns.COLUMN_LONGITUDE,
            EventColumns.COLUMN_STATE
    };

    public EventMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public ContentValues toContentValues(Event event) {
        Event.validate(event);

        ContentValues contentValues = new ContentValues();
        contentValues.put(EventColumns.COLUMN_ID, event.getId());
        contentValues.put(EventColumns.COLUMN_UID, event.getUid());
        contentValues.put(EventColumns.COLUMN_CODE, event.getCode());
        contentValues.put(EventColumns.COLUMN_CREATED, event.getCreated().toString());
        contentValues.put(EventColumns.COLUMN_LAST_UPDATED, event.getLastUpdated().toString());
        contentValues.put(EventColumns.COLUMN_NAME, event.getName());
        contentValues.put(EventColumns.COLUMN_DISPLAY_NAME, event.getDisplayName());
        contentValues.put(EventColumns.COLUMN_COMPLETED_DATE, event.getCompletedDate().toString());
        contentValues.put(EventColumns.COLUMN_EVENT_DATE, event.getEventDate().toString());
        contentValues.put(EventColumns.COLUMN_EVENT_STATUS, event.getStatus().toString());
        contentValues.put(EventColumns.COLUMN_ORGANISATION_UNIT, event.getOrgUnit());
        contentValues.put(EventColumns.COLUMN_PROGRAM, event.getProgram());
        contentValues.put(EventColumns.COLUMN_PROGRAM_STAGE, event.getProgramStage());
        contentValues.put(EventColumns.COLUMN_LATITUDE, event.getCoordinate().getLatitude());
        contentValues.put(EventColumns.COLUMN_LONGITUDE, event.getCoordinate().getLongitude());
        contentValues.put(EventColumns.COLUMN_STATE, event.getState().toString());

        return contentValues;
    }

    @Override
    public Event toModel(Cursor cursor) {
        Event event = new Event();

        event.setId(getInt(cursor, EventColumns.COLUMN_ID));
        event.setUid(getString(cursor, EventColumns.COLUMN_UID));
        event.setCode(getString(cursor, EventColumns.COLUMN_CODE));
        event.setName(getString(cursor, EventColumns.COLUMN_NAME));
        event.setDisplayName(getString(cursor, EventColumns.COLUMN_DISPLAY_NAME));
        event.setStatus(EventStatus.valueOf(getString(cursor, EventColumns.COLUMN_EVENT_STATUS)));
        event.setOrgUnit(getString(cursor, EventColumns.COLUMN_ORGANISATION_UNIT));
        event.setProgram(getString(cursor, EventColumns.COLUMN_PROGRAM));
        event.setProgramStage(getString(cursor, EventColumns.COLUMN_PROGRAM_STAGE));
        event.setState(State.valueOf(getString(cursor, EventColumns.COLUMN_STATE)));

        event.setCoordinate(new Coordinates(
                getDouble(cursor, EventColumns.COLUMN_LATITUDE),
                getDouble(cursor, EventColumns.COLUMN_LONGITUDE)));

        try {

            event.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, EventColumns.COLUMN_CREATED)));
            event.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, EventColumns.COLUMN_LAST_UPDATED)));

            event.setCompletedDate(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, EventColumns.COLUMN_COMPLETED_DATE)));
            event.setEventDate(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(getString(cursor, EventColumns.COLUMN_EVENT_DATE)));

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return event;
    }
}
