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
package org.hisp.dhis.android.core.trackedentity.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipSamples
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.util.Date

@RunWith(JUnit4::class)
class NewTrackerImporterTrackedEntityPostStateManagerShould {

    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()
    private val enrollmentStore: EnrollmentStore = mock()
    private val eventStore: EventStore = mock()
    private val relationshipStore: RelationshipStore = mock()
    private val fileResourceStore: FileResourceStore = mock()

    private lateinit var stateManager: NewTrackerImporterTrackedEntityPostStateManager

    @Before
    fun setUp() {
        stateManager = NewTrackerImporterTrackedEntityPostStateManager(
            trackedEntityInstanceStore,
            enrollmentStore,
            eventStore,
            relationshipStore,
            fileResourceStore,
            StatePersistorHelper(),
        )
    }

    @Test
    fun not_touch_stores_when_payload_is_empty() = runTest {
        stateManager.setStates(NewTrackerImporterPayload(), State.UPLOADING)

        verifyNoInteractions(
            trackedEntityInstanceStore,
            enrollmentStore,
            eventStore,
            relationshipStore,
            fileResourceStore,
        )
    }

    @Test
    fun set_forced_state_to_every_object_in_payload_including_nested_ones() = runTest {
        val nestedEvent = event("nestedEvent")
        val nestedEnrollment = enrollment("nestedEnrollment", events = listOf(nestedEvent))
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(trackedEntity("tei1", enrollments = listOf(nestedEnrollment))),
            enrollments = mutableListOf(enrollment("enrollment1", events = listOf(event("enrollmentEvent")))),
            events = mutableListOf(event("event1")),
            relationships = mutableListOf(relationship("relationship1")),
        )

        stateManager.setStates(payload, State.UPLOADING)

