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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidsList
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStoreImpl.Companion.create
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteCreateProjection
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class EventPostPayloadGeneratorMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {
    private val event1Id = "event1Id"
    private val event2Id = "event2Id"
    private val event3Id = "event3Id"
    private val event4Id = "event4Id"

    @After
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun build_payload_with_different_enrollments() {
        storeEvents()
        val events = payloadGenerator.getEvents(eventStore.querySingleEventsToPost())
        assertThat(events.size).isEqualTo(4)
        for (event in events) {
            assertThat(event.trackedEntityDataValues()!!.size).isEqualTo(1)
        }
    }

    @Test
    fun handle_import_conflicts_correctly() {
        storeEvents()
        dhis2MockServer.enqueueMockResponse("imports/web_response_with_event_import_conflicts.json")
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)
    }

    @Test
    fun delete_old_import_conflicts() {
        storeEvents()
        dhis2MockServer.enqueueMockResponse("imports/web_response_with_event_import_conflicts.json")
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3)

        eventStore.setSyncState(event1Id, State.TO_POST)
        eventStore.setSyncState(event2Id, State.TO_POST)
        eventStore.setSyncState(event3Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event1Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event2Id, State.TO_POST)
        eventStore.setAggregatedSyncState(event3Id, State.TO_POST)

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_event_import_conflicts2.json")
        d2.eventModule().events().blockingUpload()
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(2)
    }

    @Test
    fun handle_event_deletions() {
        storeEvents()
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(4)
        d2.eventModule().events().uid("event1Id").blockingDelete()

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_event_import_conflicts2.json")
        d2.eventModule().events().blockingUpload()

        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(3)
    }

    @Test
    fun recreate_events_with_filters() {
        val event1 = "event1"
        val event2 = "event2"
        val event3 = "event3"
        val event4 = "event4"
        val program = d2.programModule().programs().one().blockingGet()

        storeSingleEvent(event1, program, State.TO_POST, false)
        storeSingleEvent(event2, program, State.TO_UPDATE, false)
        storeSingleEvent(event3, program, State.TO_UPDATE, true)
        storeSingleEvent(event4, program, State.SYNCED, false)

        val events = payloadGenerator.getEvents(
            d2.eventModule().events().byProgramUid().eq(program.uid())
                .bySyncState().`in`(*uploadableStatesIncludingError()).blockingGet()
        )

        assertThat(events.size).isEqualTo(3)
        assertThat(getUidsList(events).containsAll(listOf(event1, event2, event3))).isTrue()
    }

    @Test
    fun build_payload_with_event_notes() {
        storeEvents()
        d2.noteModule().notes().blockingAdd(
            NoteCreateProjection.builder()
                .event(event1Id)
                .noteType(Note.NoteType.EVENT_NOTE)
                .value("This is an event note")
                .build()
        )
        val events = payloadGenerator.getEvents(eventStore.querySingleEventsToPost())
        for (event in events) {
            if (event1Id == event.uid()) {
                assertThat(event.notes()!!.size).isEqualTo(1)
            } else {
                assertThat(event.notes()!!.size).isEqualTo(0)
            }
        }
    }

    private fun storeEvents() {
        val orgUnit = d2.organisationUnitModule().organisationUnits().one().blockingGet()
        val program = d2.programModule().programs().one().blockingGet()
        val programStage = d2.programModule().programStages().one().blockingGet()
        val dataValue1 = TrackedEntityDataValueSamples.get().toBuilder().event(event1Id).build()

        val event1 = Event.builder()
            .uid(event1Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue1))
            .build()

        val dataValue2 = TrackedEntityDataValueSamples.get().toBuilder().event(event2Id).build()

        val event2 = Event.builder()
            .uid(event2Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue2))
            .build()

        val dataValue3 = TrackedEntityDataValueSamples.get().toBuilder().event(event3Id).build()

        val event3 = Event.builder()
            .uid(event3Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.TO_POST)
            .aggregatedSyncState(State.TO_POST)
            .trackedEntityDataValues(listOf(dataValue3))
            .build()

        val dataValue4 = TrackedEntityDataValueSamples.get().toBuilder().event(event4Id).build()

        val event4 = Event.builder()
            .uid(event4Id)
            .organisationUnit(orgUnit.uid())
            .program(program.uid())
            .programStage(programStage.uid())
            .syncState(State.ERROR)
            .aggregatedSyncState(State.ERROR)
            .trackedEntityDataValues(listOf(dataValue4))
            .build()

        eventStore.insert(event1)
        eventStore.insert(event2)
        eventStore.insert(event3)
        eventStore.insert(event4)

        val tedvStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter)
        tedvStore.insert(dataValue1)
        tedvStore.insert(dataValue2)
        tedvStore.insert(dataValue3)
        tedvStore.insert(dataValue4)

        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(4)
    }

    private fun storeSingleEvent(eventUid: String, program: Program, state: State, deleted: Boolean) {
        val orgUnit = d2.organisationUnitModule().organisationUnits().one().blockingGet()
        val programStage = d2.programModule().programStages().one().blockingGet()
        eventStore.insert(
            Event.builder()
                .uid(eventUid)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .syncState(state)
                .aggregatedSyncState(state)
                .deleted(deleted)
                .build()
        )
    }

    companion object {
        private lateinit var payloadGenerator: EventPostPayloadGenerator
        private lateinit var eventStore: EventStore

        @BeforeClass
        @JvmStatic
        @Throws(Exception::class)
        fun setUpClass() {
            BaseMockIntegrationTestMetadataEnqueable.setUpClass()
            eventStore = create(
                objects.databaseAdapter
            )
            payloadGenerator = objects.d2DIComponent.eventPostPayloadGenerator()
        }
    }
}
