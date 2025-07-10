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
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreWithState
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.DataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.IdentifiableDataObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.IdentifiableDataObjectSQLStatementBuilder

internal open class IdentifiableDataObjectStoreImpl<D, P : EntityDB<D>>(
    override val daoProvider: () -> IdentifiableDataObjectDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: IdentifiableDataObjectSQLStatementBuilder,
) : StoreWithState<D>, IdentifiableObjectStoreImpl<D, P>(daoProvider, mapper, builder)
    where D : DataObject, D : ObjectWithUidInterface {

    @Throws(RuntimeException::class)
    override suspend fun setSyncState(uid: String, state: State): Int {
        val identifiableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val query: RoomRawQuery = builder.setSyncState(uid, state.toString())
        return identifiableDataObjectDao.intRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun setSyncState(uids: List<String>, state: State): Int {
        val identifiableDataObjectDao = daoProvider()
        val nonNullUids = uids.filterNotNull()
        val query: RoomRawQuery = builder.setSyncState(nonNullUids, state.toString())
        return identifiableDataObjectDao.intRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun setSyncStateIfUploading(uid: String, state: State): Int {
        val identifiableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val query: RoomRawQuery = builder.setSyncStateIfUploading(uid, state.toString())
        return identifiableDataObjectDao.intRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun getSyncState(uid: String): State? {
        val identifiableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val query: RoomRawQuery = builder.getSyncState(uid)
        return identifiableDataObjectDao.stateRawQuery(query)
    }

    @Throws(RuntimeException::class)
    override suspend fun exists(uid: String): Boolean {
        val identifiableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val query: RoomRawQuery = builder.exists(uid)
        return identifiableDataObjectDao.intRawQuery(query) > 0
    }

    @Throws(RuntimeException::class)
    override suspend fun getUploadableSyncStatesIncludingError(): List<D> {
        val identifiableDataObjectDao = daoProvider()
        val query: RoomRawQuery = builder.getUploadableSyncStatesIncludingError()
        val entitiesDB = identifiableDataObjectDao.objectListRawQuery(query)
        return entitiesDB.map { it.toDomain() }
    }
}
