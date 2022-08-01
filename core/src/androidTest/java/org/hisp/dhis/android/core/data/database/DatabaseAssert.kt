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
package org.hisp.dhis.android.core.data.database

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.junit.Assert.fail

class DatabaseAssert private constructor(var databaseAdapter: DatabaseAdapter) {

    val isEmpty: DatabaseAssert
        get() {
            tablesCount().forEach {
                if (it.count > 0) { fail("Table ${it.tableName} is not empty") }
            }
            return this
        }

    val isNotEmpty: DatabaseAssert
        get() {
            assertThat(tablesCount().any { c -> c.count > 0 }).isTrue()
            return this
        }

    val isFull: DatabaseAssert
        get() {
            tablesCount().forEach {
                if (it.count <= 0) { fail("Table ${it.tableName} is empty") }
            }
            return this
        }

    fun isEmptyTable(tableName: String): DatabaseAssert {
        assertThat(tableCount(tableName) == 0).isTrue()
        return this
    }

    fun isNotEmptyTable(tableName: String): DatabaseAssert {
        assertThat(tableCount(tableName) == 0).isFalse()
        return this
    }

    private fun tablesCount(): List<TableCount> {
        val excludedTables = listOf("android_metadata", "sqlite_sequence", "Configuration")

        val cursor = databaseAdapter.rawQuery(
            " SELECT name FROM sqlite_master WHERE type='table' and " +
                excludedTables.joinToString(" and ") { "name != '$it'" }
        )

        val tablesCount = ArrayList<TableCount>()
        cursor.use { c ->
            val value = cursor.getColumnIndex("name")
            if (value != -1 && c.count > 0) {
                c.moveToFirst()
                do {
                    val tableName = cursor.getString(value)
                    val count = tableCount(tableName)

                    tablesCount.add(TableCount(tableName, count))
                } while (c.moveToNext())
            }
        }
        return tablesCount
    }

    private fun tableCount(tableName: String): Int {
        val cursor = databaseAdapter.rawQuery("SELECT COUNT(*) from $tableName")

        return cursor.use { c ->
            if (c.count >= 1) {
                c.moveToFirst()
                cursor.getInt(0)
            } else {
                0
            }
        }
    }

    companion object {
        @JvmStatic
        fun assertThatDatabase(databaseAdapter: DatabaseAdapter): DatabaseAssert {
            return DatabaseAssert(databaseAdapter)
        }
    }

    data class TableCount(val tableName: String, val count: Int)
}
