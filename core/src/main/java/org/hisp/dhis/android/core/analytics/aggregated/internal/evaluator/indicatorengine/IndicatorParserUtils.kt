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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem.DataElementItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem.ProgramAttributeItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem.ProgramDataElementItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem.ProgramIndicatorItem
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.*
import org.hisp.dhis.parser.expression.antlr.ExpressionParser

/*
* Copyright (c) 2004-2020, University of Oslo
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this
* list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
* Neither the name of the HISP project nor the names of its contributors may
* be used to endorse or promote products derived from this software without
* specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

internal object IndicatorParserUtils {

    val INDICATOR_EXPRESSION_ITEMS = ParserUtils.COMMON_EXPRESSION_ITEMS +
        mapOf(
            ExpressionParser.D2_CEIL to D2Ceil(),
            ExpressionParser.D2_CONCATENATE to D2Concatenate(),
            ExpressionParser.D2_FLOOR to D2Floor(),
            ExpressionParser.D2_LEFT to D2Left(),
            ExpressionParser.D2_LENGTH to D2Length(),
            ExpressionParser.D2_MODULUS to D2Modulus(),
            ExpressionParser.D2_RIGHT to D2Right(),
            ExpressionParser.D2_ROUND to D2Round(),
            ExpressionParser.D2_SPLIT to D2Split(),
            ExpressionParser.D2_SUBSTRING to D2Substring(),
            ExpressionParser.D2_OIZP to D2Oizp(),
            ExpressionParser.D2_VALIDATE_PATTERN to D2ValidatePattern(),
            ExpressionParser.D2_ZING to D2Zing(),
            ExpressionParser.D2_ZPVC to D2Zpvc(),
            ExpressionParser.D2_MINUTES_BETWEEN to D2MinutesBetween(),
            ExpressionParser.D2_DAYS_BETWEEN to D2DaysBetween(),
            ExpressionParser.D2_WEEKS_BETWEEN to D2WeeksBetween(),
            ExpressionParser.D2_MONTHS_BETWEEN to D2MonthsBetween(),
            ExpressionParser.D2_YEARS_BETWEEN to D2YearsBetween(),
            ExpressionParser.D2_ADD_DAYS to D2AddDays(),
            ExpressionParser.D2_COUNT to D2Count(),
            ExpressionParser.D2_COUNT_IF_CONDITION to D2CountIfCondition(),
            ExpressionParser.D2_COUNT_IF_VALUE to D2CountIfValue(),
            ExpressionParser.D2_HAS_VALUE to D2HasValue(),
            ExpressionParser.D2_CONDITION to D2Condition(),

            // Data items
            ExpressionParser.HASH_BRACE to DataElementItem(),
            ExpressionParser.I_BRACE to ProgramIndicatorItem(),
            ExpressionParser.D_BRACE to ProgramDataElementItem(),
            ExpressionParser.A_BRACE to ProgramAttributeItem()
        )

    fun getDays(
        contextEvaluationItem: AnalyticsServiceEvaluationItem,
        contextMetadata: Map<String, MetadataItem>
    ): Int? {
        val periods = contextEvaluationItem.allDimensionItems
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

        val start = periods.mapNotNull { it.startDate() }.minByOrNull { it.time }
        val end = periods.mapNotNull { it.endDate() }.maxByOrNull { it.time }

        return if (start != null && end != null) {
            return PeriodHelper.getDays(Period.builder().startDate(start).endDate(end).build())
        } else {
            null
        }
    }

    fun roundValue(valueStr: String?, decimals: Int?): String? {
        return if (valueStr != null && ParserUtils.isNumeric(valueStr)) {
            ParserUtils.getRounded(valueStr.toDouble(), decimals ?: 2).toString()
        } else {
            valueStr
        }
    }
}
