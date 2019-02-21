/*
 * Copyright (c) 2004-2019, University of Oslo
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

package org.hisp.dhis.android.core.utils.support;

import android.util.Log;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.android.core.utils.support.math.ExpressionFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Lars Helge Overland
 */
public final class ExpressionUtils {
    private static final String CLASS_TAG = ExpressionUtils.class.getSimpleName();
    private static final Map<String, Object> functions = new HashMap<String, Object>() {{
        put(ExpressionFunctions.NAMESPACE, ExpressionFunctions.class);
    }};
    private static final JexlEngine JEXL =
            new JexlBuilder().cache(512).silent(false).namespaces(functions).create();
    private static final JexlEngine JEXL_STRICT =
            new JexlBuilder().cache(512).silent(false).strict(true).namespaces(functions).create();

    private static final Map<String, String> EL_SQL_MAP = new HashMap<>();
    private static final String IGNORED_KEYWORDS_REGEX =
            "(^|[^:])(SUM|sum|AVERAGE|average|COUNT|count|STDDEV|stddev|VARIANCE|variance|MIN|min|MAX|max|NONE|none)";

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^(-?0|-?[1-9]\\d*)(\\.\\d+)?$");

    private ExpressionUtils() {
        // no instances
    }

    static {
        EL_SQL_MAP.put("&&", "and");
        EL_SQL_MAP.put("\\|\\|", "or");
        EL_SQL_MAP.put("==", "=");

        //TODO Add support for textual operators like eq, ne and lt
    }

    /**
     * Evaluates the given expression. The given variables will be substituted
     * in the expression.
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return the result of the evaluation.
     */
    private static Object evaluate(String expression, Map<String, Object> vars) {
        try {
            return evaluate(expression, vars, false);
        } catch (Exception e) {
            Log.e(CLASS_TAG, e.toString());
            return null;
        }
    }

    /**
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @param strict     indicates whether to use strict or lenient engine mode.
     * @return the result of the evaluation.
     */
    private static Object evaluate(String expression, Map<String, Object> vars, boolean strict) {
        String formattedExpression = expression.replaceAll(IGNORED_KEYWORDS_REGEX, StringUtils.EMPTY);

        JexlEngine engine = strict ? JEXL_STRICT : JEXL;

        JexlExpression exp = engine.createExpression(formattedExpression);

        JexlContext context = vars == null ? new MapContext() : new MapContext(vars);

        return exp.evaluate(context);
    }

    /**
     * Evaluates the given expression. The given variables will be substituted
     * in the expression. Converts the result of the evaluation to a Double.
     * Throws an IllegalStateException if the result could not be converted to
     * a Double
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return the result of the evaluation.
     */
    public static Double evaluateToDouble(String expression, Map<String, Object> vars) {
        String result = evaluateToString(expression, vars);

        if (!isNumeric(result)) {
            throw new IllegalStateException("Result must be numeric: " + result + ", " + result.getClass());
        }

        return Double.valueOf(result);
    }

    /**
     * Evaluates the given expression. The given variables will be substituted
     * in the expression. Converts the result of the evaluation to a Double.
     * Throws an IllegalStateException if the result could not be converted to
     * a Double
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return the result of the evaluation.
     */
    public static String evaluateToString(String expression, Map<String, Object> vars) {
        Object result = evaluate(expression, vars);

        if (result == null) {
            throw new IllegalStateException("Result must be not null");
        }

        return String.valueOf(result);
    }

    /**
     * Evaluates the given expression to true or false. The given variables will
     * be substituted in the expression.
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return true or false.
     */
    public static boolean isTrue(String expression, Map<String, Object> vars) {
        Object result = evaluate(expression, vars);

        return result instanceof Boolean && (Boolean) result;
    }

    /**
     * Indicates whether the given expression is valid and evaluates to true or
     * false.
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return true or false.
     */
    public static boolean isBoolean(String expression, Map<String, Object> vars) {
        try {
            Object result = evaluate(expression, vars);

            return result instanceof Boolean;
        } catch (JexlException ex) {
            return false;
        }
    }

    public static boolean isBoolean(String value) {
        return value != null && ("true".equals(value) || "false".equals(value));
    }

    /**
     * Indicates whether the given expression is valid, i.e. can be successfully
     * evaluated.
     *
     * @param expression the expression.
     * @param vars       the variables, can be null.
     * @return true or false.
     */
    public static boolean isValid(String expression, Map<String, Object> vars) {
        try {
            Object result = evaluate(expression, vars, true);

            return result != null;
        } catch (JexlException ex) {
            //TODO Masking bug in Jexl, fix
            return ex.getMessage().contains("divide error");
        }
    }

    /**
     * Indicates whether the given value is numeric.
     *
     * @param value the value.
     * @return true or false.
     */
    public static boolean isNumeric(String value) {
        return NUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * Converts the given expression into a valid SQL clause.
     *
     * @param expression the expression.
     * @return an SQL clause.
     */
    public static String asSql(String expression) {
        if (expression == null) {
            return null;
        }

        String result = "";
        for (Map.Entry<String, String> entry : EL_SQL_MAP.entrySet()) {
            result = expression.replaceAll(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
