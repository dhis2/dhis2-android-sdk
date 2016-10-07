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

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStore.ProgramColumns;
import org.hisp.dhis.client.sdk.models.event.Event;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class EventStoreImpl extends AbsIdentifiableObjectDataStore<Event> implements EventStore {

    public static final String CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " +
            EventColumns.TABLE_NAME + " (" +
            EventColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EventColumns.COLUMN_UID + " TEXT NOT NULL," +
            EventColumns.COLUMN_NAME + " TEXT NOT NULL," +
            EventColumns.COLUMN_DISPLAY_NAME + " TEXT NOT NULL," +
            EventColumns.COLUMN_CODE + " TEXT," +
            EventColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            EventColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            EventColumns.COLUMN_EVENT_STATUS + " TEXT NOT NULL," +
            EventColumns.COLUMN_PROGRAM_STAGE + " TEXT NOT NULL," +
            EventColumns.COLUMN_ORGANISATION_UNIT + " TEXT NOT NULL," +
            EventColumns.COLUMN_EVENT_DATE + " TEXT," +
            EventColumns.COLUMN_COMPLETED_DATE + " TEXT," +
            EventColumns.COLUMN_LONGITUDE + " REAL," +
            EventColumns.COLUMN_LATITUDE + " REAL," +
            EventColumns.COLUMN_PROGRAM + " TEXT," +
            EventColumns.COLUMN_STATE + " TEXT," +
            "FOREIGN KEY " + "(" + EventColumns.COLUMN_PROGRAM + ")" +
            "REFERENCES " + ProgramColumns.TABLE_NAME + "(" + ProgramColumns.COLUMN_UID + ")" +
            " ON DELETE CASCADE " +
            " UNIQUE " + "(" + ProgramColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    public static final String DROP_TABLE_EVENTS = "DROP TABLE IF EXISTS " +
            EventColumns.TABLE_NAME;

    public EventStoreImpl(ContentResolver contentResolver) {
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
}
