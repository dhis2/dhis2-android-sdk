/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EnrollmentAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EventAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.ProgramStageAlias
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.TrackedEntityInstanceAlias
import org.hisp.dhis.android.core.util.SqlUtils.getColumnValueCast
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.program.ProgramStageTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo

internal class ProgramDataElementEvaluator(
    private val item: TrackerLineListItem.ProgramDataElement,
    private val metadata: Map<String, MetadataItem>,
) : TrackerLineListEvaluator() {
    override suspend fun getSelectSQLForEvent(): String {
        val selectEventClause = "= $EventAlias.${EventTableInfo.Columns.UID} "

        return getSelectClause(selectEventClause)
    }

    override suspend fun getSelectSQLForEnrollment(): String {
        val enrollmentSelectClause = "= $EnrollmentAlias.${EnrollmentTableInfo.Columns.UID}"
        val eventSelectClause = getEventSelectClause(enrollmentSelectClause)
        return getSelectClause(eventSelectClause)
    }

    override suspend fun getSelectSQLForTrackedEntityInstance(): String {
        val enrollmentSelectClause = "IN (SELECT " +
            "$EnrollmentAlias.${EnrollmentTableInfo.Columns.UID} " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} $EnrollmentAlias " +
            "WHERE $EnrollmentAlias.${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} = " +
            "$TrackedEntityInstanceAlias.${TrackedEntityInstanceTableInfo.Columns.UID} " +
            "AND $EnrollmentAlias.${EnrollmentTableInfo.Columns.PROGRAM} IN ( " +
            "SELECT $ProgramStageAlias.${ProgramStageTableInfo.Columns.PROGRAM} " +
            "FROM  ${ProgramStageTableInfo.TABLE_INFO.name()} $ProgramStageAlias " +
            "WHERE $ProgramStageAlias.${ProgramStageTableInfo.Columns.UID} = '${item.programStage}') " +
            "ORDER BY $EnrollmentAlias.${EnrollmentTableInfo.Columns.ENROLLMENT_DATE} DESC " +
            "LIMIT 1 )"

        val eventSelectClause = getEventSelectClause(enrollmentSelectClause)
        return getSelectClause(eventSelectClause)
    }

    override suspend fun getCommonWhereSQL(): String {
        return DataFilterHelper.getWhereClause(item.id, item.filters)
    }

    private fun getEventSelectClause(enrollmentSelectClause: String): String {
        /** eventIdx meaning:
         * -> 0: newest event
         * -> -1: newest event - 1 (second newest event)
         * -> 1: oldest event
         * -> 2: oldest event - 1 (second oldest event
         */
        val eventIdx = item.repetitionIndexes?.firstOrNull() ?: 0

        return "IN (SELECT ${EventTableInfo.Columns.UID} " +
            "FROM ${EventTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EventTableInfo.Columns.ENROLLMENT} $enrollmentSelectClause " +
            (item.programStage.let { "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$it' " }) +
            "ORDER BY ${EventTableInfo.Columns.EVENT_DATE} ${if (eventIdx <= 0) "DESC" else "ASC"} " +
            "LIMIT 1 " +
            "OFFSET ${
                if (eventIdx <= 0) {
                    -eventIdx
                } else {
                    eventIdx - 1
                }
            })"
    }

    private fun getSelectClause(selectEventClause: String): String {
        return "SELECT ${getColumnSql()} " +
            "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
            "WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} $selectEventClause " +
            "AND ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '${item.dataElement}'"
    }

    private fun getColumnSql(): String {
        val dataElementMetadata = metadata[item.dataElement]
            ?: throw AnalyticsException.InvalidDataElement(item.id)
        val dataElement = ((dataElementMetadata) as MetadataItem.DataElementItem).item

        return getColumnValueCast(
            column = TrackedEntityDataValueTableInfo.Columns.VALUE,
            valueType = dataElement.valueType(),
        )
    }
}
