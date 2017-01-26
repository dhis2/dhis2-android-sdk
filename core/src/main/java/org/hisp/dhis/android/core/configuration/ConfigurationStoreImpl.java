/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConfigurationStoreImpl implements ConfigurationStore {
    private static final long CONFIGURATION_ID = 1L;
    private static final String[] PROJECTION = {
            ConfigurationModel.Columns.ID,
            ConfigurationModel.Columns.SERVER_URL
    };

    private final SQLiteDatabase sqLiteDatabase;

    public ConfigurationStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public long save(@NonNull String serverUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, serverUrl);

        int updatedRows = update(contentValues);
        if (updatedRows <= 0) {
            insert(contentValues);
        }

        return 1;
    }

    @Nullable
    @Override
    public ConfigurationModel query() {
        Cursor queryCursor = sqLiteDatabase.query(ConfigurationModel.CONFIGURATION,
                PROJECTION, ConfigurationModel.Columns.ID + " = ?", new String[]{
                        String.valueOf(CONFIGURATION_ID)
                }, null, null, null);

        ConfigurationModel configuration = null;

        try {
            if (queryCursor != null && queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();

                configuration = ConfigurationModel.create(queryCursor);
            }
        } finally {
            if (queryCursor != null) {
                queryCursor.close();
            }
        }

        return configuration;
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(ConfigurationModel.CONFIGURATION, null, null);
    }

    private int update(@NonNull ContentValues contentValues) {
        return sqLiteDatabase.update(ConfigurationModel.CONFIGURATION, contentValues,
                ConfigurationModel.Columns.ID + " = ?", new String[]{
                        String.valueOf(CONFIGURATION_ID)
                });
    }

    private long insert(@NonNull ContentValues contentValues) {
        return sqLiteDatabase.insertWithOnConflict(ConfigurationModel.CONFIGURATION, null,
                contentValues, SQLiteDatabase.CONFLICT_FAIL);
    }
}
