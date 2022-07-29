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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import java.util.*
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.program.*
import org.hisp.dhis.android.core.program.programindicatorengine.internal.AnalyticsBoundaryParser
import org.hisp.dhis.android.core.program.programindicatorengine.internal.AnalyticsBoundaryTarget
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.enrollment
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.event
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo

@Suppress("TooManyFunctions")
internal object ProgramIndicatorEvaluatorHelper {

    fun getProgramIndicator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): ProgramIndicator {
        val programIndicatorItem = evaluationItem.allDimensionItems
            .find { it is DimensionItem.DataItem.ProgramIndicatorItem }
            ?: throw AnalyticsException.InvalidArguments("Invalid arguments: no program indicator dimension provided.")

        return (metadata[programIndicatorItem.id] as MetadataItem.ProgramIndicatorItem).item
    }

    private fun hasDefaultBoundaries(programIndicator: ProgramIndicator): Boolean {
        return programIndicator.analyticsPeriodBoundaries()?.let { boundaries ->
            boundaries.size == 2 &&
                (
                    hasDefaultTargetBoundaries(
                        programIndicator,
                        AnalyticsType.EVENT,
                        BoundaryTargetType.EventDate
                    ) ||
                        hasDefaultTargetBoundaries(
                            programIndicator,
                            AnalyticsType.ENROLLMENT,
                            BoundaryTargetType.EnrollmentDate
                        )
                    )
        } ?: false
    }

    private fun hasDefaultTargetBoundaries(
        programIndicator: ProgramIndicator,
        type: AnalyticsType,
        targetType: BoundaryTargetType
    ): Boolean {
        val hasTargetTypeAndNoOffset = programIndicator.analyticsPeriodBoundaries()!!.all {
            it.boundaryTargetType() == targetType &&
                (it.offsetPeriods() == null || it.offsetPeriods() == 0 || it.offsetPeriodType() == null)
        }
        val hasStartBoundary =
            programIndicator.analyticsPeriodBoundaries()!!.any {
                it.analyticsPeriodBoundaryType() == AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD
            }

        val hasEndBoundary =
            programIndicator.analyticsPeriodBoundaries()!!.any {
                it.analyticsPeriodBoundaryType() == AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD
            }

        return programIndicator.analyticsType() == type &&
            hasTargetTypeAndNoOffset &&
            hasStartBoundary &&
            hasEndBoundary
    }

    fun getEventWhereClause(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        val items = AnalyticsDimensionHelper.getItemsByDimension(evaluationItem)

        return WhereClauseBuilder().apply {
            appendComplexQuery(
                WhereClauseBuilder().apply {
                    appendOrKeyNumberValue(EventTableInfo.Columns.DELETED, 0)
                    appendOrIsNullValue(EventTableInfo.Columns.DELETED)
                }.build()
            )
            appendInSubQuery(
                EventTableInfo.Columns.PROGRAM_STAGE,
                "SELECT ${ProgramStageTableInfo.Columns.UID} " +
                    "FROM ${ProgramStageTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramStageTableInfo.Columns.PROGRAM} = '${programIndicator.program()?.uid()}'"
            )

            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period -> {
                        appendProgramIndicatorPeriodClauses(
                            defaultColumn = "$event.${EventTableInfo.Columns.EVENT_DATE}",
                            programIndicator = programIndicator,
                            dimensions = entry.value,
                            builder = this,
                            metadata = metadata
                        )
                    }
                    is Dimension.OrganisationUnit ->
                        AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
                            columnName = EventTableInfo.Columns.ORGANISATION_UNIT,
                            items = entry.value,
                            builder = this,
                            metadata = metadata
                        )
                    is Dimension.Category ->
                        AnalyticsEvaluatorHelper.appendCategoryWhereClause(
                            attributeColumnName = EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                            disaggregationColumnName = null,
                            items = entry.value,
                            builder = this,
                            metadata = metadata
                        )
                    else -> {
                    }
                }
            }
        }.build()
    }

    fun getEnrollmentWhereClause(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        val items = AnalyticsDimensionHelper.getItemsByDimension(evaluationItem)

        return WhereClauseBuilder().apply {
            appendComplexQuery(
                WhereClauseBuilder().apply {
                    appendOrKeyNumberValue(EnrollmentTableInfo.Columns.DELETED, 0)
                    appendOrIsNullValue(EnrollmentTableInfo.Columns.DELETED)
                }.build()
            )
            appendKeyStringValue(EnrollmentTableInfo.Columns.PROGRAM, programIndicator.program()?.uid())

            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period ->
                        appendProgramIndicatorPeriodClauses(
                            defaultColumn = "$enrollment.${EnrollmentTableInfo.Columns.ENROLLMENT_DATE}",
                            programIndicator = programIndicator,
                            dimensions = entry.value,
                            builder = this,
                            metadata = metadata
                        )
                    is Dimension.OrganisationUnit ->
                        AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
                            columnName = EnrollmentTableInfo.Columns.ORGANISATION_UNIT,
                            items = entry.value,
                            builder = this,
                            metadata = metadata
                        )
                    is Dimension.Category -> TODO()
                    else -> {
                    }
                }
            }
        }.build()
    }

    private fun appendProgramIndicatorPeriodClauses(
        dimensions: List<DimensionItem>,
        metadata: Map<String, MetadataItem>,
        programIndicator: ProgramIndicator,
        defaultColumn: String,
        builder: WhereClauseBuilder,
    ) {
        if (hasDefaultBoundaries(programIndicator)) {
            builder.appendComplexQuery(
                buildDefaultBoundariesClause(
                    column = defaultColumn,
                    dimensions = dimensions,
                    metadata = metadata
                )
            )
        } else {
            buildNonDefaultBoundariesClauses(programIndicator, dimensions, metadata).forEach {
                builder.appendComplexQuery(it)
            }
        }
    }

    private fun buildDefaultBoundariesClause(
        column: String,
        dimensions: List<DimensionItem>,
        metadata: Map<String, MetadataItem>
    ): String {
        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(dimensions, metadata)

        return WhereClauseBuilder().apply {
            reportingPeriods.forEach { period ->
                appendOrComplexQuery(
                    AnalyticsEvaluatorHelper.getPeriodWhereClause(
                        columnStart = column,
                        columnEnd = column,
                        period = period
                    )
                )
            }
        }.build()
    }

    private fun buildNonDefaultBoundariesClauses(
        programIndicator: ProgramIndicator,
        dimensions: List<DimensionItem>,
        metadata: Map<String, MetadataItem>
    ): List<String> {
        val startDate: Date? = AnalyticsEvaluatorHelper.getStartDate(dimensions, metadata)
        val endDate: Date? = AnalyticsEvaluatorHelper.getEndDate(dimensions, metadata)

        return if (startDate != null && endDate != null) {
            val boundariesByTarget = programIndicator.analyticsPeriodBoundaries()?.groupBy {
                AnalyticsBoundaryParser.parseBoundaryTarget(it.boundaryTarget())
            }

            boundariesByTarget?.mapNotNull { (target, list) ->
                target?.let {
                    getBoundaryTargetClauses(target, list, programIndicator, startDate, endDate)
                }
            }?.flatten() ?: emptyList()
        } else {
            emptyList()
        }
    }

    @Suppress("LongMethod", "ComplexMethod")
    private fun getBoundaryTargetClauses(
        target: AnalyticsBoundaryTarget,
        boundaries: List<AnalyticsPeriodBoundary>,
        programIndicator: ProgramIndicator,
        startDate: Date,
        endDate: Date
    ): List<String> {
        val analyticsType = programIndicator.analyticsType() ?: AnalyticsType.ENROLLMENT

        return when (target) {
            AnalyticsBoundaryTarget.EventDate -> {
                val column = EventTableInfo.Columns.EVENT_DATE
                val targetColumn = when (analyticsType) {
                    AnalyticsType.EVENT -> "$event.$column"
                    AnalyticsType.ENROLLMENT -> ProgramIndicatorSQLUtils.getEventColumnForEnrollmentWhereClause(column)
                }
                boundaries.map { getBoundaryCondition(targetColumn, it, startDate, endDate) }
            }

            AnalyticsBoundaryTarget.EnrollmentDate -> {
                val column = EnrollmentTableInfo.Columns.ENROLLMENT_DATE
                val targetColumn = when (analyticsType) {
                    AnalyticsType.EVENT -> ProgramIndicatorSQLUtils.getEnrollmentColumnForEventWhereClause(column)
                    AnalyticsType.ENROLLMENT -> "$enrollment.$column"
                }
                boundaries.map { getBoundaryCondition(targetColumn, it, startDate, endDate) }
            }

            AnalyticsBoundaryTarget.IncidentDate -> {
                val column = EnrollmentTableInfo.Columns.INCIDENT_DATE
                val targetColumn = when (analyticsType) {
                    AnalyticsType.EVENT -> ProgramIndicatorSQLUtils.getEnrollmentColumnForEventWhereClause(column)
                    AnalyticsType.ENROLLMENT -> "$enrollment.$column"
                }
                boundaries.map { getBoundaryCondition(targetColumn, it, startDate, endDate) }
            }

            is AnalyticsBoundaryTarget.Custom.DataElement -> {
                val targetColumn = ProgramIndicatorSQLUtils.getTrackerDataValueWhereClause(
                    column = TrackedEntityDataValueTableInfo.Columns.VALUE,
                    programStageUid = target.programStageUid,
                    dataElementUid = target.dataElementUid,
                    programIndicator = programIndicator
                )
                boundaries.map { getBoundaryCondition(targetColumn, it, startDate, endDate) }
            }

            is AnalyticsBoundaryTarget.Custom.Attribute -> {
                val targetColumn = ProgramIndicatorSQLUtils.getAttributeWhereClause(
                    column = TrackedEntityAttributeValueTableInfo.Columns.VALUE,
                    attributeUid = target.attributeUid,
                    programIndicator = programIndicator
                )
                boundaries.map { getBoundaryCondition(targetColumn, it, startDate, endDate) }
            }

            is AnalyticsBoundaryTarget.Custom.PSEventDate ->
                when (analyticsType) {
                    AnalyticsType.EVENT ->
                        throw AnalyticsException.InvalidArguments("PS_EVENTDATE not supported for EVENT analytics")
                    AnalyticsType.ENROLLMENT -> {
                        val whereClauses = boundaries.map {
                            getBoundaryCondition(
                                column = EventTableInfo.Columns.EVENT_DATE,
                                boundary = it,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }

                        val whereClause = ProgramIndicatorSQLUtils.getExistsEventForEnrollmentWhere(
                            programStageUid = target.programStageUid,
                            whereClause = WhereClauseBuilder().apply {
                                whereClauses.forEach { appendComplexQuery(it) }
                            }.build()
                        )

                        listOf(whereClause)
                    }
                }
        }
    }

    private fun getBoundaryCondition(
        column: String,
        boundary: AnalyticsPeriodBoundary,
        startDate: Date,
        endDate: Date
    ): String {
        val operator = when (boundary.analyticsPeriodBoundaryType()) {
            AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD,
            AnalyticsPeriodBoundaryType.AFTER_END_OF_REPORTING_PERIOD -> ">="
            else -> "<="
        }

        val date = when (boundary.analyticsPeriodBoundaryType()) {
            AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD,
            AnalyticsPeriodBoundaryType.BEFORE_START_OF_REPORTING_PERIOD -> startDate
            else -> endDate
        }

        val dateWithOffset = if (boundary.offsetPeriods() != null && boundary.offsetPeriodType() != null) {
            DateUtils.dateWithOffset(date, boundary.offsetPeriods()!!, boundary.offsetPeriodType()!!)
        } else {
            date
        }

        return "julianday($column) $operator julianday('${DateUtils.DATE_FORMAT.format(dateWithOffset)}')"
    }

    fun getAggregator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        programIndicator: ProgramIndicator
    ): AggregationType {
        val aggregationType =
            if (evaluationItem.aggregationType != AggregationType.DEFAULT) {
                evaluationItem.aggregationType
            } else {
                programIndicator.aggregationType()
            }

        return when (aggregationType) {
            null -> AggregationType.AVERAGE

            AggregationType.AVERAGE,
            AggregationType.SUM,
            AggregationType.COUNT,
            AggregationType.MIN,
            AggregationType.MAX -> aggregationType

            AggregationType.AVERAGE_SUM_ORG_UNIT,
            AggregationType.FIRST,
            AggregationType.LAST,
            AggregationType.LAST_IN_PERIOD -> AggregationType.SUM

            AggregationType.FIRST_AVERAGE_ORG_UNIT,
            AggregationType.LAST_AVERAGE_ORG_UNIT,
            AggregationType.LAST_IN_PERIOD_AVERAGE_ORG_UNIT,
            AggregationType.DEFAULT -> AggregationType.AVERAGE

            AggregationType.VARIANCE,
            AggregationType.STDDEV,
            AggregationType.CUSTOM,
            AggregationType.NONE ->
                throw AnalyticsException.UnsupportedAggregationType(programIndicator.aggregationType()!!)
        }
    }
}
