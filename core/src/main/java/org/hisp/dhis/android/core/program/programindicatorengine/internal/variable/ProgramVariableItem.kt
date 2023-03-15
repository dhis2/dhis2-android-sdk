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
package org.hisp.dhis.android.core.program.programindicatorengine.internal.variable

import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class ProgramVariableItem : ProgramExpressionItem() {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val programVariable = getProgramVariable(ctx)
        return programVariable.evaluate(ctx, visitor)
    }

    override fun count(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val programVariable = getProgramVariable(ctx)
        return programVariable.count(ctx, visitor)
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val programVariable = getProgramVariable(ctx)
        return programVariable.getSql(ctx, visitor)
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    private fun getProgramVariable(ctx: ExprContext): ExpressionItem {
        return PROGRAM_VARIABLES[ctx.programVariable().`var`.type]
            ?: throw ParserExceptionWithoutContext("Can't find program variable ${ctx.programVariable().`var`.text}")
    }

    companion object {
        private val PROGRAM_VARIABLES = mapOf(
            ExpressionParser.V_ANALYTICS_PERIOD_END to VAnalyticsEndDate(),
            ExpressionParser.V_ANALYTICS_PERIOD_START to VAnalyticsStartDate(),
            ExpressionParser.V_CREATION_DATE to VCreationDate(),
            ExpressionParser.V_CURRENT_DATE to VCurrentDate(),
            ExpressionParser.V_COMPLETED_DATE to VCompletedDate(),
            ExpressionParser.V_DUE_DATE to VDueDate(),
            ExpressionParser.V_ENROLLMENT_COUNT to VEnrollmentCount(),
            ExpressionParser.V_ENROLLMENT_DATE to VEnrollmentDate(),
            ExpressionParser.V_ENROLLMENT_STATUS to VEnrollmentStatus(),
            ExpressionParser.V_EVENT_STATUS to VEventStatus(),
            ExpressionParser.V_EVENT_COUNT to VEventCount(),
            ExpressionParser.V_EVENT_DATE to VEventDate(),
            ExpressionParser.V_EXECUTION_DATE to VEventDate(),
            ExpressionParser.V_INCIDENT_DATE to VIncidentDate(),
            ExpressionParser.V_ORG_UNIT_COUNT to VOrgUnitCount(),
            ExpressionParser.V_SYNC_DATE to VSyncDate(),
            ExpressionParser.V_TEI_COUNT to VTeiCount(),
            ExpressionParser.V_VALUE_COUNT to VValueCount(),
            ExpressionParser.V_ZERO_POS_VALUE_COUNT to VZeroPosValueCount(),
            ExpressionParser.V_PROGRAM_STAGE_ID to VProgramStageId(),
            ExpressionParser.V_PROGRAM_STAGE_NAME to VProgramStageName()
        )
    }
}
