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

import java.lang.NumberFormatException
import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.appendCategoryWhereClause
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.appendOrgunitWhereClause
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.getItemsByDimension
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.getPeriodWhereClause
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.AnalyticsEvaluatorHelper.getReportingPeriods
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine

internal class ProgramIndicatorEvaluator @Inject constructor(
    private val eventStore: EventStore,
    private val enrollmentStore: EnrollmentStore,
    private val programIndicatorEngine: ProgramIndicatorEngine
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {

        val programIndicator = getProgramIndicator(evaluationItem, metadata)

        val values: List<String?> = when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                evaluateEventProgramIndicator(programIndicator, evaluationItem, metadata)
            AnalyticsType.ENROLLMENT, null ->
                evaluateEnrollmentProgramIndicator(programIndicator, evaluationItem, metadata)
        }

        return aggregateValues(programIndicator.aggregationType(), values)
    }

    private fun getProgramIndicator(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): ProgramIndicator {
        val programIndicatorItem = (evaluationItem.dimensionItems + evaluationItem.filters)
            .map { it as DimensionItem }
            .find { it is DimensionItem.DataItem.ProgramIndicatorItem }
            ?: throw AnalyticsException.InvalidArguments("Invalid arguments: no program indicator dimension provided.")

        return (metadata[programIndicatorItem.id] as MetadataItem.ProgramIndicatorItem).item
    }

    private fun evaluateEventProgramIndicator(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): List<String?> {
        return getFilteredEventUids(programIndicator, evaluationItem, metadata).map {
            programIndicatorEngine.getEventProgramIndicatorValue(it, programIndicator.uid())
        }
    }

    private fun getFilteredEventUids(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): List<String> {
        val items = getItemsByDimension(evaluationItem)

        val whereClause = WhereClauseBuilder().apply {
            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period -> {
                        val reportingPeriods = getReportingPeriods(entry.value, metadata)
                        appendComplexQuery(
                            WhereClauseBuilder().apply {
                                reportingPeriods.forEach { period ->
                                    appendOrComplexQuery(
                                        getPeriodWhereClause(
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
                        appendOrgunitWhereClause(EventTableInfo.Columns.ORGANISATION_UNIT, entry.value, this, metadata)
                    is Dimension.Category ->
                        appendCategoryWhereClause(EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO, entry.value, this)
                    else -> {
                    }
                }
            }
            appendKeyNumberValue(EventTableInfo.Columns.DELETED, 0)
        }.build()

        return eventStore.selectUidsWhere(whereClause)
    }

    private fun evaluateEnrollmentProgramIndicator(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): List<String?> {
        return getFilteredEnrollmentUids(programIndicator, evaluationItem, metadata).map {
            programIndicatorEngine.getEnrollmentProgramIndicatorValue(it, programIndicator.uid())
        }
    }

    private fun getFilteredEnrollmentUids(
        programIndicator: ProgramIndicator,
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): List<String> {
        val items = getItemsByDimension(evaluationItem)

        val whereClause = WhereClauseBuilder().apply {
            items.entries.forEach { entry ->
                when (entry.key) {
                    is Dimension.Period -> {
                        val reportingPeriods = getReportingPeriods(entry.value, metadata)
                        appendComplexQuery(
                            WhereClauseBuilder().apply {
                                reportingPeriods.forEach { period ->
                                    appendOrComplexQuery(
                                        getPeriodWhereClause(
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
                        appendOrgunitWhereClause(
                            EnrollmentTableInfo.Columns.ORGANISATION_UNIT,
                            entry.value, this, metadata
                        )
                    is Dimension.Category -> TODO()
                    else -> {
                    }
                }
            }
            appendKeyNumberValue(EnrollmentTableInfo.Columns.DELETED, 0)
        }.build()

        return enrollmentStore.selectUidsWhere(whereClause)
    }

    private fun aggregateValues(aggregationType: AggregationType?, values: List<String?>): String? {
        try {
            val floatValues = values.mapNotNull { it?.toFloatOrNull() }

            return when (aggregationType ?: AggregationType.NONE) {
                AggregationType.SUM -> floatValues.sum()
                AggregationType.MAX -> floatValues.max()
                AggregationType.MIN -> floatValues.min()
                AggregationType.AVERAGE,
                AggregationType.AVERAGE_SUM_ORG_UNIT -> floatValues.average().toFloat()
                AggregationType.COUNT -> floatValues.size.toFloat()
                AggregationType.LAST,
                AggregationType.LAST_AVERAGE_ORG_UNIT -> floatValues.lastOrNull()
                else -> floatValues.sum()
            }.toString()
        } catch (e: NumberFormatException) {
            return null
        }
    }
}
