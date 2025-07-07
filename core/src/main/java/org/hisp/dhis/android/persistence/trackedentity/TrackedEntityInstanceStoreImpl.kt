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

package org.hisp.dhis.android.persistence.trackedentity

import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.persistence.common.querybuilders.IdentifiableDeletableDataObjectSQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.IdentifiableDeletableDataObjectStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityInstanceStoreImpl(
    private val appDatabase: AppDatabase,
) : TrackedEntityInstanceStore,
    IdentifiableDeletableDataObjectStoreImpl<TrackedEntityInstance, TrackedEntityInstanceDB>(
        appDatabase.trackedEntityInstanceDao(),
        TrackedEntityInstance::toDB,
        IdentifiableDeletableDataObjectSQLStatementBuilderImpl(TrackedEntityInstanceTableInfo.TABLE_INFO),
    ) {
    override suspend fun queryTrackedEntityInstancesToSync(): List<TrackedEntityInstance> {
        val uploadableStatesString = uploadableStatesIncludingError().map { it.name }
        val whereToSyncClause = WhereClauseBuilder()
            .appendInKeyStringValues(DataColumns.AGGREGATED_SYNC_STATE, uploadableStatesString)
            .build()
        return selectWhere(whereToSyncClause)
    }

    override suspend fun queryTrackedEntityInstancesToPost(): List<TrackedEntityInstance> {
        val whereToPostClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.TO_POST.name)
            .build()
        return selectWhere(whereToPostClause)
    }

    override suspend fun querySyncedTrackedEntityInstanceUids(): List<String> {
        val whereSyncedClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.SYNCED)
            .build()
        return selectUidsWhere(whereSyncedClause)
    }

    override suspend fun queryMissingRelationshipsUids(): List<String> {
        val whereRelationshipsClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.RELATIONSHIP)
            .appendIsNullValue(TrackedEntityInstanceTableInfo.Columns.ORGANISATION_UNIT)
            .build()
        return selectUidsWhere(whereRelationshipsClause)
    }

    override suspend fun setAggregatedSyncState(uid: String, state: State): Int {
        val dao = appDatabase.trackedEntityInstanceDao()
        return dao.setAggregatedSyncState(state.name, uid)
    }
}
