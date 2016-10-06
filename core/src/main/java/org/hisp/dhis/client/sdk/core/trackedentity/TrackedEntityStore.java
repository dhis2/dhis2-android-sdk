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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.DbContract;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class TrackedEntityStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;

    public static final String CREATE_TABLE_TRACKED_ENTITY = "CREATE TABLE IF NOT EXISTS " + TrackedEntityColumns.TABLE_NAME + " (" +
            TrackedEntityColumns.COLUMN_NAME_KEY + " TEXT PRIMARY KEY," +
            TrackedEntityColumns.COLUMN_NAME_VALUE + " TEXT NOT NULL" + " )";

    public static final String DROP_TABLE_TRACKED_ENTITY = "DROP TABLE IF EXISTS " +
            TrackedEntityColumns.TABLE_NAME;

    public interface TrackedEntityColumns extends DbContract.KeyValueColumns {
        String TABLE_NAME = "trackedEntities";
    }

    public TrackedEntityStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public synchronized boolean save(List<TrackedEntity> trackedEntities) throws JsonProcessingException {
        isNull(trackedEntities, "TrackedEntities must not be null");
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = mapToContentValues(trackedEntities);

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(TrackedEntityColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();
        return true;
    }

    private List<ContentValues> mapToContentValues(List<TrackedEntity> trackedEntities) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (TrackedEntity trackedEntity : trackedEntities) {
            String programRuleJson;
            try {
                programRuleJson = objectMapper.writeValueAsString(trackedEntity);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(TrackedEntityColumns.COLUMN_NAME_KEY, trackedEntity.getUid());
            contentValues.put(TrackedEntityColumns.COLUMN_NAME_VALUE, programRuleJson);

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    //TODO: test this:
    public TrackedEntity getTrackedEntity(String uid) {
        isNull(uid, "Uid must not be null");
        TrackedEntity trackedEntity = null;
        Cursor cursor = sqLiteOpenHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM" + TrackedEntityStore.TrackedEntityColumns.TABLE_NAME +
                        " where " +
                        TrackedEntityColumns.COLUMN_NAME_KEY + "=" + uid,
                null);

        if(cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            try {
                trackedEntity = objectMapper.readValue(cursor.getString(cursor.getColumnIndex(TrackedEntityColumns.COLUMN_NAME_VALUE)), TrackedEntity.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return trackedEntity;
    }
}
