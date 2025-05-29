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

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.ObjectWithoutUidDao
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilder

internal open class ObjectWithoutUidStoreImpl<D : CoreObject, P : EntityDB<D>>(
    protected val objectWithoutUidDao: ObjectWithoutUidDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: SQLStatementBuilder,
) : ObjectStoreImpl<D, P>(
    objectWithoutUidDao,
    mapper,
    builder,
) {

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    suspend fun updateWhere(domainObj: D) {
        CollectionsHelper.isNull(domainObj)
        val entity = domainObj.toDB()
        val updated = objectWithoutUidDao.update(entity)
        if (updated == 0) {
            throw RuntimeException("No rows affected")
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionThrown")
    suspend fun deleteWhere(domainObj: D): Boolean {
        CollectionsHelper.isNull(domainObj)
        val entity = domainObj.toDB()
        return objectWithoutUidDao.delete(entity) > 0
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    suspend fun deleteWhereIfExists(domainObj: D) {
        try {
            val entity = domainObj.toDB()
            objectWithoutUidDao.delete(entity)
        } catch (e: RuntimeException) {
            if (e.message != "No rows affected") {
                throw e
            }
        }
    }

    @Throws(RuntimeException::class)
    @Suppress("TooGenericExceptionCaught")
    suspend fun updateOrInsertWhere(domainObj: D): HandleAction {
        val entity = domainObj.toDB()
        val updated = objectWithoutUidDao.update(entity)
        return if (updated == 0) {
            insert(domainObj)
            HandleAction.Insert
        } else {
            HandleAction.Update
        }
    }
}
