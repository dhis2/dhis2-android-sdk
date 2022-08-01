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

import javax.inject.Inject
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.enrollment
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.event

internal class ProgramIndicatorEvaluator @Inject constructor(
    private val eventStore: EventStore,
    private val enrollmentStore: EnrollmentStore,
    private val programIndicatorEngine: ProgramIndicatorEngine
) : AnalyticsEvaluator {

    override fun evaluate(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {

        val programIndicator = ProgramIndicatorEvaluatorHelper.getProgramIndicator(evaluationItem, metadata)

        val aggregationType = ProgramIndicatorEvaluatorHelper.getAggregator(evaluationItem, programIndicator)

        val values: List<String?> = when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                evaluateEventProgramIndicator(programIndicator, evaluationItem, metadata)
            AnalyticsType.ENROLLMENT, null ->
                evaluateEnrollmentProgramIndicator(programIndicator, evaluationItem, metadata)
        }

        return aggregateValues(aggregationType, values)
    }

    override fun getSql(
        evaluationItem: AnalyticsServiceEvaluationItem,
        metadata: Map<String, MetadataItem>
    ): String? {
        throw AnalyticsException.SQLException("Method getSql not implemented for ProgramIndicatorEvaluator")
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
        val whereClause = ProgramIndicatorEvaluatorHelper
            .getEventWhereClause(programIndicator, evaluationItem, metadata)

        val rawClause = "SELECT * FROM ${EventTableInfo.TABLE_INFO.name()} $event WHERE $whereClause"
        return eventStore.selectRawQuery(rawClause).map { it.uid() }
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
        val whereClause =
            ProgramIndicatorEvaluatorHelper.getEnrollmentWhereClause(programIndicator, evaluationItem, metadata)

        val rawClause = "SELECT * FROM ${EnrollmentTableInfo.TABLE_INFO.name()} $enrollment WHERE $whereClause"
        return enrollmentStore.selectRawQuery(rawClause).map { it.uid() }
    }

    private fun aggregateValues(aggregationType: AggregationType?, values: List<String?>): String? {
        try {
            val floatValues = values.mapNotNull { it?.toFloatOrNull() }

            return when (aggregationType ?: AggregationType.NONE) {
                AggregationType.SUM -> floatValues.sum()
                AggregationType.MAX -> floatValues.maxOrNull()
                AggregationType.MIN -> floatValues.minOrNull()
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
