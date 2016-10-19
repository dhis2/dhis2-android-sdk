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

package org.hisp.dhis.client.sdk.core.commons.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hisp.dhis.client.sdk.core.event.EventTable;
import org.hisp.dhis.client.sdk.core.option.OptionSetTable;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitTable;
import org.hisp.dhis.client.sdk.core.program.ProgramTable;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueTable;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityTable;
import org.hisp.dhis.client.sdk.core.user.UserTable;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DhisDb.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(ProgramTable.CREATE_TABLE_PROGRAMS);
        database.execSQL(UserTable.CREATE_TABLE_USERS);
        database.execSQL(OptionSetTable.CREATE_TABLE_OPTION_SETS);
        database.execSQL(TrackedEntityTable.CREATE_TABLE_TRACKED_ENTITIES);
        database.execSQL(EventTable.CREATE_TABLE_EVENTS);
        database.execSQL(TrackedEntityDataValueTable.CREATE_TABLE_TRACKED_ENTITY_DATA_VALUES);
        database.execSQL(OrganisationUnitTable.CREATE_TABLE_ORGANISATION_UNITS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(ProgramTable.DROP_TABLE_PROGRAMS);
        database.execSQL(UserTable.DROP_TABLE_USERS);
        database.execSQL(OptionSetTable.DROP_TABLE_OPTION_SETS);
        database.execSQL(TrackedEntityTable.DROP_TABLE_TRACKED_ENTITIES);
        database.execSQL(EventTable.DROP_TABLE_EVENTS);
        database.execSQL(TrackedEntityDataValueTable.DROP_TABLE_TRACKED_ENTITY_DATA_VALUES);
        database.execSQL(OrganisationUnitTable.DROP_TABLE_ORGANISATION_UNITS);
    }
}
