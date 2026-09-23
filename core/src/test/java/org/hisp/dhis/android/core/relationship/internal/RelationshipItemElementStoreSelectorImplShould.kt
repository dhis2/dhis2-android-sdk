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
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo.Columns
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock

@RunWith(JUnit4::class)
class RelationshipItemElementStoreSelectorImplShould {

    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()

    // object to test
    private lateinit var selector: RelationshipItemElementStoreSelectorImpl

    @Before
    fun setUp() {
        selector = RelationshipItemElementStoreSelectorImpl(
            trackedEntityInstanceStore,
            enrollmentStore,
            eventStore,
        )
    }

    @Test
    fun return_tei_store_for_a_tracked_entity_instance_relative() {
        val store = selector.getElementStore(relative(Columns.TRACKED_ENTITY_INSTANCE))

        assertThat(store).isSameInstanceAs(trackedEntityInstanceStore)
    }

    @Test
    fun return_enrollment_store_for_an_enrollment_relative() {
        val store = selector.getElementStore(relative(Columns.ENROLLMENT))

        assertThat(store).isSameInstanceAs(enrollmentStore)
    }

    @Test
    fun return_event_store_for_an_event_relative() {
        val store = selector.getElementStore(relative(Columns.EVENT))

        assertThat(store).isSameInstanceAs(eventStore)
    }

    @Test
    fun return_event_store_for_an_unknown_element_type() {
        val store = selector.getElementStore(relative("unknownElementType"))

        assertThat(store).isSameInstanceAs(eventStore)
    }

    @Test
    fun return_the_matching_store_for_a_relationship_item() {
        assertThat(selector.getElementStore(RelationshipHelper.teiItem("tei1")))
            .isSameInstanceAs(trackedEntityInstanceStore)
        assertThat(selector.getElementStore(RelationshipHelper.enrollmentItem("enrollment1")))
            .isSameInstanceAs(enrollmentStore)
        assertThat(selector.getElementStore(RelationshipHelper.eventItem("event1")))
            .isSameInstanceAs(eventStore)
    }

    @Test
    fun return_event_store_when_the_relationship_item_is_null() {
        val store = selector.getElementStore(null)

        assertThat(store).isSameInstanceAs(eventStore)
    }

    private fun relative(itemType: String) = RelationshipItemRelative(
        itemUid = "itemUid",
        itemType = itemType,
        relationshipTypeUid = "relationshipType1",
        constraintType = RelationshipConstraintType.FROM,
    )
}
