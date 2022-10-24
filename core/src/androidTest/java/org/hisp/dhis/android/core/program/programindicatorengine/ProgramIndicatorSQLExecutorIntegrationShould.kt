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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.constant1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement3
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191101
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191102
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20191110
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.day20201202
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.relationshipType
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.att
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.cons
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ProgramIndicatorSQLExecutorIntegrationShould : BaseProgramIndicatorSQLExecutorIntegrationShould() {

    @Test
    fun should_evaluate_event_data_values() {
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

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("15")

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("15")
    }

    @Test
    fun should_evaluate_constants() {
        helper.createSingleEvent(generator.generate(), program.uid(), programStage1.uid(), orgunitChild1.uid())
        helper.createSingleEvent(generator.generate(), program.uid(), programStage1.uid(), orgunitChild1.uid())

        assertThat(
            evaluateProgramIndicator(
                expression = cons(constant1.uid()),
                analyticsType = AnalyticsType.EVENT
            )
        ).isEqualTo("10")
    }

    @Test
    fun should_evaluate_attribute_values() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        assertThat(
            evaluateProgramIndicator(
                expression = att(attribute1.uid()),
                analyticsType = AnalyticsType.EVENT
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = att(attribute1.uid()),
                analyticsType = AnalyticsType.ENROLLMENT
            )
        ).isEqualTo("0")

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "8")

        assertThat(
            evaluateProgramIndicator(
                expression = att(attribute1.uid()),
                analyticsType = AnalyticsType.EVENT
            )
        ).isEqualTo("8")

        assertThat(
            evaluateProgramIndicator(
                expression = att(attribute1.uid()),
                analyticsType = AnalyticsType.ENROLLMENT
            )
        ).isEqualTo("8")
    }

    @Test
    fun should_evaluate_tei_and_enrollment_count() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment2, program.uid(), orgunitChild1.uid())
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid()
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid()
        )

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("enrollment_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("enrollment_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("2")

        // This use case is not supported. DHIS2 backend does not support it neither (2.36)
        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_date_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191101
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20201202
        )

        val dateDiff = "${`var`("enrollment_date")}, ${`var`("event_date")}"

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:yearsBetween($dateDiff)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:monthsBetween($dateDiff)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("13")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:weeksBetween($dateDiff)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("56")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:daysBetween($dateDiff)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("397")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:minutesBetween($dateDiff)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("571680")
    }

    @Test
    fun should_evaluate_ps_event_date_variable() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191101
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage2.uid(), orgunitChild1.uid(),
            eventDate = day20201202
        )

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:monthsBetween(${`var`("enrollment_date")}, " +
                    "PS_EVENTDATE:${programStage2.uid()})",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("13")
    }

    @Test
    fun should_evaluate_d2_condition() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "30")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:condition('${de(programStage1.uid(), dataElement1.uid())} > 10', 10, -10)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("10")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:condition('${de(programStage1.uid(), dataElement1.uid())} < 10', 10, -10)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("-10")
    }

    @Test
    fun should_evaluate_count_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event1, dataElement2.uid(), "15")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")
        helper.insertTrackedEntityDataValue(event2, dataElement2.uid(), "2")
        helper.insertTrackedEntityDataValue(event2, dataElement3.uid(), "POSITIVE")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:count(${de(programStage1.uid(), dataElement1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, '< 15')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, '< 5')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, " +
                    "'< ${de(programStage1.uid(), dataElement2.uid())}')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:countIfValue(${de(programStage1.uid(), dataElement1.uid())}, 10)",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:countIfValue(${de(programStage1.uid(), dataElement3.uid())}, 'POSITIVE')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_has_value_function() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "2")
        helper.insertTrackedEntityDataValue(event1, dataElement2.uid(), "4")
        helper.insertTrackedEntityDataValue(event2, dataElement2.uid(), "20")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "d2:hasValue(${de(programStage1.uid(), dataElement1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "d2:hasValue(${de(programStage1.uid(), dataElement2.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("2")
    }

    @Test
    fun should_evaluate_null_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement2.uid(), "10")

        assertThat(
            evaluateProgramIndicator(
                expression = "firstNonNull(${de(programStage1.uid(), dataElement1.uid())}, " +
                    "${de(programStage1.uid(), dataElement2.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("10")

        assertThat(
            evaluateProgramIndicator(
                expression = "if(isNull(${de(programStage1.uid(), dataElement1.uid())}), '50', '500')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("100")

        assertThat(
            evaluateProgramIndicator(
                expression = "if(isNotNull(${de(programStage1.uid(), dataElement1.uid())}), '50', '500')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1000")
    }

    @Test
    fun should_filter_program_stage_data_values() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "2")
        helper.insertTrackedEntityDataValue(event1, dataElement2.uid(), "4")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")
        helper.insertTrackedEntityDataValue(event2, dataElement2.uid(), "10")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "${de(programStage1.uid(), dataElement1.uid())} > 3",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "${de(programStage1.uid(), dataElement2.uid())} > 3",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("2")
    }

    @Test
    fun should_evaluate_status_variables() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            status = EnrollmentStatus.COMPLETED
        )
        val enrollment2 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment2, program.uid(), orgunitChild1.uid(),
            status = EnrollmentStatus.ACTIVE
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(),
            orgunitChild1.uid(), status = EventStatus.COMPLETED
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage2.uid(),
            orgunitChild1.uid(), status = EventStatus.ACTIVE
        )

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("enrollment_count"),
                filter = "${`var`("enrollment_status")} == 'COMPLETED'",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "${`var`("event_status")} == 'COMPLETED'",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_value_count() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "9")
        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "-3")

        val expression = "${de(programStage1.uid(), dataElement1.uid())} + " +
            "${de(programStage1.uid(), dataElement2.uid())} + ${att(attribute1.uid())}"

        assertThat(
            evaluateProgramIndicator(
                expression = expression,
                filter = "${`var`("value_count")} >= 2",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("6")

        assertThat(
            evaluateProgramIndicator(
                expression = expression,
                filter = "${`var`("value_count")} < 2",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isNull()

        assertThat(
            evaluateProgramIndicator(
                expression = expression,
                filter = "${`var`("zero_pos_value_count")} >= 1",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("6")

        assertThat(
            evaluateProgramIndicator(
                expression = expression,
                filter = "${`var`("zero_pos_value_count")} < 1",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isNull()
    }

    @Test
    fun should_evaluate_special_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "9")
        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "-3")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:oizp(${de(programStage1.uid(), dataElement1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:oizp(${de(programStage1.uid(), dataElement1.uid())} + ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:zing(${de(programStage1.uid(), dataElement1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("0")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:zing(${de(programStage1.uid(), dataElement1.uid())} + ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("6")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:zpvc(${de(programStage1.uid(), dataElement1.uid())}, " +
                    "${de(programStage1.uid(), dataElement2.uid())}, ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:zing(d2:oizp(d2:zpvc(${att(attribute1.uid())})))",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_max_min_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "3")
        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "12")

        assertThat(
            evaluateProgramIndicator(
                expression = "greatest(${de(programStage1.uid(), dataElement1.uid())}, ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("12")

        assertThat(
            evaluateProgramIndicator(
                expression = "greatest(${de(programStage1.uid(), dataElement2.uid())}, ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("3")

        assertThat(
            evaluateProgramIndicator(
                expression = "least(${de(programStage1.uid(), dataElement1.uid())}, ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("3")

        assertThat(
            evaluateProgramIndicator(
                expression = "least(${de(programStage1.uid(), dataElement2.uid())}, ${att(attribute1.uid())})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("3")
    }

    @Test
    fun should_evaluate_string_comparison() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement3.uid(), "POSITIVE")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                filter = "${de(programStage1.uid(), dataElement3.uid())} == 'POSITIVE'",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_boolean_comparison() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "2")
        helper.insertTrackedEntityDataValue(event1, dataElement4.uid(), "true")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "${de(programStage1.uid(), dataElement4.uid())} == true",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("event_count"),
                filter = "(${de(programStage1.uid(), dataElement1.uid())} > 0) == true",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_most_recent_existing_value_for_enrollment_analytics() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1,
            enrollment1,
            program.uid(),
            programStage1.uid(),
            orgunitChild1.uid(),
            eventDate = day20191101
        )
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2,
            enrollment1,
            program.uid(),
            programStage1.uid(),
            orgunitChild1.uid(),
            eventDate = day20191102
        )
        val event3 = generator.generate()
        helper.createTrackerEvent(
            event3,
            enrollment1,
            program.uid(),
            programStage1.uid(),
            orgunitChild1.uid(),
            eventDate = day20201202
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "1")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "2")

        assertThat(
            evaluateProgramIndicator(
                expression = de(programStage1.uid(), dataElement1.uid()),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("2")
    }

    @Test
    fun should_evaluate_program_stage_variables() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                filter = "${`var`("program_stage_id")} == '${programStage1.uid()}'",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                filter = "${`var`("program_stage_id")} == '${programStage1.uid()}'",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                filter = "${`var`("program_stage_name")} == '${programStage1.name()}'",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("tei_count"),
                filter = "${`var`("program_stage_name")} == '${programStage1.name()}'",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_analytics_period_variables() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = day20191102
        )
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191110
        )

        assertThat(
            evaluateTeiCount(
                filter = "d2:daysBetween(${`var`("analytics_period_start")}, ${`var`("event_date")}) < 15",
                listOf(period201911)
            )
        ).isEqualTo("1")

        assertThat(
            evaluateTeiCount(
                filter = "d2:daysBetween(${`var`("analytics_period_start")}, ${`var`("event_date")}) < 5",
                listOf(period201911)
            )
        ).isEqualTo("0")

        assertThat(
            evaluateTeiCount(
                filter = "d2:daysBetween(${`var`("event_date")}, ${`var`("analytics_period_end")}) < 5",
                listOf(period201911)
            )
        ).isEqualTo("0")

        assertThat(
            evaluateTeiCount(
                filter = "d2:daysBetween(${`var`("event_date")}, ${`var`("analytics_period_end")}) < 24",
                listOf(period201911)
            )
        ).isEqualTo("1")
    }

    @Test
    fun should_evaluate_orgunit_count_variable() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = day20191101
        )
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild2.uid(),
            eventDate = day20191101
        )

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("org_unit_count"),
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = `var`("org_unit_count"),
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.COUNT
            )
        ).isEqualTo("2")
    }

    @Test
    fun should_evaluate_relationship_count() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid()
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid()
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild2.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild2.uid())
        helper.createTrackerEvent(
            generator.generate(), enrollment2, program.uid(), programStage1.uid(), orgunitChild2.uid()
        )

        helper.createRelationship(relationshipType.uid(), trackedEntity1.uid(), trackedEntity2.uid())

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:relationshipCount()",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:relationshipCount('${relationshipType.uid()}')",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("2")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:relationshipCount()",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("3")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:relationshipCount('${relationshipType.uid()}')",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("3")
    }

    @Test
    fun should_evaluate_creation_and_sync_date_variables() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            created = day20191101, lastUpdated = day20191102
        )
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            created = day20191101, lastUpdated = day20191110
        )

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:daysBetween(${`var`("creation_date")}, ${`var`("sync_date")})",
                analyticsType = AnalyticsType.ENROLLMENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("1")

        assertThat(
            evaluateProgramIndicator(
                expression = "d2:daysBetween(${`var`("creation_date")}, ${`var`("sync_date")})",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("9")
    }

    @Test
    fun should_aggregate_by_event() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            created = day20191101, lastUpdated = day20191102
        )
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            created = day20191101, lastUpdated = day20191110
        )
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            created = day20191101, lastUpdated = day20191110
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "4")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "7")

        assertThat(
            evaluateProgramIndicator(
                expression = "${de(programStage1.uid(), dataElement1.uid())} + 100",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.SUM
            )
        ).isEqualTo("211")

        assertThat(
            evaluateProgramIndicator(
                expression = "${de(programStage1.uid(), dataElement1.uid())} % 5",
                analyticsType = AnalyticsType.EVENT,
                aggregationType = AggregationType.AVERAGE
            )
        ).isEqualTo("3")
    }
}
