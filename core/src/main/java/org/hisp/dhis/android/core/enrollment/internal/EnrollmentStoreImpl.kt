/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.enrollment.internal

import android.content.ContentValues
import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo

internal class EnrollmentStoreImpl private constructor(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilderImpl,
    binder: StatementBinder<Enrollment>,
    objectFactory: Function1<Cursor, Enrollment>
) : IdentifiableDeletableDataObjectStoreImpl<Enrollment>(databaseAdapter, builder, binder, objectFactory),
    EnrollmentStore {

    override fun queryEnrollmentsToPost(): Map<String, List<Enrollment>> {
        val enrollmentsToPostQuery = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.AGGREGATED_SYNC_STATE,
                EnumHelper.asStringList(State.uploadableStatesIncludingError().toList())
            ).build()
        val enrollmentList: List<Enrollment> = selectWhere(enrollmentsToPostQuery)

        return enrollmentList.groupBy { it.trackedEntityInstance()!! }
    }

    override fun queryMissingRelationshipsUids(): List<String> {
        val whereRelationshipsClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.AGGREGATED_SYNC_STATE, State.RELATIONSHIP)
            .appendIsNullValue(EventTableInfo.Columns.ORGANISATION_UNIT)
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

    override fun selectAggregatedSyncStateWhere(whereClause: String): List<State> {
        val statesStr = selectStringColumnsWhereClause(DataColumns.AGGREGATED_SYNC_STATE, whereClause)

        return statesStr.map { State.valueOf(it) }
    }

    override fun selectByTrackedEntityInstanceAndAttribute(
        teiUid: String,
        attributeUid: String
    ): List<Enrollment> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teiUid)
            .appendInSubQuery(
                EnrollmentTableInfo.Columns.PROGRAM,
                "SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} " +
                    "FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} = " +
                    "'$attributeUid'"
            ).build()

        return selectWhere(whereClause)
    }

    companion object {
        private val BINDER = StatementBinder { o: Enrollment, w: StatementWrapper ->
            w.bind(1, o.uid())
            w.bind(2, o.created())
            w.bind(3, o.lastUpdated())
            w.bind(4, o.createdAtClient())
            w.bind(5, o.lastUpdatedAtClient())
            w.bind(6, o.organisationUnit())
            w.bind(7, o.program())
            w.bind(8, o.enrollmentDate())
            w.bind(9, o.incidentDate())
            w.bind(10, o.completedDate())
            w.bind(11, o.followUp())
            w.bind(12, o.status())
            w.bind(13, o.trackedEntityInstance())
            w.bind(14, if (o.geometry() == null) null else o.geometry()!!.type())
            w.bind(15, if (o.geometry() == null) null else o.geometry()!!.coordinates())
            w.bind(16, o.syncState())
            w.bind(17, o.aggregatedSyncState())
            w.bind(18, o.deleted())
        }

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): EnrollmentStore {
            val statementBuilder = SQLStatementBuilderImpl(
                EnrollmentTableInfo.TABLE_INFO.name(),
                EnrollmentTableInfo.TABLE_INFO.columns()
            )
            return EnrollmentStoreImpl(
                databaseAdapter,
                statementBuilder,
                BINDER
            ) { cursor: Cursor? -> Enrollment.create(cursor) }
        }
    }
}
