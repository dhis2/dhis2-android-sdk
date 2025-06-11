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

import android.database.sqlite.SQLiteConstraintException
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.ObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.LinkSQLStatementBuilder

internal open class LinkStoreImpl<D : CoreObject, P : EntityDB<D>>(
    protected val linkStoreDao: ObjectDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: LinkSQLStatementBuilder,
) : ObjectStoreImpl<D, P>(linkStoreDao, mapper, builder) {

    @Throws(RuntimeException::class)
    suspend fun insertIfNotExists(domainObj: D): HandleAction {
        return try {
            insert(domainObj)
            HandleAction.Insert
        } catch (e: SQLiteConstraintException) {
            HandleAction.NoAction
        }
    }

    @Throws(RuntimeException::class)
    suspend fun deleteLinksForMasterUid(parentUid: String) {
        CollectionsHelper.isNull(parentUid)
        val query = builder.deleteLinksForParentUid(parentUid)
        linkStoreDao.intRawQuery(query)
    }

    @Throws(RuntimeException::class)
    suspend fun deleteAllLinks(): Int {
        val query = builder.deleteTable()
        return linkStoreDao.intRawQuery(query)
    }

    @Throws(RuntimeException::class)
    suspend fun selectDistinctSlaves(childColumn: String): Set<String> {
        CollectionsHelper.isNull(childColumn)
        val query = builder.selectDistinctChildren(childColumn)
        return linkStoreDao.stringListRawQuery(query).toSet()
    }

    @Throws(RuntimeException::class)
    suspend fun selectLinksForMasterUid(parentUid: String): List<D> {
        CollectionsHelper.isNull(parentUid)
        val query = builder.selectLinksForParentUid(parentUid)
        val entitiesDB = linkStoreDao.objectListRawQuery(query)
        return entitiesDB.map { it.toDomain() }
    }
}
