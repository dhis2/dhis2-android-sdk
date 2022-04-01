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
package org.hisp.dhis.android.core.indicator.datasetindicatorengine

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.junit.Before
import org.junit.Test

class DataSetIndicatorEvaluatorShould {

    private val indicator: Indicator = mock()
    private val indicatorType: IndicatorType = mock()

    private val days: Int = 31

    private val constant: Constant = Constant.builder().uid("SOp2Q9u5vf2").value(3.5).build()
    private val constantMap: Map<String, Constant> = mapOf(constant.uid() to constant)

    private val diItem1 = "Jc3zxCRAB86"
    private val diItem2 = "qoiAI2HJSIa.uCfsdJFdyfR"
    private val diObject1: DimensionalItemObject = mock()
    private val diObject2: DimensionalItemObject = mock()

    private lateinit var valueMap: Map<DimensionalItemObject, Double>
    private lateinit var orgunitGroupMap: Map<String, Int>

    private lateinit var expressionService: ExpressionService
    private lateinit var evaluator: DataSetIndicatorEvaluator

    @Before
    fun setUp() {
        expressionService = ExpressionService(mock(), mock(), mock(), mock())
        evaluator = DataSetIndicatorEvaluator(expressionService)

        whenever(indicatorType.factor()) doReturn 1
        whenever(diObject1.dimensionItem) doReturn diItem1
        whenever(diObject2.dimensionItem) doReturn diItem2

        whenever(indicator.numerator()) doReturn "#{$diItem1}"
        whenever(indicator.denominator()) doReturn "#{$diItem2}"
        whenever(indicator.decimals()) doReturn 2

        orgunitGroupMap = mapOf()
    }

    @Test
    fun evaluate_indicator_factor() {
        valueMap = mapOf(
            diObject1 to 5.0,
            diObject2 to 10.0
        )

        whenever(indicatorType.factor()) doReturn 1
        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(0.5)

        whenever(indicatorType.factor()) doReturn 100
        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(50)

        whenever(indicatorType.factor()) doReturn -10
        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(-5)
    }

    @Test
    fun evaluate_indicator_decimals_default() {
        valueMap = mapOf(
            diObject1 to 10.0,
            diObject2 to 3.0
        )

        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(3.33)
    }

    @Test
    fun evaluate_indicator_decimals_configurable() {
        whenever(indicator.decimals()) doReturn 3

        valueMap = mapOf(
            diObject1 to 10.0,
            diObject2 to 3.0
        )

        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(3.333)
    }

    @Test
    fun evaluate_null_numerator() {
        valueMap = mapOf(
            diObject2 to 10.0
        )

        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(0.0)
    }

    @Test
    fun evaluate_null_denominator() {
        valueMap = mapOf(
            diObject1 to 10.0
        )

        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(0.0)
    }

    @Test
    fun evaluate_zero_denominator() {
        valueMap = mapOf(
            diObject1 to 10.0,
            diObject2 to 0.0
        )

        assertThat(
            evaluator.evaluate(indicator, indicatorType, valueMap, constantMap, orgunitGroupMap, days)
        ).isEqualTo(0.0)
    }
}
