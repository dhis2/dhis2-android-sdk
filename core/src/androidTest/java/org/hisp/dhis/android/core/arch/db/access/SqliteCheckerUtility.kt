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
package org.hisp.dhis.android.core.arch.db.access

import androidx.room.RoomRawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

object SqliteCheckerUtility {
    suspend fun isTableEmpty(databaseAdapter: DatabaseAdapter, table: String): Boolean {
        val d2Dao = databaseAdapter.getCurrentDatabase().d2Dao()
        val query = RoomRawQuery("SELECT COUNT(*) FROM $table")
        val count = d2Dao.intRawQuery(query)
        return count == 0
    }

    suspend fun isDatabaseEmpty(databaseAdapter: DatabaseAdapter): Boolean {
        val d2Dao = databaseAdapter.getCurrentDatabase().d2Dao()
        val query = SimpleSQLiteQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND " +
                "name NOT LIKE 'android_%' AND name NOT LIKE 'sqlite_%' AND " +
                "name NOT LIKE 'room_master_table'",
        )
        val tableNames = d2Dao.stringListRawQuery(query)

        if (tableNames.isEmpty()) {
            return true // No user tables found
        }

        for (tableName in tableNames) {
            if (!isTableEmpty(databaseAdapter, tableName)) {
                // If we find any table that is not empty, the database is not empty.
                return false
            }
        }
        return true
    }

    suspend fun ifTableExist(table: String, databaseAdapter: DatabaseAdapter): Boolean {
        val result = databaseAdapter.rawQuery("PRAGMA table_info($table)")
        return result.isNotEmpty()
    }
}
