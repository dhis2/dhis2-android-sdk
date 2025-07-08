/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.data.database.migrations;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore;
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.persistence.user.UserTableInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.arch.db.access.SqliteCheckerUtility.ifTableExist;

@RunWith(AndroidJUnit4.class)
public class DataBaseMigrationShould {
    private DatabaseAdapter databaseAdapter;
    private String dbName = null;

    @Before
    public void deleteDB() {
        this.closeAndDeleteDatabase();
    }

    @After
    public void tearDown() {
        this.closeAndDeleteDatabase();
    }

    private void closeAndDeleteDatabase() {
        if (databaseAdapter != null) {
            databaseAdapter.close();
        }
        if (dbName != null) {
            InstrumentationRegistry.getInstrumentation().getContext().deleteDatabase(dbName);
        }
    }

    @Test
    public void have_user_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(UserTableInfo.TABLE_INFO.name(), databaseAdapter)).isTrue();
    }

    @Test
    public void not_have_tracked_entity_attribute_reserved_value_table_after_migration_1() {
        initCoreDataBase(1);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), databaseAdapter)).isFalse();
    }

    @Test
    public void have_tracked_entity_attribute_reserved_value_table_after_first_migration_2() {
        initCoreDataBase(2);
        assertThat(ifTableExist(TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(), databaseAdapter)).isTrue();
    }

    public DatabaseAdapter initCoreDataBase(int databaseVersion) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        DatabaseAdapterFactory databaseAdapterFactory = DatabaseAdapterFactory.create(context, new InMemorySecureStore());
        databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, dbName, false, databaseVersion);
        return databaseAdapter;
    }
}
