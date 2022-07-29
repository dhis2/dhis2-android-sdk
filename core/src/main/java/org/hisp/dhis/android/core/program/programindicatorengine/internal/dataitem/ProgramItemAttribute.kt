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
package org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorParserUtils.assumeProgramAttributeSyntax
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getColumnValueCast
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getDefaultValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class ProgramItemAttribute : ProgramExpressionItem() {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        assumeProgramAttributeSyntax(ctx)

        val attributeUid = ctx.uid0.text

        val attributeValue = visitor.programIndicatorContext.attributeValues[attributeUid]
        val attribute = visitor.trackedEntityAttributeStore.selectByUid(attributeUid)

        val value = attributeValue?.value()
        val handledValue = visitor.handleNulls(value)
        val strValue = handledValue?.toString()

        return formatValue(strValue, attribute!!.valueType())
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        assumeProgramAttributeSyntax(ctx)

        val attributeUid = ctx.uid0.text

        // TODO Manage null and boolean values

        val attribute = visitor.trackedEntityAttributeStore.selectByUid(attributeUid)
            ?: throw IllegalArgumentException("Attribute $attributeUid does not exist.")

        val valueCastExpression = getColumnValueCast(
            TrackedEntityAttributeValueTableInfo.Columns.VALUE,
            attribute.valueType()
        )

        val selectExpression = ProgramIndicatorSQLUtils.getAttributeWhereClause(
            column = valueCastExpression,
            attributeUid = attributeUid,
            programIndicator = visitor.programIndicatorSQLContext.programIndicator
        )

        return if (visitor.state.replaceNulls) {
            "(COALESCE($selectExpression, ${getDefaultValue(attribute.valueType())}))"
        } else {
            selectExpression
        }
    }

    override fun getItemId(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        return visitor.itemIds.add(
            DimensionalItemId.builder()
                .dimensionalItemType(DimensionalItemType.TRACKED_ENTITY_ATTRIBUTE)
                .id0(ctx.uid0.text)
                .build()
        )
    }
}
