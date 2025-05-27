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

import android.content.ContentValues
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Update
import androidx.room.Upsert
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.querybuilders.ReadOnlySQLStatementBuilder

internal abstract class ObjectDao<P : EntityDB<*>>(
    tableName: String,
    builder: ReadOnlySQLStatementBuilder,
) : ReadableDao<P>(tableName, builder) {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: P): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entities: Collection<P>)

    @Update
    abstract suspend fun update(entity: P): Int

    @Upsert
    abstract suspend fun upsert(entity: P): Int

    suspend fun delete(): Int {
        val query = RoomRawQuery("DELETE FROM $tableName;")
        return intRawQuery(query)
    }

    suspend fun deleteById(id: Long): Int {
        val query = RoomRawQuery("DELETE FROM $tableName WHERE " + CoreColumns.ID + "='" + id + "';")
        return intRawQuery(query)
    }

    suspend fun deleteWhere(clause: String): Boolean {
        val query = RoomRawQuery("DELETE FROM $tableName WHERE $clause;")
        return intRawQuery(query) > 0
    }

    suspend fun updateWhere(updates: ContentValues, whereClause: String): Int {
        val setClause = updates.valueSet().joinToString(", ") { "${it.key} = ${it.value}" }
        val query = RoomRawQuery("UPDATE $tableName SET $setClause WHERE $whereClause;")
        return intRawQuery(query)
    }

    suspend fun selectStringColumn(column: String, clause: String): List<String> {
        val query = RoomRawQuery("SELECT $column FROM $tableName WHERE $clause;")
        return stringListRawQuery(query)
    }

    @RawQuery
    protected abstract suspend fun objectRawQuery(query: RoomRawQuery): P?

    @RawQuery
    abstract suspend fun stringListRawQuery(query: RoomRawQuery): List<String>

    @RawQuery
    protected abstract suspend fun stringSetRawQuery(query: RoomRawQuery): Set<String>
}
