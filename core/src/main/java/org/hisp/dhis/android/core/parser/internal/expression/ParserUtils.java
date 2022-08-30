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

package org.hisp.dhis.android.core.parser.internal.expression;

import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionFirstNonNull;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionGreatest;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionIf;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionIsNotNull;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionIsNull;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionLeast;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionLog;
import org.hisp.dhis.android.core.parser.internal.expression.function.FunctionLog10;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareEqual;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareGreaterThan;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareGreaterThanOrEqual;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareLessThan;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareLessThanOrEqual;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorCompareNotEqual;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorGroupingParentheses;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorLogicalAnd;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorLogicalNot;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorLogicalOr;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathDivide;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathMinus;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathModulus;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathMultiply;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathPlus;
import org.hisp.dhis.android.core.parser.internal.expression.operator.OperatorMathPower;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemConstant;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemDays;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.ItemOrgUnitGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.AMPERSAND_2;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.AND;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.C_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.DAYS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.DIV;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.EQ;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.EXCLAMATION_POINT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.FIRST_NON_NULL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.GEQ;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.GREATEST;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.GT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.IF;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.IS_NOT_NULL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.IS_NULL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.LEAST;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.LEQ;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.LOG;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.LOG10;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.LT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.MINUS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.MOD;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.MUL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.NE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.NOT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.OR;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.OUG_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.PAREN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.PLUS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.POWER;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.VERTICAL_BAR_2;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyStaticImports"})
public final class ParserUtils {

    public final static double DOUBLE_VALUE_IF_NULL = 0.0;

    private static final String NUMERIC_REGEXP = "^(-?0|-?[1-9]\\d*)(\\.\\d+)?(E(-)?\\d+)?$";

    private static final Pattern NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEXP);

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public final static ExpressionItemMethod ITEM_GET_DESCRIPTIONS = ExpressionItem::getDescription;

    public final static ExpressionItemMethod ITEM_GET_IDS = ExpressionItem::getItemId;

    public final static ExpressionItemMethod ITEM_GET_ORG_UNIT_GROUPS = ExpressionItem::getOrgUnitGroup;

    public final static ExpressionItemMethod ITEM_EVALUATE = ExpressionItem::evaluate;

    public final static ExpressionItemMethod ITEM_GET_SQL = ExpressionItem::getSql;

    public final static ExpressionItemMethod ITEM_REGENERATE = ExpressionItem::regenerate;

    public final static ExpressionItemMethod ITEM_VALUE_COUNT = ExpressionItem::count;

    public final static Map<Integer, ExpressionItem> COMMON_EXPRESSION_ITEMS;

    static {
        Map<Integer, ExpressionItem> m = new HashMap<>();

        m.put(PAREN, new OperatorGroupingParentheses());
        m.put(PLUS, new OperatorMathPlus());
        m.put(MINUS, new OperatorMathMinus());
        m.put(POWER, new OperatorMathPower());
        m.put(MUL, new OperatorMathMultiply());
        m.put(DIV, new OperatorMathDivide());
        m.put(MOD, new OperatorMathModulus());
        m.put(NOT, new OperatorLogicalNot());
        m.put(EXCLAMATION_POINT, new OperatorLogicalNot());
        m.put(AND, new OperatorLogicalAnd());
        m.put(AMPERSAND_2, new OperatorLogicalAnd());
        m.put(OR, new OperatorLogicalOr());
        m.put(VERTICAL_BAR_2, new OperatorLogicalOr());

        // Comparison operators

        m.put(EQ, new OperatorCompareEqual());
        m.put(NE, new OperatorCompareNotEqual());
        m.put(GT, new OperatorCompareGreaterThan());
        m.put(LT, new OperatorCompareLessThan());
        m.put(GEQ, new OperatorCompareGreaterThanOrEqual());
        m.put(LEQ, new OperatorCompareLessThanOrEqual());

        // Functions

        m.put(FIRST_NON_NULL, new FunctionFirstNonNull());
        m.put(GREATEST, new FunctionGreatest());
        m.put(IF, new FunctionIf());
        m.put(IS_NOT_NULL, new FunctionIsNotNull());
        m.put(IS_NULL, new FunctionIsNull());
        m.put(LEAST, new FunctionLeast());

        m.put(LOG, new FunctionLog());
        m.put(LOG10, new FunctionLog10());

        // Common variables

        m.put(OUG_BRACE, new ItemOrgUnitGroup());
        m.put(DAYS, new ItemDays());
        m.put(C_BRACE, new ItemConstant());

        COMMON_EXPRESSION_ITEMS = m;
    }

    private ParserUtils() {
    }

    static boolean isZeroOrPositive(String value) {
        return isNumeric(value) && Double.parseDouble(value) >= 0d;
    }

    public static boolean isNumeric(String value) {
        return value != null && isDouble(value) && NUMERIC_PATTERN.matcher(value).matches();
    }

    private static boolean isDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String fromDouble(Double value) {
        if (value != null && !Double.isNaN(value)) {
            Double rounded = getRounded(value, 2);
            return String.valueOf(rounded).replaceAll("\\.0+$", "");
        }

        return "";
    }

    public static double getRounded(double value, int decimals) {
        final double factor = Math.pow(10, decimals);

        return Math.round(value * factor) / factor;
    }

    /**
     * Formats a Date to the format YYYY-MM-DD.
     *
     * @param date the Date to parse.
     * @return A formatted date string. Null if argument is null.
     */
    public static String getMediumDateString(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat();

        format.applyPattern(DEFAULT_DATE_FORMAT);

        return date == null ? null : format.format(date);
    }
}
