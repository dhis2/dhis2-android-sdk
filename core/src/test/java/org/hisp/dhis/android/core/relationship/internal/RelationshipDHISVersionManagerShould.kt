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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo.Columns
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RelationshipDHISVersionManagerShould {
    private val relationshipTypeStore: RelationshipTypeStore = mock()

    private val relationshipType: RelationshipType = mock()

    private lateinit var relationshipDHISVersionManager: RelationshipDHISVersionManager

    @Before
    fun setUp() {
        relationshipDHISVersionManager = RelationshipDHISVersionManager(relationshipTypeStore)
    }

    @Test
    fun get_owned_relationships_in_bidirectional() = runTest {
        whenever(relationshipTypeStore.selectByUid(RelationshipSamples.TYPE)).thenReturn(relationshipType)
        whenever(relationshipType.bidirectional()).thenReturn(true)

        val relationships = listOf(RelationshipSamples.get230())
        val ownedRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(relationships, RelationshipSamples.TO_UID)

        assertThat(ownedRelationships.size).isEqualTo(1)
    }

    @Test
    fun get_owned_relationships_in_non_bidirectional() = runTest {
        whenever(relationshipTypeStore.selectByUid(RelationshipSamples.TYPE)).thenReturn(relationshipType)
        whenever(relationshipType.bidirectional()).thenReturn(false)

        val relationships = listOf(RelationshipSamples.get230())
        val ownedToRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(relationships, RelationshipSamples.TO_UID)
        val ownedFromRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(relationships, RelationshipSamples.FROM_UID)

        assertThat(ownedToRelationships).isEmpty()
        assertThat(ownedFromRelationships.size).isEqualTo(1)
    }

    @Test
    fun not_own_relationship_when_the_from_item_is_null() = runTest {
        whenever(relationshipTypeStore.selectByUid(RelationshipSamples.TYPE)).thenReturn(relationshipType)
        whenever(relationshipType.bidirectional()).thenReturn(false)

        val relationships = listOf(relationshipWithItems(null, RelationshipSamples.toItem))
        val ownedRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(relationships, RelationshipSamples.FROM_UID)

        assertThat(ownedRelationships).isEmpty()
    }

    @Test
    fun not_own_relationship_when_it_has_no_type() = runTest {
        val relationships = listOf(
            RelationshipSamples.get230().toBuilder().relationshipType(null).build(),
        )
        val ownedRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(relationships, RelationshipSamples.TO_UID)

        assertThat(ownedRelationships).isEmpty()
    }

    @Test
    fun save_tei_relative_when_parent_is_the_from_item() {
        val relatives = RelationshipItemRelatives()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(RelationshipSamples.get230()),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).containsExactly(
            RelationshipItemRelative(
                itemUid = RelationshipSamples.TO_UID,
                itemType = Columns.TRACKED_ENTITY_INSTANCE,
                relationshipTypeUid = RelationshipSamples.TYPE,
                constraintType = RelationshipConstraintType.TO,
            ),
        )
        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        assertThat(relatives.getRelativeEvents()).isEmpty()
    }

    @Test
    fun save_tei_relative_when_parent_is_the_to_item() {
        val relatives = RelationshipItemRelatives()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(RelationshipSamples.get230()),
            RelationshipSamples.TO_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).containsExactly(
            RelationshipItemRelative(
                itemUid = RelationshipSamples.FROM_UID,
                itemType = Columns.TRACKED_ENTITY_INSTANCE,
                relationshipTypeUid = RelationshipSamples.TYPE,
                constraintType = RelationshipConstraintType.FROM,
            ),
        )
    }

    @Test
    fun not_save_any_relative_when_parent_matches_neither_side() {
        val relatives = RelationshipItemRelatives()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(RelationshipSamples.get230()),
            "unrelatedUid",
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        assertThat(relatives.getRelativeEvents()).isEmpty()
    }

    @Test
    fun save_enrollment_relative_in_the_enrollment_set() {
        val relatives = RelationshipItemRelatives()
        val relationship = relationshipWithTo(RelationshipHelper.enrollmentItem("enrollment1"))

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationship),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeEnrollments()).containsExactly(
            RelationshipItemRelative(
                itemUid = "enrollment1",
                itemType = Columns.ENROLLMENT,
                relationshipTypeUid = RelationshipSamples.TYPE,
                constraintType = RelationshipConstraintType.TO,
            ),
        )
        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
    }

    @Test
    fun save_event_relative_in_the_event_set() {
        val relatives = RelationshipItemRelatives()
        val relationship = relationshipWithTo(RelationshipHelper.eventItem("event1"))

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationship),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeEvents()).containsExactly(
            RelationshipItemRelative(
                itemUid = "event1",
                itemType = Columns.EVENT,
                relationshipTypeUid = RelationshipSamples.TYPE,
                constraintType = RelationshipConstraintType.TO,
            ),
        )
        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
    }

    @Test
    fun not_save_relative_when_the_opposite_item_is_null() {
        val relatives = RelationshipItemRelatives()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationshipWithItems(RelationshipSamples.fromItem, null)),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
    }

    @Test
    fun not_save_relative_when_the_from_item_is_null_and_parent_is_the_to_item() {
        val relatives = RelationshipItemRelatives()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationshipWithItems(null, RelationshipSamples.toItem)),
            RelationshipSamples.TO_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
    }

    @Test
    fun not_save_relative_when_relationship_has_no_type() {
        val relatives = RelationshipItemRelatives()
        val relationship = Relationship.builder()
            .uid(RelationshipSamples.UID)
            .relationshipType(null)
            .from(RelationshipSamples.fromItem)
            .to(RelationshipSamples.toItem)
            .build()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationship),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
    }

    @Test
    fun not_duplicate_relatives_when_the_same_relationship_is_processed_twice() {
        val relatives = RelationshipItemRelatives()
        val relationship = RelationshipSamples.get230()

        relationshipDHISVersionManager.saveRelativesIfNotExist(
            listOf(relationship, relationship),
            RelationshipSamples.FROM_UID,
            relatives,
        )

        assertThat(relatives.getRelativeTrackedEntityInstances()).hasSize(1)
    }

    private fun relationshipWithTo(to: RelationshipItem): Relationship =
        relationshipWithItems(RelationshipSamples.fromItem, to)

    private fun relationshipWithItems(from: RelationshipItem?, to: RelationshipItem?): Relationship =
        RelationshipSamples.get230(RelationshipSamples.UID, from, to)
}
