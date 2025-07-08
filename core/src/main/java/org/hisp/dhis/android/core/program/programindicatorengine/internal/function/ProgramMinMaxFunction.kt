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

import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.getDataValueEventWhereClause
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal abstract class ProgramMinMaxFunction : ExpressionItem {

    abstract fun evaluateFn(values: List<String>): Any?

    abstract fun getSqlAggregator(): String

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val programStageUid = ctx.uid0.text
        val mappedValues: List<String> = (visitor.programIndicatorContext!!.events[programStageUid] ?: emptyList())
            .mapNotNull { event ->
                when (ctx.uid1) {
                    null -> {
                        // It has the form PS_EVENTDATE:programStageUid
                        event.eventDate()?.let { DateUtils.DATE_FORMAT.format(it) }
                    }
                    else -> {
                        event.trackedEntityDataValues()?.find { it.dataElement() == ctx.uid1.text }?.value()
                    }
                }
            }

        return evaluateFn(mappedValues)
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val isDataElement = ctx.uid1 != null
        val programStageId = ctx.uid0.text

        return if (isDataElement) {
            val dataElementId = ctx.uid1.text
            "(SELECT ${getSqlAggregator()}(${TrackedEntityDataValueTableInfo.Columns.VALUE}) " +
                "FROM ${TrackedEntityDataValueTableInfo.TABLE_INFO.name()} " +
                "INNER JOIN ${EventTableInfo.TABLE_INFO.name()} " +
                "ON ${TrackedEntityDataValueTableInfo.Columns.EVENT} = ${EventTableInfo.Columns.UID} " +
                "WHERE ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = '$dataElementId' " +
                "AND ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageId' " +
                "AND ${getDataValueEventWhereClause(visitor.programIndicatorSQLContext!!.programIndicator)} " +
                ")"
        } else {
            "(SELECT ${getSqlAggregator()}(${EventTableInfo.Columns.EVENT_DATE}) " +
                "FROM ${EventTableInfo.TABLE_INFO.name()} " +
                "WHERE ${EventTableInfo.Columns.PROGRAM_STAGE} = '$programStageId' " +
                "AND ${getDataValueEventWhereClause(visitor.programIndicatorSQLContext!!.programIndicator)} " +
                ")"
        }
    }
}
