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
package org.hisp.dhis.android.core.parser.internal.service.dataitem

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.DOUBLE_VALUE_IF_NULL
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

/**
 * Parsed expression item as handled by the expression service.
 *
 *
 * When getting item id and org unit group, just return default values
 * (because not every item implements these, only those that need to.)
 *
 * @author Jim Grace
 */
internal class DimItemDataElementAndOperand : DimensionalItem() {
    override fun getDescription(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val dataElement = visitor.dataElementStore!!.selectByUid(ctx.uid0.text)

        dataElement?.displayName()?.let { deName ->
            visitor.itemDescriptions[ctx.text] =
                buildString {
                    append(deName)
                    if (isDataElementOperandSyntax(ctx)) {
                        val categoryOptionCombo = visitor.categoryOptionComboStore!!.selectByUid(ctx.uid1.text)
                        val cocDescription = categoryOptionCombo?.displayName() ?: ctx.uid1.text
                        append(" ($cocDescription)")
                    }
                }
        }

        return DOUBLE_VALUE_IF_NULL
    }

    override fun getDimensionalItemId(ctx: ExprContext): DimensionalItemId {
        return if (isDataElementOperandSyntax(ctx)) {
            DimensionalItemId(
                dimensionalItemType = DimensionalItemType.DATA_ELEMENT_OPERAND,
                id0 = ctx.uid0.text,
                id1 = ctx.uid1?.text,
                id2 = ctx.uid2?.text,
            )
        } else {
            DimensionalItemId(
                dimensionalItemType = DimensionalItemType.DATA_ELEMENT,
                id0 = ctx.uid0.text,
            )
        }
    }

    override fun getId(ctx: ExprContext): String {
        return if (isDataElementOperandSyntax(ctx)) {
            ctx.uid0.text + "." +
                (ctx.uid1?.text ?: "*") +
                (ctx.uid2?.text?.let { ".$it" } ?: "")
        } else {
            // Data element:
            ctx.uid0.text
        }
    }

    /**
     * Does an item of the form #{...} have the syntax of a
     * data element operand (as opposed to a data element)?
     *
     * @param ctx the item context
     * @return true if data element operand syntax
     */
    private fun isDataElementOperandSyntax(ctx: ExprContext): Boolean {
        if (ctx.uid0 == null) {
            throw ParserExceptionWithoutContext("Data Element or DataElementOperand must have a uid " + ctx.text)
        }
        return listOf(ctx.uid1, ctx.uid2).any { it != null }
    }
}
