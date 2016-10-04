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

package org.hisp.dhis.client.sdk.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class UserStore {
    private final SQLiteOpenHelper sqLiteOpenHelper;
    private final ObjectMapper objectMapper;

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + UserColumns.TABLE_NAME + " (" +
            UserColumns.COLUMN_NAME_KEY + " TEXT PRIMARY KEY," +
            UserColumns.COLUMN_NAME_VALUE + " TEXT NOT NULL," +
            UserColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL" + " )";

    public static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS " + UserColumns.TABLE_NAME;

    public interface UserColumns extends DbContract.KeyValueColumns, DbContract.LastUpdatedColumn {
        String TABLE_NAME = "me";
    }

    public UserStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public boolean insert(List<User> users) {
        isNull(users, "Users cannot be null");
        List<ContentValues> contentValuesList = mapToContentValues(users);

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(UserColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();
        return true;
    }

    private List<ContentValues> mapToContentValues(List<User> users) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (User user : users) {
            String userJson;
            try {
                userJson = objectMapper.writeValueAsString(user);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }

            Date lastUpdated = user.getLastUpdated();

            ContentValues contentValues = new ContentValues();
            contentValues.put(UserColumns.COLUMN_NAME_KEY, user.getUid());
            contentValues.put(UserColumns.COLUMN_LAST_UPDATED,
                    lastUpdated != null ? lastUpdated.toString() : "");
            contentValues.put(UserColumns.COLUMN_NAME_VALUE, userJson);

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    /**
     *
     * @return the only logged in user
     */
    public User list() {

        List<User> users = new ArrayList<>();

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = database.query(UserColumns.TABLE_NAME, null,
                null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String json = cursor.getString(1);
                    User user = objectMapper.readValue(json, User.class);

                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            cursor.close();
        }

        return users.get(0);
    }
}
