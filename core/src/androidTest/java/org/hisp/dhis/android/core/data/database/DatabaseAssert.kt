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
package org.hisp.dhis.android.core.data.database

import androidx.room.RoomRawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import junit.framework.Assert.fail
import org.hisp.dhis.android.persistence.common.daos.D2Dao
import org.junit.Assert

internal class DatabaseAssert private constructor(private val d2Dao: D2Dao) {

    // Properties might need to become suspend functions if they do DB work
    suspend fun isEmpty(): DatabaseAssert {
        tablesCount().forEach {
            if (it.count > 0) {
                fail("Table ${it.tableName} is not empty, count: ${it.count}")
            }
        }
        return this
    }

    suspend fun isNotEmpty(): DatabaseAssert {
        Assert.assertTrue("Database should not be empty", tablesCount().any { c -> c.count > 0 })
        return this
    }

    suspend fun isFull(expectedTables: List<String>? = null): DatabaseAssert {
        val counts = tablesCount()
        val tablesWithData = counts.filter { it.count > 0 }.map { it.tableName }

        if (expectedTables != null) {
            expectedTables.forEach { expectedTable ->
                val tableData = counts.find { it.tableName == expectedTable }
                if (tableData == null || tableData.count <= 0) {
                    fail("Table $expectedTable is empty or missing.")
                }
            }
            // Optional: Check if ONLY expected tables have data
            val unexpectedTablesWithData = tablesWithData.filterNot { expectedTables.contains(it) }
            if (unexpectedTablesWithData.isNotEmpty()) {
                fail("Unexpected tables have data: $unexpectedTablesWithData")
            }
        } else {
            // Original "isFull" logic: all non-excluded tables must have some data
            counts.forEach {
                if (it.count <= 0) {
                    fail("Table ${it.tableName} is empty, expected it to be full.")
                }
            }
        }
        return this
    }

    suspend fun isEmptyTable(tableName: String): DatabaseAssert {
        val count = tableCount(tableName)
        Assert.assertTrue("Table $tableName should be empty, but count is $count", count == 0)
        return this
    }

    suspend fun isNotEmptyTable(tableName: String): DatabaseAssert {
        val count = tableCount(tableName)
        Assert.assertTrue("Table $tableName should not be empty", count > 0)
        return this
    }

    private suspend fun tablesCount(): List<TableCount> {
        val excludedTables =
            listOf("android_metadata", "sqlite_sequence", "Configuration", "room_master_table") // Add room_master_table
        val queryBuilder = StringBuilder("SELECT name FROM sqlite_master WHERE type='table'")
        excludedTables.forEach { excludedTable ->
            queryBuilder.append(" AND name != '$excludedTable'")
        }
        queryBuilder.append(" ORDER BY name")

        val tableNames = d2Dao.stringListRawQuery(SimpleSQLiteQuery(queryBuilder.toString()))

        val tablesCountResult = mutableListOf<TableCount>()
        for (tableName in tableNames) {
            val count = tableCount(tableName)
            tablesCountResult.add(TableCount(tableName, count))
        }
        return tablesCountResult
    }

    private suspend fun tableCount(tableName: String): Int {
        // Important: Table names with special characters might need quoting, but typically
        // table names from sqlite_master are safe. If not, this part is tricky with SimpleSQLiteQuery.
        // Room DAOs with @Query("SELECT COUNT(*) FROM ${tableName}") are safer if you could generate DAOs per table,
        // but that's overkill for a generic assert.
        // For SimpleSQLiteQuery, ensure `tableName` is sanitized or comes from a trusted source (like sqlite_master).
        if (!tableName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
            // Basic sanitization or warning, ideally table names are simple
            // For true safety against SQL injection if tableName could be arbitrary,
            // this is complex. But here it's from sqlite_master.
            println(
                "Warning: Table name '$tableName' might be problematic for direct SQL concatenation if it contains special characters.",
            )
        }
        val query = RoomRawQuery("SELECT COUNT(*) FROM `$tableName`") // Use backticks for safety
        return d2Dao.intRawQuery(query) ?: 0 // Assuming intRawQuery returns Int? or default to 0
    }

    companion object {
        // The factory method doesn't change much, but the methods it returns are now suspend
        @JvmStatic
        fun assertThatDatabase(d2Dao: D2Dao): DatabaseAssert { // Or your KMP DatabaseAdapter
            return DatabaseAssert(d2Dao)
        }
    }

    data class TableCount(val tableName: String, val count: Int)
}
