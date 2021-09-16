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
package org.hisp.dhis.android.core.program.programindicatorengine

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorIntegrationShould
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.constant1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement3
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.firstNovember
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.secondDecember2020
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntity2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.`var`
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.att
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.cons
import org.hisp.dhis.android.core.program.programindicatorengine.BaseTrackerDataIntegrationHelper.Companion.de
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLExecutor
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ProgramIndicatorSQLExecutorIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val programIndicatorEvaluator = ProgramIndicatorSQLExecutor(
        ConstantStore.create(databaseAdapter),
        DataElementStore.create(databaseAdapter),
        databaseAdapter
    )

    private val helper = BaseTrackerDataIntegrationHelper(databaseAdapter)

    @Test
    fun should_evaluate_event_data_values() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = firstNovember
        )

        helper.createTrackedEntity(trackedEntity2.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment2 = generator.generate()
        helper.createEnrollment(trackedEntity2.uid(), enrollment2, program.uid(), orgunitChild1.uid())
        val event2 = generator.generate()
        helper.createTrackerEvent(
            event2, enrollment2, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = firstNovember
        )

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "10")
        helper.insertTrackedEntityDataValue(event2, dataElement1.uid(), "20")

        val eventProgramIndicator = setProgramIndicator(
            expression = de(programStage1.uid(), dataElement1.uid()),
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        )

        val eventValue = programIndicatorEvaluator.getProgramIndicatorValue(eventProgramIndicator)

        assertThat(eventValue).isEqualTo("15")

        val enrollmentProgramIndicator = setProgramIndicator(
            expression = de(programStage1.uid(), dataElement1.uid()),
            analyticsType = AnalyticsType.ENROLLMENT,
            aggregationType = AggregationType.AVERAGE
        )

        val enrollmentValue = programIndicatorEvaluator.getProgramIndicatorValue(enrollmentProgramIndicator)

        assertThat(enrollmentValue).isEqualTo("15")
    }

    @Test
    fun should_evaluate_constants() {
        helper.createSingleEvent(generator.generate(), program.uid(), programStage1.uid(), orgunitChild1.uid())
        helper.createSingleEvent(generator.generate(), program.uid(), programStage1.uid(), orgunitChild1.uid())

        val indicator = setProgramIndicator(
            expression = cons(constant1.uid()),
            analyticsType = AnalyticsType.EVENT
        )

        val value = programIndicatorEvaluator.getProgramIndicatorValue(indicator)

        assertThat(value).isEqualTo("10")
    }

    @Test
    fun should_evaluate_attribute_values() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(
            event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = firstNovember
        )

        val emptyEventValue = programIndicatorEvaluator.getProgramIndicatorValue(
            setProgramIndicator(expression = att(attribute1.uid()), analyticsType = AnalyticsType.EVENT)
        )
        assertThat(emptyEventValue).isNull()

        val emptyEnrollmentValue = programIndicatorEvaluator.getProgramIndicatorValue(
            setProgramIndicator(expression = att(attribute1.uid()), analyticsType = AnalyticsType.ENROLLMENT)
        )
        assertThat(emptyEnrollmentValue).isNull()


        helper.insertTrackedEntityAttributeValue(trackedEntity1.uid(), attribute1.uid(), "8")

        val eventValue = programIndicatorEvaluator.getProgramIndicatorValue(
            setProgramIndicator(expression = att(attribute1.uid()), analyticsType = AnalyticsType.EVENT)
        )
        assertThat(eventValue).isEqualTo("8")

        val enrollmentValue = programIndicatorEvaluator.getProgramIndicatorValue(
            setProgramIndicator(expression = att(attribute1.uid()), analyticsType = AnalyticsType.ENROLLMENT)
        )
        assertThat(enrollmentValue).isEqualTo("8")
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

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("tei_count"),
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("tei_count"),
            analyticsType = AnalyticsType.ENROLLMENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("enrollment_count"),
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("enrollment_count"),
            analyticsType = AnalyticsType.ENROLLMENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("2")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("event_count"),
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("2")

        // This use case is not supported. DHIS2 backend does not support it neither (2.36)
        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = `var`("event_count"),
            analyticsType = AnalyticsType.ENROLLMENT,
            aggregationType = AggregationType.COUNT
        ))).isEqualTo("1")
    }

    @Test
    fun should_evaluate_date_functions() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(
            trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid(),
            enrollmentDate = firstNovember
        )
        helper.createTrackerEvent(
            generator.generate(), enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid(),
            eventDate = secondDecember2020
        )

        val dateDiff = "${`var`("enrollment_date")}, ${`var`("event_date")}"

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:yearsBetween($dateDiff)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:monthsBetween($dateDiff)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        ))).isEqualTo("13")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:weeksBetween($dateDiff)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        ))).isEqualTo("56")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:daysBetween($dateDiff)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        ))).isEqualTo("397")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:minutesBetween($dateDiff)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.AVERAGE
        ))).isEqualTo("571680")
    }

    @Test
    fun should_evaluate_d2_condition() {
        helper.createTrackedEntity(trackedEntity1.uid(), orgunitChild1.uid(), trackedEntityType.uid())
        val enrollment1 = generator.generate()
        helper.createEnrollment(trackedEntity1.uid(), enrollment1, program.uid(), orgunitChild1.uid())
        val event1 = generator.generate()
        helper.createTrackerEvent(event1, enrollment1, program.uid(), programStage1.uid(), orgunitChild1.uid())

        helper.insertTrackedEntityDataValue(event1, dataElement1.uid(), "30")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:condition('${de(programStage1.uid(), dataElement1.uid())} > 10', 10, -10)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("10")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:condition('${de(programStage1.uid(), dataElement1.uid())} < 10', 10, -10)",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("-10")
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

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:count(${de(programStage1.uid(), dataElement1.uid())})",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("2")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, '< 15')",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, '< 5')",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("0")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:countIfCondition(${de(programStage1.uid(), dataElement1.uid())}, " +
                    "'< ${de(programStage1.uid(), dataElement2.uid())}')",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:countIfValue(${de(programStage1.uid(), dataElement1.uid())}, '10')",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("1")

        assertThat(programIndicatorEvaluator.getProgramIndicatorValue(setProgramIndicator(
            expression = "d2:countIfValue(${de(programStage1.uid(), dataElement3.uid())}, 'POSITIVE')",
            analyticsType = AnalyticsType.EVENT,
            aggregationType = AggregationType.SUM
        ))).isEqualTo("1")
    }

    private fun setProgramIndicator(
        expression: String,
        filter: String? = null,
        analyticsType: AnalyticsType? = AnalyticsType.EVENT,
        aggregationType: AggregationType? = AggregationType.SUM
    ): ProgramIndicator {
        val programIndicator = ProgramIndicator.builder()
            .uid(generator.generate())
            .displayName("Program indicator")
            .program(ObjectWithUid.create(program.uid()))
            .aggregationType(aggregationType)
            .analyticsType(analyticsType)
            .expression(expression)
            .filter(filter)
            .build()

        helper.setProgramIndicator(programIndicator)
        return programIndicator
    }
}
