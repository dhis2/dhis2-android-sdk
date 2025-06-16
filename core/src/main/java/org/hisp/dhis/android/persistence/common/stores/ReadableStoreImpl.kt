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

package org.hisp.dhis.android.persistence.common.stores

import androidx.room.RoomRawQuery
import org.hisp.dhis.android.core.arch.db.sqlorder.internal.SQLOrderType
import org.hisp.dhis.android.core.arch.db.stores.internal.ReadableStore
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.daos.ReadableDao
import org.hisp.dhis.android.persistence.common.querybuilders.ReadOnlySQLStatementBuilder

@Suppress("TooManyFunctions")
internal open class ReadableStoreImpl<D, P : EntityDB<D>>(
    protected val readableDao: ReadableDao<P>,
    protected open val builder: ReadOnlySQLStatementBuilder,
) : ReadableStore<D> {
    override suspend fun selectAll(): List<D> {
        val query = builder.selectAll()
        val dbEntities = readableDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectWhere(whereClause: String): List<D> {
        val query = builder.selectWhere(whereClause)
        val dbEntities = readableDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectWhere(filterWhereClause: String, orderByClause: String?): List<D> {
        val query = builder.selectWhere(filterWhereClause, orderByClause)
        val dbEntities = readableDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectWhere(filterWhereClause: String, orderByClause: String?, limit: Int): List<D> {
        val query = builder.selectWhere(filterWhereClause, orderByClause, limit)
        val dbEntities = readableDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectWhere(
        filterWhereClause: String,
        orderByClause: String?,
        limit: Int,
        offset: Int?,
    ): List<D> {
        val query = builder.selectWhere(filterWhereClause, orderByClause, limit, offset)
        val dbEntities = readableDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectOneOrderedBy(orderingColumName: String, orderingType: SQLOrderType): D? {
        val query = builder.selectOneOrderedBy(orderingColumName, orderingType)
        val dbEntity = readableDao.objectListRawQuery(query).firstOrNull()
        return dbEntity?.toDomain()
    }

    override suspend fun selectRawQuery(sqlRawQuery: String): List<D> {
        return selectRawQuery(RoomRawQuery(sqlRawQuery))
    }

    suspend fun selectRawQuery(sqlRawQuery: RoomRawQuery): List<D> {
        val dbEntities = readableDao.objectListRawQuery(sqlRawQuery)
        return dbEntities.map { it.toDomain() }
    }

    override suspend fun selectOneWhere(whereClause: String): D? {
        val query = builder.selectWhere(whereClause, 1)
        val dbEntity = readableDao.objectListRawQuery(query).firstOrNull()
        return dbEntity?.toDomain()
    }

    override suspend fun selectFirst(): D? {
        val query = builder.selectAll()
        val entityDB = readableDao.objectListRawQuery(query).firstOrNull()
        return entityDB?.toDomain()
    }

    override suspend fun count(): Int {
        val query = builder.count()
        return readableDao.intRawQuery(query)
    }

    override suspend fun countWhere(whereClause: String): Int {
        val query = builder.countWhere(whereClause)
        return readableDao.intRawQuery(query)
    }

    override suspend fun groupAndGetCountBy(column: String): Map<String, Int> {
        val query = builder.countAndGroupBy(column)
        val groupCountList = readableDao.groupCountListRawQuery(query)
        return groupCountList.associate { it.key to it.count }
    }
}
