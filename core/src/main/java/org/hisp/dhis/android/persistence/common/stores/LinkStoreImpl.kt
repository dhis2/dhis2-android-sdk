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
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.LinkDao
import org.hisp.dhis.android.persistence.common.querybuilders.LinkSQLStatementBuilder

internal open class LinkStoreImpl<D : CoreObject, P : EntityDB<D>>(
    override val daoProvider: () -> LinkDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: LinkSQLStatementBuilder,
) : LinkStore<D>, ObjectStoreImpl<D, P>(daoProvider, mapper, builder) {

    @Throws(RuntimeException::class)
    override suspend fun insertIfNotExists(o: D): HandleAction {
        return try {
            insert(o)
            HandleAction.Insert
        } catch (e: SQLiteConstraintException) {
            HandleAction.NoAction
        }
    }

    @Throws(RuntimeException::class)
    override suspend fun deleteLinksForMasterUid(parentUid: String) {
        val linkStoreDao = daoProvider()
        CollectionsHelper.isNull(parentUid)
        val query = builder.deleteLinksForParentUid(parentUid)
        linkStoreDao.intRawQuery(query) // Corregir esto, no se puede usar raw query para editar
    }

    @Throws(RuntimeException::class)
    override suspend fun deleteAllLinks(): Int {
        val linkStoreDao = daoProvider()
        val query = builder.deleteTable()
        return linkStoreDao.intRawQuery(query) // Corregir esto, no se puede usar raw query para editar
    }

    @Throws(RuntimeException::class)
    override suspend fun selectDistinctSlaves(childColumn: String): Set<String> {
        val linkStoreDao = daoProvider()
        CollectionsHelper.isNull(childColumn)
        val query = builder.selectDistinctChildren(childColumn)
        return linkStoreDao.stringListRawQuery(query).toSet()
    }

    @Throws(RuntimeException::class)
    override suspend fun selectLinksForMasterUid(parentUid: String): List<D> {
        val linkStoreDao = daoProvider()
        CollectionsHelper.isNull(parentUid)
        val query = builder.selectLinksForParentUid(parentUid)
        val entitiesDB = linkStoreDao.objectListRawQuery(query)
        return entitiesDB.map { it.toDomain() }
    }
}
