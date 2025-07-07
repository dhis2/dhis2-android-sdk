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

package org.hisp.dhis.android.persistence.enrollment

import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.persistence.common.querybuilders.IdentifiableDeletableDataObjectSQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.IdentifiableDeletableDataObjectStoreImpl
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.program.ProgramTrackedEntityAttributeTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class EnrollmentStoreImpl(
    private val appDatabase: AppDatabase,
) : EnrollmentStore, IdentifiableDeletableDataObjectStoreImpl<Enrollment, EnrollmentDB>(
    appDatabase.enrollmentDao(),
    Enrollment::toDB,
    IdentifiableDeletableDataObjectSQLStatementBuilderImpl(EnrollmentTableInfo.TABLE_INFO),
) {
    override suspend fun queryEnrollmentsToPost(): Map<String, List<Enrollment>> {
        val enrollmentsToPostQuery = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.AGGREGATED_SYNC_STATE,
                EnumHelper.asStringList(State.uploadableStatesIncludingError().toList()),
            ).build()
        val enrollmentList: List<Enrollment> = selectWhere(enrollmentsToPostQuery)

        return enrollmentList.groupBy { it.trackedEntityInstance()!! }
    }

    override suspend fun queryMissingRelationshipsUids(): List<String> {
        val whereRelationshipsClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.RELATIONSHIP)
            .appendIsNullValue(EventTableInfo.Columns.ORGANISATION_UNIT)
            .build()

        return selectUidsWhere(whereRelationshipsClause)
    }

    override suspend fun setAggregatedSyncState(uid: String, state: State): Int {
        val dao = appDatabase.enrollmentDao()
        return dao.setAggregatedSyncState(state.name, uid)
    }

    override suspend fun selectAggregatedSyncStateWhere(whereClause: String): List<State> {
        val statesStr = selectStringColumnsWhereClause(DataColumns.AGGREGATED_SYNC_STATE, whereClause)

        return statesStr.map { State.valueOf(it) }
    }

    override suspend fun selectByTrackedEntityInstanceAndAttribute(
        teiUid: String,
        attributeUid: String,
    ): List<Enrollment> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                teiUid,
            )
            .appendInSubQuery(
                EnrollmentTableInfo.Columns.PROGRAM,
                "SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} " +
                    "FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} = " +
                    "'$attributeUid'",
            ).build()

        return selectWhere(whereClause)
    }
}
