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

package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemAttribute
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemPsEventdate
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemStageElement
import org.hisp.dhis.android.core.program.programindicatorengine.internal.function.*
import org.hisp.dhis.android.core.program.programindicatorengine.internal.variable.ProgramVariableItem
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
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

internal object ProgramIndicatorParserUtils {

    private val COMMON_PROGRAM_INDICATORS_EXPRESSION_ITEMS = mapOf(
        ExpressionParser.D2_CONDITION to D2Condition(), // Data items
        ExpressionParser.D2_COUNT to D2Count(),
        ExpressionParser.D2_COUNT_IF_CONDITION to D2CountIfCondition(),
        ExpressionParser.D2_COUNT_IF_VALUE to D2CountIfValue(),
        ExpressionParser.D2_DAYS_BETWEEN to D2DaysBetween(),
        ExpressionParser.D2_HAS_VALUE to D2HasValue(),
        ExpressionParser.D2_MINUTES_BETWEEN to D2MinutesBetween(),
        ExpressionParser.D2_MONTHS_BETWEEN to D2MonthsBetween(),
        ExpressionParser.D2_OIZP to D2Oizp(),
        ExpressionParser.D2_RELATIONSHIP_COUNT to D2RelationshipCount(),
        ExpressionParser.D2_WEEKS_BETWEEN to D2WeeksBetween(),
        ExpressionParser.D2_YEARS_BETWEEN to D2YearsBetween(),
        ExpressionParser.D2_ZING to D2Zing(),
        ExpressionParser.D2_ZPVC to D2Zpvc(),

        ExpressionParser.HASH_BRACE to ProgramItemStageElement(),
        ExpressionParser.A_BRACE to ProgramItemAttribute(),
        ExpressionParser.PS_EVENTDATE to ProgramItemPsEventdate(), // Program variables
        ExpressionParser.V_BRACE to ProgramVariableItem()
    )

    @JvmField
    val PROGRAM_INDICATOR_EXPRESSION_ITEMS =
        ParserUtils.COMMON_EXPRESSION_ITEMS +
            COMMON_PROGRAM_INDICATORS_EXPRESSION_ITEMS +
            mapOf(
                ExpressionParser.D2_ADD_DAYS to D2AddDays(),
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
                ExpressionParser.D2_VALIDATE_PATTERN to D2ValidatePattern()
            )

    @JvmField
    val PROGRAM_INDICATOR_SQL_EXPRESSION_ITEMS =
        ParserUtils.COMMON_EXPRESSION_ITEMS +
            COMMON_PROGRAM_INDICATORS_EXPRESSION_ITEMS

    @JvmStatic
    fun wrap(input: String?): String {
        return input ?: ""
    }

    @SuppressWarnings("ComplexCondition")
    fun assumeStageElementSyntax(ctx: ExpressionParser.ExprContext) {
        if (ctx.uid0 == null || ctx.uid1 == null || ctx.uid2 != null || ctx.wild2 != null) {
            throw ParserExceptionWithoutContext("Invalid program stage / Data element syntax: ${ctx.text}")
        }
    }

    fun assumeProgramAttributeSyntax(ctx: ExpressionParser.ExprContext) {
        if (ctx.uid0 == null || ctx.uid1 != null) {
            throw ParserExceptionWithoutContext("Program attribute must have one UID: ${ctx.text}")
        }
    }
}
