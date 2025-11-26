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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.program.ProgramStageDataElementTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class TrackedEntityDataValueStoreImpl(
    private val databaseAdapter: DatabaseAdapter,
) : TrackedEntityDataValueStore, ObjectWithoutUidStoreImpl<TrackedEntityDataValue, TrackedEntityDataValueDB>(
    { databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao() },
    TrackedEntityDataValue::toDB,
    SQLStatementBuilderImpl(TrackedEntityDataValueTableInfo.TABLE_INFO),
) {

    override suspend fun deleteByEventAndNotInDataElements(
        eventUid: String,
        dataElementUids: List<String>,
    ): Boolean {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao()
        return dao.deleteByEventAndNotInDataElements(eventUid, dataElementUids) > 0
    }

    override suspend fun deleteByEventAndDataElement(
        eventUid: String,
        dataElementUid: String,
    ): Boolean {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao()
        return dao.deleteByEventAndDataElement(eventUid, dataElementUid) > 0
    }

    override suspend fun deleteByEvent(eventUid: String): Boolean {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao()
        return dao.deleteByEvent(eventUid) > 0
    }

    override suspend fun queryTrackedEntityDataValuesByEventUid(eventUid: String): List<TrackedEntityDataValue> {
        val whereClauseBuilder =
            WhereClauseBuilder().appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
        return selectWhere(whereClauseBuilder.build())
    }

    override suspend fun querySingleEventsTrackedEntityDataValues(): Map<String, List<TrackedEntityDataValue>> {
        val queryStatement =
            "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid)" +
                " WHERE Event.enrollment ISNULL " + "AND " + eventInUploadableState() + ";"
        return queryTrackedEntityDataValues(queryStatement)
    }

    override suspend fun removeDeletedDataValuesByEvent(eventUid: String) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao()
        dao.removeDeletedDataValuesByEvent(eventUid)
    }

    override suspend fun removeUnassignedDataValuesByEvent(eventUid: String) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityDataValueDao()
        dao.removeUnassignedDataValuesByEvent(eventUid)
    }

    private fun eventInUploadableState(): String {
        val states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(
                uploadableStatesIncludingError().map { it.name },
            ),
        )
        return "(Event." + EventTableInfo.Columns.AGGREGATED_SYNC_STATE + " IN (" + states + "))"
    }

    override suspend fun queryTrackerTrackedEntityDataValues(): Map<String, List<TrackedEntityDataValue>> {
        val queryStatement =
            "SELECT TrackedEntityDataValue.* " +
                " FROM (TrackedEntityDataValue INNER JOIN Event ON TrackedEntityDataValue.event = Event.uid) " +
                " WHERE Event.enrollment IS NOT NULL " + "AND " + eventInUploadableState() + ";"
        return queryTrackedEntityDataValues(queryStatement)
    }

    override suspend fun queryToPostByEvent(eventUid: String): List<TrackedEntityDataValue> {
        val queryStatement =
            WhereClauseBuilder().appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .appendInSubQuery(
                    TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT,
                    getInProgramStageDataElementsSubQuery(eventUid),
                ).build()

        return selectWhere(queryStatement)
    }

    override suspend fun setSyncStateByEvent(eventUid: String, syncState: State) {
        val dao = daoProvider() as TrackedEntityDataValueDao
        dao.setSyncStateByEvent(eventUid, syncState.name)
    }

    override suspend fun getForEvent(eventUid: String): List<TrackedEntityDataValue> {
        val whereClause =
            WhereClauseBuilder()
                .appendKeyStringValue(TrackedEntityDataValueTableInfo.Columns.EVENT, eventUid)
                .build()
        val selectStatement = builder.selectWhere(whereClause)
        return selectRawQuery(selectStatement)
    }

    private fun getInProgramStageDataElementsSubQuery(eventUid: String): String {
        val psDataElementName = ProgramStageDataElementTableInfo.TABLE_INFO.name()
        val eventName = EventTableInfo.TABLE_INFO.name()

        return "SELECT ${ProgramStageDataElementTableInfo.Columns.DATA_ELEMENT}" +
            " FROM $psDataElementName INNER JOIN $eventName " +
            " ON $psDataElementName.${ProgramStageDataElementTableInfo.Columns.PROGRAM_STAGE}" +
            " = $eventName.${EventTableInfo.Columns.PROGRAM_STAGE}" +
            " WHERE $eventName.${EventTableInfo.Columns.UID} = '$eventUid'"
    }

    private suspend fun queryTrackedEntityDataValues(
        queryStatement: String,
    ): Map<String, List<TrackedEntityDataValue>> {
        val dataValueList = selectRawQuery(queryStatement)

        return dataValueList.filter { it.event() != null }.groupBy { it.event()!! }
    }
}
