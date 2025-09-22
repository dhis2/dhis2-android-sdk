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
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper.asStringList
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityAttributeValueStoreImpl(
    private val databaseAdapter: DatabaseAdapter,
) : TrackedEntityAttributeValueStore,
    ObjectWithoutUidStoreImpl<TrackedEntityAttributeValue, TrackedEntityAttributeValueDB>(
        { databaseAdapter.getCurrentDatabase().trackedEntityAttributeValueDao() },
        TrackedEntityAttributeValue::toDB,
        SQLStatementBuilderImpl(TrackedEntityAttributeValueTableInfo.TABLE_INFO),
    ) {

    override suspend fun queryTrackedEntityAttributeValueToPost(): Map<String, List<TrackedEntityAttributeValue>> {
        val toPostQuery = "SELECT TrackedEntityAttributeValue.* " +
            "FROM (TrackedEntityAttributeValue INNER JOIN TrackedEntityInstance " +
            "ON TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE " + teiInUploadableState() + ";"
        val valueList = selectRawQuery(toPostQuery)

        return valueList.filter { it.trackedEntityInstance() != null }.groupBy { it.trackedEntityInstance()!! }
    }

    private fun teiInUploadableState(): String {
        val states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(
                asStringList(uploadableStatesIncludingError().asList()),
            ),
        )
        return "(" + TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE +
            "." +
            DataColumns.AGGREGATED_SYNC_STATE +
            " IN (" + states + "))"
    }

    override suspend fun queryByTrackedEntityInstance(
        trackedEntityInstanceUid: String,
    ): List<TrackedEntityAttributeValue> {
        val selectByTrackedEntityInstanceQuery = WhereClauseBuilder().appendKeyStringValue(
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            trackedEntityInstanceUid,
        ).build()
        return selectWhere(selectByTrackedEntityInstanceQuery)
    }

    override suspend fun deleteByInstanceAndNotInAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
    ) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityAttributeValueDao()
        dao.deleteByInstanceAndNotInAttributes(trackedEntityInstanceUid, trackedEntityAttributeUids)
    }

    override suspend fun deleteByInstanceAndNotInProgramAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        program: String,
    ) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityAttributeValueDao()
        dao.deleteByInstanceAndNotInProgramAttributes(trackedEntityInstanceUid, trackedEntityAttributeUids, program)
    }

    override suspend fun deleteByInstanceAndNotInAccessibleAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        teiType: String,
        programs: List<String>,
    ) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityAttributeValueDao()
        dao.deleteByInstanceAndNotInAccessibleAttributes(
            trackedEntityInstanceUid,
            trackedEntityAttributeUids,
            programs,
            teiType,
        )
    }

    override suspend fun removeDeletedAttributeValuesByInstance(trackedEntityInstanceUid: String) {
        val dao = databaseAdapter.getCurrentDatabase().trackedEntityAttributeValueDao()
        dao.removeDeletedAttributeValuesByInstance(trackedEntityInstanceUid)
    }

    override suspend fun setSyncStateByInstance(trackedEntityInstanceUid: String, syncState: State) {
        val dao = daoProvider() as TrackedEntityAttributeValueDao
        dao.setSyncStateByInstance(syncState.name, trackedEntityInstanceUid)
    }
}
