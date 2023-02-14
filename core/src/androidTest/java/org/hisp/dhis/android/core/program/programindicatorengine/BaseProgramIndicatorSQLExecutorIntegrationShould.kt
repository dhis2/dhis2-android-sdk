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
package org.hisp.dhis.android.core.program.programindicatorengine

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorIntegrationShould
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLExecutor
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore

internal open class BaseProgramIndicatorSQLExecutorIntegrationShould : BaseEvaluatorIntegrationShould() {

    protected val programIndicatorEvaluator = ProgramIndicatorSQLExecutor(
        ConstantStore.create(databaseAdapter),
        DataElementStore.create(databaseAdapter),
        TrackedEntityAttributeStore.create(databaseAdapter),
        databaseAdapter
    )

    protected val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    protected fun evaluateTeiCount(
        filter: String? = null,
        periods: List<Period>? = null,
        boundaries: List<AnalyticsPeriodBoundary>? = null
    ): String? {
        return evaluateProgramIndicator(
            expression = `var`("tei_count"),
            filter = filter,
            analyticsType = AnalyticsType.ENROLLMENT,
            aggregationType = AggregationType.COUNT,
            periods = periods,
            boundaries = boundaries
        )
    }

    protected fun evaluateEventCount(
        filter: String? = null,
        periods: List<Period>? = null,
        boundaries: List<AnalyticsPeriodBoundary>? = null
    ): String? {
        return evaluateProgramIndicator(
            expression = `var`("event_count"),
            filter = filter,
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.COUNT,
            periods = periods,
            boundaries = boundaries
        )
    }

    protected fun evaluateProgramIndicator(
        expression: String,
        filter: String? = null,
        analyticsType: AnalyticsType = AnalyticsType.EVENT,
        aggregationType: AggregationType? = AggregationType.SUM,
        periods: List<Period>? = null,
        boundaries: List<AnalyticsPeriodBoundary>? = null
    ): String? {
        val programIndicator = setProgramIndicator(
            expression = expression,
            filter = filter,
            analyticsType = analyticsType,
            aggregationType = aggregationType,
            boundaries = boundaries
        )

        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(DimensionItem.DataItem.ProgramIndicatorItem(programIndicator.uid())),
            filters = periods?.map { DimensionItem.PeriodItem.Absolute(it.periodId()!!) } ?: emptyList()
        )

        return programIndicatorEvaluator.getProgramIndicatorValue(
            evaluationItem = evaluationItem,
            metadata = metadata +
                (programIndicator.uid() to MetadataItem.ProgramIndicatorItem(programIndicator)) +
                (periods?.associate { it.periodId()!! to MetadataItem.PeriodItem(it) } ?: emptyMap())
        )
    }

    private fun setProgramIndicator(
        expression: String,
        filter: String? = null,
        analyticsType: AnalyticsType = AnalyticsType.EVENT,
        aggregationType: AggregationType? = AggregationType.SUM,
        boundaries: List<AnalyticsPeriodBoundary>? = null
    ): ProgramIndicator {
        val actualBoundaries: List<AnalyticsPeriodBoundary> =
            boundaries ?: when (analyticsType) {
                AnalyticsType.EVENT -> defaultEventBoundaries
                AnalyticsType.ENROLLMENT -> defaultEnrollmentBoundaries
            }

        val programIndicator = ProgramIndicator.builder()
            .uid(generator.generate())
            .displayName("Program indicator")
            .program(ObjectWithUid.create(program.uid()))
            .aggregationType(aggregationType)
            .analyticsType(analyticsType)
            .expression(expression)
            .filter(filter)
            .analyticsPeriodBoundaries(actualBoundaries)
            .build()

        return programIndicator.also {
            helper.setProgramIndicator(it)
        }
    }

    private val defaultEventBoundaries: List<AnalyticsPeriodBoundary> =
        listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .build()
        )

    private val defaultEnrollmentBoundaries: List<AnalyticsPeriodBoundary> =
        listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("ENROLLMENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("ENROLLMENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .build()
        )
}
