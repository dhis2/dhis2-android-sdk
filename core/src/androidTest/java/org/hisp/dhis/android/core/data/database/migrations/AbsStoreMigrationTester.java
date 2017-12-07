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

package org.hisp.dhis.android.core.data.database.migrations;

import static com.google.common.truth.Truth.assertThat;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class AbsStoreMigrationTester {
    private SQLiteDatabase sqLiteDatabase;
    public DatabaseAdapter databaseAdapter;
    protected DbOpenHelperMigrationTester dbOpenHelper;
    String testDbName = "test.db";

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getTargetContext().getApplicationContext().deleteDatabase(testDbName);
        InstrumentationRegistry.getTargetContext().getApplicationContext().deleteDatabase(testDbName);
         dbOpenHelper = new DbOpenHelperMigrationTester(InstrumentationRegistry.getTargetContext().getApplicationContext()
                , testDbName);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
    }

    public void forceUpgradeDataBase(int databaseVersion){
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
        dbOpenHelper = new DbOpenHelperMigrationTester(InstrumentationRegistry.getTargetContext().getApplicationContext()
                , testDbName, databaseVersion);

        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    @After
    public void tearDown() throws IOException {
        assertThat(sqLiteDatabase).isNotNull();
        sqLiteDatabase.close();
        InstrumentationRegistry.getTargetContext().getApplicationContext().deleteDatabase(testDbName);
    }

    protected SQLiteDatabase database() {
        return sqLiteDatabase;
    }

    protected DatabaseAdapter databaseAdapter() {
        return databaseAdapter;
    }
}