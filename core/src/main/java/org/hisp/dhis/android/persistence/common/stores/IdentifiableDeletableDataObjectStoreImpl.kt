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

import org.hisp.dhis.android.core.arch.db.stores.internal.DeletableStoreWithState
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DeletableDataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.MapperToDB
import org.hisp.dhis.android.persistence.common.daos.IdentifiableDeletableDataObjectDao
import org.hisp.dhis.android.persistence.common.querybuilders.IdentifiableDeletableDataObjectSQLStatementBuilder

internal open class IdentifiableDeletableDataObjectStoreImpl<D, P : EntityDB<D>>(
    override val daoProvider: () -> IdentifiableDeletableDataObjectDao<P>,
    mapper: MapperToDB<D, P>,
    override val builder: IdentifiableDeletableDataObjectSQLStatementBuilder,
) : DeletableStoreWithState<D>, IdentifiableDataObjectStoreImpl<D, P>(
    daoProvider,
    mapper,
    builder,
) where D : CoreObject, D : DeletableDataObject, D : ObjectWithUidInterface {

    @Throws(RuntimeException::class)
    override suspend fun setSyncStateOrDelete(uid: String, state: State): HandleAction {
        val identifiableDeletableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        val deleted =
            if (state == State.SYNCED) {
                identifiableDeletableDataObjectDao
                    .deleteWhere(uid = uid, deleted = true, state = State.UPLOADING)
                    .also {
                        println("Deleted $it")
                    } > 0
            } else {
                false
            }
        return if (deleted) {
            HandleAction.Delete
        } else {
            if (setSyncStateIfUploading(uid, state) == 0) HandleAction.NoAction else HandleAction.Update
        }
    }

    @Throws(RuntimeException::class)
    override suspend fun setDeleted(uid: String): Int {
        val identifiableDeletableDataObjectDao = daoProvider()
        CollectionsHelper.isNull(uid)
        return identifiableDeletableDataObjectDao.setDeleted(uid)
    }

    override suspend fun selectSyncStateWhere(where: String): List<State> {
        val identifiableDeletableDataObjectDao = daoProvider()
        val query = builder.selectSyncStateWhere(where)
        return identifiableDeletableDataObjectDao.stateListRawQuery(query)
    }
}
