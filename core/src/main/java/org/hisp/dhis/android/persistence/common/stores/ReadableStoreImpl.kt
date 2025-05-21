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
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.daos.ReadableDao

internal open class ReadableStoreImpl<D, P : EntityDB<D>>(
    protected val readableDao: ReadableDao<P>,
) {
    suspend fun selectAll(): List<D> {
        val dbEntities = readableDao.selectAll()
        return dbEntities.map { it.toDomain() }
    }

    suspend fun selectWhere(whereClause: String): List<D> {
        val dbEntities = readableDao.selectWhere(whereClause)
        return dbEntities.map { it.toDomain() }
    }

    suspend fun selectWhere(filterWhereClause: String, orderByClause: String): List<D> {
        val dbEntities = readableDao.selectWhere(filterWhereClause, orderByClause)
        return dbEntities.map { it.toDomain() }
    }

    suspend fun selectWhere(filterWhereClause: String, orderByClause: String, limit: Int): List<D> {
        val dbEntities = readableDao.selectWhere(filterWhereClause, orderByClause, limit)
        return dbEntities.map { it.toDomain() }
    }

    suspend fun selectOneOrderedBy(orderingColumName: String, orderingType: SQLOrderType): D? {
        return readableDao.selectOneOrderedBy(orderingColumName, orderingType)?.toDomain()
    }

    suspend fun selectRawQuery(sqlRawQuery: String): List<D> {
        val dbEntities = readableDao.objectListRawQuery(RoomRawQuery(sqlRawQuery))
        return dbEntities.map { it.toDomain() }
    }

    suspend fun selectOneWhere(whereClause: String): D? {
        return readableDao.selectOneWhere(whereClause)?.toDomain()
    }

    suspend fun selectFirst(): D? {
        return readableDao.selectFirst()?.toDomain()
    }

    suspend fun count(): Int {
        return readableDao.count()
    }

    suspend fun countWhere(whereClause: String): Int {
        return readableDao.countWhere(whereClause)
    }

    suspend fun groupAndGetCountBy(column: String): Map<String, Int> {
        return readableDao.groupAndGetCountBy(column)
    }
}
