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

package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class TrackedEntityDataValueStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;

    public static final String CREATE_TABLE_TRACKED_ENTITY_DATA_VALUES = "CREATE TABLE IF NOT EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME + " (" +
            TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_EVENT + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_STORED_BY + " TEXT NOT NULL," +
            TrackedEntityDataValueColumns.COLUMN_VALUE + " TEXT" + " )";

    public static final String DROP_TABLE_TRACKED_ENTITY_DATA_VALUES = "DROP TABLE IF EXISTS " +
            TrackedEntityDataValueColumns.TABLE_NAME;

    public interface TrackedEntityDataValueColumns {
        String TABLE_NAME = "trackedEntityDataValues";
        String COLUMN_DATA_ELEMENT = "dataElement";
        String COLUMN_EVENT = "event";
        String COLUMN_STORED_BY = "storedBy";
        String COLUMN_VALUE = "value";
    }

    public TrackedEntityDataValueStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public synchronized boolean save(List<TrackedEntityDataValue> trackedEntityDataValues) throws JsonProcessingException {
        isNull(trackedEntityDataValues, "Events cannot be null");
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = mapToContentValues(trackedEntityDataValues);

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(ProgramStore.ProgramColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();
        return true;
    }

    private List<ContentValues> mapToContentValues(List<TrackedEntityDataValue> trackedEntityDataValues) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (TrackedEntityDataValue trackedEntityDataValue : trackedEntityDataValues) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TrackedEntityDataValueColumns.COLUMN_EVENT, trackedEntityDataValue.getEventUid());
            contentValues.put(TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT, trackedEntityDataValue.getDataElement());
            contentValues.put(TrackedEntityDataValueColumns.COLUMN_STORED_BY, trackedEntityDataValue.getStoredBy());
            contentValues.put(TrackedEntityDataValueColumns.COLUMN_VALUE, trackedEntityDataValue.getValue());

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    public List<TrackedEntityDataValue> list() {
        List<TrackedEntityDataValue> trackedEntityDataValues = new ArrayList<>();

        String[] projection = new String[]{
                TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT,
                TrackedEntityDataValueColumns.COLUMN_EVENT,
                TrackedEntityDataValueColumns.COLUMN_STORED_BY,
                TrackedEntityDataValueColumns.COLUMN_VALUE,
        };

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(TrackedEntityDataValueColumns.TABLE_NAME, projection,
                null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String dataElement = cursor.getString(0);
                    String event = cursor.getString(1);
                    String storedBy = cursor.getString(2);
                    String value = cursor.getString(3);

                    TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
                    trackedEntityDataValue.setDataElement(dataElement);
                    trackedEntityDataValue.setEventUid(event);
                    trackedEntityDataValue.setStoredBy(storedBy);
                    trackedEntityDataValue.setValue(value);

                    trackedEntityDataValues.add(trackedEntityDataValue);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return trackedEntityDataValues;
    }

    /**
     * @param projection
     * @return
     */
    public List<TrackedEntityDataValue> listBy(String[] projection) {
        isNull(projection, "Projection must not be null");

        List<TrackedEntityDataValue> trackedEntityDataValues = new ArrayList<>();
        SparseArray<String> columnIndices = new SparseArray<>();

        for (int i = 0; i < projection.length; i++) {
            columnIndices.append(i, projection[i]);
        }

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TrackedEntityDataValueColumns.TABLE_NAME, projection,
                null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (columnIndices.get(i)) {
                        case TrackedEntityDataValueColumns.COLUMN_DATA_ELEMENT:
                            String dataElement = cursor.getString(i);
                            trackedEntityDataValue.setDataElement(dataElement);
                            break;
                        case TrackedEntityDataValueColumns.COLUMN_EVENT:
                            String event = cursor.getString(i);
                            trackedEntityDataValue.setEventUid(event);
                            break;
                        case TrackedEntityDataValueColumns.COLUMN_STORED_BY:
                            String storedBy = cursor.getString(i);
                            trackedEntityDataValue.setStoredBy(storedBy);
                            break;
                        case TrackedEntityDataValueColumns.COLUMN_VALUE:
                            String value = cursor.getString(i);
                            trackedEntityDataValue.setValue(value);
                            break;
                    }
                }
                trackedEntityDataValues.add(trackedEntityDataValue);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trackedEntityDataValues;
    }
}
