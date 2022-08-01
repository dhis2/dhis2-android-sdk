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
package org.hisp.dhis.android.core.parser.internal.expression.function

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.antlr.AntlrParserUtils
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

/**
 * Function if
 * <pre>
 *
 * In-memory Logic:
 *
 * arg0   returns
 * ----   -------
 * true   arg1
 * false  arg2
 * null   null
 *
 * SQL logic (CASE WHEN arg0 THEN arg1 ELSE arg2 END):
 *
 * arg0   returns
 * ----   -------
 * true   arg1
 * false  arg2
 * null   arg2
</pre> *
 *
 * @author Jim Grace
 */
internal class FunctionIf : ExpressionItem {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val arg0 = visitor.castBooleanVisit(ctx.expr(0))

        return when {
            arg0 == null -> null
            arg0 -> visitor.visit(ctx.expr(1))
            else -> visitor.visit(ctx.expr(2))
        }
    }

    override fun evaluateAllPaths(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val arg0 = visitor.castBooleanVisit(ctx.expr(0))
        val arg1 = visitor.visit(ctx.expr(1))
        val arg2 = visitor.visit(ctx.expr(2))

        if (arg1 != null) {
            AntlrParserUtils.castClass(arg1.javaClass, arg2)
        }

        return when {
            arg0 != null && arg0 -> arg1
            else -> arg2
        }
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        return "CASE WHEN ${visitor.castStringVisit(ctx.expr(0))} " +
            "THEN ${visitor.castStringVisit(ctx.expr(1))} " +
            "ELSE ${visitor.castStringVisit(ctx.expr(2))} END"
    }
}
