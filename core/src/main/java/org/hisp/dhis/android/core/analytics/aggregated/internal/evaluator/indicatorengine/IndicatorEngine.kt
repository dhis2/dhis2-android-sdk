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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine

import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.analyticexpressionengine.AnalyticExpressionEngineFactory
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.analyticexpressionengine.AnalyticExpressionParserUtils
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import javax.inject.Inject

internal class IndicatorEngine @Inject constructor(
    private val indicatorTypeStore: IdentifiableObjectStore<IndicatorType>,
    private val analyticExpressionEngineFactory: AnalyticExpressionEngineFactory,
    private val expressionService: ExpressionService
) {

    fun evaluateIndicator(
        indicator: Indicator,
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): String? {
        val indicatorType = indicator.indicatorType()?.let {
            indicatorTypeStore.selectByUid(it.uid())
        }

        val engine = analyticExpressionEngineFactory.getEngine(
            method = ParserUtils.ITEM_EVALUATE,
            contextEvaluationItem = contextEvaluationItem,
            contextMetadata = contextMetadata,
            days = AnalyticExpressionParserUtils.getDays(contextEvaluationItem, contextMetadata)
        )

        val numerator = engine.evaluate(indicator.numerator()!!)
        val denominator = engine.evaluate(indicator.denominator()!!)

        return if (numerator != null && denominator != null) {
            val formula = "${indicatorType?.factor() ?: 1} * $numerator / $denominator"
            expressionService.getExpressionValue(formula)?.let {
                val valueStr = it.toString()
                AnalyticExpressionParserUtils.roundValue(valueStr, indicator.decimals())
            }
        } else {
            null
        }
    }
}
