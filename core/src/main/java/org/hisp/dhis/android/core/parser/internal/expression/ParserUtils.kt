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
package org.hisp.dhis.android.core.parser.internal.expression

import kotlinx.datetime.LocalDate
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.parser.internal.expression.function.*
import org.hisp.dhis.android.core.parser.internal.expression.literal.NullLiteral
import org.hisp.dhis.android.core.parser.internal.expression.operator.*
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemConstant
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemDays
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemOrgUnitGroup
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.roundToInt

internal object ParserUtils {
    const val DOUBLE_VALUE_IF_NULL = 0.0
    const val TEXT_VALUE_IF_NULL = ""
    private const val NUMERIC_REGEXP = "^(-?0|-?[1-9]\\d*)(\\.\\d+)?(E(-)?\\d+)?$"
    private val NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEXP)
    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    private val trailingPeriodDigits = "\\d+$".toRegex()

    val ITEM_GET_DESCRIPTIONS = ExpressionItem::getDescription
    val ITEM_GET_IDS = ExpressionItem::getItemId
    val ITEM_GET_ORG_UNIT_GROUPS = ExpressionItem::getOrgUnitGroup
    val ITEM_EVALUATE = ExpressionItem::evaluate
    val ITEM_GET_SQL = ExpressionItem::getSql
    val ITEM_REGENERATE = ExpressionItem::regenerate
    val ITEM_VALUE_COUNT = ExpressionItem::count

    val COMMON_EXPRESSION_ITEMS = hashMapOf(
        ExpressionParser.PAREN to OperatorGroupingParentheses(),
        ExpressionParser.PLUS to OperatorMathPlus(),
        ExpressionParser.MINUS to OperatorMathMinus(),
        ExpressionParser.POWER to OperatorMathPower(),
        ExpressionParser.MUL to OperatorMathMultiply(),
        ExpressionParser.DIV to OperatorMathDivide(),
        ExpressionParser.DIV to OperatorMathDivide(),
        ExpressionParser.MOD to OperatorMathModulus(),
        ExpressionParser.NOT to OperatorLogicalNot(),
        ExpressionParser.EXCLAMATION_POINT to OperatorLogicalNot(),
        ExpressionParser.AND to OperatorLogicalAnd(),
        ExpressionParser.AMPERSAND_2 to OperatorLogicalAnd(),
        ExpressionParser.OR to OperatorLogicalOr(),
        ExpressionParser.VERTICAL_BAR_2 to OperatorLogicalOr(),
        ExpressionParser.EQ to OperatorCompareEqual(),
        ExpressionParser.NE to OperatorCompareNotEqual(),
        ExpressionParser.GT to OperatorCompareGreaterThan(),
        ExpressionParser.LT to OperatorCompareLessThan(),
        ExpressionParser.GEQ to OperatorCompareGreaterThanOrEqual(),
        ExpressionParser.LEQ to OperatorCompareLessThanOrEqual(),

        // Functions
        ExpressionParser.FIRST_NON_NULL to FunctionFirstNonNull(),
        ExpressionParser.GREATEST to FunctionGreatest(),
        ExpressionParser.IS to FunctionIs(),
        ExpressionParser.IF to FunctionIf(),
        ExpressionParser.IS_NOT_NULL to FunctionIsNotNull(),
        ExpressionParser.IS_NULL to FunctionIsNull(),
        ExpressionParser.LEAST to FunctionLeast(),
        ExpressionParser.LOG to FunctionLog(),
        ExpressionParser.LOG10 to FunctionLog10(),
        ExpressionParser.CONTAINS to FunctionContains(),
        ExpressionParser.CONTAINS_ITEMS to FunctionContainsItems(),

        // Common variables
        ExpressionParser.OUG_BRACE to ItemOrgUnitGroup(),
        ExpressionParser.DAYS to ItemDays(),
        ExpressionParser.C_BRACE to ItemConstant(),

        // Literals
        ExpressionParser.NULL to NullLiteral(),
    )

    @JvmStatic
    fun isZeroOrPositive(value: String): Boolean {
        return isNumeric(value) && value.toDouble() >= 0.0
    }

    fun isNumeric(value: String?): Boolean {
        return value != null && isDouble(value) && NUMERIC_PATTERN.matcher(value).matches()
    }

    private fun isDouble(value: String): Boolean {
        return try {
            value.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun fromDouble(value: Double?): String {
        return if (value != null && !value.isNaN()) {
            val rounded = getRounded(value, 2)
            rounded.toString().replace("\\.0+$".toRegex(), "")
        } else {
            ""
        }
    }

    fun getRounded(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (value * factor).roundToInt() / factor
    }

    /**
     * Formats a Date to the format YYYY-MM-DD.
     *
     * @param date the Date to parse.
     * @return A formatted date string. Null if argument is null.
     */
    fun getMediumDateString(date: Date?): String? {
        return date?.let {
            val format = SimpleDateFormat()
            format.applyPattern(DEFAULT_DATE_FORMAT)
            format.format(date)
        }
    }

    fun getMediumDateString(localDate: LocalDate?): String? {
        return localDate?.let {
            "${it.year}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')}"
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun parseExpressionDate(expression: String): LocalDate {
        return try {
            val tokens = expression.split('-')
            val dateStr = tokens[0] + "-" + tokens[1].padStart(2, '0') + "-" + tokens[2].padStart(2, '0')
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            throw ParserExceptionWithoutContext("Invalid date: $expression - ${e.message}")
        }
    }

    fun getTrailingDigits(periodId: String): Int? {
        return trailingPeriodDigits.find(periodId)?.value?.toInt()
    }

    @Suppress("MagicNumber")
    fun getPeriodInYear(period: Period): Int {
        val periodIsoDate = DateUtils.SIMPLE_DATE_FORMAT.format(period.startDate()!!)

        return when (period.periodType()!!) {
            PeriodType.Daily -> LocalDate.parse(periodIsoDate).dayOfYear
            PeriodType.Monthly,
            PeriodType.BiMonthly,
            -> period.periodId()!!.substring(4, 6).toInt()
            PeriodType.Yearly,
            PeriodType.FinancialApril,
            PeriodType.FinancialJuly,
            PeriodType.FinancialNov,
            PeriodType.FinancialOct,
            -> 1
            else -> getTrailingDigits(period.periodId()!!) ?: 0
        }
    }
}
