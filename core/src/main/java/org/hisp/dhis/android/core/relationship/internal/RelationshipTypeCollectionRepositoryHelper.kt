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
package org.hisp.dhis.android.core.relationship.internal

import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipConstraintTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipConstraintTableInfo.Columns
import org.hisp.dhis.android.persistence.relationship.RelationshipTypeTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo

object RelationshipTypeCollectionRepositoryHelper {

    @JvmStatic
    fun availableForTrackedEntityInstanceRawQuery(teiUid: String): String {
        return availableForItemRawQuery(teiUid, availabilityTeiQuery)
    }

    private val availabilityTeiQuery = { teiUid: String, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            type?.let { appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name) }
            appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
            appendInSubQuery(
                Columns.TRACKED_ENTITY_TYPE,
                selectColumnByUid(
                    TrackedEntityInstanceTableInfo.TABLE_INFO.name(),
                    TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE,
                    teiUid,
                ),
            )
            appendComplexQuery(appendOptionalEnrollmentInProgram(teiUid))
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    private fun appendOptionalEnrollmentInProgram(teiUid: String): String {
        return WhereClauseBuilder()
            .appendIsNullValue(Columns.PROGRAM)
            .appendOrInSubQuery(
                Columns.PROGRAM,
                "SELECT ${EnrollmentTableInfo.Columns.PROGRAM} FROM ${EnrollmentTableInfo.TABLE_INFO.name()} " +
                    "WHERE " +
                    WhereClauseBuilder()
                        .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teiUid)
                        .build(),
            )
            .build()
    }

    @JvmStatic
    fun availableForEnrollmentRawQuery(enrollmentUid: String): String {
        return availableForItemRawQuery(enrollmentUid, availableForEnrollment)
    }

    private val availableForEnrollment = { enrollmentUid: String, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            type?.let { appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name) }
            appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.PROGRAM_INSTANCE)
            appendInSubQuery(
                Columns.PROGRAM,
                selectColumnByUid(
                    EnrollmentTableInfo.TABLE_INFO.name(),
                    EnrollmentTableInfo.Columns.PROGRAM,
                    enrollmentUid,
                ),
            )
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    @JvmStatic
    fun availableForEventRawQuery(eventUid: String): String {
        return availableForItemRawQuery(eventUid, availableForEvent)
    }

    private val availableForEvent = { eventUid: String, type: RelationshipConstraintType? ->
        val whereClause = WhereClauseBuilder().apply {
            type?.let { appendKeyStringValue(Columns.CONSTRAINT_TYPE, type.name) }
            appendKeyStringValue(Columns.RELATIONSHIP_ENTITY, RelationshipEntityType.PROGRAM_STAGE_INSTANCE)
            appendComplexQuery(
                WhereClauseBuilder()
                    .appendInSubQuery(
                        Columns.PROGRAM,
                        selectColumnByUid(EventTableInfo.TABLE_INFO.name(), EventTableInfo.Columns.PROGRAM, eventUid),
                    )
                    .appendOrInSubQuery(
                        Columns.PROGRAM_STAGE,
                        selectColumnByUid(
                            EventTableInfo.TABLE_INFO.name(),
                            EventTableInfo.Columns.PROGRAM_STAGE,
                            eventUid,
                        ),
                    )
                    .build(),
            )
        }

        relationshipTypeInConstraint(whereClause.build())
    }

    private fun availableForItemRawQuery(
        uid: String,
        availableForItem: (String, RelationshipConstraintType?) -> String,
    ): String {
        return "SELECT DISTINCT ${RelationshipTypeTableInfo.Columns.UID} " +
            "FROM ${RelationshipTypeTableInfo.TABLE_INFO.name()} " +
            "WHERE ${RelationshipTypeTableInfo.Columns.UID} IN (${availableForItem(uid, FROM)}) " +
            "OR (${RelationshipTypeTableInfo.Columns.BIDIRECTIONAL} = 1 " +
            "AND ${RelationshipTypeTableInfo.Columns.UID} IN (${availableForItem(uid, null)}))"
    }

    private fun selectColumnByUid(table: String, column: String, uid: String): String {
        return "SELECT $column FROM $table WHERE " +
            WhereClauseBuilder().appendKeyStringValue(IdentifiableColumns.UID, uid).build()
    }

    private fun relationshipTypeInConstraint(whereClause: String): String {
        return "SELECT ${Columns.RELATIONSHIP_TYPE} FROM ${RelationshipConstraintTableInfo.TABLE_INFO.name()} " +
            "WHERE $whereClause"
    }
}
