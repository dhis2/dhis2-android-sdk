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
package org.hisp.dhis.android.core.arch.db.access.internal

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorExecutorImpl
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class DatabaseFromMigrationsIntegrationShould {

    companion object {
        private const val DB_NAME = "database-from-migrations-integration-should.db"
        private lateinit var databaseAdapterFactory: DatabaseAdapterFactory

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            val context = InstrumentationRegistry.getInstrumentation().context
            databaseAdapterFactory = DatabaseAdapterFactory.create(context, InMemorySecureStore())
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            val context = InstrumentationRegistry.getInstrumentation().context
            context.deleteDatabase("$DB_NAME.db")
        }
    }

    @Test
    fun get_adapter_create_and_close() {
        val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
        databaseAdapterFactory.createOrOpenDatabase(databaseAdapter, DB_NAME, false)
        val schema = getSchema(databaseAdapter)
        assertThat(schema).isNotEmpty()
        databaseAdapter.close()
    }

    private fun getSchema(databaseAdapter: DatabaseAdapter): List<Row> {
        val cursor = databaseAdapter.rawQuery("SELECT name, sql FROM sqlite_master ORDER BY name")
        val list = mutableListOf<Row>()
        val cursorExecutor = CursorExecutorImpl {c -> Row(c.getString(0), c.getString(1)) }
        cursorExecutor.addObjectsToCollection(cursor, list)
        return list.toList()
    }


    private data class Row(val name: String, val sql: String?)
}