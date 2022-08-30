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
package org.hisp.dhis.android.core.program.programindicatorengine.internal.function

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser
import org.hisp.dhis.antlr.AntlrParserUtils.trimQuotes
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class D2CountIfCondition : ProgramCountFunction() {

    override fun countIf(ctx: ExprContext, visitor: CommonExpressionVisitor, value: String?): Boolean {
        val expression = value + trimQuotes(ctx.stringLiteral().text)
        val result = visitor.programIndicatorExecutor.getProgramIndicatorExpressionValue(expression)

        return "true" == result
    }

    override fun getConditionalSql(ctx: ExprContext, visitor: CommonExpressionVisitor): String {
        val conditionExpression = getAuxConditionExpression(ctx)

        val conditionSql = CommonParser.visit(conditionExpression, visitor)

        return extractConditionExpression(conditionSql.toString())
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Gets a complete expression that is used to test a condition (e.g. "<5")
     * by putting a "0" in front to get a complete expression (e.g., "0<5").
     *
     * @param ctx the expression context
     * @return the complete expression
     */
    private fun getAuxConditionExpression(ctx: ExprContext): String {
        return AuxExpression + trimQuotes(ctx.stringLiteral().text)
    }

    private fun extractConditionExpression(expression: String): String {
        return expression.substring(AuxExpression.length)
    }

    private companion object {
        const val AuxExpression = "0.0"
    }
}
