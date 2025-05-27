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
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.ObjectDao

internal open class ObjectStoreImpl<D : CoreObject, P : EntityDB<D>>(
    protected val objectDao: ObjectDao<P>,
    protected val mapper: MapperToDB<D, P>,
) : ReadableStoreImpl<D, P>(objectDao), MapperToDB<D, P> by mapper {

    suspend fun selectStringColumnsWhereClause(column: String, clause: String): List<String> {
        return objectDao.selectStringColumn(column, clause)
    }

    open suspend fun insert(domainObj: D): Int {
        return objectDao.insert(domainObj.toDB())
    }

    suspend fun insert(objects: Collection<D>) {
        objectDao.insert(objects.map { it.toDB() })
    }

    suspend fun delete(): Int {
        return objectDao.delete()
    }

    suspend fun deleteById(domainObj: D): Boolean {
        return domainObj.id()?.let { objectDao.deleteById(it) > 0 } ?: false
    }

    suspend fun deleteWhere(clause: String): Boolean {
        return objectDao.deleteWhere(clause)
    }

    suspend fun updateWhere(updates: ContentValues, whereClause: String): Int {
        return objectDao.updateWhere(updates, whereClause)
    }

    suspend fun deleteWhereIfExists(whereClause: String) {
        deleteWhere(whereClause)
    }

    val isReady: Boolean
        get() = true // TODO: Check what to do with this
}
