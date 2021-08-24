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
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidsList
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteCreateProjection
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityInstancePostPayloadGenerator29MockIntegrationShould : BasePayloadGeneratorMockIntegration() {

    @Test
    fun build_payload_with_different_enrollments() {
        storeTrackerData()

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
        get() = payloadGenerator29.getTrackedEntityInstancesPartitions29(
            teiStore.queryTrackedEntityInstancesToSync()
        )

    @Test
    fun build_payload_without_events_marked_as_error() {
        storeTrackerData()

        enrollmentStore.setAggregatedSyncState(enrollment3Id, State.TO_POST)

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
        storeTrackerData()

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)
    }

    @Test
    fun delete_old_import_conflicts() {
        storeTrackerData()

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)

        teiStore.setAggregatedSyncState("teiId", State.TO_POST)
        enrollmentStore.setAggregatedSyncState("enrollment1Id", State.TO_POST)
        enrollmentStore.setAggregatedSyncState("enrollment2Id", State.TO_POST)
        eventStore.setSyncStateOrDelete("event1Id", State.TO_POST)
        eventStore.setSyncStateOrDelete("event2Id", State.TO_POST)

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_3.json")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(1)
    }

    @Test
    @Throws(D2Error::class)
    fun handle_tei_deletions() {
        storeTrackerData()

        d2.trackedEntityModule().trackedEntityInstances().uid("teiId").blockingDelete()

        // There is no TEIs to upload, so there is no request to enqueue.
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        assertThat(d2.trackedEntityModule().trackedEntityInstances().blockingCount()).isEqualTo(0)
        assertThat(d2.enrollmentModule().enrollments().blockingCount()).isEqualTo(0)
        assertThat(d2.eventModule().events().byEnrollmentUid().isNotNull.blockingCount()).isEqualTo(0)
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

        val partitions = payloadGenerator29.getTrackedEntityInstancesPartitions29(
            d2.trackedEntityModule().trackedEntityInstances().byUid().eq(tei1)
                .byAggregatedSyncState().`in`(*State.uploadableStates()).blockingGet()
        )

        assertThat(partitions.size).isEqualTo(1)
        assertThat(partitions[0].size).isEqualTo(3)
        assertThat(getUidsList(partitions[0]).containsAll(Lists.newArrayList(tei1, tei2, tei3))).isTrue()
    }

    @Test
    fun restore_payload_states_when_error_500() {
        storeTrackerData()

        dhis2MockServer.enqueueMockResponse(500, "Internal Server Error")
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val instance = teiStore.selectFirst()
        assertThat(instance!!.syncState()).isEqualTo(State.TO_POST)

        val enrollments = enrollmentStore.selectAll()
        for (enrollment in enrollments) {
            if (enrollment1Id == enrollment.uid() || enrollment2Id == enrollment.uid()) {
                assertThat(enrollment.syncState()).isEqualTo(State.TO_POST)
            }
        }

        val events = eventStore.selectAll()
        for (event in events) {
            if (event1Id == event.uid()) {
                assertThat(event.syncState()).isEqualTo(State.TO_UPDATE)
            }
            if (event2Id == event.uid()) {
                assertThat(event.syncState()).isEqualTo(State.SYNCED_VIA_SMS)
            }
        }
    }

    @Test
    @Throws(D2Error::class)
    fun build_payload_with_enrollment_notes() {
        storeTrackerData()
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
        storeTrackerData()

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
        storeTrackerData()

        // Only enrollment1 and event1 are TO_UPDATE
        enrollmentStore.setAggregatedSyncState(enrollment2Id, State.SYNCED)
        enrollmentStore.setSyncState(enrollment2Id, State.SYNCED)
        eventStore.setAggregatedSyncState(event2Id, State.SYNCED)
        eventStore.setSyncState(event2Id, State.SYNCED)

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_empty_events.json")
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val trackedEntityInstance = teiStore.selectByUid(teiId)
        assertThat(trackedEntityInstance!!.syncState()).isEqualTo(State.SYNCED)
        assertThat(trackedEntityInstance.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

        assertThat(enrollmentStore.selectByUid(enrollment1Id)!!.syncState()).isEqualTo(State.SYNCED)
        assertThat(enrollmentStore.selectByUid(enrollment1Id)!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)
        assertThat(enrollmentStore.selectByUid(enrollment2Id)!!.syncState()).isEqualTo(State.SYNCED)
        assertThat(enrollmentStore.selectByUid(enrollment2Id)!!.aggregatedSyncState()).isEqualTo(State.SYNCED)

        assertThat(eventStore.selectByUid(event1Id)!!.syncState()).isEqualTo(State.TO_UPDATE)
        assertThat(eventStore.selectByUid(event2Id)!!.syncState()).isEqualTo(State.SYNCED)
    }
}
