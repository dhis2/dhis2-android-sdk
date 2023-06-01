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
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.BaseEvaluatorSamples.program
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.analyticexpressionengine.AnalyticExpressionEngineFactoryHelper
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.expressiondimensionitemengine.ExpressionDimensionItemEngine
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItem
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
internal class ExpressionDimensionItemEvaluatorIntegrationBaseShould : BaseEvaluatorIntegrationShould() {

    private lateinit var evaluator: ExpressionDimensionItemEvaluator

    @Before
    fun setup() {
        val engine = ExpressionDimensionItemEngine(
            AnalyticExpressionEngineFactoryHelper.getFactory(d2),
            expressionService
        )

        evaluator = ExpressionDimensionItemEvaluator(engine)
    }

    @Test
    fun should_evaluate_mathematical_expressions() {
        val item = createExpression(expression = "4 * 5 / 2")

        val value = evaluateForThisMonth(item)

        assertThat(value).isEqualTo("10.0")
    }

    @Test
    fun should_evaluate_sum_of_data_elements() {
        createDataValue("2", dataElementUid = dataElement1.uid())
        createDataValue("3", dataElementUid = dataElement2.uid())

        val item = createExpression(expression = "${de(dataElement1.uid())} + ${de(dataElement2.uid())}")

        val value = evaluateForThisMonth(item)

        assertThat(value).isEqualTo("5.0")
    }

    @Test
    fun should_evaluate_days_variable() {
        createDataValue("62", dataElementUid = dataElement1.uid())

        val item = createExpression(expression = "${de(dataElement1.uid())} + [days]")

        val value = evaluateForThisMonth(item)

        assertThat(value).isEqualTo("93.0")
    }

    @Test
    fun should_evaluate_constants() {
        createDataValue("10", dataElementUid = dataElement1.uid())

        val item = createExpression(expression = cons(constant1.uid()))

        val value = evaluateForThisMonth(item)

        assertThat(value).isEqualTo("5.0")
    }

    @Test
    fun should_evaluate_event_data_elements() {
        createEventAndValue("5", dataElement1.uid())
        createEventAndValue("15", dataElement1.uid())

        val indicator = createExpression(
            expression = eventDE(program.uid(), dataElement1.uid())
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("20.0")
    }

    @Test
    fun should_evaluate_event_attributes() {
        createTEIAndAttribute("10", attribute1.uid())
        createTEIAndAttribute("5", attribute1.uid())

        val indicator = createExpression(
            expression = eventAtt(program.uid(), attribute1.uid())
        )

        val value = evaluateForThisMonth(indicator)

        assertThat(value).isEqualTo("15.0")
    }

    private fun createExpression(
        expression: String
    ): ExpressionDimensionItem {
        val expressionDimensionItem = ExpressionDimensionItem.builder()
            .uid(generator.generate())
            .displayName("Expression Dimension Item")
            .expression(expression)
            .build()

        expressionDimensionItemStore.insert(expressionDimensionItem)

        return expressionDimensionItem
    }

    private fun evaluateForThisMonth(expressionDimensionItem: ExpressionDimensionItem): String? {
        val evaluationItem = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.ExpressionDimensionItem(expressionDimensionItem.uid()),
                DimensionItem.OrganisationUnitItem.Absolute(orgunitParent.uid())
            ),
            filters = listOf(
                DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_MONTH)
            )
        )

        return evaluator.evaluate(
            evaluationItem,
            metadata +
                (expressionDimensionItem.uid() to MetadataItem.ExpressionDimensionItemItem(expressionDimensionItem))
        )
    }
}
