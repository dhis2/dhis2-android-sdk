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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attribute1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.attributeOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.constant1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitChild1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.periodDec
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.programStage1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.trackedEntityType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal abstract class IndicatorEvaluatorIntegrationBaseShould : BaseEvaluatorIntegrationShould() {

    abstract val indicatorEvaluator: AnalyticsEvaluator

    @Test
    fun should_evaluate_mathematical_expressions() {
        val indicator = createIndicator(
            numerator = "4 * 5 / 2",
            denominator = "2 + 7 - 4"
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("2.0")
    }

    @Test
    fun should_evaluate_sum_of_data_elements() {
        createDataValue("2", dataElementUid = dataElement1.uid())
        createDataValue("3", dataElementUid = dataElement2.uid())

        val indicator = createIndicator(numerator = "${de(dataElement1.uid())} + ${de(dataElement2.uid())}")

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("5.0")
    }

    @Test
    fun should_evaluate_days_variable() {
        createDataValue("62", dataElementUid = dataElement1.uid())

        val indicator = createIndicator(
            numerator = de(dataElement1.uid()),
            denominator = "[days]"
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("2.0")
    }

    @Test
    fun should_round_using_decimals_property() {
        createDataValue("10", dataElementUid = dataElement1.uid())
        createDataValue("3", dataElementUid = dataElement2.uid())

        val indicator = createIndicator(
            numerator = de(dataElement1.uid()),
            denominator = de(dataElement2.uid()),
            decimals = 4
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("3.3333")
    }

    @Test
    fun should_evaluate_constants() {
        createDataValue("10", dataElementUid = dataElement1.uid())

        val indicator = createIndicator(
            numerator = de(dataElement1.uid()),
            denominator = cons(constant1.uid())
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("2.0")
    }

    @Test
    fun should_evaluate_event_data_elements() {
        createEventAndValue("5", dataElement1.uid())
        createEventAndValue("15", dataElement1.uid())

        val indicator = createIndicator(
            numerator = eventDE(program.uid(), dataElement1.uid())
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("20.0")
    }

    @Test
    fun should_evaluate_event_attributes() {
        createTEIAndAttribute("10", attribute1.uid())
        createTEIAndAttribute("5", attribute1.uid())

        val indicator = createIndicator(
            numerator = eventAtt(program.uid(), attribute1.uid())
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("15.0")
    }

    private fun evaluateForThisMonth(
        indicator: Indicator
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.IndicatorItem(indicator.uid()),
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            filters = listOf(
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        return indicatorEvaluator.evaluate(
            evaluationItem,
            metadata + (indicator.uid() to MetadataItem.IndicatorItem(indicator))
        )
    }

    private fun createIndicator(
        numerator: String? = "1",
        denominator: String? = "1",
        decimals: Int? = null,
        factor: Int? = 1
    ): Indicator {
        val indicatorType = IndicatorType.builder()
            .uid(generator.generate())
            .factor(factor)
            .build()
        val indicator = Indicator.builder()
            .uid(generator.generate())
            .displayName("Indicator")
            .indicatorType(ObjectWithUid.create(indicatorType.uid()))
            .decimals(decimals)
            .numerator(numerator)
            .denominator(denominator)
            .build()

        indicatorTypeStore.updateOrInsert(indicatorType)
        indicatorStore.updateOrInsert(indicator)

        return indicator
    }

    private fun createDataValue(
        value: String,
        dataElementUid: String = dataElement1.uid(),
        orgunitUid: String = orgunitParent.uid(),
        periodId: String = periodDec.periodId()!!
    ) {
        val dataValue = DataValue.builder()
            .value(value)
            .dataElement(dataElementUid)
            .period(periodId)
            .organisationUnit(orgunitUid)
            .categoryOptionCombo(categoryOptionCombo.uid())
            .attributeOptionCombo(attributeOptionCombo.uid())
            .build()

        dataValueStore.insert(dataValue)
    }

    private fun createEventAndValue(
        value: String,
        dataElementUid: String,
        enrollmentUid: String? = null
    ) {
        val event = Event.builder()
            .uid(generator.generate())
            .eventDate(periodDec.startDate())
            .enrollment(enrollmentUid)
            .program(program.uid())
            .programStage(programStage1.uid())
            .organisationUnit(orgunitChild1.uid())
            .deleted(false)
            .build()

        eventStore.insert(event)

        val dataValue = TrackedEntityDataValue.builder()
            .event(event.uid())
            .dataElement(dataElementUid)
            .value(value)
            .build()

        trackedEntityDataValueStore.insert(dataValue)
    }

    private fun createTEIAndAttribute(
        value: String?,
        attributeUid: String
    ) {
        val tei = TrackedEntityInstance.builder()
            .uid(generator.generate())
            .trackedEntityType(trackedEntityType.uid())
            .organisationUnit(orgunitChild1.uid())
            .deleted(false)
            .build()

        trackedEntityStore.insert(tei)

        val enrollment = Enrollment.builder()
            .uid(generator.generate())
            .trackedEntityInstance(tei.uid())
            .organisationUnit(orgunitChild1.uid())
            .program(program.uid())
            .deleted(false)
            .build()

        enrollmentStore.insert(enrollment)

        val attributeValue = TrackedEntityAttributeValue.builder()
            .trackedEntityInstance(tei.uid())
            .trackedEntityAttribute(attributeUid)
            .value(value)
            .build()

        trackedEntityAttributeValueStore.insert(attributeValue)
        createEventAndValue("0", dataElement1.uid(), enrollment.uid())
    }

    private fun de(dataElementUid: String): String {
        return "#{$dataElementUid}"
    }

    private fun eventDE(programUid: String, dataElementUid: String): String {
        return "D{$programUid.$dataElementUid}"
    }

    private fun eventAtt(programUid: String, attributeUid: String): String {
        return "A{$programUid.$attributeUid}"
    }

    private fun cons(constantUid: String): String {
        return "C{$constantUid}"
    }
}
