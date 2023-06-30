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
package org.hisp.dhis.android.core.trackedentity.internal

import android.content.ContentValues
import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo

internal class TrackedEntityInstanceStoreImpl(
    databaseAdapter: DatabaseAdapter
) : TrackedEntityInstanceStore,
    IdentifiableDeletableDataObjectStoreImpl<TrackedEntityInstance>(
        databaseAdapter,
        TrackedEntityInstanceTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> TrackedEntityInstance.create(cursor) }
    ) {
    override fun queryTrackedEntityInstancesToSync(): List<TrackedEntityInstance> {
        val uploadableStatesString = uploadableStatesIncludingError().map { it.name }
        val whereToSyncClause = WhereClauseBuilder()
            .appendInKeyStringValues(DataColumns.AGGREGATED_SYNC_STATE, uploadableStatesString)
            .build()
        return selectWhere(whereToSyncClause)
    }

    override fun queryTrackedEntityInstancesToPost(): List<TrackedEntityInstance> {
        val whereToPostClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.TO_POST.name)
            .build()
        return selectWhere(whereToPostClause)
    }

    override fun querySyncedTrackedEntityInstanceUids(): List<String> {
        val whereSyncedClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.SYNCED)
            .build()
        return selectUidsWhere(whereSyncedClause)
    }

    override fun queryMissingRelationshipsUids(): List<String> {
        val whereRelationshipsClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.RELATIONSHIP)
            .appendIsNullValue(TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT)
            .build()
        return selectUidsWhere(whereRelationshipsClause)
    }

    override fun setAggregatedSyncState(uid: String, state: State): Int {
        val updates = ContentValues()
        updates.put(DataColumns.AGGREGATED_SYNC_STATE, state.toString())
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(IdentifiableColumns.UID, uid)
            .build()
        return updateWhere(updates, whereClause)
    }

    companion object {
        private val BINDER = StatementBinder { o: TrackedEntityInstance, w: StatementWrapper ->
            w.bind(1, o.uid())
            w.bind(2, o.created())
            w.bind(3, o.lastUpdated())
            w.bind(4, o.createdAtClient())
            w.bind(5, o.lastUpdatedAtClient())
            w.bind(6, o.organisationUnit())
            w.bind(7, o.trackedEntityType())
            w.bind(8, if (o.geometry() == null) null else o.geometry()!!.type())
            w.bind(9, if (o.geometry() == null) null else o.geometry()!!.coordinates())
            w.bind(10, o.syncState())
            w.bind(11, o.aggregatedSyncState())
            w.bind(12, o.deleted())
        }
    }
}
