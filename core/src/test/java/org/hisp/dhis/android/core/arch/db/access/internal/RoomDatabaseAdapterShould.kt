/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.persistence.db.access.RoomDatabaseAdapter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock

class RoomDatabaseAdapterShould {
    private val appDatabase: AppDatabase = mock()
    var database: SQLiteDatabase = mock()
    private val roomDatabaseAdapter = RoomDatabaseAdapter() // the class we are testing

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        roomDatabaseAdapter.activate(appDatabase, "dbName")
    }

    @Test
    fun verify_query_on_readable_data_base_when_set_query_in_data_base_adapter() = runTest {
        val sql: String = "SELECT * FROM TABLE_NAME"
        roomDatabaseAdapter.rawQuery(sql, null)
        Mockito.verify(database).rawQuery(sql, null)
    }

    @Test
    fun delete_in_data_base_when_delete_in_data_base_adapter() = runTest {
        roomDatabaseAdapter.delete("tableName", null, null)
        Mockito.verify(database).delete("tableName", null, null)
    }

    @Test
    fun verify_the_transaction_begin_in_data_base_when_execute_begin_new_transaction_on_data_base_adapter() {
        roomDatabaseAdapter.beginNewTransaction()
        Mockito.verify(database).beginTransaction()
    }
}
