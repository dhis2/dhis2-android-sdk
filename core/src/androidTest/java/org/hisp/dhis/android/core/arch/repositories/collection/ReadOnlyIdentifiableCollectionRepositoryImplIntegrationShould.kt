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
package org.hisp.dhis.android.core.arch.repositories.collection

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityTypeSamples
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class ReadOnlyIdentifiableCollectionRepositoryImplIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {
    @Test
    fun get_relationship_1_from_object_repository_without_children() {
        val relationshipType = relationshipTypeCollectionRepository
            .uid(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1)
            .blockingGet()

        RelationshipTypeAsserts.assertTypesWithoutConstraints(
            relationshipType,
            RelationshipTypeSamples.RELATIONSHIP_TYPE_1
        )
    }

    @Test
    fun get_relationship_by_from_tracked_entity_type() {
        val relationshipType = relationshipTypeCollectionRepository
            .byConstraint(
                RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
                RelationshipTypeSamples.TET_FOR_RELATIONSHIP_3_UID
            )
            .withConstraints()
            .blockingGet()

        assertThat(relationshipType.size).isEqualTo(2)
    }

    @Test
    fun get_relationship_by_from_tracked_entity_type_and_constraint_type() {
        val relationshipType = relationshipTypeCollectionRepository
            .byConstraint(
                RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
                RelationshipTypeSamples.TET_FOR_RELATIONSHIP_3_UID,
                RelationshipConstraintType.FROM
            )
            .withConstraints()
            .blockingGet()

        assertThat(relationshipType.size).isEqualTo(1)
        assertThat(relationshipType[0].uid()).isEqualTo(RelationshipTypeSamples.RELATIONSHIP_TYPE_3.uid())
        assertThat(relationshipType[0].fromConstraint()?.trackedEntityType()?.uid())
            .isEqualTo(RelationshipTypeSamples.TET_FOR_RELATIONSHIP_3_UID)
    }

    @Test
    fun get_relationship_2_from_object_repository_without_children() {
        val relationshipType =
            relationshipTypeCollectionRepository
                .uid(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_2)
                .blockingGet()

        RelationshipTypeAsserts.assertTypesWithoutConstraints(
            relationshipType,
            RelationshipTypeSamples.RELATIONSHIP_TYPE_2
        )
    }

    @Test
    fun get_relationship_1_from_object_repository_with_children() {
        val relationshipType = relationshipTypeCollectionRepository
            .withConstraints()
            .uid(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1)
            .blockingGet()

        RelationshipTypeAsserts.assertTypeWithConstraints(
            relationshipType,
            RelationshipTypeSamples.RELATIONSHIP_TYPE_1
        )
    }

    @Test
    fun get_relationship_2_from_object_repository_with_children() {
        val relationshipType = relationshipTypeCollectionRepository
            .withConstraints()
            .uid(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_2)
            .blockingGet()

        RelationshipTypeAsserts.assertTypeWithConstraints(
            relationshipType,
            RelationshipTypeSamples.RELATIONSHIP_TYPE_2
        )
    }

    companion object {
        private lateinit var relationshipTypeCollectionRepository: RelationshipTypeCollectionRepository

        @BeforeClass
        @JvmStatic
        fun setUpTestClass() {
            setUpClass()
            val rtHandler = objects.d2DIComponent.relationshipTypeHandler()
            val tetHandler = objects.d2DIComponent.trackedEntityTypeHandler()

            tetHandler.handle(TrackedEntityTypeSamples.get())
            rtHandler.handle(RelationshipTypeSamples.RELATIONSHIP_TYPE_1)
            rtHandler.handle(RelationshipTypeSamples.RELATIONSHIP_TYPE_2)
            rtHandler.handle(RelationshipTypeSamples.RELATIONSHIP_TYPE_3)

            relationshipTypeCollectionRepository = d2.relationshipModule().relationshipTypes()
        }
    }
}
