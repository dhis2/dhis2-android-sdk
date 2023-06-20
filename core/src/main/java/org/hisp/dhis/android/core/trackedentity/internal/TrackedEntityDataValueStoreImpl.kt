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

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo

internal class TrackedEntityDataValueStoreImpl(
    databaseAdapter: DatabaseAdapter
) : TrackedEntityDataValueStore,
    ObjectWithoutUidStoreImpl<TrackedEntityDataValue>(
        databaseAdapter,
        TrackedEntityDataValueTableInfo.TABLE_INFO,
        BINDER,
        WHERE_UPDATE_BINDER,
        WHERE_DELETE_BINDER,
        { cursor: Cursor -> TrackedEntityDataValue.create(cursor) }
    ) {

    override fun deleteByEventAndNotInDataElements(
        eventUid: String,
        dataElementUids: List<String>
    ): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
            .appendNotInKeyStringValues(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT, dataElementUids)
            .build()
        return deleteWhere(whereClause)
    }

    override fun deleteByEvent(eventUid: String): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
            .build()
        return deleteWhere(whereClause)
    }

    override fun queryTrackedEntityDataValuesByEventUid(eventUid: String): List<TrackedEntityDataValue> {
        val whereClauseBuilder = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
        return selectWhere(whereClauseBuilder.build())
    }

    override fun querySingleEventsTrackedEntityDataValues(): Map<String, MutableList<TrackedEntityDataValue>> {
        val queryStatement = "SELECT TrackedEntityDataValue.* " +
            " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid)" +
            " WHERE Event.enrollment ISNULL " +
            "AND " + eventInUploadableState() + ";"
        return queryTrackedEntityDataValues(queryStatement)
    }

    override fun removeDeletedDataValuesByEvent(eventUid: String) {
        val deleteWhereQuery = WhereClauseBuilder()
            .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
            .appendIsNullValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE)
            .build()
        deleteWhere(deleteWhereQuery)
    }

    private fun eventInUploadableState(): String {
        val states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(
                uploadableStatesIncludingError().map { it.name }
            )
        )
        return "(Event." + EventTableInfo.Columns.AGGREGATED_SYNC_STATE + " IN (" + states + "))"
    }

    override fun queryTrackerTrackedEntityDataValues(): Map<String, MutableList<TrackedEntityDataValue>> {
        val queryStatement = "SELECT TrackedEntityDataValue.* " +
            " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
            " WHERE Event.enrollment IS NOT NULL " +
            "AND " + eventInUploadableState() + ";"
        return queryTrackedEntityDataValues(queryStatement)
    }

    override fun queryByUploadableEvents(): Map<String, MutableList<TrackedEntityDataValue>> {
        val queryStatement = "SELECT TrackedEntityDataValue.* " +
            " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
            " WHERE " + eventInUploadableState() + ";"
        return queryTrackedEntityDataValues(queryStatement)
    }

    private fun queryTrackedEntityDataValues(queryStatement: String): Map<String, MutableList<TrackedEntityDataValue>> {
        val dataValueList: MutableList<TrackedEntityDataValue> = ArrayList()
        val cursor = databaseAdapter.rawQuery(queryStatement)
        addObjectsToCollection(cursor, dataValueList)
        val dataValuesMap: MutableMap<String, MutableList<TrackedEntityDataValue>> = HashMap()
        for (dataValue in dataValueList) {
            addDataValuesToMap(dataValuesMap, dataValue)
        }
        return dataValuesMap
    }

    private fun addDataValuesToMap(
        dataValuesMap: MutableMap<String, MutableList<TrackedEntityDataValue>>,
        dataValue: TrackedEntityDataValue
    ) {
        if (dataValuesMap[dataValue.event()] == null) {
            dataValuesMap[dataValue.event()!!] = ArrayList()
        }
        dataValuesMap[dataValue.event()]!!.add(dataValue)
    }

    companion object {
        private val BINDER = StatementBinder { o: TrackedEntityDataValue, w: StatementWrapper ->
            w.bind(1, o.event())
            w.bind(2, o.created())
            w.bind(3, o.lastUpdated())
            w.bind(4, o.dataElement())
            w.bind(5, o.storedBy())
            w.bind(6, o.value())
            w.bind(7, o.providedElsewhere())
        }
        private val WHERE_UPDATE_BINDER = WhereStatementBinder { o: TrackedEntityDataValue, w: StatementWrapper ->
            w.bind(8, o.event())
            w.bind(9, o.dataElement())
        }
        private val WHERE_DELETE_BINDER = WhereStatementBinder { o: TrackedEntityDataValue, w: StatementWrapper ->
            w.bind(1, o.event())
            w.bind(2, o.dataElement())
        }
        val CHILD_PROJECTION = SingleParentChildProjection(
            TrackedEntityDataValueTableInfo.TABLE_INFO, TrackedEntityDataValueTableInfo.Columns.EVENT
        )
    }
}
