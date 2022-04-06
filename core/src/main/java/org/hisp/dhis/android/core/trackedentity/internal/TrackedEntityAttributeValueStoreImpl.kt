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
package org.hisp.dhis.android.core.trackedentity.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper.asStringList
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo
import org.hisp.dhis.android.core.trackedentity.*

internal class TrackedEntityAttributeValueStoreImpl private constructor(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilderImpl
) : ObjectWithoutUidStoreImpl<TrackedEntityAttributeValue>(
    databaseAdapter,
    builder,
    BINDER,
    WHERE_UPDATE_BINDER,
    WHERE_DELETE_BINDER,
    { cursor: Cursor -> TrackedEntityAttributeValue.create(cursor) }
),
    TrackedEntityAttributeValueStore {

    override fun queryTrackedEntityAttributeValueToPost(): Map<String, List<TrackedEntityAttributeValue>> {
        val toPostQuery = "SELECT TrackedEntityAttributeValue.* " +
            "FROM (TrackedEntityAttributeValue INNER JOIN TrackedEntityInstance " +
            "ON TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE " + teiInUploadableState() + ";"
        val valueList = trackedEntityAttributeValueListFromQuery(toPostQuery)

        return valueList.filter { it.trackedEntityInstance() != null }.groupBy { it.trackedEntityInstance()!! }
    }

    private fun teiInUploadableState(): String {
        val states = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(
                asStringList(uploadableStatesIncludingError().asList())
            )
        )
        return "(" + TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE +
            "." +
            DataColumns.AGGREGATED_SYNC_STATE +
            " IN (" + states + "))"
    }

    override fun queryByTrackedEntityInstance(trackedEntityInstanceUid: String): List<TrackedEntityAttributeValue> {
        val selectByTrackedEntityInstanceQuery = WhereClauseBuilder().appendKeyStringValue(
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid
        ).build()
        return selectWhere(selectByTrackedEntityInstanceQuery)
    }

    override fun deleteByInstanceAndNotInAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>
    ) {
        val deleteWhereQuery = WhereClauseBuilder()
            .appendKeyStringValue(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUid
            )
            .appendNotInKeyStringValues(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                trackedEntityAttributeUids
            )
            .build()
        deleteWhere(deleteWhereQuery)
    }

    override fun deleteByInstanceAndNotInProgramAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        program: String
    ) {
        val deleteWhereQuery = WhereClauseBuilder()
            .appendKeyStringValue(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUid
            )
            .appendNotInKeyStringValues(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                trackedEntityAttributeUids
            )
            .appendInSubQuery(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                "SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} " +
                    "FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} = '$program'"
            )
            .build()
        deleteWhere(deleteWhereQuery)
    }

    override fun deleteByInstanceAndNotInAccessibleAttributes(
        trackedEntityInstanceUid: String,
        trackedEntityAttributeUids: List<String>,
        teiType: String,
        programs: List<String>
    ) {
        val programsStr = programs.joinToString(separator = ",", prefix = "'", postfix = "'")
        val deleteWhereQuery = WhereClauseBuilder()
            .appendKeyStringValue(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUid
            )
            .appendNotInKeyStringValues(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                trackedEntityAttributeUids
            )
            .appendInSubQuery(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE,
                "SELECT ${ProgramTrackedEntityAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} " +
                    "FROM ${ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramTrackedEntityAttributeTableInfo.Columns.PROGRAM} IN ($programsStr) " +
                    "UNION ALL " +
                    "SELECT ${TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} " +
                    "FROM ${TrackedEntityTypeAttributeTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${TrackedEntityTypeAttributeTableInfo.Columns.TRACKED_ENTITY_TYPE} = '$teiType'"
            )
            .build()
        deleteWhere(deleteWhereQuery)
    }

    override fun removeDeletedAttributeValuesByInstance(trackedEntityInstanceUid: String) {
        val deleteWhereQuery = WhereClauseBuilder()
            .appendKeyStringValue(
                TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUid
            )
            .appendIsNullValue(TrackedEntityAttributeValueTableInfo.Columns.VALUE)
            .build()
        deleteWhere(deleteWhereQuery)
    }

    private fun trackedEntityAttributeValueListFromQuery(query: String): List<TrackedEntityAttributeValue> {
        val trackedEntityAttributeValueList: MutableList<TrackedEntityAttributeValue> = ArrayList()
        val cursor = databaseAdapter.rawQuery(query)
        addObjectsToCollection(cursor, trackedEntityAttributeValueList)
        return trackedEntityAttributeValueList
    }

    companion object {
        private val BINDER =
            StatementBinder<TrackedEntityAttributeValue> { o: TrackedEntityAttributeValue, w: StatementWrapper ->
                w.bind(1, o.value())
                w.bind(2, o.created())
                w.bind(3, o.lastUpdated())
                w.bind(4, o.trackedEntityAttribute())
                w.bind(5, o.trackedEntityInstance())
            }
        private val WHERE_UPDATE_BINDER =
            WhereStatementBinder<TrackedEntityAttributeValue> { o: TrackedEntityAttributeValue, w: StatementWrapper ->
                w.bind(6, o.trackedEntityAttribute())
                w.bind(7, o.trackedEntityInstance())
            }
        private val WHERE_DELETE_BINDER =
            WhereStatementBinder<TrackedEntityAttributeValue> { o: TrackedEntityAttributeValue, w: StatementWrapper ->
                w.bind(1, o.trackedEntityAttribute())
                w.bind(2, o.trackedEntityInstance())
            }

        @JvmField
        val CHILD_PROJECTION = SingleParentChildProjection(
            TrackedEntityAttributeValueTableInfo.TABLE_INFO,
            TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE
        )

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): TrackedEntityAttributeValueStore {
            val statementBuilder = SQLStatementBuilderImpl(
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.name(),
                TrackedEntityAttributeValueTableInfo.TABLE_INFO.columns()
            )
            return TrackedEntityAttributeValueStoreImpl(
                databaseAdapter,
                statementBuilder
            )
        }
    }
}
