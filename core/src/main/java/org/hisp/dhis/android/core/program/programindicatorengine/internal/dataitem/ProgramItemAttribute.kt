/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramExpressionItem
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.enrollment
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.event
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.antlr.ParserExceptionWithoutContext
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class ProgramItemAttribute : ProgramExpressionItem() {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val attributeUid = getProgramAttributeId(ctx)

        val attributeValue = visitor.programIndicatorContext.attributeValues[attributeUid]
        val attribute = visitor.trackedEntityAttributeStore.selectByUid(attributeUid)

        val value = attributeValue?.value()
        val handledValue = visitor.handleNulls(value)
        val strValue = handledValue?.toString()

        return formatValue(strValue, attribute!!.valueType())
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {
        val attributeUid = getProgramAttributeId(ctx)

        // TODO Manage null and boolean values

        val enrollmentSelector = when (visitor.programIndicatorSQLContext.programIndicator.analyticsType()) {
            AnalyticsType.EVENT -> "$event.${EventTableInfo.Columns.ENROLLMENT}"
            AnalyticsType.ENROLLMENT, null -> "$enrollment.${EnrollmentTableInfo.Columns.UID}"
        }

        return "(SELECT ${TrackedEntityAttributeValueTableInfo.Columns.VALUE} " +
                "FROM ${TrackedEntityAttributeValueTableInfo.TABLE_INFO.name()} " +
                "WHERE ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE} = '$attributeUid' " +
                "AND ${TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE} IN (" +
                "SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
                "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
                "WHERE ${EnrollmentTableInfo.Columns.UID} = $enrollmentSelector" +
                ")" +
                ")"
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Makes sure that the parsed A{...} has a syntax that could be used
     * be used in an program expression for A{attributeUid}
     *
     * @param ctx the item context
     * @return the attribute UID.
     */
    private fun getProgramAttributeId(ctx: ExprContext): String {
        if (ctx.uid1 != null) {
            throw ParserExceptionWithoutContext("Program attribute must have one UID: ${ctx.text}")
        }
        return ctx.uid0.text
    }
}