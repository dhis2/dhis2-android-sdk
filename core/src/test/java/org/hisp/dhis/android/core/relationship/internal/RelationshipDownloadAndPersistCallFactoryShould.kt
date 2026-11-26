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
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreWithState
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentEndpointCallFactory
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentPersistenceCallFactory
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventEndpointCallFactory
import org.hisp.dhis.android.core.event.internal.EventPersistenceCallFactory
import org.hisp.dhis.android.core.maintenance.D2ErrorSamples
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEnrollment
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePersistenceCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo.Columns
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class RelationshipDownloadAndPersistCallFactoryShould {

    private val relationshipStore: RelationshipStore = mock()
    private val programStore: ProgramStore = mock()
    private val trackerParentCallFactory: TrackerParentCallFactory = mock()
    private val teiPersistenceCallFactory: TrackedEntityInstancePersistenceCallFactory = mock()
    private val enrollmentPersistenceCallFactory: EnrollmentPersistenceCallFactory = mock()
    private val eventPersistenceCallFactory: EventPersistenceCallFactory = mock()
    private val relationshipItemStoreSelector: RelationshipItemElementStoreSelector = mock()
    private val coroutineAPICallExecutor = CoroutineAPICallExecutorMock()

    private val eventEndpointCallFactory: EventEndpointCallFactory = mock()
    private val enrollmentEndpointCallFactory: EnrollmentEndpointCallFactory = mock()
    private val teiEndpointCallFactory: TrackedEntityEndpointCallFactory = mock()
    private val elementStore: StoreWithState<Any> = mock()

    private val relatives = RelationshipItemRelatives()
    private val corruptedRelationship = RelationshipSamples.get230("corrupted1", "fromTei9", "toTei9")

    // object to test
    private lateinit var factory: RelationshipDownloadAndPersistCallFactory

    @Before
    fun setUp() = runTest {
        whenever(trackerParentCallFactory.getEventCall()).doReturn(eventEndpointCallFactory)
        whenever(trackerParentCallFactory.getEnrollmentCall()).doReturn(enrollmentEndpointCallFactory)
        whenever(trackerParentCallFactory.getTrackedEntityCall()).doReturn(teiEndpointCallFactory)
        whenever(relationshipItemStoreSelector.getElementStore(any<RelationshipItemRelative>()))
            .doReturn(elementStore)
        whenever(elementStore.exists(any())).doReturn(false)
        whenever(relationshipStore.getRelationshipsByItem(any<RelationshipItem>())).doReturn(emptyList())

        factory = RelationshipDownloadAndPersistCallFactory(
            relationshipStore,
            programStore,
            trackerParentCallFactory,
            teiPersistenceCallFactory,
            enrollmentPersistenceCallFactory,
            eventPersistenceCallFactory,
            coroutineAPICallExecutor,
            relationshipItemStoreSelector,
        )
    }

    // region events

    @Test
    fun download_and_persist_relative_events() = runTest {
        val event = event("event1")
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event)

        factory.downloadAndPersist(relatives)

        verify(eventPersistenceCallFactory).persistAsRelationships(listOf(event))
        verify(relationshipStore, never()).deleteIfExists(any())
    }

    @Test
    fun persist_every_event_in_a_multi_item_payload() = runTest {
        val first = event("event1")
        val second = event("event2")
        relatives.addEvent(eventRelative("event1"))
        whenever(eventEndpointCallFactory.getRelationshipEntityCall(eventRelative("event1")))
            .doReturn(payloadOf(first, second))

        factory.downloadAndPersist(relatives)

        verify(eventPersistenceCallFactory).persistAsRelationships(listOf(first, second))
    }

    @Test
    fun skip_event_download_when_the_item_already_exists_locally() = runTest {
        relatives.addEvent(eventRelative("event1"))
        whenever(elementStore.exists("event1")).doReturn(true)

        factory.downloadAndPersist(relatives)

        verify(eventEndpointCallFactory, never()).getRelationshipEntityCall(any())
        verify(eventPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun clean_corrupted_relationships_when_event_download_fails() = runTest {
        relatives.addEvent(eventRelative("event1"))
        whenever(eventEndpointCallFactory.getRelationshipEntityCall(eventRelative("event1")))
            .doAnswer { throw D2ErrorSamples.get() }
        whenever(relationshipStore.getRelationshipsByItem(eventItem("event1")))
            .doReturn(listOf(corruptedRelationship))

        factory.downloadAndPersist(relatives)

        verify(relationshipStore).getRelationshipsByItem(eventItem("event1"))
        verify(relationshipStore).deleteIfExists("corrupted1")
        verify(eventPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun continue_with_remaining_events_when_one_download_fails() = runTest {
        val second = event("event2")
        relatives.addEvent(eventRelative("event1"))
        relatives.addEvent(eventRelative("event2"))
        whenever(eventEndpointCallFactory.getRelationshipEntityCall(eventRelative("event1")))
            .doAnswer { throw D2ErrorSamples.get() }
        givenEventDownloadReturns("event2", second)

        factory.downloadAndPersist(relatives)

        verify(eventPersistenceCallFactory).persistAsRelationships(listOf(second))
        verify(relationshipStore).getRelationshipsByItem(eventItem("event1"))
    }

    // endregion

    // region getTrackerEnrollment

    @Test
    fun queue_enrollment_relative_when_event_belongs_to_a_tracker_program() = runTest {
        val enrollment = enrollment("enrollment1")
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event("event1", program = "program1", enrollment = "enrollment1"))
        whenever(programStore.selectByUid("program1")).doReturn(trackerProgram("program1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment)

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).containsExactly(enrollmentRelative("enrollment1"))
        verify(enrollmentEndpointCallFactory).getRelationshipEntityCall(enrollmentRelative("enrollment1"))
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(listOf(enrollment))
    }

    @Test
    fun not_queue_enrollment_relative_when_event_has_no_program() = runTest {
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event("event1", program = null, enrollment = "enrollment1"))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        verify(programStore, never()).selectByUid(any())
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun not_queue_enrollment_relative_when_program_is_not_found() = runTest {
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event("event1", program = "program1", enrollment = "enrollment1"))
        whenever(programStore.selectByUid("program1")).doReturn(null)

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        verify(programStore).selectByUid("program1")
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun not_queue_enrollment_relative_when_program_is_without_registration() = runTest {
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event("event1", program = "program1", enrollment = "enrollment1"))
        whenever(programStore.selectByUid("program1"))
            .doReturn(program("program1", ProgramType.WITHOUT_REGISTRATION))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun not_queue_enrollment_relative_when_tracker_event_has_no_enrollment() = runTest {
        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event("event1", program = "program1", enrollment = null))
        whenever(programStore.selectByUid("program1")).doReturn(trackerProgram("program1"))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).isEmpty()
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    // endregion

    // region enrollments

    @Test
    fun download_and_persist_relative_enrollments() = runTest {
        val enrollment = enrollment("enrollment1")
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment)

        factory.downloadAndPersist(relatives)

        verify(enrollmentPersistenceCallFactory).persistAsRelationships(listOf(enrollment))
        verify(teiEndpointCallFactory, never()).getRelationshipEntityCall(any())
    }

    @Test
    fun queue_tei_relative_when_enrollment_has_a_tracked_entity_instance() = runTest {
        val tei = trackedEntityInstance("tei1")
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment("enrollment1", trackedEntityInstance = "tei1"))
        whenever(teiEndpointCallFactory.getRelationshipEntityCall(teiRelative("tei1")))
            .doReturn(payloadOf(tei))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeTrackedEntityInstances()).containsExactly(teiRelative("tei1"))
        verify(teiEndpointCallFactory).getRelationshipEntityCall(teiRelative("tei1"))
        verify(teiPersistenceCallFactory).persistRelationships(listOf(tei))
    }

    @Test
    fun not_queue_tei_relative_when_enrollment_has_no_tracked_entity_instance() = runTest {
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment("enrollment1", trackedEntityInstance = null))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeTrackedEntityInstances()).isEmpty()
        verify(teiPersistenceCallFactory).persistRelationships(emptyList())
    }

    @Test
    fun skip_enrollment_download_when_the_item_already_exists_locally() = runTest {
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        whenever(elementStore.exists("enrollment1")).doReturn(true)

        factory.downloadAndPersist(relatives)

        verify(enrollmentEndpointCallFactory, never()).getRelationshipEntityCall(any())
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    @Test
    fun clean_corrupted_relationships_when_enrollment_download_fails() = runTest {
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doAnswer { throw D2ErrorSamples.get() }
        whenever(relationshipStore.getRelationshipsByItem(enrollmentItem("enrollment1")))
            .doReturn(listOf(corruptedRelationship))

        factory.downloadAndPersist(relatives)

        verify(relationshipStore).getRelationshipsByItem(enrollmentItem("enrollment1"))
        verify(relationshipStore).deleteIfExists("corrupted1")
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
    }

    // endregion

    // region tracked entity instances

    @Test
    fun download_and_persist_relative_teis() = runTest {
        val tei = trackedEntityInstance("tei1")
        relatives.addTrackedEntityInstance(teiRelative("tei1"))
        whenever(teiEndpointCallFactory.getRelationshipEntityCall(teiRelative("tei1")))
            .doReturn(payloadOf(tei))

        factory.downloadAndPersist(relatives)

        verify(teiPersistenceCallFactory).persistRelationships(listOf(tei))
        verify(relationshipStore, never()).deleteIfExists(any())
    }

    @Test
    fun skip_tei_download_when_the_item_already_exists_locally() = runTest {
        relatives.addTrackedEntityInstance(teiRelative("tei1"))
        whenever(elementStore.exists("tei1")).doReturn(true)

        factory.downloadAndPersist(relatives)

        verify(teiEndpointCallFactory, never()).getRelationshipEntityCall(any())
        verify(teiPersistenceCallFactory).persistRelationships(emptyList())
    }

    @Test
    fun clean_corrupted_relationships_when_tei_download_fails() = runTest {
        relatives.addTrackedEntityInstance(teiRelative("tei1"))
        whenever(teiEndpointCallFactory.getRelationshipEntityCall(teiRelative("tei1")))
            .doAnswer { throw D2ErrorSamples.get() }
        whenever(relationshipStore.getRelationshipsByItem(teiItem("tei1")))
            .doReturn(listOf(corruptedRelationship))

        factory.downloadAndPersist(relatives)

        verify(relationshipStore).getRelationshipsByItem(teiItem("tei1"))
        verify(relationshipStore).deleteIfExists("corrupted1")
        verify(teiPersistenceCallFactory).persistRelationships(emptyList())
    }

    // endregion

    // region cross cutting

    @Test
    fun persist_empty_lists_when_there_are_no_relatives() = runTest {
        factory.downloadAndPersist(relatives)

        verify(eventPersistenceCallFactory).persistAsRelationships(emptyList())
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
        verify(teiPersistenceCallFactory).persistRelationships(emptyList())
        verifyNoInteractions(eventEndpointCallFactory, enrollmentEndpointCallFactory, teiEndpointCallFactory)
        verify(relationshipStore, never()).deleteIfExists(any())
    }

    @Test
    fun run_download_phases_in_order_events_then_enrollments_then_teis() = runTest {
        factory.downloadAndPersist(relatives)

        inOrder(eventPersistenceCallFactory, enrollmentPersistenceCallFactory, teiPersistenceCallFactory) {
            verify(eventPersistenceCallFactory).persistAsRelationships(emptyList())
            verify(enrollmentPersistenceCallFactory).persistAsRelationships(emptyList())
            verify(teiPersistenceCallFactory).persistRelationships(emptyList())
        }
    }

    @Test
    fun cascade_from_event_to_enrollment_to_tei_in_a_single_pass() = runTest {
        val event = event("event1", program = "program1", enrollment = "enrollment1")
        val enrollment = enrollment("enrollment1", trackedEntityInstance = "tei1")
        val tei = trackedEntityInstance("tei1")

        relatives.addEvent(eventRelative("event1"))
        givenEventDownloadReturns("event1", event)
        whenever(programStore.selectByUid("program1")).doReturn(trackerProgram("program1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment)
        whenever(teiEndpointCallFactory.getRelationshipEntityCall(teiRelative("tei1")))
            .doReturn(payloadOf(tei))

        factory.downloadAndPersist(relatives)

        assertThat(relatives.getRelativeEnrollments()).containsExactly(enrollmentRelative("enrollment1"))
        assertThat(relatives.getRelativeTrackedEntityInstances()).containsExactly(teiRelative("tei1"))
        verify(eventPersistenceCallFactory).persistAsRelationships(listOf(event))
        verify(enrollmentPersistenceCallFactory).persistAsRelationships(listOf(enrollment))
        verify(teiPersistenceCallFactory).persistRelationships(listOf(tei))
    }

    @Test
    fun delete_the_same_corrupted_relationship_once_per_failed_item() = runTest {
        relatives.addEvent(eventRelative("event1"))
        relatives.addEvent(eventRelative("event2"))
        whenever(eventEndpointCallFactory.getRelationshipEntityCall(any()))
            .doAnswer { throw D2ErrorSamples.get() }
        whenever(relationshipStore.getRelationshipsByItem(eventItem("event1")))
            .doReturn(listOf(corruptedRelationship))
        whenever(relationshipStore.getRelationshipsByItem(eventItem("event2")))
            .doReturn(listOf(corruptedRelationship))

        factory.downloadAndPersist(relatives)

        verify(relationshipStore, times(2)).deleteIfExists("corrupted1")
    }

    @Test
    fun not_clean_any_relationship_when_all_downloads_succeed() = runTest {
        relatives.addEvent(eventRelative("event1"))
        relatives.addEnrollment(enrollmentRelative("enrollment1"))
        relatives.addTrackedEntityInstance(teiRelative("tei1"))
        givenEventDownloadReturns("event1", event("event1"))
        whenever(enrollmentEndpointCallFactory.getRelationshipEntityCall(enrollmentRelative("enrollment1")))
            .doReturn(enrollment("enrollment1"))
        whenever(teiEndpointCallFactory.getRelationshipEntityCall(teiRelative("tei1")))
            .doReturn(payloadOf(trackedEntityInstance("tei1")))

        factory.downloadAndPersist(relatives)

        verify(relationshipStore, never()).getRelationshipsByItem(any<RelationshipItem>())
        verify(relationshipStore, never()).deleteIfExists(any())
    }

    // endregion

    private suspend fun givenEventDownloadReturns(itemUid: String, vararg events: Event) {
        whenever(eventEndpointCallFactory.getRelationshipEntityCall(eventRelative(itemUid)))
            .doReturn(payloadOf(*events))
    }

    private fun eventRelative(uid: String) = relative(uid, Columns.EVENT)

    private fun enrollmentRelative(uid: String) = relative(uid, Columns.ENROLLMENT)

    private fun teiRelative(uid: String) = relative(uid, Columns.TRACKED_ENTITY_INSTANCE)

    private fun relative(uid: String, itemType: String) = RelationshipItemRelative(
        itemUid = uid,
        itemType = itemType,
        relationshipTypeUid = "relationshipType1",
        constraintType = RelationshipConstraintType.FROM,
    )

    private fun eventItem(uid: String) = RelationshipItem.builder()
        .event(RelationshipItemEvent.builder().event(uid).build())
        .build()

    private fun enrollmentItem(uid: String) = RelationshipItem.builder()
        .enrollment(RelationshipItemEnrollment.builder().enrollment(uid).build())
        .build()

    private fun teiItem(uid: String) = RelationshipItem.builder()
        .trackedEntityInstance(
            RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(uid).build(),
        )
        .build()

    private fun event(uid: String, program: String? = null, enrollment: String? = null): Event =
        Event.builder().uid(uid).program(program).enrollment(enrollment).build()

    private fun enrollment(uid: String, trackedEntityInstance: String? = null): Enrollment =
        Enrollment.builder()
            .uid(uid)
            .attributeOptionCombo("attributeOptionCombo")
            .trackedEntityInstance(trackedEntityInstance)
            .build()

    private fun trackedEntityInstance(uid: String): TrackedEntityInstance =
        TrackedEntityInstance.builder().uid(uid).build()

    private fun trackerProgram(uid: String): Program = program(uid, ProgramType.WITH_REGISTRATION)

    private fun program(uid: String, programType: ProgramType): Program =
        Program.builder()
            .uid(uid)
            .programType(programType)
            .categoryCombo(ObjectWithUid.create("categoryCombo"))
            .enrollmentCategoryCombo(ObjectWithUid.create("categoryCombo"))
            .build()

    private fun <T> payloadOf(vararg items: T): Payload<T> = PayloadJson(itemList = items.toList())
}
