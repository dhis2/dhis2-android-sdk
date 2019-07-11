/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.core.user.UserTableInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hisp.dhis.android.core.data.database.SqliteCheckerUtility.ifTableExist;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataBaseMigrationShould {
    private DatabaseAdapter databaseAdapter;
    private DbOpenHelper dbOpenHelper;
    private String dbName = null;
    private SQLiteDatabase databaseInMemory;

    @Before
    public void deleteDB() {
        this.closeAndDeleteDatabase();
        dbOpenHelper = null;
        databaseInMemory = null;
    }

    @After
    public void tearDown() {
        this.closeAndDeleteDatabase();
    }

    private void closeAndDeleteDatabase() {
        if (databaseInMemory != null) {
            databaseInMemory.close();
        }
        if (dbName != null) {
            InstrumentationRegistry.getContext().deleteDatabase(dbName);
        }
    }

    @Test
    public void have_user_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(UserTableInfo.TABLE_INFO.name(), databaseAdapter), is(true));
    }

    @Test
    public void not_have_tracked_entity_attribute_reserved_value_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), databaseAdapter), is(false));
    }

    @Test
    public void have_tracked_entity_attribute_reserved_value_table_after_first_migration_2() {
        initCoreDataBase(2);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), databaseAdapter), is(true));
    }

    public DatabaseAdapter initCoreDataBase(int databaseVersion) {
        if (databaseAdapter == null) {
            dbOpenHelper = new DbOpenHelper(
                    InstrumentationRegistry.getTargetContext().getApplicationContext()
                    , dbName, databaseVersion);
            databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
            databaseInMemory = databaseAdapter.database();
        } else if (dbName == null) {
            if (databaseInMemory.getVersion() < databaseVersion) {
                dbOpenHelper.onUpgrade(databaseInMemory, databaseInMemory.getVersion(),
                        databaseVersion);
                databaseInMemory.setVersion(databaseVersion);
            } else if (databaseInMemory.getVersion() > databaseVersion) {
                dbOpenHelper.onDowngrade(databaseInMemory, databaseInMemory.getVersion(),
                        databaseVersion);
                databaseInMemory.setVersion(databaseVersion);
            }
        }
        return databaseAdapter;
    }
}