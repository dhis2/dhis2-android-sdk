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

package org.hisp.dhis.android.core.relationship.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.relationship.RelationshipConstraintTableInfo
import org.hisp.dhis.android.core.relationship.RelationshipConstraintTableInfo.Columns
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

object RelationshipTypeCollectionRepositoryHelper {

    @JvmStatic
    fun availableForTrackedEntityInstanceRawQuery(tei: TrackedEntityInstance?): String {
        return availableForItemRawQuery(tei, availabilityTeiQuery)
    }

    private val availabilityTeiQuery = { tei: TrackedEntityInstance?, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            tei?.let {
                if (type != null) {
                    appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name)
                }
                appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
                appendKeyStringValue(Columns.TRACKED_ENTITY_TYPE, it.trackedEntityType())
                appendComplexQuery(appendOptionalEnrollmentInProgram(tei))
            }
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    private fun appendOptionalEnrollmentInProgram(tei: TrackedEntityInstance): String {
        return WhereClauseBuilder()
            .appendIsNullValue(Columns.PROGRAM)
            .appendOrInSubQuery(
                Columns.PROGRAM,
                "SELECT ${EnrollmentTableInfo.Columns.PROGRAM} FROM ${EnrollmentTableInfo.TABLE_INFO.name()}" +
                    " WHERE ${EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE} == '${tei.uid()}'"
            )
            .build()
    }

    @JvmStatic
    fun availableForEnrollmentRawQuery(enrollment: Enrollment?): String {
        return availableForItemRawQuery(enrollment, availableForEnrollment)
    }

    private val availableForEnrollment = { enrollment: Enrollment?, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            enrollment?.let {
                if (type != null) {
                    appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name)
                }
                appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.PROGRAM_INSTANCE)
                appendKeyStringValue(Columns.PROGRAM, enrollment.program())
            }
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    @JvmStatic
    fun availableForEventRawQuery(event: Event?): String {
        return availableForItemRawQuery(event, availableForEvent)
    }

    private val availableForEvent = { event: Event?, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            event?.let {
                if (type != null) {
                    appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name)
                }
                appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.PROGRAM_STAGE_INSTANCE)
                appendComplexQuery(
                    WhereClauseBuilder()
                        .appendOrKeyStringValue(Columns.PROGRAM, it.program())
                        .appendOrKeyStringValue(Columns.PROGRAM_STAGE, it.programStage())
                        .build()
                )
            }
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    private fun <T> availableForItemRawQuery(
        t: T?,
        availableForItem: (T, RelationshipConstraintType?) -> String
    ): String {
        return t?.let {
            "SELECT DISTINCT ${RelationshipTypeTableInfo.Columns.UID} " +
                "FROM ${RelationshipTypeTableInfo.TABLE_INFO.name()} " +
                "WHERE ${RelationshipTypeTableInfo.Columns.UID} IN (${availableForItem(it, FROM)}) " +
                "OR (${RelationshipTypeTableInfo.Columns.BIDIRECTIONAL} = 1 " +
                "AND ${RelationshipTypeTableInfo.Columns.UID} IN (${availableForItem(it, null)}))"
        } ?: ""
    }

    private fun relationshipTypeInConstraint(whereClause: String): String {
        return "SELECT ${Columns.RELATIONSHIP_TYPE} FROM ${RelationshipConstraintTableInfo.TABLE_INFO.name()} " +
            "WHERE $whereClause"
    }
}
