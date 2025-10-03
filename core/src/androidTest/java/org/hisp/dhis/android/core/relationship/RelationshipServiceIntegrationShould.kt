/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.relationship

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class RelationshipServiceIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun getAvailableRelationshipsForTrackedEntities() {
        val relationshipsWithoutProgram = d2.relationshipModule().relationshipService()
            .getRelationshipTypesForTrackedEntities("nEenWmSyUEp")

        assertThat(relationshipsWithoutProgram.size).isEqualTo(2)
        assertThat(
            relationshipsWithoutProgram.any { r ->
                r.relationshipType.uid() == "WiH6923nMtb" && r.entitySide == RelationshipConstraintType.TO
            },
        ).isTrue()
        assertThat(
            relationshipsWithoutProgram.any { r ->
                r.relationshipType.uid() == "V2kkHafqs8G" && r.entitySide == RelationshipConstraintType.FROM
            },
        ).isTrue()

        val relationshipsWithProgram = d2.relationshipModule().relationshipService()
            .getRelationshipTypesForTrackedEntities("nEenWmSyUEp", "other_program")

        assertThat(relationshipsWithProgram.size).isEqualTo(1)
        assertThat(relationshipsWithProgram[0].relationshipType.uid()).isEqualTo("WiH6923nMtb")
        assertThat(relationshipsWithProgram[0].entitySide).isEqualTo(RelationshipConstraintType.TO)
    }

    @Test
    fun getAvailableRelationshipsForEnrollments() {
        val relationships = d2.relationshipModule().relationshipService()
            .getRelationshipTypesForEnrollments("IpHINAT79UW")

        assertThat(relationships.size).isEqualTo(1)
        assertThat(relationships[0].relationshipType.uid()).isEqualTo("WiH6923nMtb")
        assertThat(relationships[0].entitySide).isEqualTo(RelationshipConstraintType.FROM)
    }

    @Test
    fun getAvailableRelationshipsForEvents() {
        val relationships = d2.relationshipModule().relationshipService()
            .getRelationshipTypesForEvents("dBwrot7S420")

        assertThat(relationships.size).isEqualTo(1)
        assertThat(relationships[0].relationshipType.uid()).isEqualTo("o51cUNONthg")
        assertThat(relationships[0].entitySide).isEqualTo(RelationshipConstraintType.FROM)
    }
}
