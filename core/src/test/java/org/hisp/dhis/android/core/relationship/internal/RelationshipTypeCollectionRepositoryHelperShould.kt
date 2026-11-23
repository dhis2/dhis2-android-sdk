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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RelationshipTypeCollectionRepositoryHelperShould {

    /**
     * Test to showcase the generated sql query
     */
    @Test
    fun create_tracked_entity_instance_raw_query() {
        val teiUid = "tei_uid"

        val rawQuery = RelationshipTypeCollectionRepositoryHelper.availableForTrackedEntityInstanceRawQuery(teiUid)
        val teiTypeSubQuery = "SELECT trackedEntityType FROM TrackedEntityInstance WHERE uid = '$teiUid'"

        assertThat(rawQuery).isEqualTo(
            "SELECT DISTINCT uid FROM RelationshipType " +
                "WHERE uid IN " +
                "(" +
                "SELECT relationshipType FROM RelationshipConstraint " +
                "WHERE constraintType = 'FROM' " +
                "AND relationshipEntity = 'TRACKED_ENTITY_INSTANCE' " +
                "AND trackedEntityType IN ($teiTypeSubQuery) " +
                "AND (program IS NULL " +
                "OR program IN (SELECT program FROM Enrollment WHERE trackedEntityInstance = '$teiUid'))" +
                ") " +
                "OR " +
                "(" +
                "bidirectional = 1 " +
                "AND uid IN " +
                "(" +
                "SELECT relationshipType FROM RelationshipConstraint " +
                "WHERE relationshipEntity = 'TRACKED_ENTITY_INSTANCE' " +
                "AND trackedEntityType IN ($teiTypeSubQuery) " +
                "AND (program IS NULL " +
                "OR program IN (SELECT program FROM Enrollment WHERE trackedEntityInstance = '$teiUid'))" +
                ")" +
                ")",
        )
    }

    @Test
    fun create_enrollment_raw_query() {
        val enrollmentUid = "enrollment_uid"

        val rawQuery = RelationshipTypeCollectionRepositoryHelper.availableForEnrollmentRawQuery(enrollmentUid)

        val constraint = "relationshipEntity = 'PROGRAM_INSTANCE' " +
            "AND program IN (SELECT program FROM Enrollment WHERE uid = '$enrollmentUid')"

        assertThat(rawQuery).isEqualTo(
            "SELECT DISTINCT uid FROM RelationshipType " +
                "WHERE uid IN (SELECT relationshipType FROM RelationshipConstraint " +
                "WHERE constraintType = 'FROM' AND $constraint) " +
                "OR (bidirectional = 1 " +
                "AND uid IN (SELECT relationshipType FROM RelationshipConstraint WHERE $constraint))",
        )
    }

    @Test
    fun create_event_raw_query() {
        val eventUid = "event_uid"

        val rawQuery = RelationshipTypeCollectionRepositoryHelper.availableForEventRawQuery(eventUid)

        val constraint = "relationshipEntity = 'PROGRAM_STAGE_INSTANCE' " +
            "AND (program IN (SELECT program FROM Event WHERE uid = '$eventUid') " +
            "OR programStage IN (SELECT programStage FROM Event WHERE uid = '$eventUid'))"

        assertThat(rawQuery).isEqualTo(
            "SELECT DISTINCT uid FROM RelationshipType " +
                "WHERE uid IN (SELECT relationshipType FROM RelationshipConstraint " +
                "WHERE constraintType = 'FROM' AND $constraint) " +
                "OR (bidirectional = 1 " +
                "AND uid IN (SELECT relationshipType FROM RelationshipConstraint WHERE $constraint))",
        )
    }
}
