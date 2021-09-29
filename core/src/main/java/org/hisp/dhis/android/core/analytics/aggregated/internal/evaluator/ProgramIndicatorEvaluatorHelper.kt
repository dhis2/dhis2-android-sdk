/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.program.*

internal object ProgramIndicatorEvaluatorHelper {

    fun getProgramIndicator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): ProgramIndicator {
        val programIndicatorItem = (evaluationItem.dimensionItems + evaluationItem.filters)
            .map { it as DimensionItem }
            .find { it is DimensionItem.DataItem.ProgramIndicatorItem }
            ?: throw AnalyticsException.InvalidArguments("Invalid arguments: no program indicator dimension provided.")

        val programIndicator = (metadata[programIndicatorItem.id] as MetadataItem.ProgramIndicatorItem).item

        if (!hasDefaultBoundaries(programIndicator)) {
            throw AnalyticsException.ProgramIndicatorCustomBoundaries(programIndicator)
        }

        return programIndicator
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
        val hasStartBoundary by lazy {
            programIndicator.analyticsPeriodBoundaries()!!.any {
                it.boundaryTargetType() == targetType &&
                    it.analyticsPeriodBoundaryType() == AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD
            }
        }
        val hasEndBoundary by lazy {
            programIndicator.analyticsPeriodBoundaries()!!.any {
                it.boundaryTargetType() == targetType &&
                    it.analyticsPeriodBoundaryType() == AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD
            }
        }

        return programIndicator.analyticsType() == type && hasStartBoundary && hasEndBoundary
    }

    fun getEventWhereClause(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String {
        val items = AnalyticsEvaluatorHelper.getItemsByDimension(evaluationItem)

        return WhereClauseBuilder().apply {
            appendKeyNumberValue(EventTableInfo.Columns.DELETED, 0)
            appendInSubQuery(
                EventTableInfo.Columns.PROGRAM_STAGE,
                "SELECT ${ProgramStageTableInfo.Columns.UID} " +
                    "FROM ${ProgramStageTableInfo.TABLE_INFO.name()} " +
                    "WHERE ${ProgramStageTableInfo.Columns.PROGRAM} = '${programIndicator.program()?.uid()}'"
            )

            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period -> {
                        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(entry.value, metadata)
                        appendComplexQuery(
                            WhereClauseBuilder().apply {
                                reportingPeriods.forEach { period ->
                                    appendOrComplexQuery(
                                        AnalyticsEvaluatorHelper.getPeriodWhereClause(
                                            columnStart = EventTableInfo.Columns.EVENT_DATE,
                                            columnEnd = EventTableInfo.Columns.EVENT_DATE,
                                            period = period
                                        )
                                    )
                                }
                            }.build()
                        )
                    }
                    is Dimension.OrganisationUnit ->
                        AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
                            EventTableInfo.Columns.ORGANISATION_UNIT,
                            entry.value,
                            this,
                            metadata
                        )
                    is Dimension.Category ->
                        AnalyticsEvaluatorHelper.appendCategoryWhereClause(
                            EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                            entry.value,
                            this
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
        val items = AnalyticsEvaluatorHelper.getItemsByDimension(evaluationItem)

        return WhereClauseBuilder().apply {
            appendKeyStringValue(EnrollmentTableInfo.Columns.PROGRAM, programIndicator.program()?.uid())
            appendKeyNumberValue(EnrollmentTableInfo.Columns.DELETED, 0)

            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period -> {
                        val reportingPeriods = AnalyticsEvaluatorHelper.getReportingPeriods(entry.value, metadata)
                        appendComplexQuery(
                            WhereClauseBuilder().apply {
                                reportingPeriods.forEach { period ->
                                    appendOrComplexQuery(
                                        AnalyticsEvaluatorHelper.getPeriodWhereClause(
                                            columnStart = EnrollmentTableInfo.Columns.ENROLLMENT_DATE,
                                            columnEnd = EnrollmentTableInfo.Columns.ENROLLMENT_DATE,
                                            period = period
                                        )
                                    )
                                }
                            }.build()
                        )
                    }
                    is Dimension.OrganisationUnit ->
                        AnalyticsEvaluatorHelper.appendOrgunitWhereClause(
                            EnrollmentTableInfo.Columns.ORGANISATION_UNIT,
                            entry.value, this, metadata
                        )
                    is Dimension.Category -> TODO()
                    else -> {
                    }
                }
            }
        }.build()
    }
}
