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

import org.hisp.dhis.android.core.common.AnalyticsType
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.enrollment
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLUtils.event
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo
import org.hisp.dhis.android.core.relationship.RelationshipTableInfo
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class D2RelationshipCount : ExpressionItem {

    private val riAlias = "ri"
    private val rAlias = "r"

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any {

        val rTypeUid = ctx.uid0?.text

        val queries = getQueries(visitor.programIndicatorSQLContext.programIndicator)

        return "(SELECT COUNT(*) " +
            "FROM ${RelationshipItemTableInfo.TABLE_INFO.name()} $riAlias " +
            "INNER JOIN ${RelationshipTableInfo.TABLE_INFO.name()} $rAlias " +
            "ON $riAlias.${RelationshipItemTableInfo.Columns.RELATIONSHIP} = " +
            "$rAlias.${RelationshipTableInfo.Columns.UID} " +
            "WHERE " +
            "($riAlias.${RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE} ${queries.teiQuery} " +
            "OR $riAlias.${RelationshipItemTableInfo.Columns.ENROLLMENT} ${queries.enrollmentQuery} " +
            "OR $riAlias.${RelationshipItemTableInfo.Columns.EVENT} ${queries.eventQuery}) " +
            "AND ($rAlias.${RelationshipTableInfo.Columns.DELETED} = 0 OR " +
            "$rAlias.${RelationshipTableInfo.Columns.DELETED} IS NULL) " +
            (rTypeUid?.let { "AND $rAlias.${RelationshipTableInfo.Columns.RELATIONSHIP_TYPE} = $it" } ?: "") +
            ")"
    }

    private fun getQueries(programIndicator: ProgramIndicator): RelationshipCountQueries {
        return when (programIndicator.analyticsType()) {
            AnalyticsType.EVENT ->
                RelationshipCountQueries(
                    teiQuery = "IN (SELECT ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} " +
                        "FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
                        "WHERE ${EnrollmentTableInfo.Columns.UID} = $event.${EventTableInfo.Columns.ENROLLMENT})",
                    enrollmentQuery = "= $event.${EventTableInfo.Columns.ENROLLMENT}",
                    eventQuery = "= $event.${EventTableInfo.Columns.UID}"
                )
            AnalyticsType.ENROLLMENT, null ->
                RelationshipCountQueries(
                    teiQuery = "= $enrollment.${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE}",
                    enrollmentQuery = "= $enrollment.${EnrollmentTableInfo.Columns.UID}",
                    eventQuery = "IN (SELECT ${EventTableInfo.Columns.UID} " +
                        "FROM ${EventTableInfo.TABLE_INFO.name()} " +
                        "WHERE ${EventTableInfo.Columns.ENROLLMENT} = " +
                        "$enrollment.${EnrollmentTableInfo.Columns.UID})"
                )
        }
    }

    internal data class RelationshipCountQueries(
        val teiQuery: String,
        val enrollmentQuery: String,
        val eventQuery: String
    )
}
