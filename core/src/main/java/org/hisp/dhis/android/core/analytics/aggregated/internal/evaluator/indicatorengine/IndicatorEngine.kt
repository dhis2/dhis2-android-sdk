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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.DataElementEvaluator
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.ProgramIndicatorSQLEvaluator
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorType
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import javax.inject.Inject

internal class IndicatorEngine @Inject constructor(
    private val indicatorTypeStore: IdentifiableObjectStore<IndicatorType>,
    private val dataElementStore: IdentifiableObjectStore<DataElement>,
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository,
    private val dataElementEvaluator: DataElementEvaluator,
    private val programIndicatorEvaluator: ProgramIndicatorSQLEvaluator,
    private val constantStore: IdentifiableObjectStore<Constant>,
    private val expressionService: ExpressionService
) {

    fun evaluateIndicator(
        indicator: Indicator,
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): String? {

        val indicatorContext = IndicatorContext(
            dataElementStore = dataElementStore,
            programIndicatorRepository = programIndicatorRepository,
            dataElementEvaluator = dataElementEvaluator,
            programIndicatorEvaluator = programIndicatorEvaluator,
            evaluationItem = contextEvaluationItem,
            contextMetadata = contextMetadata
        )

        val indicatorType = indicator.indicatorType()?.let {
            indicatorTypeStore.selectByUid(it.uid())
        }

        val visitor = newVisitor(indicatorContext)

        getDays(contextEvaluationItem, contextMetadata)?.let {
            visitor.days = it.toDouble()
        }

        val numerator = evaluate(indicator.numerator()!!, visitor)
        val denominator = evaluate(indicator.denominator()!!, visitor)

        return if (numerator != null && denominator != null) {
            val formula = "$numerator * ${indicatorType?.factor() ?: 1} / $denominator"
            expressionService.getExpressionValue(formula)?.let {
                val valueStr = it.toString()
                if (ParserUtils.isNumeric(valueStr)) {
                    ParserUtils.getRounded(valueStr.toDouble(), indicator.decimals() ?: 2).toString()
                } else {
                    valueStr
                }
            }
        } else {
            null
        }
    }

    private fun evaluate(expression: String, visitor: CommonExpressionVisitor): Any? {
        return CommonParser.visit(expression, visitor)
    }

    private val constantMap: Map<String, Constant>
        get() {
            val constants = constantStore.selectAll()
            return mapByUid(constants)
        }

    private fun getDays(
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): Int? {
        val periods = (contextEvaluationItem.dimensionItems + contextEvaluationItem.filters)
            .map { it as DimensionItem }
            .mapNotNull { item ->
                when (item) {
                    is DimensionItem.PeriodItem.Absolute -> {
                        contextMetadata[item.periodId]?.let {
                            listOf((it as MetadataItem.PeriodItem).item)
                        }
                    }
                    is DimensionItem.PeriodItem.Relative -> {
                        contextMetadata[item.relative.name]?.let {
                            (it as MetadataItem.RelativePeriodItem).periods
                        }
                    }
                    else -> null
                }
            }.flatten()

        val start = periods.mapNotNull { it.startDate() }.minBy { it.time }
        val end = periods.mapNotNull { it.endDate() }.maxBy { it.time }

        return if (start != null && end != null) {
            return PeriodHelper.getDays(Period.builder().startDate(start).endDate(end).build())
        } else {
            null
        }
    }

    private fun newVisitor(indicatorContext: IndicatorContext): CommonExpressionVisitor {
        return CommonExpressionVisitor.newBuilder()
            .withItemMap(IndicatorParserUtils.INDICATOR_EXPRESSION_ITEMS)
            .withItemMethod(ParserUtils.ITEM_EVALUATE)
            .withConstantMap(constantMap)
            .withIndicatorContext(indicatorContext)
            .buildForAnalyticsIndicator()
    }
}
