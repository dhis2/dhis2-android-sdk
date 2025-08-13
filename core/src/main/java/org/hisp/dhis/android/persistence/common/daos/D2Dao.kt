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

package org.hisp.dhis.android.persistence.common.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteQuery
import org.hisp.dhis.android.core.arch.db.access.internal.migrations.DatabaseCodeMigration133DataValue
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.common.SchemaRow

@Dao
internal interface D2Dao {

    @RawQuery
    suspend fun intRawQuery(sqlRawQuery: RoomRawQuery): Int

    @RawQuery
    suspend fun stringListRawQuery(query: SupportSQLiteQuery): List<String>

    @RawQuery
    suspend fun stringRawQuery(query: RoomRawQuery): String

    @RawQuery
    suspend fun queryStringValue(query: SupportSQLiteQuery): String?

    @TypeConverters(StateTypeConverter::class)
    @RawQuery
    suspend fun getTypedSyncStates(query: SupportSQLiteQuery): List<State>

    @RawQuery
    suspend fun getCodeMigration133DataValue(query: SupportSQLiteQuery): List<DatabaseCodeMigration133DataValue>

    @Query("SELECT name, sql FROM sqlite_master ORDER BY name")
    suspend fun getSchemaRows(): List<SchemaRow>

    @RawQuery
    suspend fun getTableInfo(query: SupportSQLiteQuery): List<PragmaTableInfoRow>
}


internal data class PragmaTableInfoRow(
    val name: String,
)
