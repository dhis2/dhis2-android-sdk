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

import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getColumnValueCast
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getDataValueEventWhereClause
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemStageElement
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal abstract class ProgramCountFunction : ProgramExpressionItem() {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        validateCountFunctionArgs(ctx)

        val programStage = ctx.uid0.text
        val dataElement = ctx.uid1.text

        var count = 0
        val stageEvents = visitor.programIndicatorContext.events[programStage]

        stageEvents?.forEach { event ->
            event.trackedEntityDataValues()?.forEach { dataValue ->
                if (dataElement == dataValue.dataElement() && countIf(ctx, visitor, dataValue.value())) {
                    count++
                }
            }
        }
        return count.toString()
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        validateCountFunctionArgs(ctx)

        val programStageId = ctx.uid0.text
        val dataElementId = ctx.uid1.text

        val dataElement = visitor.dataElementStore.selectByUid(dataElementId)
            ?: throw IllegalArgumentException("DataElement $dataElementId does not exist.")

        val valueCastExpression = getColumnValueCast(
            TrackedEntityDataValueTableInfo.Columns.VALUE,
            dataElement.valueType()
        )

        val conditionalSql = getConditionalSql(ctx, visitor)

        return "(SELECT COUNT() " +
            "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
            "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} " +
            "ON ${TrackedEntityDataValueTableInfo.Columns.EVENT} = ${EventTableInfo.Columns.UID} " +
            "WHERE ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '$dataElementId' " +
            "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageId' " +
            "AND ${getDataValueEventWhereClause(visitor.programIndicatorSQLContext.programIndicator)} " +
            "AND ${TrackedEntityDataValueTableInfo.Columns.VALUE} IS NOT NULL " +
            "AND $valueCastExpression $conditionalSql " +
            ")"
    }

    protected abstract fun countIf(ctx: ExprContext, visitor: CommonExpressionVisitor, value: String?): Boolean

    protected abstract fun getConditionalSql(ctx: ExprContext, visitor: CommonExpressionVisitor): String

    private fun validateCountFunctionArgs(ctx: ExprContext) {
        if (getProgramArgType(ctx) !is ProgramItemStageElement) {
            throw ParserExceptionWithoutContext(
                "First argument not supported for d2:count... functions: ${ctx.text}"
            )
        }
    }
}
