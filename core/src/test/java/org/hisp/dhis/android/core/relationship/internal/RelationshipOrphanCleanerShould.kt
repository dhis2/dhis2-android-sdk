/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class RelationshipOrphanCleanerShould {

    private val relationshipStore: RelationshipStore = mock()
    private val relationshipRepository: RelationshipCollectionRepository = mock()

    private val parent = TrackedEntityInstance.builder().uid("parentTei").build()

    // object to test
    private lateinit var cleaner: TEIRelationshipOrphanCleaner

    @Before
    fun setUp() {
        cleaner = TEIRelationshipOrphanCleaner(relationshipStore, relationshipRepository)
    }

    @Test
    fun return_false_when_parent_is_null() = runTest {
        val deleted = cleaner.deleteOrphan(null, emptyList())

        assertThat(deleted).isFalse()
        verifyNoInteractions(relationshipRepository, relationshipStore)
    }

    @Test
    fun return_false_when_children_are_null() = runTest {
        val deleted = cleaner.deleteOrphan(parent, null)

        assertThat(deleted).isFalse()
        verifyNoInteractions(relationshipRepository, relationshipStore)
    }

    @Test
    fun delete_synced_relationship_that_is_not_in_the_downloaded_list() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED)
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, emptyList())

        assertThat(deleted).isTrue()
        verify(relationshipRepository).getByItemInternal(RelationshipHelper.teiItem("parentTei"), true, false)
        verify(relationshipStore).deleteByEntity(existing)
    }

    @Test
    fun delete_relationship_synced_via_sms() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED_VIA_SMS)
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, emptyList())

        assertThat(deleted).isTrue()
        verify(relationshipStore).deleteByEntity(existing)
    }

    @Test
    fun not_delete_relationship_that_is_still_in_the_downloaded_list() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED)
        val downloaded = relationship("relationship2", "fromTei1", "toTei1", State.SYNCED)
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, listOf(downloaded))

        assertThat(deleted).isFalse()
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun not_delete_relationship_that_is_not_synced() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.TO_POST)
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, emptyList())

        assertThat(deleted).isFalse()
        verify(relationshipStore, never()).deleteByEntity(any())
    }

    @Test
    fun delete_relationship_when_one_of_its_items_is_null() = runTest {
        val withoutTo = relationshipWithItems("relationship1", RelationshipHelper.teiItem("fromTei1"), null)
        val withoutFrom = relationshipWithItems("relationship2", null, RelationshipHelper.teiItem("toTei1"))
        val downloaded = relationship("relationship3", "fromTei1", "toTei1", State.SYNCED)
        givenExistingRelationships(withoutTo, withoutFrom)

        val deleted = cleaner.deleteOrphan(parent, listOf(downloaded))

        assertThat(deleted).isTrue()
        verify(relationshipStore).deleteByEntity(withoutTo)
        verify(relationshipStore).deleteByEntity(withoutFrom)
    }

    @Test
    fun delete_relationship_when_only_the_type_differs() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED)
        val downloaded = RelationshipSamples.get230(
            "relationship2",
            RelationshipHelper.teiItem("fromTei1"),
            RelationshipHelper.teiItem("toTei1"),
        ).toBuilder().relationshipType("anotherType").build()
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, listOf(downloaded))

        assertThat(deleted).isTrue()
        verify(relationshipStore).deleteByEntity(existing)
    }

    @Test
    fun delete_relationship_when_the_downloaded_one_has_no_items() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED)
        val downloadedWithoutFrom = relationshipWithItems(
            "relationship2",
            null,
            RelationshipHelper.teiItem("toTei1"),
        )
        val downloadedWithoutTo = relationshipWithItems(
            "relationship3",
            RelationshipHelper.teiItem("fromTei1"),
            null,
        )
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, listOf(downloadedWithoutFrom, downloadedWithoutTo))

        assertThat(deleted).isTrue()
        verify(relationshipStore).deleteByEntity(existing)
    }

    @Test
    fun delete_relationship_when_its_items_differ_from_the_downloaded_ones() = runTest {
        val existing = relationship("relationship1", "fromTei1", "toTei1", State.SYNCED)
        val differentFrom = relationship("relationship2", "anotherFromTei", "toTei1", State.SYNCED)
        val differentTo = relationship("relationship3", "fromTei1", "anotherToTei", State.SYNCED)
        givenExistingRelationships(existing)

        val deleted = cleaner.deleteOrphan(parent, listOf(differentFrom, differentTo))

        assertThat(deleted).isTrue()
        verify(relationshipStore).deleteByEntity(existing)
    }

    @Test
    fun build_the_right_item_for_each_cleaner_subclass() = runTest {
        whenever(relationshipRepository.getByItemInternal(any(), any(), any())).doReturn(emptyList())

        cleaner.deleteOrphan(parent, emptyList())
        EnrollmentRelationshipOrphanCleaner(relationshipStore, relationshipRepository)
            .deleteOrphan(enrollment("parentEnrollment"), emptyList())
        EventRelationshipOrphanCleaner(relationshipStore, relationshipRepository)
            .deleteOrphan(event("parentEvent"), emptyList())

        verify(relationshipRepository).getByItemInternal(RelationshipHelper.teiItem("parentTei"), true, false)
        verify(relationshipRepository)
            .getByItemInternal(RelationshipHelper.enrollmentItem("parentEnrollment"), true, false)
        verify(relationshipRepository).getByItemInternal(RelationshipHelper.eventItem("parentEvent"), true, false)
    }

    private suspend fun givenExistingRelationships(vararg relationships: Relationship) {
        whenever(relationshipRepository.getByItemInternal(any<RelationshipItem>(), any(), any()))
            .doReturn(relationships.toList())
    }

    private fun relationship(uid: String, fromUid: String, toUid: String, syncState: State): Relationship =
        relationshipWithItems(
            uid,
            RelationshipHelper.teiItem(fromUid),
            RelationshipHelper.teiItem(toUid),
            syncState,
        )

    private fun relationshipWithItems(
        uid: String,
        from: RelationshipItem?,
        to: RelationshipItem?,
        syncState: State = State.SYNCED,
    ): Relationship =
        Relationship.builder()
            .uid(uid)
            .relationshipType(RelationshipSamples.TYPE)
            .syncState(syncState)
            .deleted(false)
            .from(from)
            .to(to)
            .build()

    private fun enrollment(uid: String): Enrollment =
        Enrollment.builder().uid(uid).attributeOptionCombo("attributeOptionCombo").build()

    private fun event(uid: String): Event = Event.builder().uid(uid).build()
}
