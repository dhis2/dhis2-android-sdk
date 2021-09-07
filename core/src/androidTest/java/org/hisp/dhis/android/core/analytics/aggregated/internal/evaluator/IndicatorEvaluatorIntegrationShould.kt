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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.categoryOptionCombo
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.generator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.orgunitParent
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.periodDec
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.IndicatorEngine
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class IndicatorEvaluatorIntegrationShould : BaseEvaluatorIntegrationShould() {

    private val dataElementEvaluator = DataElementEvaluator(databaseAdapter)
    private val programIndicatorEvaluator = ProgramIndicatorEvaluator(
        EventStoreImpl.create(databaseAdapter),
        EnrollmentStoreImpl.create(databaseAdapter),
        d2.programModule().programIndicatorEngine()
    )

    private val expressionService = ExpressionService(
        DataElementStore.create(databaseAdapter),
        CategoryOptionComboStoreImpl.create(databaseAdapter),
        OrganisationUnitGroupStore.create(databaseAdapter)
    )

    private val indicatorEngine = IndicatorEngine(
        indicatorTypeStore,
        DataElementStore.create(databaseAdapter),
        d2.programModule().programIndicators(),
        dataElementEvaluator,
        programIndicatorEvaluator,
        ConstantStore.create(databaseAdapter),
        expressionService
    )

    private val indicatorEvaluator = IndicatorEvaluator(indicatorEngine)

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

    private fun evaluateForThisMonth(
        indicator: Indicator,
        relativePeriod: RelativePeriod = RelativePeriod.THIS_MONTH
    ): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.IndicatorItem(indicator.uid()),
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            filters = listOf(
                DimensionItem.PeriodItem.Relative(relativePeriod)
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
            .attributeOptionCombo(categoryOptionCombo.uid())
            .build()

        dataValueStore.insert(dataValue)
    }

    private fun de(dataElementUid: String): String {
        return "#{$dataElementUid}"
    }
}
