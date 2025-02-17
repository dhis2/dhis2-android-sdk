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

package org.hisp.dhis.android.core.datastore.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerBaseImpl
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.core.datastore.DataStoreEntryTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class DataStoreHandler (
    private val store: DataStoreEntryStore,
) : LinkHandler<DataStoreEntry, DataStoreEntry>, HandlerBaseImpl<DataStoreEntry>() {

    override fun handleMany(
        masterUid: String,
        slaves: Collection<DataStoreEntry>?,
        transformer: (DataStoreEntry) -> DataStoreEntry,
    ) {
        val entriesToHandle = filterNotSyncedEntries(masterUid, slaves)
        handleMany(entriesToHandle)
        cleanOrphan(masterUid, entriesToHandle)
    }

    override fun resetAllLinks() {
        store.delete()
    }

    override fun deleteOrPersist(o: DataStoreEntry): HandleAction {
        return store.updateOrInsertWhere(o)
    }

    private fun filterNotSyncedEntries(
        namespace: String,
        slaves: Collection<DataStoreEntry>?,
    ): List<DataStoreEntry>? {
        return slaves?.let {
            val whereClause = WhereClauseBuilder().run {
                appendKeyStringValue(DataStoreEntryTableInfo.Columns.NAMESPACE, namespace)
                appendNotInKeyStringValues(
                    DataColumns.SYNC_STATE,
                    listOf(State.SYNCED.name, State.SYNCED_VIA_SMS.name),
                )
                build()
            }
            val entriesPendingToSync = store.selectWhere(whereClause)

            slaves.filter { entry ->
                entriesPendingToSync.none { it.key() == entry.key() }
            }
        }
    }

    private fun cleanOrphan(
        namespace: String,
        slaves: Collection<DataStoreEntry>?,
    ) {
        val notInSlaves = WhereClauseBuilder().run {
            appendKeyStringValue(DataStoreEntryTableInfo.Columns.NAMESPACE, namespace)
            appendInKeyEnumValues(DataColumns.SYNC_STATE, listOf(State.SYNCED, State.SYNCED_VIA_SMS))

            if (!slaves.isNullOrEmpty()) {
                appendNotInKeyStringValues(DataStoreEntryTableInfo.Columns.KEY, slaves.map { it.key() })
            }
            build()
        }

        store.deleteWhere(notInSlaves)
    }
}
