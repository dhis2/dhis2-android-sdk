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
package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo

@Suppress("TooManyFunctions")
internal object ProgramIndicatorSQLUtils {
    const val event = "eventAlias"
    const val enrollment = "enrollmentAlias"

    fun getEventColumnForEnrollmentWhereClause(column: String, programStageId: String? = null): String {
        return "(SELECT $column FROM ${EventTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EventTableInfo.Columns.ENROLLMENT} = $enrollment.${EnrollmentTableInfo.Columns.UID} " +
            "AND $column IS NOT NULL " +
            (
                programStageId?.let {
                    "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$it' "
                } ?: ""
                ) +
            "ORDER BY ${EventTableInfo.Columns.EVENT_DATE} DESC LIMIT 1)"
    }

    fun getExistsEventForEnrollmentWhere(programStageUid: String, whereClause: String): String {
        return "EXISTS(SELECT 1 FROM ${EventTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EventTableInfo.Columns.ENROLLMENT} = $enrollment.${EnrollmentTableInfo.Columns.UID} " +
            "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageUid' " +
            "AND (${EventTableInfo.Columns.DELETED} IS NULL OR ${EventTableInfo.Columns.DELETED} = 0)" +
            "AND $whereClause)"
    }

    fun getEnrollmentColumnForEventWhereClause(column: String): String {
        return "(SELECT $column " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EnrollmentTableInfo.Columns.UID} = $event.${EventTableInfo.Columns.ENROLLMENT}" +
            ")"
    }

    fun getTrackerDataValueWhereClause(
        column: String,
        programStageUid: String,
        dataElementUid: String,
        programIndicator: ProgramIndicator
    ): String {
        return "(SELECT $column " +
            "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
            "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} " +
            "ON ${TrackedEntityDataValueTableInfo.Columns.EVENT} = ${EventTableInfo.Columns.UID} " +
            "WHERE ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '$dataElementUid' " +
            "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageUid' " +
            "AND ${getDataValueEventWhereClause(programIndicator)} " +
            "AND ${TrackedEntityDataValueTableInfo.Columns.VALUE} IS NOT NULL " +
            "ORDER BY ${EventTableInfo.Columns.EVENT_DATE} DESC LIMIT 1" +
            ")"
    }

    fun getAttributeWhereClause(
        column: String,
        attributeUid: String,
        programIndicator: ProgramIndicator
    ): String {
        return "(SELECT $column " +
            "FROM ${TrackedEntityAttributeValueTableInfo.TABLE_INFO.name()} " +
            "WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} = '$attributeUid' " +
            "AND ${getAttributeValueTEIWhereClause(programIndicator)} " +
            ")"
    }

    fun getDataValueEventWhereClause(programIndicator: ProgramIndicator): String {
        return when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                "${EventTableInfo.TABLE_INFO.name()}.${EventTableInfo.Columns.UID} = " +
                    "$event.${EventTableInfo.Columns.UID}"
            AnalyticsType.ENROLLMENT, null ->
                "${EventTableInfo.TABLE_INFO.name()}.${EventTableInfo.Columns.ENROLLMENT} = " +
                    "$enrollment.${EventTableInfo.Columns.UID}"
        }
    }

    private fun getAttributeValueTEIWhereClause(programIndicator: ProgramIndicator): String {
        val enrollmentSelector = getEnrollmentWhereClause(programIndicator)

        return "${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} IN (" +
            "SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EnrollmentTableInfo.Columns.UID} = $enrollmentSelector " +
            ")"
    }

    fun getEnrollmentWhereClause(programIndicator: ProgramIndicator): String {
        return when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT -> "$event.${EventTableInfo.Columns.ENROLLMENT}"
            AnalyticsType.ENROLLMENT, null -> "$enrollment.${EnrollmentTableInfo.Columns.UID}"
        }
    }

    fun getColumnValueCast(
        column: String,
        valueType: ValueType?
    ): String {
        return when {
            valueType?.isNumeric == true ->
                "CAST($column AS NUMERIC)"
            valueType?.isBoolean == true ->
                "CASE WHEN $column = 'true' THEN 1 ELSE 0 END"
            else ->
                column
        }
    }

    fun getDefaultValue(
        valueType: ValueType?
    ): String {
        return if (valueType?.isNumeric == true || valueType?.isBoolean == true)
            "CAST(0 AS NUMERIC)"
        else
            "''"
    }

    fun valueCountExpression(
        itemIds: Set<DimensionalItemId>,
        programIndicator: ProgramIndicator,
        conditionalValueExpression: String? = null
    ): String {
        val stageElementItems = itemIds.filter {
            it.dimensionalItemType() == DimensionalItemType.TRACKED_ENTITY_DATA_VALUE
        }
        val attributeItems = itemIds.filter {
            it.dimensionalItemType() == DimensionalItemType.TRACKED_ENTITY_ATTRIBUTE
        }

        val stageElementsSql = if (!stageElementItems.isNullOrEmpty()) {
            val stageElementWhereClause = stageElementItems.joinToString(" OR ") {
                "(${EventTableInfo.Columns.PROGRAM_STAGE} = '${it.id0()}' " +
                    "AND " +
                    "${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '${it.id1()}')"
            }

            "SELECT COUNT(*) " +
                "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
                "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} " +
                "ON ${TrackedEntityDataValueTableInfo.Columns.EVENT} = ${EventTableInfo.Columns.UID} " +
                "WHERE ($stageElementWhereClause) " +
                "AND ${getDataValueEventWhereClause(programIndicator)} " +
                "AND ${TrackedEntityDataValueTableInfo.Columns.VALUE} IS NOT NULL " +
                (
                    conditionalValueExpression?.let {
                        "AND CAST(${TrackedEntityDataValueTableInfo.Columns.VALUE} AS NUMERIC) $it"
                    } ?: ""
                    )
        } else {
            "0"
        }

        val attributesSql = if (!attributeItems.isNullOrEmpty()) {
            val attributesWhereClause = "${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} IN " +
                "(${attributeItems.joinToString(",") { "'${it.id0()}'" }})"

            "SELECT COUNT(*) " +
                "FROM ${TrackedEntityAttributeValueTableInfo.TABLE_INFO.name()} " +
                "WHERE $attributesWhereClause " +
                "AND ${getAttributeValueTEIWhereClause(programIndicator)} " +
                (
                    conditionalValueExpression?.let {
                        "AND CAST(${TrackedEntityAttributeValueTableInfo.Columns.VALUE} AS NUMERIC) $it"
                    } ?: ""
                    )
        } else {
            "0"
        }

        return "(($stageElementsSql) + ($attributesSql))"
    }
}
