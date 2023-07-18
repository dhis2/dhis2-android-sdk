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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import java.util.*
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.junit.Before

class EventPostCallRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var eventStore: EventStore
    private lateinit var trackedEntityDataValueStore: TrackedEntityDataValueStore
    private lateinit var eventUid1: String
    private lateinit var eventUid2: String

    private var orgUnitUid: String? = null
    private var programUid: String? = null
    private var programStageUid: String? = null
    private var dataElementUid: String? = null
    private var attributeOptionCombo: String? = null

    @Before
    override fun setUp() {
        super.setUp()
        eventStore = EventStoreImpl(d2.databaseAdapter())
        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl(d2.databaseAdapter())
        val uidGenerator: UidGenerator = UidGeneratorImpl()
        eventUid1 = uidGenerator.generate()
        eventUid2 = uidGenerator.generate()
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun successful_response_after_sync_events() {
        downloadMetadata()
        createDummyDataToPost(eventUid1)
        d2.eventModule().events().blockingUpload()
    }

    // @Test
    @Throws(Exception::class)
    fun create_event_with_repository() {
        downloadMetadata()
        val eventUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.builder()
                .organisationUnit(orgUnitUid)
                .program(programUid)
                .programStage(programStageUid)
                .attributeOptionCombo(attributeOptionCombo)
                .build()
        )
        val repo = d2.eventModule().events().uid(eventUid)
        repo.setEventDate(Date())
        d2.eventModule().events().blockingUpload()
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun pull_event_with_correct_category_combo_after_be_pushed() {
        downloadMetadata()
        createDummyDataToPost(eventUid1)
        pushDummyEvent()
        val pushedEvent = eventFromDB
        d2.wipeModule().wipeEverything()
        downloadMetadata()
        downloadEvents()
        assertThatEventPushedIsDownloaded(pushedEvent)
    }

    // @Test
    @Throws(Exception::class)
    fun pull_events_delete_with_repository_and_post() {
        downloadMetadata()
        d2.eventModule().eventDownloader().limit(10).blockingDownload()
        val uid = d2.eventModule().events().one().blockingGet()!!.uid()
        d2.eventModule().events().uid(uid).blockingDelete()
        d2.eventModule().events().blockingUpload()
    }

    // commented out since it is a flaky test that works against a real server.
    // @Test
    @Throws(Exception::class)
    fun pull_two_events_with_correct_category_combo_after_be_pushed() {
        downloadMetadata()
        createDummyDataToPost(eventUid1)
        createDummyDataToPost(eventUid2)
        pushDummyEvent()
        val pushedEvent = eventFromDB
        d2.wipeModule().wipeEverything()
        downloadMetadata()
        downloadEvents()
        assertThatEventPushedIsDownloaded(pushedEvent)
    }

    private fun createDummyDataToPost(eventUid: String?) {
        eventStore.insert(
            Event.builder().uid(eventUid).created(Date()).lastUpdated(Date())
                .status(EventStatus.ACTIVE).program(programUid)
                .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(Date())
                .completedDate(Date()).dueDate(Date()).syncState(State.TO_POST)
                .attributeOptionCombo(attributeOptionCombo).build()
        )
        val trackedEntityDataValue = TrackedEntityDataValue.builder()
            .event(eventUid)
            .created(Date())
            .lastUpdated(Date())
            .dataElement(dataElementUid)
            .storedBy("user_name")
            .value("12")
            .providedElsewhere(java.lang.Boolean.FALSE)
            .build()
        trackedEntityDataValueStore.insert(trackedEntityDataValue)
    }

    private fun assertThatEventPushedIsDownloaded(pushedEvent: Event?) {
        val downloadedEvents = eventStore.querySingleEvents()
        Truth.assertThat(verifyPushedEventIsInPullList(pushedEvent, downloadedEvents)).isTrue()
    }

    @Throws(Exception::class)
    private fun downloadEvents() {
        val eventEndpointCall = EventCallFactory.create(
            d2.retrofit(), orgUnitUid, 50, emptyList()
        )
        val events = eventEndpointCall.blockingGet().items()
        for (event in events) {
            eventStore.insert(event)
        }
        assertThat(events.isEmpty()).isFalse()
    }

    private val eventFromDB: Event?
        private get() {
            var event: Event? = null
            val storedEvents = eventStore.selectAll()
            for (storedEvent in storedEvents) {
                if (storedEvent.uid() == eventUid1) {
                    event = storedEvent
                }
            }
            return event
        }

    private fun pushDummyEvent() {
        d2.eventModule().events().blockingUpload()
    }

    private fun downloadMetadata() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        orgUnitUid = d2.organisationUnitModule().organisationUnits()
            .one().blockingGet()!!
            .uid()
        programUid = d2.programModule().programs()
            .byOrganisationUnitUid(orgUnitUid)
            .byProgramType().eq(ProgramType.WITHOUT_REGISTRATION)
            .one().blockingGet()!!
            .uid()

        // Before running, make sure no data elements are compulsory
        programStageUid = d2.programModule().programStages()
            .byProgramUid().eq(programUid)
            .one().blockingGet()!!
            .uid()
        dataElementUid = d2.programModule().programStageDataElements()
            .byProgramStage().eq(programStageUid)
            .one().blockingGet()!!.dataElement()
            ?.uid()
        attributeOptionCombo = d2.categoryModule().categoryOptionCombos()
            .one().blockingGet()!!
            .uid()
    }

    private fun verifyPushedEventIsInPullList(event: Event?, eventList: List<Event>): Boolean {
        for (pullEvent in eventList) {
            if (event!!.uid() == pullEvent.uid() && event.attributeOptionCombo() == pullEvent.attributeOptionCombo()) {
                return true
            }
        }
        return false
    }
}
