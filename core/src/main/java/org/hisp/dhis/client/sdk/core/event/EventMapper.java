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

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.event.EventTable.EventColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.event.EventStatus;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getDouble;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class EventMapper extends AbsMapper<Event> {

    EventMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return EventTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(EventTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return EventTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(Event event) {
        if (!event.isValid()) {
            throw new IllegalArgumentException("Event is not valid");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventColumns.COLUMN_ID, event.id());
        contentValues.put(EventColumns.COLUMN_UID, event.uid());
        contentValues.put(EventColumns.COLUMN_CREATED, BaseIdentifiableObject.DATE_FORMAT.format(event.created()));
        contentValues.put(EventColumns.COLUMN_LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(event.lastUpdated()));
        contentValues.put(EventColumns.COLUMN_COMPLETED_DATE, BaseIdentifiableObject.DATE_FORMAT.format(event.completedDate()));
        contentValues.put(EventColumns.COLUMN_EVENT_DATE, BaseIdentifiableObject.DATE_FORMAT.format(event.eventDate()));
        contentValues.put(EventColumns.COLUMN_EVENT_STATUS, event.status().toString());
        contentValues.put(EventColumns.COLUMN_ORGANISATION_UNIT, event.organisationUnit());
        contentValues.put(EventColumns.COLUMN_PROGRAM, event.program());
        contentValues.put(EventColumns.COLUMN_PROGRAM_STAGE, event.programStage());
        contentValues.put(EventColumns.COLUMN_LATITUDE, event.coordinates().latitude());
        contentValues.put(EventColumns.COLUMN_LONGITUDE, event.coordinates().longitude());
        contentValues.put(EventColumns.COLUMN_STATE, event.state().toString());

        return contentValues;
    }

    @Override
    public Event toModel(Cursor cursor) {
        Event event = null;

        try {
            event = Event.builder()
                    .id(getLong(cursor, EventColumns.COLUMN_ID))
                    .uid(getString(cursor, EventColumns.COLUMN_UID))
                    .status(EventStatus.valueOf(getString(cursor, EventColumns.COLUMN_EVENT_STATUS)))
                    .program(getString(cursor, EventColumns.COLUMN_PROGRAM))
                    .programStage(getString(cursor, EventColumns.COLUMN_PROGRAM_STAGE))
                    .state(State.valueOf(getString(cursor, EventColumns.COLUMN_STATE)))
                    .coordinates(Coordinates.builder().latitude(getDouble(cursor, EventColumns.COLUMN_LATITUDE))
                            .longitude(getDouble(cursor, EventColumns.COLUMN_LONGITUDE)).build())
                    .created(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EventColumns.COLUMN_CREATED)))
                    .lastUpdated(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EventColumns.COLUMN_LAST_UPDATED)))
                    .completedDate(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EventColumns.COLUMN_COMPLETED_DATE)))
                    .eventDate(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EventColumns.COLUMN_EVENT_DATE))).build();

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return event;
    }
}