        verify(trackedEntityInstanceStore).setSyncState(listOf("tei1"), State.UPLOADING)
        verify(enrollmentStore).setSyncState(listOf("nestedEnrollment", "enrollment1"), State.UPLOADING)
        verify(eventStore).setSyncState(listOf("nestedEvent", "enrollmentEvent", "event1"), State.UPLOADING)
        verify(relationshipStore).setSyncState(listOf("relationship1"), State.UPLOADING)
        verifyNoInteractions(fileResourceStore)
    }

    @Test
    fun ignore_null_nested_lists_in_payload() = runTest {
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(trackedEntity("tei1", enrollments = null)),
            enrollments = mutableListOf(enrollment("enrollment1", events = null)),
        )

        stateManager.setStates(payload, State.UPLOADING)

        verify(trackedEntityInstanceStore).setSyncState(listOf("tei1"), State.UPLOADING)
        verify(enrollmentStore).setSyncState(listOf("enrollment1"), State.UPLOADING)
        verifyNoInteractions(eventStore, relationshipStore)
    }

    @Test
    fun restore_payload_states_grouping_objects_by_their_own_state() = runTest {
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(
                trackedEntity("tei1", State.TO_POST),
                trackedEntity("tei2", State.TO_UPDATE),
                trackedEntity("tei3", State.TO_POST),
            ),
            events = mutableListOf(event("event1", State.ERROR)),
        )

        stateManager.restoreStates(payload)

        verify(trackedEntityInstanceStore).setSyncState(listOf("tei1", "tei3"), State.TO_POST)
        verify(trackedEntityInstanceStore).setSyncState(listOf("tei2"), State.TO_UPDATE)
        verify(eventStore).setSyncState(listOf("event1"), State.ERROR)
    }

    @Test
    fun restore_uploading_objects_in_payload_as_to_update() = runTest {
        val payload = NewTrackerImporterPayload(
            enrollments = mutableListOf(enrollment("enrollment1", syncState = State.UPLOADING)),
            relationships = mutableListOf(relationship("relationship1", State.UPLOADING)),
        )

        stateManager.restoreStates(payload)

        verify(enrollmentStore).setSyncState(listOf("enrollment1"), State.TO_UPDATE)
        verify(relationshipStore).setSyncState(listOf("relationship1"), State.TO_UPDATE)
        verify(enrollmentStore, never()).setSyncState(any<List<String>>(), eq(State.UPLOADING))
    }

    @Test
    fun skip_payload_objects_without_sync_state_when_restoring() = runTest {
        val payload = NewTrackerImporterPayload(
            trackedEntities = mutableListOf(trackedEntity("tei1", syncState = null)),
        )

        stateManager.restoreStates(payload)

        verifyNoInteractions(trackedEntityInstanceStore)
    }

    @Test
    fun set_forced_state_to_every_job_object_type() = runTest {
        givenStoredObjects()

        stateManager.setStates(
            listOf(
                jobObject(TrackerImporterObjectType.TRACKED_ENTITY, "tei1"),
                jobObject(TrackerImporterObjectType.ENROLLMENT, "enrollment1"),
                jobObject(TrackerImporterObjectType.EVENT, "event1", fileResources = listOf("fileResource1")),
                jobObject(TrackerImporterObjectType.RELATIONSHIP, "relationship1"),
            ),
            State.UPLOADING,
        )

        verify(trackedEntityInstanceStore).setSyncState(listOf("tei1"), State.UPLOADING)
        verify(enrollmentStore).setSyncState(listOf("enrollment1"), State.UPLOADING)
        verify(eventStore).setSyncState(listOf("event1"), State.UPLOADING)
        verify(relationshipStore).setSyncState(listOf("relationship1"), State.UPLOADING)
        verify(fileResourceStore).setSyncState(listOf("fileResource1"), State.UPLOADING)
    }

    @Test
    fun restore_job_objects_using_their_stored_state() = runTest {
        givenStoredObjects()

        stateManager.restoreStates(
            listOf(
                jobObject(TrackerImporterObjectType.TRACKED_ENTITY, "tei1"),
                jobObject(TrackerImporterObjectType.ENROLLMENT, "enrollment1"),
                jobObject(TrackerImporterObjectType.EVENT, "event1", fileResources = listOf("fileResource1")),
                jobObject(TrackerImporterObjectType.RELATIONSHIP, "relationship1"),
            ),
        )

        verify(trackedEntityInstanceStore).setSyncState(listOf("tei1"), State.TO_UPDATE)
        verify(enrollmentStore).setSyncState(listOf("enrollment1"), State.TO_POST)
        verify(eventStore).setSyncState(listOf("event1"), State.ERROR)
        verify(relationshipStore).setSyncState(listOf("relationship1"), State.SYNCED)
        verify(fileResourceStore).setSyncState(listOf("fileResource1"), State.TO_POST)
    }

    @Test
    fun skip_job_objects_not_found_in_the_database() = runTest {
        stateManager.setStates(
            listOf(
                jobObject(TrackerImporterObjectType.TRACKED_ENTITY, "tei1"),
                jobObject(TrackerImporterObjectType.ENROLLMENT, "enrollment1"),
                jobObject(TrackerImporterObjectType.EVENT, "event1", fileResources = listOf("fileResource1")),
                jobObject(TrackerImporterObjectType.RELATIONSHIP, "relationship1"),
            ),
            State.UPLOADING,
        )

        verify(trackedEntityInstanceStore).selectByUid("tei1")
        verify(enrollmentStore).selectByUid("enrollment1")
        verify(eventStore).selectByUid("event1")
        verify(relationshipStore).selectByUid("relationship1")
        verify(fileResourceStore).selectByUid("fileResource1")

        verify(trackedEntityInstanceStore, never()).setSyncState(any<List<String>>(), any())
        verify(enrollmentStore, never()).setSyncState(any<List<String>>(), any())
        verify(eventStore, never()).setSyncState(any<List<String>>(), any())
        verify(relationshipStore, never()).setSyncState(any<List<String>>(), any())
        verify(fileResourceStore, never()).setSyncState(any<List<String>>(), any())
    }

    @Test
    fun not_touch_stores_when_job_object_list_is_empty() = runTest {
        stateManager.setStates(emptyList(), State.UPLOADING)

        verifyNoInteractions(
            trackedEntityInstanceStore,
            enrollmentStore,
            eventStore,
            relationshipStore,
            fileResourceStore,
        )
    }

    private suspend fun givenStoredObjects() {
        whenever(trackedEntityInstanceStore.selectByUid("tei1")).doReturn(
            TrackedEntityInstance.builder().uid("tei1").syncState(State.UPLOADING).build(),
        )
        whenever(enrollmentStore.selectByUid("enrollment1")).doReturn(
            Enrollment.builder().uid("enrollment1").attributeOptionCombo("aoc").syncState(State.TO_POST).build(),
        )
        whenever(eventStore.selectByUid("event1")).doReturn(
            Event.builder().uid("event1").syncState(State.ERROR).build(),
        )
        whenever(relationshipStore.selectByUid("relationship1")).doReturn(
            RelationshipSamples.get230("relationship1", "from", "to"),
        )
        whenever(fileResourceStore.selectByUid("fileResource1")).doReturn(
            FileResource.builder().uid("fileResource1").syncState(State.TO_POST).build(),
        )
    }

    private fun jobObject(
        type: TrackerImporterObjectType,
        uid: String,
        fileResources: List<String> = emptyList(),
    ) = TrackerJobObject(
        trackerType = type,
        objectUid = uid,
        jobUid = "job",
        lastUpdated = Date(),
        fileResources = fileResources,
    )

    private fun trackedEntity(
        uid: String,
        syncState: State? = State.TO_POST,
        enrollments: List<NewTrackerImporterEnrollment>? = emptyList(),
    ) = NewTrackerImporterTrackedEntity(
        uid = uid,
        deleted = false,
        syncState = syncState,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        organisationUnit = null,
        trackedEntityType = null,
        geometry = null,
        aggregatedSyncState = syncState,
        enrollments = enrollments,
    )

    private fun enrollment(
        uid: String,
        syncState: State? = State.TO_POST,
        events: List<NewTrackerImporterEvent>? = emptyList(),
    ) = NewTrackerImporterEnrollment(
        uid = uid,
        deleted = false,
        syncState = syncState,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        organisationUnit = null,
        program = null,
        enrolledAt = null,
        occurredAt = null,
        completedAt = null,
        followUp = null,
        status = null,
        trackedEntity = null,
        geometry = null,
        attributeOptionCombo = null,
        aggregatedSyncState = syncState,
        events = events,
    )

    private fun event(uid: String, syncState: State? = State.TO_POST) = NewTrackerImporterEvent(
        uid = uid,
        deleted = false,
        syncState = syncState,
        enrollment = null,
        createdAt = null,
        updatedAt = null,
        createdAtClient = null,
        updatedAtClient = null,
        program = null,
        programStage = null,
        organisationUnit = null,
        occurredAt = null,
        status = null,
        geometry = null,
        completedAt = null,
        completedBy = null,
        scheduledAt = null,
        attributeOptionCombo = null,
        assignedUser = null,
        aggregatedSyncState = syncState,
    )

    private fun relationship(uid: String, syncState: State? = State.TO_POST) = NewTrackerImporterRelationship(
        uid = uid,
        deleted = false,
        syncState = syncState,
        relationshipType = null,
        relationshipName = null,
        createdAt = null,
        updatedAt = null,
        bidirectional = null,
        from = null,
        to = null,
    )
}
