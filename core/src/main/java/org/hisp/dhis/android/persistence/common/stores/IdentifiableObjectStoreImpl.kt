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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.IdentifiableObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilder

internal open class IdentifiableObjectStoreImpl<D, P : EntityDB<D>>(
    override val daoProvider: () -> IdentifiableObjectDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: SQLStatementBuilder,
) : IdentifiableObjectStore<D>,
    ObjectStoreImpl<D, P>(daoProvider, mapper, builder) where D : CoreObject, D : ObjectWithUidInterface {

    private val upsertMutex = Mutex()

    @Throws(RuntimeException::class)
    override suspend fun insert(o: D): Long {
        CollectionsHelper.isNull(o)
        CollectionsHelper.isNull(o.uid())
        return super.insert(o)
    }

    @Throws(RuntimeException::class)
    override suspend fun selectUids(): List<String> {
        val identifiableObjectDao = daoProvider()
        val query = builder.selectUids()
        return identifiableObjectDao.stringListRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun selectUidsWhere(whereClause: String): List<String> {
        val identifiableObjectDao = daoProvider()
        val query = builder.selectUidsWhere(whereClause)
        return identifiableObjectDao.stringListRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun selectUidsWhere(whereClause: String, orderByClause: String?): List<String> {
        val identifiableObjectDao = daoProvider()
        val query = builder.selectUidsWhere(whereClause, orderByClause)
        return identifiableObjectDao.stringListRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun selectByUid(uid: String): D? {
        val identifiableObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val query = builder.selectByUid(uid)
        val dbEntity = identifiableObjectDao.objectRawQuery(query)
        return dbEntity?.toDomain()
    }

    @Throws(RuntimeException::class)
    override suspend fun selectByUids(uid: List<String>): List<D> {
        val identifiableObjectDao = daoProvider()
        val whereClause = "uid IN (${uid.joinToString(",") { "'$it'" }})"
        val query = builder.selectWhere(whereClause)
        val dbEntities = identifiableObjectDao.objectListRawQuery(query)
        return dbEntities.map { it.toDomain() }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    override suspend fun update(o: D) {
        val identifiableObjectDao = daoProvider()
        CollectionsHelper.isNull(o)
        val entity = o.toDB()
        val updated = identifiableObjectDao.update(entity)
        if (updated == 0) {
            throw RuntimeException("No rows affected")
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateOrInsert(o: D): HandleAction = upsertMutex.withLock {
        return try {
            update(o)
            HandleAction.Update
        } catch (e: Exception) {
            insert(o)
            HandleAction.Insert
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    override suspend fun delete(uid: String) {
        val identifiableObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val deleted = identifiableObjectDao.delete(uid)
        if (deleted == 0) {
            throw RuntimeException("No rows affected")
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteIfExists(uid: String) {
        try {
            delete(uid)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }
}
