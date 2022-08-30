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

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.validation.MissingValueStrategy

@Reusable
internal class DataSetIndicatorEvaluator @Inject constructor(private val expressionService: ExpressionService) {

    @Suppress("LongParameterList")
    fun evaluate(
        indicator: Indicator,
        indicatorType: IndicatorType,
        valueMap: Map<DimensionalItemObject, Double>,
        constantMap: Map<String, Constant>,
        orgUnitCountMap: Map<String, Int>,
        days: Int
    ): Double {
        val numerator = expressionService.getExpressionValue(
            indicator.numerator(), valueMap, constantMap,
            orgUnitCountMap, days, MissingValueStrategy.NEVER_SKIP
        ) as Double

        val denominator = expressionService.getExpressionValue(
            indicator.denominator(), valueMap, constantMap,
            orgUnitCountMap, days, MissingValueStrategy.NEVER_SKIP
        ) as Double

        val formula = "$numerator * ${indicatorType.factor() ?: 1} / $denominator"
        val value = (expressionService.getExpressionValue(formula) ?: 0.0) as Double

        return ParserUtils.getRounded(value, indicator.decimals() ?: 2)
    }
}
