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

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseEncryptionPasswordManager
import org.hisp.dhis.android.persistence.common.SchemaRow
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseManager
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class DatabaseFromMigrationsIntegrationShould {

    companion object {
        private const val DB_NAME_1 = "database-from-migrations-integration-should-1.db"
        private const val DB_NAME_2 = "database-from-migrations-integration-should-2.db"
        private val databasAdapter = RoomDatabaseAdapter()
        private val context = InstrumentationRegistry.getInstrumentation().context
        private val secureStore = AndroidSecureStore(context)
        private val passwordManager = DatabaseEncryptionPasswordManager.Companion.create(secureStore)
        private val databaseManager = RoomDatabaseManager(databasAdapter, context, passwordManager)

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            deleteDatabases()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            deleteDatabases()
        }

        private fun deleteDatabases() {
            context.deleteDatabase(DB_NAME_1)
            context.deleteDatabase(DB_NAME_2)
        }
    }

    @Test
    fun ensure_db_from_snapshots_and_from_migrations_have_the_same_schema() = runTest {
        createDb(DB_NAME_1)
        val schema1 = getSchema(Companion.databasAdapter)

        createDb(DB_NAME_2)
        val schema2 = getSchema(Companion.databasAdapter)

        Companion.databasAdapter.close()

        val diff1 = schema1 - schema2
        val diff2 = schema2 - schema1

        assertThat(diff1).isEmpty()
        assertThat(diff2).isEmpty()
    }

    private fun createDb(name: String) {
        databaseManager.createOrOpenUnencryptedDatabase(name)
    }

    private suspend fun getSchema(databaseAdapter: DatabaseAdapter): List<SchemaRow> {
        val dao = databaseAdapter.getCurrentDatabase().d2Dao()
        val schemaRowList = dao.getSchemaRows()
        return schemaRowList.map { row -> row.copy(sql = row.sql?.replace("\"", "")) }
    }
}
