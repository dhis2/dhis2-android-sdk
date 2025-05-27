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

import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.querybuilders.ReadOnlySQLStatementBuilder

@Suppress("TooManyFunctions")
internal abstract class ReadableDao<P : EntityDB<*>>(
    val tableName: String,
    protected open val builder: ReadOnlySQLStatementBuilder,
) {
    suspend fun selectAll(): List<P> {
        val query = builder.selectAll()
        return objectListRawQuery(query)
    }

    suspend fun selectWhere(whereClause: String): List<P> {
        val query = builder.selectWhere(whereClause)
        return objectListRawQuery(query)
    }

    suspend fun selectWhere(filterWhereClause: String, orderByClause: String): List<P> {
        val query = builder.selectWhere(filterWhereClause, orderByClause)
        return objectListRawQuery(query)
    }

    suspend fun selectWhere(filterWhereClause: String, orderByClause: String, limit: Int): List<P> {
        val query = builder.selectWhere(filterWhereClause, orderByClause, limit)
        return objectListRawQuery(query)
    }

    suspend fun selectOneOrderedBy(orderingColumName: String, orderingType: SQLOrderType): P? {
        val query = builder.selectOneOrderedBy(orderingColumName, orderingType)
        return objectListRawQuery(query).firstOrNull()
    }

    suspend fun selectOneWhere(whereClause: String): P? {
        val query = builder.selectWhere(whereClause, 1)
        return objectListRawQuery(query).firstOrNull()
    }

    suspend fun selectFirst(): P? {
        val query = builder.selectAll()
        return objectListRawQuery(query).firstOrNull()
    }

    suspend fun count(): Int {
        val query = builder.count()
        return intRawQuery(query)
    }

    suspend fun countWhere(whereClause: String): Int {
        val query = builder.countWhere(whereClause)
        return intRawQuery(query)
    }

    suspend fun groupAndGetCountBy(column: String): Map<String, Int> {
        val query = builder.countAndGroupBy(column)
        return countMapRawQuery(query)
    }

    @RawQuery
    abstract suspend fun objectListRawQuery(sqlRawQuery: RoomRawQuery): List<P>

    @RawQuery
    protected abstract suspend fun intRawQuery(sqlRawQuery: RoomRawQuery): Int

    @RawQuery
    protected abstract suspend fun countMapRawQuery(sqlRawQuery: RoomRawQuery): Map<String, Int>
}
