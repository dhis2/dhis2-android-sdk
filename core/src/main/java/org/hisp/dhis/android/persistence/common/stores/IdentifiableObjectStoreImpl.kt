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

package org.hisp.dhis.android.persistence.common.stores

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.IdentifiableObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilder

internal open class IdentifiableObjectStoreImpl<D, P : EntityDB<D>>(
    protected val identifiableDao: IdentifiableObjectDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: SQLStatementBuilder,
) : ObjectStoreImpl<D, P>(identifiableDao, mapper, builder) where D : CoreObject, D : ObjectWithUidInterface {

    @Throws(RuntimeException::class)
    override suspend fun insert(domainObj: D): Long {
        CollectionsHelper.isNull(domainObj)
        CollectionsHelper.isNull(domainObj.uid())
        return super.insert(domainObj)
    }

    @Throws(RuntimeException::class)
    suspend fun selectUids(): List<String> {
        val query = builder.selectUids()
        return identifiableDao.selectUids(query)
    }

    @Throws(RuntimeException::class)
    suspend fun selectUidsWhere(whereClause: String): List<String> {
        val query = builder.selectUidsWhere(whereClause)
        return identifiableDao.selectUidsWhere(query)
    }

    @Throws(RuntimeException::class)
    suspend fun selectUidsWhere(whereClause: String, orderByClause: String): List<String> {
        val query = builder.selectUidsWhere(whereClause, orderByClause)
        return identifiableDao.selectUidsWhere(query)
    }

    @Throws(RuntimeException::class)
    suspend fun selectByUid(uid: String): D? {
        CollectionsHelper.isNull(uid)
        val query = builder.selectByUid(uid)
        val dbEntity = identifiableDao.selectByUid(query)
        return dbEntity?.toDomain()
    }

    @Throws(RuntimeException::class)
    suspend fun selectByUids(uids: List<String>): List<D> {
        val whereClause = "uid IN (${uids.joinToString(",") { "'$it'" }})"
        val query = builder.selectWhere(whereClause)
        val dbEntities = identifiableDao.selectByUids(query)
        return dbEntities.map { it.toDomain() }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    suspend fun update(domainObj: D) {
        CollectionsHelper.isNull(domainObj)
        val entity = domainObj.toDB()
        val updated = identifiableDao.update(entity)
        if (updated == 0) {
            throw RuntimeException("No rows affected")
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    suspend fun updateOrInsert(domainObj: D): HandleAction {
        return try {
            update(domainObj)
            HandleAction.Update
        } catch (e: Exception) {
            insert(domainObj)
            HandleAction.Insert
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    suspend fun delete(uid: String) {
        CollectionsHelper.isNull(uid)
        val deleted = identifiableDao.deleteById(query)
        if (deleted == 0) {
            throw RuntimeException("No rows affected")
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    suspend fun deleteIfExists(uid: String) {
        try {
            delete(uid)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }
}
