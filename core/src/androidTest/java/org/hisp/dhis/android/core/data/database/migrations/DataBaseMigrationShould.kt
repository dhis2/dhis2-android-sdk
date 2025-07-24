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
package org.hisp.dhis.android.core.data.database.migrations

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.SqliteCheckerUtility
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseManager
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueTableInfo
import org.hisp.dhis.android.persistence.user.UserTableInfo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataBaseMigrationShould {
    private var databaseAdapter: DatabaseAdapter = RoomDatabaseAdapter()
    private val dbName: String = "test.db"

    @Before
    fun deleteDB() {
        this.closeAndDeleteDatabase()
    }

    @After
    fun tearDown() {
        this.closeAndDeleteDatabase()
    }

    private fun closeAndDeleteDatabase() {
        if (databaseAdapter.isReady) {
            databaseAdapter.close()
        }
        if (dbName != null) {
            InstrumentationRegistry.getInstrumentation().context.deleteDatabase(dbName)
        }
    }

    @Test
    fun have_user_table_after_migration_1() = runTest {
        initCoreDataBase(1)
        Truth.assertThat(
            SqliteCheckerUtility.ifTableExist(
                UserTableInfo.TABLE_INFO.name(),
                databaseAdapter,
            ),
        ).isTrue()
    }

    @Test
    fun not_have_tracked_entity_attribute_reserved_value_table_after_migration_1() = runTest {
        initCoreDataBase(1)
        Truth.assertThat(
            SqliteCheckerUtility.ifTableExist(
                TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(),
                databaseAdapter,
            ),
        ).isFalse()
    }

    @Test
    fun have_tracked_entity_attribute_reserved_value_table_after_first_migration_2() = runTest {
        initCoreDataBase(2)
        Truth.assertThat(
            SqliteCheckerUtility.ifTableExist(
                TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO.name(),
                databaseAdapter,
            ),
        ).isTrue()
    }

    fun initCoreDataBase(databaseVersion: Int): DatabaseAdapter? {
        val context = InstrumentationRegistry.getInstrumentation().context
        val secureStore = InMemorySecureStore()
        val passwordManager = DatabaseEncryptionPasswordManager.create(secureStore)
        val databaseManager = RoomDatabaseManager(databaseAdapter, context, passwordManager)
        databaseManager.createOrOpenUnencryptedDatabase(dbName)

        return databaseAdapter
    }
}
