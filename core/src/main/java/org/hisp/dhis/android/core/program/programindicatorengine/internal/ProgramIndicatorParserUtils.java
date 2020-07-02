package org.hisp.dhis.android.core.program.programindicatorengine.internal;

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

import com.google.common.collect.ImmutableMap;

import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem;
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemAttribute;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemPsEventdate;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemStageElement;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2AddDays;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Ceil;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Concatenate;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Condition;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Count;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2CountIfCondition;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2CountIfValue;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2DaysBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Floor;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2HasValue;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Left;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Length;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2MinutesBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Modulus;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2MonthsBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Oizp;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Right;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Round;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Split;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Substring;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2ValidatePattern;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2WeeksBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2YearsBetween;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Zing;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.D2Zpvc;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.variable.ProgramVariableItem;

import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.A_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_ADD_DAYS;
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
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.D2_MINUTES_BETWEEN;
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

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyStaticImports"})
public final class ProgramIndicatorParserUtils {

    final static ImmutableMap<Integer, ExpressionItem>
            PROGRAM_INDICATOR_EXPRESSION_ITEMS = ImmutableMap.<Integer, ExpressionItem>builder()

            .putAll(ParserUtils.COMMON_EXPRESSION_ITEMS)

            .put(D2_CEIL, new D2Ceil())
            .put(D2_CONCATENATE, new D2Concatenate())
            .put(D2_FLOOR, new D2Floor())
            .put(D2_LEFT, new D2Left())
            .put(D2_LENGTH, new D2Length())
            .put(D2_MODULUS, new D2Modulus())
            .put(D2_RIGHT, new D2Right())
            .put(D2_ROUND, new D2Round())
            .put(D2_SPLIT, new D2Split())
            .put(D2_SUBSTRING, new D2Substring())
            .put(D2_OIZP, new D2Oizp())
            .put(D2_VALIDATE_PATTERN, new D2ValidatePattern())
            .put(D2_ZING, new D2Zing())
            .put(D2_ZPVC, new D2Zpvc())

            .put(D2_MINUTES_BETWEEN, new D2MinutesBetween())
            .put(D2_DAYS_BETWEEN, new D2DaysBetween())
            .put(D2_WEEKS_BETWEEN, new D2WeeksBetween())
            .put(D2_MONTHS_BETWEEN, new D2MonthsBetween())
            .put(D2_YEARS_BETWEEN, new D2YearsBetween())
            .put(D2_ADD_DAYS, new D2AddDays())

            .put(D2_COUNT, new D2Count())
            .put(D2_COUNT_IF_CONDITION, new D2CountIfCondition())
            .put(D2_COUNT_IF_VALUE, new D2CountIfValue())
            .put(D2_HAS_VALUE, new D2HasValue())
            .put(D2_CONDITION, new D2Condition())

            // Data items

            .put(HASH_BRACE, new ProgramItemStageElement())
            .put(A_BRACE, new ProgramItemAttribute())
            .put(PS_EVENTDATE, new ProgramItemPsEventdate())

            // Program variables

            .put(V_BRACE, new ProgramVariableItem())

            .build();

    private ProgramIndicatorParserUtils() {
    }

    static public String wrap(String input) {
        if (input == null) {
            return "";
        }
        return input;
    }
}
