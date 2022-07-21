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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191101
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191102
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191110
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191201
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20201202
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202012
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundaryType
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.att
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.psEventDate
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ProgramIndicatorBoundariesSQLExecutorIntegrationShould :
    BaseProgramIndicatorSQLExecutorIntegrationShould() {

    @Test
    fun should_evaluate_static_boundaries() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(
            trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20201202
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.EVENT,
                periods = listOf(period201912)
            )
        ).isEqualTo("10")

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.EVENT,
                periods = listOf(period201912),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget("EVENT_DATE")
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("20")

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.EVENT,
                periods = listOf(period201911),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget("ENROLLMENT_DATE")
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget("ENROLLMENT_DATE")
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("30")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget("EVENT_DATE")
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget("EVENT_DATE")
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_data_element_boundaries() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment1, program.uid(), programStage2.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "2020-12-03")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "2020-11-14")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(de(programStage1.uid(), dataElement1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(de(programStage1.uid(), dataElement1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(de(programStage1.uid(), dataElement1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(de(programStage1.uid(), dataElement1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_attribute_boundaries() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage2.uid(), orgunitChild1.uid(),
            eventDate = day20191110
        )

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "2020-12-03")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(att(attribute1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(att(attribute1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = listOf(
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(att(attribute1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                        .build(),
                    AnalyticsPeriodBoundary.builder()
                        .boundaryTarget(att(attribute1.uid()))
                        .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                        .build()
                )
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_ps_event_boundaries() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191101
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20201202
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(
            trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment2, program.uid(), programStage2.uid(), orgunitChild1.uid(),
            eventDate = day20191110
        )

        val boundaries = listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(psEventDate(programStage1.uid()))
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(psEventDate(programStage1.uid()))
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .build()
        )

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period201911),
                boundaries = boundaries
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period201912),
                boundaries = boundaries
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202001),
                boundaries = boundaries
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT,
                periods = listOf(period202012),
                boundaries = boundaries
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_offsets() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191101
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20201202
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(
            trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191201
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment2, program.uid(), programStage2.uid(), orgunitChild1.uid(),
            eventDate = day20191110
        )

        // Test moving a period backwards
        val previousMonthEventBoundaries = onePeriodBackwardsBoundaries("EVENT_DATE")

        assertThat(
            evaluateEventCount(
                periods = listOf(period201911),
                boundaries = previousMonthEventBoundaries
            )
        ).isEqualTo("0")

        assertThat(
            evaluateEventCount(
                periods = listOf(period201912),
                boundaries = previousMonthEventBoundaries
            )
        ).isEqualTo("1")

        assertThat(
            evaluateEventCount(
                periods = listOf(period202001),
                boundaries = previousMonthEventBoundaries
            )
        ).isEqualTo("2")

        // Test moving a period backwards
        val previousMonthEnrollmentBoundaries = onePeriodBackwardsBoundaries("ENROLLMENT_DATE")
        assertThat(
            evaluateTeiCount(
                periods = listOf(period201911),
                boundaries = previousMonthEnrollmentBoundaries
            )
        ).isEqualTo("0")

        assertThat(
            evaluateTeiCount(
                periods = listOf(period201912),
                boundaries = previousMonthEnrollmentBoundaries
            )
        ).isEqualTo("1")

        assertThat(
            evaluateTeiCount(
                periods = listOf(period202001),
                boundaries = previousMonthEnrollmentBoundaries
            )
        ).isEqualTo("1")

        // Test adding periods before the start and after the end
        val wideRangeBoundaries = listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .offsetPeriods(-5)
                .offsetPeriodType(PeriodType.Monthly)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget("EVENT_DATE")
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .offsetPeriods(15)
                .offsetPeriodType(PeriodType.Monthly)
                .build()
        )

        assertThat(
            evaluateEventCount(
                periods = listOf(period201912),
                boundaries = wideRangeBoundaries
            )
        ).isEqualTo("4")
    }

    fun onePeriodBackwardsBoundaries(column: String): List<AnalyticsPeriodBoundary> {
        return listOf(
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(column)
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.AFTER_START_OF_REPORTING_PERIOD)
                .offsetPeriods(-1)
                .offsetPeriodType(PeriodType.Monthly)
                .build(),
            AnalyticsPeriodBoundary.builder()
                .boundaryTarget(column)
                .analyticsPeriodBoundaryType(AnalyticsPeriodBoundaryType.BEFORE_END_OF_REPORTING_PERIOD)
                .offsetPeriods(-1)
                .offsetPeriodType(PeriodType.Monthly)
                .build()
        )
    }
}
