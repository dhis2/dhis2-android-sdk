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

package org.hisp.dhis.client.sdk.core.commons;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hisp.dhis.client.sdk.core.event.EventStoreImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.user.UserStore;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DhisDb.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(ProgramStore.CREATE_TABLE_PROGRAMS);
        database.execSQL(UserStore.CREATE_TABLE_USERS);
        database.execSQL(OptionSetStore.CREATE_TABLE_OPTION_SET);
        database.execSQL(TrackedEntityStore.CREATE_TABLE_TRACKED_ENTITY);
        database.execSQL(EventStoreImpl.CREATE_TABLE_EVENTS);
        database.execSQL(TrackedEntityDataValueStore.CREATE_TABLE_TRACKED_ENTITY_DATA_VALUES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(ProgramStore.DROP_TABLE_PROGRAMS);
        sqLiteDatabase.execSQL(UserStore.DROP_TABLE_USERS);
        sqLiteDatabase.execSQL(OptionSetStore.DROP_TABLE_OPTION_SET);
        sqLiteDatabase.execSQL(TrackedEntityStore.DROP_TABLE_TRACKED_ENTITY);
        sqLiteDatabase.execSQL(EventStoreImpl.DROP_TABLE_EVENTS);
        sqLiteDatabase.execSQL(TrackedEntityDataValueStore.DROP_TABLE_TRACKED_ENTITY_DATA_VALUES);
    }
}
