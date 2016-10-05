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
import android.util.SparseArray;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.models.option.OptionSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class OptionSetStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;

    public static final String CREATE_TABLE_OPTION_SET = "CREATE TABLE IF NOT EXISTS " + OptionSetColumns.TABLE_NAME + " (" +
            OptionSetColumns.COLUMN_NAME_KEY + " TEXT PRIMARY KEY," +
            OptionSetColumns.COLUMN_NAME_VALUE + " TEXT NOT NULL," +
            OptionSetColumns.COLUMN_VERSION + " INTEGER NOT NULL" + " )";

    public static final String DROP_TABLE_OPTION_SET = "DROP TABLE IF EXISTS " +
            OptionSetColumns.TABLE_NAME;

    public interface OptionSetColumns extends DbContract.KeyValueColumns, DbContract.VersionColumn {
        String TABLE_NAME = "optionSet";
    }

    public OptionSetStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public synchronized boolean save(List<OptionSet> optionSets) throws JsonProcessingException {
        isNull(optionSets, "OptionSets cannot be null");

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        List<ContentValues> contentValuesList = mapToContentValues(optionSets);

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(OptionSetColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();

        return true;
    }

    private List<ContentValues> mapToContentValues(List<OptionSet> optionSets) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (OptionSet optionSet : optionSets) {
            String optionSetJson;
            try {
                optionSetJson = objectMapper.writeValueAsString(optionSet);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(OptionSetColumns.COLUMN_NAME_KEY, optionSet.getUid());
            contentValues.put(OptionSetColumns.COLUMN_VERSION, optionSet.getVersion());
            contentValues.put(OptionSetColumns.COLUMN_NAME_VALUE, optionSetJson);

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    public List<OptionSet> list() {
        List<OptionSet> optionSets = new ArrayList<>();

        String[] projection = new String[]{
                OptionSetColumns.COLUMN_NAME_KEY,
                OptionSetColumns.COLUMN_VERSION,
                OptionSetColumns.COLUMN_NAME_VALUE
        };

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(OptionSetColumns.TABLE_NAME, projection,
                null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String json = cursor.getString(1);
                    OptionSet optionSet = objectMapper.readValue(json, OptionSet.class);

                    optionSets.add(optionSet);
                } while (cursor.moveToNext());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            cursor.close();
        }

        return optionSets;
    }

    public List<OptionSet> listBy(String[] projection) {

        List<OptionSet> optionSets = new ArrayList<>();

        SparseArray<String> columnIndices = new SparseArray<>();
        for (int i = 0; i < projection.length; i++) {
            columnIndices.append(i, projection[i]);
        }

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(OptionSetColumns.TABLE_NAME, projection,
                null, null, null, null, null);


        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                OptionSet optionSet = new OptionSet();
                IOException error = null;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (columnIndices.get(i)) {
                        case OptionSetColumns.COLUMN_NAME_KEY:
                            String uid = cursor.getString(i);
                            optionSet.setUid(uid);
                            break;
                        case OptionSetColumns.COLUMN_NAME_VALUE:
                            String json = cursor.getString(i);
                            try {
                                optionSet = objectMapper.readValue(json, OptionSet.class);
                            } catch (IOException e) {
                                error = e;
                                e.printStackTrace();
                            }
                            break;
                        case OptionSetColumns.COLUMN_VERSION:
                            int version = cursor.getInt(i);
                            optionSet.setVersion(version);
                            break;
                    }
                }
                if (error == null) {
                    optionSets.add(optionSet);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return optionSets;
    }

    //ToDo get OptionSet
    public OptionSet get(String uid) {

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        OptionSet optionSet = new OptionSet();

        Cursor cursor = database.rawQuery("SELECT * FROM " + OptionSetColumns.TABLE_NAME + " where " + OptionSetColumns.COLUMN_NAME_KEY + "=" + uid, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                IOException error = null;
                String id = cursor.getString(cursor.getColumnIndex(OptionSetColumns.COLUMN_NAME_KEY));
                int version = cursor.getInt(cursor.getColumnIndex(OptionSetColumns.COLUMN_VERSION));
                String value = cursor.getString(cursor.getColumnIndex(OptionSetColumns.COLUMN_NAME_VALUE));

                try {
                    optionSet = objectMapper.readValue(value, OptionSet.class);
                } catch (IOException e) {
                    error = e;
                    e.printStackTrace();
                }

                if (error == null) {
                    optionSet.setUid(id);
                    optionSet.setVersion(version);
                }
            } while (cursor.moveToNext());

        }
        return optionSet;
    }
}
