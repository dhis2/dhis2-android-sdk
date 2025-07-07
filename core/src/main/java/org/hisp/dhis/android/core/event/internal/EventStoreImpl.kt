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
package org.hisp.dhis.android.core.event.internal

import android.content.ContentValues
import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStoreImpl
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper.asStringList
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventTableInfo
import org.koin.core.annotation.Singleton
import java.util.*

@Suppress("TooManyFunctions")
internal class EventStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : EventStore,
    IdentifiableDeletableDataObjectStoreImpl<Event>(
        databaseAdapter,
        EventTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> Event.create(cursor) },
    ) {

    override suspend fun queryEventsAttachedToEnrollmentToPost(): Map<String, List<Event>> {
        val eventsAttachedToEnrollmentsQuery = WhereClauseBuilder()
            .appendIsNotNullValue(EventTableInfo.Columns.ENROLLMENT)
            .appendInKeyStringValues(
                EventTableInfo.Columns.AGGREGATED_SYNC_STATE,
                asStringList(uploadableStatesIncludingError().toList()),
            ).build()
        val eventList = selectWhere(eventsAttachedToEnrollmentsQuery)

        return eventList.filter { it.enrollment() != null }.groupBy { it.enrollment()!! }
    }

    override suspend fun querySingleEventsToPost(): List<Event> {
        val states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(asStringList(uploadableStatesIncludingError().toList())),
        )
        val singleEventsToPostQuery = QUERY_SINGLE_EVENTS +
            " AND (" + EventTableInfo.Columns.SYNC_STATE + " IN (" + states + "))"
        return eventListFromQuery(singleEventsToPostQuery)
    }

    override suspend fun querySingleEvents(): List<Event> {
        return eventListFromQuery(QUERY_SINGLE_EVENTS)
    }

    override suspend fun queryOrderedForEnrollmentAndProgramStage(
        enrollmentUid: String,
        programStageUid: String,
        includeDeleted: Boolean,
    ): List<Event> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollmentUid)
            .appendKeyStringValue(EventTableInfo.Columns.PROGRAM_STAGE, programStageUid)
        if (!includeDeleted) {
            whereClause.appendIsNullOrValue(EventTableInfo.Columns.DELETED, "0")
        }
        val query = "SELECT * FROM " + EventTableInfo.TABLE_INFO.name() + " " +
            "WHERE " + whereClause.build() +
            "ORDER BY " + EventTableInfo.Columns.EVENT_DATE + ", " + EventTableInfo.Columns.LAST_UPDATED
        return eventListFromQuery(query)
    }

    override suspend fun countEventsForEnrollment(enrollmentUid: String, includeDeleted: Boolean): Int {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollmentUid)
        if (!includeDeleted) {
            whereClause.appendIsNullOrValue(EventTableInfo.Columns.DELETED, "0")
        }
        val query = "SELECT * FROM " + EventTableInfo.TABLE_INFO.name() + " " +
            "WHERE " + whereClause.build()
        val events = eventListFromQuery(query)
        return events.size
    }

    override suspend fun countTeisWhereEvents(whereClause: String): Int {
        val query = "SELECT COUNT(DISTINCT a." + EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE + ") " +
            "FROM " + EnrollmentTableInfo.TABLE_INFO.name() + " a " +
            "INNER JOIN " +
            "(SELECT DISTINCT " + EventTableInfo.Columns.ENROLLMENT +
            " FROM " + EventTableInfo.TABLE_INFO.name() + " WHERE $whereClause) b " +
            "ON a." + IdentifiableColumns.UID + " = b." + EventTableInfo.Columns.ENROLLMENT
        return processCount(databaseAdapter.rawQuery(query))
    }

    override suspend fun queryMissingRelationshipsUids(): List<String> {
        val whereRelationshipsClause = WhereClauseBuilder()
            .appendKeyStringValue(DataColumns.SYNC_STATE, State.RELATIONSHIP)
            .appendIsNullValue(EventTableInfo.Columns.ORGANISATION_UNIT)
            .build()
        return selectUidsWhere(whereRelationshipsClause)
    }

    override suspend fun setAggregatedSyncState(uid: String, state: State): Int {
        val updates = ContentValues()
        updates.put(DataColumns.AGGREGATED_SYNC_STATE, state.toString())
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(IdentifiableColumns.UID, uid)
            .build()

        return updateWhere(updates, whereClause)
    }

    override suspend fun selectAggregatedSyncStateWhere(whereClause: String): List<State> {
        val statesStr = selectStringColumnsWhereClause(DataColumns.AGGREGATED_SYNC_STATE, whereClause)

        return statesStr.map { State.valueOf(it) }
    }

    private fun eventListFromQuery(query: String): List<Event> {
        val eventList: MutableList<Event> = ArrayList()
        val cursor = databaseAdapter.rawQuery(query)
        addObjectsToCollection(cursor, eventList)
        return eventList
    }

    companion object {
        private const val QUERY_SINGLE_EVENTS = "SELECT Event.* FROM Event WHERE Event.enrollment IS NULL"
        private val BINDER = StatementBinder { o: Event, w: StatementWrapper ->
            w.bind(1, o.uid())
            w.bind(2, o.enrollment())
            w.bind(3, o.created())
            w.bind(4, o.lastUpdated())
            w.bind(5, o.createdAtClient())
            w.bind(6, o.lastUpdatedAtClient())
            w.bind(7, o.status())
            w.bind(8, if (o.geometry() == null) null else o.geometry()!!.type())
            w.bind(9, if (o.geometry() == null) null else o.geometry()!!.coordinates())
            w.bind(10, o.program())
            w.bind(11, o.programStage())
            w.bind(12, o.organisationUnit())
            w.bind(13, o.eventDate())
            w.bind(14, o.completedDate())
            w.bind(15, o.dueDate())
            w.bind(16, o.syncState())
            w.bind(17, o.aggregatedSyncState())
            w.bind(18, o.attributeOptionCombo())
            w.bind(19, o.deleted())
            w.bind(20, o.assignedUser())
            w.bind(21, o.completedBy())
        }
    }
}
