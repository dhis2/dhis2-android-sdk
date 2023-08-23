/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.constant1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201910
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201911
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period201912
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019Q4
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period2019SunW25
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.period202001
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
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

    @Test
    fun should_override_aggregation_type() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)

        val indicator = createIndicator(numerator = de(dataElement1.uid()))

        val defaultValue = evaluateForAbsolute(indicator, period2019Q4.periodId()!!)
        assertThat(defaultValue).isEqualTo("5.0")

        val overrideValue = evaluateForAbsolute(indicator, period2019Q4.periodId()!!, AggregationType.AVERAGE)
        assertThat(overrideValue).isEqualTo("2.5")
    }

    @Test
    fun should_evaluate_period_offset() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)

        val indicator = createIndicator(numerator = "(${de(dataElement1.uid())}.periodOffset(-2)).periodOffset(+1)")

        val value = evaluateForAbsolute(indicator, periodId = period201912.periodId()!!)
        assertThat(value).isEqualTo("2.0")
    }

    @Test
    fun should_evaluate_relative_period_offset() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)
        createDataValue("20", dataElementUid = dataElement2.uid(), periodId = period201911.periodId()!!)
        createDataValue("30", dataElementUid = dataElement2.uid(), periodId = period201912.periodId()!!)

        val expression = "${de(dataElement1.uid())} + ${de(dataElement2.uid())}.periodOffset(-1)"
        val indicator = createIndicator(numerator = expression)

        val value = evaluateForThisMonth(indicator)
        assertThat(value).isEqualTo("23.0")
    }

    @Test
    fun should_evaluate_aggregation_type_function() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("3", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)

        val sumIndicator = createIndicator(numerator = "${de(dataElement1.uid())}.aggregationType(SUM)")
        val sumResult = evaluateForAbsolute(sumIndicator, periodId = period2019Q4.periodId()!!)
        assertThat(sumResult).isEqualTo("5.0")

        val avgIndicator = createIndicator(numerator = "${de(dataElement1.uid())}.aggregationType(AVERAGE)")
        val avgResult = evaluateForAbsolute(avgIndicator, periodId = period2019Q4.periodId()!!)
        assertThat(avgResult).isEqualTo("2.5")
    }

    @Test
    fun should_evaluate_min_date_function() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201910.periodId()!!)
        createDataValue("4", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("8", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)

        mapOf(
            "${de(dataElement1.uid())}.minDate(2019-10-05)" to "12.0",
            "${de(dataElement1.uid())}.maxDate(2019-12-01)" to "6.0",
            "${de(dataElement1.uid())}.minDate(2019-10-05).maxDate(2019-12-01)" to "4.0"
        ).forEach { (numerator, expected) ->
            val indicator = createIndicator(numerator = numerator)
            val result = evaluateForAbsolute(indicator, periodId = period2019Q4.periodId()!!)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun should_evaluate_yearly_period_count_item() {
        val indicator = createIndicator(numerator = "[yearlyPeriodCount]")

        mapOf(
            period201910 to "12.0",
            period2019Q4 to "4.0",
            period2019SunW25 to "52.0"
        ).forEach { (period, expected) ->
            val result = evaluateForAbsolute(indicator, periodId = period.periodId()!!)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun should_evaluate_period_in_year() {
        val indicator = createIndicator(numerator = "[periodInYear]")

        mapOf(
            period201910 to "10.0",
            period2019Q4 to "4.0",
            period2019SunW25 to "25.0"
        ).forEach { (period, expected) ->
            val result = evaluateForAbsolute(indicator, periodId = period.periodId()!!)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun should_evaluate_year_to_date() {
        createDataValue("2", dataElementUid = dataElement1.uid(), periodId = period201910.periodId()!!)
        createDataValue("4", dataElementUid = dataElement1.uid(), periodId = period201911.periodId()!!)
        createDataValue("8", dataElementUid = dataElement1.uid(), periodId = period201912.periodId()!!)
        createDataValue("16", dataElementUid = dataElement1.uid(), periodId = period202001.periodId()!!)

        val indicator = createIndicator(numerator = "${de(dataElement1.uid())}.yearToDate()")

        mapOf(
            period201910 to "2.0",
            period201911 to "6.0",
            period201912 to "14.0",
            period202001 to "16.0"
        ).forEach { (period, expected) ->
            val result = evaluateForAbsolute(indicator, periodId = period.periodId()!!)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun should_evaluate_null_literal() {
        val indicator = createIndicator(numerator = "firstNonNull(null, 4, 2)")
        val result = evaluateForThisMonth(indicator)
        assertThat(result).isEqualTo("4.0")
    }

    @Test
    fun should_evaluate_missing_values() {
        val indicator = createIndicator(numerator = "${de(dataElement1.uid())} / ${de(dataElement2.uid())}")
        val result = evaluateForThisMonth(indicator)
        assertThat(result).isEqualTo("0.0")
    }

    private fun evaluateForThisMonth(
        indicator: Indicator,
        aggregationType: AggregationType = AggregationType.DEFAULT
    ): String? {
        return evaluateFor(
            indicator = indicator,
            aggregationType = aggregationType,
            periodItem = DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
        )
    }

    private fun evaluateForAbsolute(
        indicator: Indicator,
        periodId: String,
        aggregationType: AggregationType = AggregationType.DEFAULT
    ): String? {
        return evaluateFor(
            indicator = indicator,
            aggregationType = aggregationType,
            periodItem = DimensionItem.PeriodItem.Absolute(periodId)
        )
    }

    private fun evaluateFor(
        indicator: Indicator,
        aggregationType: AggregationType = AggregationType.DEFAULT,
        periodItem: DimensionItem.PeriodItem
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.IndicatorItem(indicator.uid()),
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            filters = listOf(
                periodItem
            ),
            aggregationType = aggregationType
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
}
