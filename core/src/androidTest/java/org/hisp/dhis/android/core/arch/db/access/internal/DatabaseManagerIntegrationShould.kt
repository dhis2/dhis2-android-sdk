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
package org.hisp.dhis.android.core.arch.db.access.internal

import androidx.room.RoomRawQuery
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseManager
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseManager
import org.junit.AfterClass
import org.junit.Test

class DatabaseManagerIntegrationShould {
    @Test
    fun get_adapter() {
        Companion.databaseManager.getAdapter()
    }

    @Test
    fun get_adapter_create_and_close() {
        val databaseAdapter = Companion.databaseManager.getAdapter()
        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
        databaseAdapter.close()
    }

    @Test
    fun get_adapter_create_close_and_recreate() {
        val databaseAdapter = Companion.databaseManager.getAdapter()
        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
        databaseAdapter.close()

        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
    }

    @Test
    fun create_and_recreate_without_closing() {
        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
    }

    @Test
    fun get_adapter_create_close_and_recreate_reading_db() = runTest {
        val databaseAdapter = Companion.databaseManager.getAdapter()
        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
        val userDao1 = databaseAdapter.getCurrentDatabase().userDao()
        val users1 = userDao1.objectListRawQuery(RoomRawQuery("SELECT * FROM User"))
        databaseAdapter.close()

        Companion.databaseManager.createOrOpenUnencryptedDatabase(DB_NAME)
        val userDao2 = databaseAdapter.getCurrentDatabase().userDao()
        val users2 = userDao2.objectListRawQuery(RoomRawQuery("SELECT * FROM User"))
        databaseAdapter.close()
    }

    companion object {
        private const val DB_NAME = "database-adapter-factory-integration-should.db"
        private lateinit var databaseManager: DatabaseManager
            private set

        init {
            val context = InstrumentationRegistry.getInstrumentation().context
            val databaseAdapter = RoomDatabaseAdapter()
            val passwordManager = DatabaseEncryptionPasswordManager.create(InMemorySecureStore())
            databaseManager = RoomDatabaseManager(databaseAdapter, context, passwordManager)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            val context = InstrumentationRegistry.getInstrumentation().context
            context.deleteDatabase(DB_NAME)
        }
    }
}
