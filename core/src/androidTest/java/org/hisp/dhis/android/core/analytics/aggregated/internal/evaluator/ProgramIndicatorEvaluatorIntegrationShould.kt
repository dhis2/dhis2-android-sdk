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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191101
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ProgramIndicatorEvaluatorIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val programIndicatorEvaluator = ProgramIndicatorEvaluator(
        EventStoreImpl.create(databaseAdapter),
        EnrollmentStoreImpl.create(databaseAdapter),
        d2.programModule().programIndicatorEngine()
    )

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    @Test
    fun should_aggregate_data_from_multiple_teis() {
        createSampleData()

        val valueSum = evaluateIndicator(
            setProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid())
            )
        )
        assertThat(valueSum).isEqualTo("30.0")

        val valueAvg = evaluateIndicator(
            setProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                aggregationType = AggregationType.AVERAGE
            )
        )
        assertThat(valueAvg).isEqualTo("15.0")
    }

    @Test
    fun should_override_aggregation_type() {
        createSampleData()

        val defaultValue = evaluateIndicator(
            setProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid())
            )
        )
        assertThat(defaultValue).isEqualTo("30.0")

        val overrideValue = evaluateIndicator(
            setProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid())
            ),
            overrideAggregationType = AggregationType.AVERAGE
        )
        assertThat(overrideValue).isEqualTo("15.0")
    }

    private fun createSampleData() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")
    }

    private fun evaluateIndicator(
        programIndicator: ProgramIndicator,
        overrideAggregationType: AggregationType = AggregationType.DEFAULT
    ): String? {
        val evaluationItemSum = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.ProgramIndicatorItem(programIndicator.uid())
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute(BaseEvaluatorSamples.orgunitParent.uid()),
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_MONTH)
            ),
            aggregationType = overrideAggregationType
        )

        return programIndicatorEvaluator.evaluate(
            evaluationItemSum,
            metadata + (programIndicator.uid() to MetadataItem.ProgramIndicatorItem(programIndicator))
        )
    }

    private fun setProgramIndicator(
        expression: String,
        filter: String? = null,
        analyticsType: AnalyticsType? = AnalyticsType.EVENT,
        aggregationType: AggregationType? = AggregationType.SUM
    ): ProgramIndicator {
        val boundaryTarget = if (analyticsType == AnalyticsType.EVENT) {
            "EVENT_DATE"
        } else {
            "ENROLLMENT_DATE"
        }

        val boundaries = listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(boundaryTarget)
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(boundaryTarget)
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .build()
        )

        val programIndicator = ProgramIndicator.builder()
            .uid(generator.generate())
            .displayName("Program indicator")
            .program(ObjectWithUid.create(program.uid()))
            .aggregationType(aggregationType)
            .analyticsType(analyticsType)
            .expression(expression)
            .filter(filter)
            .analyticsPeriodBoundaries(boundaries)
            .build()

        helper.setProgramIndicator(programIndicator)
        return programIndicator
    }
}
