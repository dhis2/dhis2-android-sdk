/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import com.google.common.collect.Lists
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidsList
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteCreateProjection
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore.create
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStoreImpl
import org.hisp.dhis.android.core.relationship.internal.RelationshipStoreImpl
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityInstancePostPayloadGeneratorMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    private val teiId = "teiId"
    private val enrollment1Id = "enrollment1Id"
    private val enrollment2Id = "enrollment2Id"
    private val enrollment3Id = "enrollment3Id"
    private val event1Id = "event1Id"
    private val event2Id = "event2Id"
    private val event3Id = "event3Id"

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun build_payload_with_different_enrollments() {
        storeTrackedEntityInstance()

        val partitions = partitions

        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions.first().size).isEqualTo(1)

        for (instance in partitions.first()) {
            assertThat(getEnrollments(instance).size).isEqualTo(2)
            for (enrollment in getEnrollments(instance)) {
                assertThat(getEvents(enrollment).size).isEqualTo(1)
                for (event in getEvents(enrollment)) {
                    assertThat(event.trackedEntityDataValues()!!.size).isEqualTo(1)
                }
            }
        }
    }

    private val partitions: List<List<TrackedEntityInstance>>
        get() = payloadGenerator.getTrackedEntityInstancesPartitions(
            teiStore.queryTrackedEntityInstancesToSync()
        )

    @Test
    fun build_payload_with_the_enrollments_events_and_values_set_for_upload() {
        storeTrackedEntityInstance()

        val partitions = partitions

        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions.first().size).isEqualTo(1)

        for (instance in partitions.first()) {
            assertThat(getEnrollments(instance).size).isEqualTo(2)
            for (enrollment in getEnrollments(instance)) {
                assertThat(getEvents(enrollment).size).isEqualTo(1)
                for (event in getEvents(enrollment)) {
                    assertThat(event.trackedEntityDataValues()!!.size).isEqualTo(1)
                }
            }
        }
    }

    @Test
    fun build_payload_without_events_marked_as_error() {
        storeTrackedEntityInstance()

        enrollmentStore.setState(enrollment3Id, State.TO_POST)

        val partitions = partitions
        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions.first().size).isEqualTo(1)

        for (instance in partitions.first()) {
            assertThat(getEnrollments(instance).size).isEqualTo(3)
            for (enrollment in getEnrollments(instance)) {
                if (enrollment.uid() == enrollment3Id) {
                    assertThat(getEvents(enrollment).size).isEqualTo(0)
                } else {
                    assertThat(getEvents(enrollment).size).isEqualTo(1)
                }
            }
        }
    }

    @Test
    fun handle_import_conflicts_correctly() {
        storeTrackedEntityInstance()

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)
    }

    @Test
    fun delete_old_import_conflicts() {
        storeTrackedEntityInstance()

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)

        teiStore.setState("teiId", State.TO_POST)
        enrollmentStore.setState("enrollment1Id", State.TO_POST)
        enrollmentStore.setState("enrollment2Id", State.TO_POST)
        eventStore.setState("event1Id", State.TO_POST)
        eventStore.setState("event2Id", State.TO_POST)

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_3.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(1)
    }

    @Test
    @Throws(D2Error::class)
    fun handle_tei_deletions() {
        storeTrackedEntityInstance()

        d2.trackedEntityModule().trackedEntityInstances().uid("teiId").blockingDelete()

        // There is no TEIs to upload, so there is no request to enqueue.
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        assertThat(d2.trackedEntityModule().trackedEntityInstances().blockingCount()).isEqualTo(0)
        assertThat(d2.enrollmentModule().enrollments().blockingCount()).isEqualTo(0)
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(0)
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun recreate_teis_with_filters_and_relationships() {
        val tei1 = "tei1"
        val tei2 = "tei2"
        val tei3 = "tei3"
        val tei4 = "tei4"
        val tei5 = "tei5"

        storeSimpleTrackedEntityInstance(tei1, State.TO_POST)
        storeSimpleTrackedEntityInstance(tei2, State.TO_POST)
        storeSimpleTrackedEntityInstance(tei3, State.TO_POST)
        storeSimpleTrackedEntityInstance(tei4, State.TO_POST)
        storeSimpleTrackedEntityInstance(tei5, State.SYNCED)

        storeRelationship("relationship1", tei1, tei2)
        storeRelationship("relationship2", tei2, tei3)
        storeRelationship("relationship3", tei1, tei5)
        storeRelationship("relationship4", tei5, tei4)

        val partitions = payloadGenerator.getTrackedEntityInstancesPartitions(
            d2.trackedEntityModule().trackedEntityInstances().byUid().eq(tei1)
                .byState().`in`(*State.uploadableStates()).blockingGet()
        )

        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions[0].size).isEqualTo(3)
        assertThat(getUidsList(partitions[0]).containsAll(Lists.newArrayList(tei1, tei2, tei3))).isTrue()
    }

    @Test
    fun mark_payload_as_uploading() {
        storeTrackedEntityInstance()

        // Ignore result. Just interested in check that target TEIs are marked as UPLOADING
        val partitions = partitions

        val instance = teiStore.selectFirst()
        assertThat(instance!!.state()).isEqualTo(State.UPLOADING)

        val enrollments = enrollmentStore.selectAll()
        for (enrollment in enrollments) {
            if ("enrollment1Id" == enrollment.uid() || "enrollment2Id" == enrollment.uid()) {
                assertThat(enrollment.state()).isEqualTo(State.UPLOADING)
            } else {
                assertThat(enrollment.state()).isNotEqualTo(State.UPLOADING)
            }
        }

        val events = eventStore.selectAll()
        for (event in events) {
            if (event1Id == event.uid() || event2Id == event.uid()) {
                assertThat(event.state()).isEqualTo(State.UPLOADING)
            } else {
                assertThat(event.state()).isNotEqualTo(State.UPLOADING)
            }
        }
    }

    @Test
    fun restore_payload_states_when_error_500() {
        storeTrackedEntityInstance()

        dhis2MockServer.enqueueMockResponse(500, "Internal Server Error")
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val instance = teiStore.selectFirst()
        assertThat(instance!!.state()).isEqualTo(State.TO_POST)

        val enrollments = enrollmentStore.selectAll()
        for (enrollment in enrollments) {
            if (enrollment1Id == enrollment.uid() || enrollment2Id == enrollment.uid()) {
                assertThat(enrollment.state()).isEqualTo(State.TO_POST)
            }
        }

        val events = eventStore.selectAll()
        for (event in events) {
            if (event1Id == event.uid()) {
                assertThat(event.state()).isEqualTo(State.TO_UPDATE)
            }
            if (event2Id == event.uid()) {
                assertThat(event.state()).isEqualTo(State.SYNCED_VIA_SMS)
            }
        }
    }

    @Test
    @Throws(D2Error::class)
    fun build_payload_with_enrollment_notes() {
        storeTrackedEntityInstance()
        d2.noteModule().notes().blockingAdd(
            NoteCreateProjection.builder()
                .enrollment(enrollment1Id)
                .noteType(Note.NoteType.ENROLLMENT_NOTE)
                .value("This is an enrollment note")
                .build()
        )
        val partitions = partitions
        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions.first().size).isEqualTo(1)
        for (instance in partitions.first()) {
            for (enrollment in getEnrollments(instance)) {
                if (enrollment.uid() == enrollment1Id) {
                    assertThat(enrollment.notes()!!.size).isEqualTo(1)
                } else {
                    assertThat(enrollment.notes()!!.size).isEqualTo(0)
                }
            }
        }
    }

    @Test
    @Throws(D2Error::class)
    fun build_payload_with_event_notes() {
        storeTrackedEntityInstance()

        d2.noteModule().notes().blockingAdd(
            NoteCreateProjection.builder()
                .event(event1Id)
                .noteType(Note.NoteType.EVENT_NOTE)
                .value("This is an event note")
                .build()
        )
        val partitions = partitions

        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions.first().size).isEqualTo(1)
        for (instance in partitions.first()) {
            for (enrollment in getEnrollments(instance)) {
                if (enrollment.uid() == enrollment1Id) {
                    for (event in getEvents(enrollment)) {
                        if (event.uid() == event1Id) {
                            assertThat(event.notes()!!.size).isEqualTo(1)
                        } else {
                            assertThat(enrollment.notes()!!.size).isEqualTo(0)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun do_not_ignore_elements_not_present_in_the_import_summary() {
        storeTrackedEntityInstance()

        // Only enrollment1 and event1 are TO_UPDATE
        enrollmentStore.setState(enrollment2Id, State.SYNCED)
        eventStore.setState(event2Id, State.SYNCED)

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_empty_events.json")
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val trackedEntityInstance = teiStore.selectByUid(teiId)
        assertThat(trackedEntityInstance!!.state()).isEqualTo(State.TO_UPDATE)

        assertThat(enrollmentStore.selectByUid(enrollment1Id)!!.state()).isEqualTo(State.TO_UPDATE)
        assertThat(enrollmentStore.selectByUid(enrollment2Id)!!.state()).isEqualTo(State.SYNCED)

        assertThat(eventStore.selectByUid(event1Id)!!.state()).isEqualTo(State.TO_UPDATE)
        assertThat(eventStore.selectByUid(event2Id)!!.state()).isEqualTo(State.SYNCED)
    }

    private fun storeTrackedEntityInstance() {
        val orgUnit = create(databaseAdapter).selectFirst()
        val teiType = TrackedEntityTypeStore.create(databaseAdapter).selectFirst()
        val program = d2.programModule().programs().one().blockingGet()
        val programStage = ProgramStageStore.create(databaseAdapter).selectFirst()

        val dataValue1 = TrackedEntityDataValueSamples.get().toBuilder().event(event1Id).build()

        val event1 = Event.builder()
            .uid(event1Id)
            .enrollment(enrollment1Id)
            .organisationUnit(orgUnit!!.uid())
            .program(program.uid())
            .programStage(programStage!!.uid())
            .state(State.TO_UPDATE)
            .trackedEntityDataValues(listOf(dataValue1))
            .build()

        val enrollment1 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event1))
            .uid(enrollment1Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .state(State.TO_POST)
            .trackedEntityInstance(teiId)
            .build()
        val dataValue2 = TrackedEntityDataValueSamples.get().toBuilder().event(event2Id).build()

        val event2 = Event.builder()
            .uid(event2Id)
            .enrollment(enrollment2Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .state(State.SYNCED_VIA_SMS)
            .trackedEntityDataValues(listOf(dataValue2))
            .build()

        val enrollment2 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event2))
            .uid(enrollment2Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .state(State.TO_POST)
            .trackedEntityInstance(teiId)
            .build()

        val dataValue3 = TrackedEntityDataValueSamples.get().toBuilder().event(event3Id).build()

        val event3 = Event.builder()
            .uid(event3Id)
            .enrollment(enrollment3Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .state(State.ERROR)
            .trackedEntityDataValues(listOf(dataValue3))
            .build()

        val enrollment3 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), listOf(event3))
            .uid(enrollment3Id)
            .program(program.uid())
            .organisationUnit(orgUnit.uid())
            .state(State.SYNCED)
            .trackedEntityInstance(teiId)
            .build()

        val tei = TrackedEntityInstanceInternalAccessor.insertEnrollments(
            TrackedEntityInstance.builder(), listOf(enrollment1, enrollment2, enrollment3)
        )
            .uid(teiId)
            .trackedEntityType(teiType!!.uid())
            .organisationUnit(orgUnit.uid())
            .state(State.TO_POST)
            .build()

        teiStore.insert(tei)
        enrollmentStore.insert(enrollment1)
        enrollmentStore.insert(enrollment2)
        enrollmentStore.insert(enrollment3)
        eventStore.insert(event1)
        eventStore.insert(event2)
        eventStore.insert(event3)
        teiDataValueStore.insert(dataValue1)
        teiDataValueStore.insert(dataValue2)
        teiDataValueStore.insert(dataValue3)
    }

    private fun storeSimpleTrackedEntityInstance(teiUid: String, state: State) {
        val orgUnit = create(databaseAdapter).selectFirst()
        val teiType = TrackedEntityTypeStore.create(databaseAdapter).selectFirst()
        TrackedEntityInstanceStoreImpl.create(databaseAdapter).insert(
            TrackedEntityInstanceSamples.get().toBuilder()
                .uid(teiUid)
                .trackedEntityType(teiType!!.uid())
                .organisationUnit(orgUnit!!.uid())
                .state(state)
                .build()
        )
    }

    @Throws(D2Error::class)
    private fun storeRelationship(relationshipUid: String, fromUid: String, toUid: String) {
        val relationshipType = RelationshipTypeStore.create(databaseAdapter).selectFirst()
        val executor = D2CallExecutor.create(databaseAdapter)
        executor.executeD2CallTransactionally<Any?> {
            RelationshipStoreImpl.create(databaseAdapter).insert(
                RelationshipSamples.get230(relationshipUid, fromUid, toUid).toBuilder()
                    .relationshipType(relationshipType!!.uid()).build()
            )
            RelationshipItemStoreImpl.create(databaseAdapter).insert(
                RelationshipItem.builder()
                    .relationship(ObjectWithUid.create(relationshipUid))
                    .relationshipItemType(RelationshipConstraintType.FROM)
                    .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(fromUid).build()
                    )
                    .build()
            )
            RelationshipItemStoreImpl.create(databaseAdapter).insert(
                RelationshipItem.builder()
                    .relationship(ObjectWithUid.create(relationshipUid))
                    .relationshipItemType(RelationshipConstraintType.TO)
                    .trackedEntityInstance(
                        RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(toUid).build()
                    )
                    .build()
            )
            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors()
            null
        }
    }

    private fun getEnrollments(trackedEntityInstance: TrackedEntityInstance): List<Enrollment> {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
    }

    private fun getEvents(enrollment: Enrollment): List<Event> {
        return EnrollmentInternalAccessor.accessEvents(enrollment)
    }

    companion object {
        private lateinit var payloadGenerator: TrackedEntityInstancePostPayloadGenerator
        private lateinit var teiStore: TrackedEntityInstanceStore
        private lateinit var teiDataValueStore: TrackedEntityDataValueStore
        private lateinit var eventStore: EventStore
        private lateinit var enrollmentStore: EnrollmentStore

        @BeforeClass
        @JvmStatic
        @Throws(Exception::class)
        fun setUp() {
            BaseMockIntegrationTestMetadataEnqueable.setUpClass()
            payloadGenerator = objects.d2DIComponent.trackedEntityInstancePostPayloadGenerator()
            teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
            teiDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter)
            eventStore = EventStoreImpl.create(databaseAdapter)
            enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter)
        }
    }
}
