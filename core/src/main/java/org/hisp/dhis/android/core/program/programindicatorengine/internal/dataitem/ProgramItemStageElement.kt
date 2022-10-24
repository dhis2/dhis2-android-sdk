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

import java.util.*
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorParserUtils.assumeStageElementSyntax
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getColumnValueCast
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getDefaultValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class ProgramItemStageElement : ProgramExpressionItem() {
    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        assumeStageElementSyntax(ctx)

        val stageId = ctx.uid0.text
        val dataElementId = ctx.uid1.text

        val eventList = visitor.programIndicatorContext.events[stageId]
        var value: String? = null

        if (eventList != null) {
            val candidates = getCandidates(eventList, dataElementId)
            if (candidates.isNotEmpty()) {
                value = candidates.last().value()
            }
        }

        val dataElement = visitor.dataElementStore.selectByUid(dataElementId)
        val handledValue = visitor.handleNulls(value)
        val strValue = handledValue?.toString()

        return formatValue(strValue, dataElement!!.valueType())
    }

    private fun getCandidates(events: List<Event>, dataElement: String): List<TrackedEntityDataValue> {
        val candidates: MutableList<TrackedEntityDataValue> = ArrayList()
        for (event in events) {
            if (event.trackedEntityDataValues() == null) {
                continue
            }
            for (dataValue in event.trackedEntityDataValues()!!) {
                if (dataElement == dataValue.dataElement()) {
                    candidates.add(dataValue)
                }
            }
        }
        return candidates
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        assumeStageElementSyntax(ctx)

        val programStageId = ctx.uid0.text
        val dataElementId = ctx.uid1.text

        // TODO Manage null and boolean values

        val dataElement = visitor.dataElementStore.selectByUid(dataElementId)
            ?: throw IllegalArgumentException("DataElement $dataElementId does not exist.")

        val valueCastExpression = getColumnValueCast(
            TrackedEntityDataValueTableInfo.Columns.VALUE,
            dataElement.valueType()
        )

        val selectExpression = ProgramIndicatorSQLUtils.getTrackerDataValueWhereClause(
            column = valueCastExpression,
            programStageUid = programStageId,
            dataElementUid = dataElementId,
            programIndicator = visitor.programIndicatorSQLContext.programIndicator
        )

        return if (visitor.state.replaceNulls) {
            "(COALESCE($selectExpression, ${getDefaultValue(dataElement.valueType())}))"
        } else {
            selectExpression
        }
    }

    override fun getItemId(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val stageId = ctx.uid0.text
        val dataElementId = ctx.uid1.text

        return visitor.itemIds.add(
            DimensionalItemId.builder()
                .dimensionalItemType(DimensionalItemType.TRACKED_ENTITY_DATA_VALUE)
                .id0(stageId)
                .id1(dataElementId)
                .build()
        )
    }
}
