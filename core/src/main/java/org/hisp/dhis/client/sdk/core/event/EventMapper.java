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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.event.EventStore.EventColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.event.EventStatus;

import java.text.ParseException;

public class EventMapper implements Mapper<Event> {
    private static Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(EventColumns.TABLE_NAME).build();

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

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_UID = 1;
    private static final int COLUMN_CODE = 2;
    private static final int COLUMN_CREATED = 3;
    private static final int COLUMN_LAST_UPDATED = 4;
    private static final int COLUMN_NAME = 5;
    private static final int COLUMN_DISPLAY_NAME = 6;
    private static final int COLUMN_COMPLETED_DATE = 7;
    private static final int COLUMN_EVENT_DATE = 8;
    private static final int COLUMN_EVENT_STATUS = 9;
    private static final int COLUMN_ORGANISATION_UNIT = 10;
    private static final int COLUMN_PROGRAM = 11;
    private static final int COLUMN_PROGRAM_STAGE = 12;
    private static final int COLUMN_LATITUDE = 13;
    private static final int COLUMN_LONGITUDE = 14;
    private static final int COLUMN_STATE = 15;

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

        event.setId(cursor.getInt(COLUMN_ID));
        event.setUid(cursor.getString(COLUMN_UID));
        event.setCode(cursor.getString(COLUMN_CODE));
        event.setName(cursor.getString(COLUMN_NAME));
        event.setDisplayName(cursor.getString(COLUMN_DISPLAY_NAME));
        event.setOrgUnit(cursor.getString(COLUMN_ORGANISATION_UNIT));
        event.setProgram(cursor.getString(COLUMN_PROGRAM));
        event.setProgramStage(cursor.getString(COLUMN_PROGRAM_STAGE));
        event.setStatus(EventStatus.valueOf(cursor.getString(COLUMN_EVENT_STATUS)));
        event.setState(State.valueOf(cursor.getString(COLUMN_STATE)));

        event.setCoordinate(new Coordinates(
                cursor.getDouble(COLUMN_LATITUDE),
                cursor.getDouble(COLUMN_LONGITUDE)));

        try {
            event.setCreated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_CREATED)));
            event.setLastUpdated(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_LAST_UPDATED)));
            event.setCompletedDate(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_COMPLETED_DATE)));
            event.setEventDate(BaseIdentifiableObject.SIMPLE_DATE_FORMAT
                    .parse(cursor.getString(COLUMN_EVENT_DATE)));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return event;
    }
}
