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

import android.content.ContentValues
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.ObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.ReadOnlySQLStatementBuilder

internal open class ObjectStoreImpl<D : CoreObject, P : EntityDB<D>>(
    protected val objectDao: ObjectDao<P>,
    protected val mapper: MapperToDB<D, P>,
    override val builder: ReadOnlySQLStatementBuilder,
) : ObjectStore<D>, ReadableStoreImpl<D, P>(objectDao, builder), MapperToDB<D, P> by mapper {

    override suspend fun selectStringColumnsWhereClause(column: String, clause: String): List<String> {
        val query = builder.selectStringColumn(column, clause)
        return objectDao.stringListRawQuery(query)
    }

    override suspend fun delete(): Int {
        val query = builder.deleteTable()
        return objectDao.intRawQuery(query)
    }

    override suspend fun deleteById(o: D): Boolean {
        val entityDB = o.toDB()
        return objectDao.delete(entityDB) > 0
    }

    open override suspend fun insert(o: D): Long {
        return objectDao.insert(o.toDB())
    }

    override suspend fun insert(objects: Collection<D>) {
        objectDao.insert(objects.map { it.toDB() })
    }

    suspend fun deleteByEntity(domainObj: D): Boolean {
        val entityDB = domainObj.toDB()
        return objectDao.delete(entityDB) > 0
    }

    override suspend fun deleteWhere(clause: String): Boolean {
        val query = builder.deleteWhere(clause)
        return objectDao.intRawQuery(query) > 0
    }

    override suspend fun updateWhere(updates: ContentValues, whereClause: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWhereIfExists(whereClause: String) {
        deleteWhere(whereClause)
    }

    override val isReady: Boolean
        get() = TODO("Not yet implemented")
}
