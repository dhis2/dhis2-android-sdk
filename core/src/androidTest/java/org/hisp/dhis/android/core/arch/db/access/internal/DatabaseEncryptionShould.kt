/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.arch.db.access.internal

import android.util.Log
import androidx.room.RoomRawQuery
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.KoinStoreRegistry
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTest
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.persistence.constant.toDB
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DatabaseEncryptionShould : BaseMockIntegrationTest() {
    private lateinit var databaseManager: RoomDatabaseManager
    private lateinit var databaseAdapter: DatabaseAdapter

    @Before
    fun setUp() {
        val storeRegistry = KoinStoreRegistry()
        databaseAdapter = RoomDatabaseAdapter(storeRegistry)
        val passwordManager = DatabaseEncryptionPasswordManager.create(InMemorySecureStore())
        val context = InstrumentationRegistry.getInstrumentation().context

        databaseManager = RoomDatabaseManager(databaseAdapter, context, passwordManager)
        databaseManager.createOrOpenEncryptedDatabase("testDB", "test")
    }

    @Test
    fun encryptedDatabaseIsCreated() {
        assertThat(databaseAdapter.isReady).isTrue()
    }

    @Test
    fun dataIsStoredInEncryptedDatabase() = runTest {
        val constant = Constant.builder().uid("test_uid").value(42.0).build()
        val constantDB = constant.toDB()
        databaseAdapter.getCurrentDatabase().constantDao().insert(constantDB)

        val insertedConstants = databaseAdapter.getCurrentDatabase().constantDao()
            .objectListRawQuery(RoomRawQuery("SELECT * FROM Constant"))

        assertThat(insertedConstants).isNotEmpty()
    }

    @Test
    fun dataIsStoredInEncryptedDatabaseTransactionally() = runTest {
        val constant = Constant.builder().uid("test_uid").value(42.0).build()
        val constantDB = constant.toDB()

        try {
            databaseAdapter.getCurrentDatabase().useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    databaseAdapter.getCurrentDatabase().constantDao().upsert(listOf(constantDB)).size
                }
            }
        } catch (t: Throwable) {
            Log.e("DatabaseEncryptionShould", "Error storing data in encrypted database", t)
        }

        val insertedConstants = databaseAdapter.getCurrentDatabase().constantDao()
            .objectListRawQuery(RoomRawQuery("SELECT * FROM Constant"))

        assertThat(insertedConstants).isNotEmpty()
    }
}
