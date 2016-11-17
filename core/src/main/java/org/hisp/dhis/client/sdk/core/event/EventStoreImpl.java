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
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.core.commons.database.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.event.EventTable.EventColumns;
import org.hisp.dhis.client.sdk.models.event.Event;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

class EventStoreImpl extends AbsDataStore<Event> implements EventStore {

    EventStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new EventMapper());
    }

    @Override
    public List<Event> query(String orgUnitUid, String programUid) {
        isNull(orgUnitUid, "organisationUnit uid must not be null");
        isNull(programUid, "program uid must not be null");

        final String selection = EventColumns.COLUMN_ORGANISATION_UNIT + " = ? AND " +
                EventColumns.COLUMN_PROGRAM + " = ?";
        final String[] selectionArgs = new String[]{
                orgUnitUid, programUid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public List<Event> queryEventsForEnrollment(String enrollmentUid) {
        isNull(enrollmentUid, "enrollment uid must not be null");

        final String selection = EventColumns.COLUMN_ENROLLMENT + " = ?";
        final String[] selectionArgs = new String[]{
                enrollmentUid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public Event query(String uid) {
        isNull(uid, "Uid must not be null");

        final String selection = EventColumns.COLUMN_UID + " = ?";
        final String[] selectionArgs = new String[]{
                uid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModel(cursor);
    }
}
