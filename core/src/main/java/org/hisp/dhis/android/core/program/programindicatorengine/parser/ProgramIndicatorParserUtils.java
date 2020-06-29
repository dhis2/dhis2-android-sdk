package org.hisp.dhis.android.core.program.programindicatorengine.parser;

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

import org.hisp.dhis.android.core.parser.expression.ExpressionItem;
import org.hisp.dhis.android.core.parser.expression.ParserUtils;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.dataitem.ProgramItemAttribute;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.dataitem.ProgramItemPsEventdate;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.dataitem.ProgramItemStageElement;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Ceil;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Concatenate;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Condition;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Count;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2CountIfCondition;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2CountIfValue;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2DaysBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Floor;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2HasValue;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Left;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Length;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2MaxValue;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2MinValue;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2MinutesBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Modulus;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2MonthsBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Oizp;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Right;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Round;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Split;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Substring;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2ValidatePattern;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2WeeksBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2YearsBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Zing;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.function.D2Zpvc;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.variable.ProgramVariableItem;

import java.util.HashMap;
import java.util.Map;

import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.A_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_CEIL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_CONCATENATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_CONDITION;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_COUNT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_COUNT_IF_CONDITION;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_COUNT_IF_VALUE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_DAYS_BETWEEN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_FLOOR;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_HAS_VALUE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_LEFT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_LENGTH;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MAX_VALUE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MINUTES_BETWEEN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MIN_VALUE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MODULUS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MONTHS_BETWEEN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_OIZP;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_RIGHT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_ROUND;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_SPLIT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_SUBSTRING;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_VALIDATE_PATTERN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_WEEKS_BETWEEN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_YEARS_BETWEEN;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_ZING;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_ZPVC;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.HASH_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.PS_EVENTDATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_BRACE;

public final class ProgramIndicatorParserUtils {

    public final static Map<Integer, ExpressionItem> PROGRAM_INDICATOR_EXPRESSION_ITEMS;

    static {

        Map<Integer, ExpressionItem> m = new HashMap<>(ParserUtils.COMMON_EXPRESSION_ITEMS);

        m.put(D2_CEIL, new D2Ceil());
        m.put(D2_CONCATENATE, new D2Concatenate());
        m.put(D2_FLOOR, new D2Floor());
        m.put(D2_LEFT, new D2Left());
        m.put(D2_LENGTH, new D2Length());
        m.put(D2_MODULUS, new D2Modulus());
        m.put(D2_RIGHT, new D2Right());
        m.put(D2_ROUND, new D2Round());
        m.put(D2_SPLIT, new D2Split());
        m.put(D2_SUBSTRING, new D2Substring());
        m.put(D2_OIZP, new D2Oizp());
        m.put(D2_VALIDATE_PATTERN, new D2ValidatePattern());
        m.put(D2_ZING, new D2Zing());
        m.put(D2_ZPVC, new D2Zpvc());

        m.put(D2_MINUTES_BETWEEN, new D2MinutesBetween());
        m.put(D2_DAYS_BETWEEN, new D2DaysBetween());
        m.put(D2_WEEKS_BETWEEN, new D2WeeksBetween());
        m.put(D2_MONTHS_BETWEEN, new D2MonthsBetween());
        m.put(D2_YEARS_BETWEEN, new D2YearsBetween());

        m.put(D2_COUNT, new D2Count());
        m.put(D2_COUNT_IF_CONDITION, new D2CountIfCondition());
        m.put(D2_COUNT_IF_VALUE, new D2CountIfValue());
        m.put(D2_HAS_VALUE, new D2HasValue());
        m.put(D2_CONDITION, new D2Condition());

        // Data items

        m.put(HASH_BRACE, new ProgramItemStageElement());
        m.put(A_BRACE, new ProgramItemAttribute());
        m.put(PS_EVENTDATE, new ProgramItemPsEventdate());

        // Program variables

        m.put(V_BRACE, new ProgramVariableItem());

        PROGRAM_INDICATOR_EXPRESSION_ITEMS = m;
    }

    private ProgramIndicatorParserUtils() {
    }

    static public String wrap(String input) {
        if (input == null) {
            return "";
        }
        return input;
    }
}
