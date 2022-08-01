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

import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getDataValueEventWhereClause
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getEnrollmentWhereClause
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemAttribute
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemStageElement
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class D2HasValue : ProgramExpressionItem() {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        return when (getProgramArgType(ctx)) {
            is ProgramItemStageElement -> hasProgramItemStageElement(ctx, visitor)
            is ProgramItemAttribute -> hasProgramAttribute(ctx, visitor)
            else -> throw ParserExceptionWithoutContext(
                "First argument not supported for d2:hasValue... function: ${ctx.text}"
            )
        }
    }

    private fun hasProgramAttribute(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val attribute = ctx.uid0.text

        return visitor.programIndicatorContext.attributeValues.values.any { attributeValue ->
            attribute == attributeValue.trackedEntityAttribute() && attributeValue.value() != null
        }.toString()
    }

    private fun hasProgramItemStageElement(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val programStage = ctx.uid0.text
        val stageEvents = visitor.programIndicatorContext.events[programStage] ?: emptyList()
        val dataElement = ctx.uid1.text

        return stageEvents.any { event ->
            event.trackedEntityDataValues()!!.any { dataValue ->
                dataElement == dataValue.dataElement() && dataValue.value() != null
            }
        }.toString()
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        return when (getProgramArgType(ctx)) {
            is ProgramItemStageElement -> hasProgramItemStageElementSQL(ctx, visitor)
            is ProgramItemAttribute -> hasProgramAttributeSQL(ctx, visitor)
            else -> throw ParserExceptionWithoutContext(
                "First argument not supported for d2:hasValue... function: ${ctx.text}"
            )
        }
    }

    private fun hasProgramAttributeSQL(ctx: ExprContext, visitor: CommonExpressionVisitor): String {
        val attributeUid = ctx.uid0.text

        val enrollmentSelector = getEnrollmentWhereClause(visitor.programIndicatorSQLContext.programIndicator)

        return "EXISTS(SELECT 1 " +
            "FROM ${TrackedEntityAttributeValueTableInfo.TABLE_INFO.name()} " +
            "WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} = '$attributeUid' " +
            "AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} IN (" +
            "SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
            "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
            "WHERE ${EnrollmentTableInfo.Columns.UID} = $enrollmentSelector " +
            ")" +
            ")"
    }

    private fun hasProgramItemStageElementSQL(ctx: ExprContext, visitor: CommonExpressionVisitor): String {
        val programStageUid = ctx.uid0.text
        val dataElementUid = ctx.uid1.text

        return "EXISTS(SELECT 1 " +
            "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
            "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} " +
            "ON ${TrackedEntityDataValueTableInfo.Columns.EVENT} = ${EventTableInfo.Columns.UID} " +
            "WHERE ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '$dataElementUid' " +
            "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageUid' " +
            "AND ${getDataValueEventWhereClause(visitor.programIndicatorSQLContext.programIndicator)} " +
            "AND ${TrackedEntityDataValueTableInfo.Columns.VALUE} IS NOT NULL " +
            "ORDER BY ${EventTableInfo.Columns.EVENT_DATE} DESC LIMIT 1" +
            ")"
    }
}
