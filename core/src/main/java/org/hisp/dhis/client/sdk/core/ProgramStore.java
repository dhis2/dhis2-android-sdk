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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ProgramStore {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ObjectMapper objectMapper;

    public static final String CREATE_TABLE_PROGRAMS = "CREATE TABLE IF NOT EXISTS " + ProgramColumns.TABLE_NAME + " (" +
            ProgramColumns.COLUMN_NAME_KEY + " TEXT PRIMARY KEY," +
            ProgramColumns.COLUMN_NAME_VALUE + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_VERSION + " INTEGER NOT NULL" + " )";

    public static final String DROP_TABLE_PROGRAMS = "DROP TABLE IF EXISTS " +
            ProgramColumns.TABLE_NAME;

    public interface ProgramColumns extends DbContract.KeyValueColumns, DbContract.VersionColumn {
        String TABLE_NAME = "program";
    }

    public ProgramStore(SQLiteOpenHelper sqLiteOpenHelper, ObjectMapper objectMapper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.objectMapper = objectMapper;
    }

    public synchronized boolean save(List<Program> programs) throws JsonProcessingException {
        isNull(programs, "Programs cannot be null");
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        List<ContentValues> contentValuesList = mapToContentValues(programs);

        if (contentValuesList.isEmpty()) {
            return false;
        }

        for (ContentValues contentValues : contentValuesList) {
            database.insertWithOnConflict(ProgramColumns.TABLE_NAME,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        database.close();
        return true;
    }

    private List<ContentValues> mapToContentValues(List<Program> programs) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (Program program : programs) {
            String programJson;
            try {
                programJson = objectMapper.writeValueAsString(program);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(ProgramColumns.COLUMN_NAME_KEY, program.getUid());
            contentValues.put(ProgramColumns.COLUMN_VERSION, program.getVersion());
            contentValues.put(ProgramColumns.COLUMN_NAME_VALUE, programJson);

            contentValuesList.add(contentValues);
        }
        return contentValuesList;
    }

    public List<Program> list() {
        List<Program> programs = new ArrayList<>();

        String[] projection = new String[]{
                ProgramColumns.COLUMN_NAME_KEY,
                ProgramColumns.COLUMN_VERSION,
                ProgramColumns.COLUMN_NAME_VALUE
        };

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(ProgramStore.ProgramColumns.TABLE_NAME, projection,
                null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String json = cursor.getString(1);
                    Program program = objectMapper.readValue(json, Program.class);

                    programs.add(program);
                } while (cursor.moveToNext());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            cursor.close();
        }

        return programs;
    }

    public List<Program> listBy(String[] projection) {
        isNull(projection, "Projection must not be null");

        List<Program> programs = new ArrayList<>();

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(ProgramStore.ProgramColumns.TABLE_NAME, projection,
                null, null, null, null, null);


        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String uid = cursor.getString(0);
                int version = cursor.getInt(1);
                Program program = new Program();
                program.setUid(uid);
                program.setVersion(version);

                programs.add(program);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return programs;
    }
}
