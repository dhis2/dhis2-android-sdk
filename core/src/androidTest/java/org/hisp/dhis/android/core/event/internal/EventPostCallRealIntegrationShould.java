/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.d2manager.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.assertTrue;

public class EventPostCallRealIntegrationShould extends BaseRealIntegrationTest {

    private D2 d2;
    private CodeGenerator codeGenerator;


    private EventStore eventStore;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    private String eventUid1;
    private String eventUid2;
    private String orgUnitUid;
    private String programUid;
    private String programStageUid;
    private String dataElementUid;
    private String attributeOptionCombo;
    private String user = "admin";
    private String password = "district";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        eventStore = EventStoreImpl.create(databaseAdapter());
        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter());

        orgUnitUid = "ImspTQPwCqd";
        programUid = "kla3mAPgvCH";
        programStageUid = "aNLq9ZYoy9W";
        dataElementUid = "b6dOUjAarHD";
        attributeOptionCombo = "nvLjum6Xbv5";
        codeGenerator = new CodeGeneratorImpl();

        eventUid1 = codeGenerator.generate();
        eventUid2 = codeGenerator.generate();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void successful_response_after_sync_events() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1,
                dataElementUid, attributeOptionCombo);

        d2.eventModule().events.upload().call();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1, dataElementUid,
                attributeOptionCombo);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeModule().wipeEverything();

        downloadMetadata();

        downloadEvents();

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_two_events_with_correct_category_combo_after_be_pushed() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1, dataElementUid,
                attributeOptionCombo);

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid2, dataElementUid,
                attributeOptionCombo);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeModule().wipeEverything();

        downloadMetadata();

        downloadEvents();

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid,
            String programStageUid, String eventUid,
            String dataElementUid, String attributeOptionCombo) {

        eventStore.insert(Event.builder().uid(eventUid).created(new Date()).lastUpdated(new Date())
                .status(EventStatus.ACTIVE).coordinate(Coordinates.create(13.21, 12.21)).program(programUid)
                .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(new Date())
                .completedDate(new Date()).dueDate(new Date()).state(State.TO_POST)
                .attributeOptionCombo(attributeOptionCombo).build());

        TrackedEntityDataValue trackedEntityDataValue = TrackedEntityDataValue.builder()
                .event(eventUid)
                .created(new Date())
                .lastUpdated(new Date())
                .dataElement(dataElementUid)
                .storedBy("user_name")
                .value("12")
                .providedElsewhere(Boolean.FALSE)
                .build();

        trackedEntityDataValueStore.insert(trackedEntityDataValue);
    }

    private void assertThatEventPushedIsDownloaded(Event pushedEvent) {
        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertTrue(verifyPushedEventIsInPullList(pushedEvent, downloadedEvents));
    }

    private void downloadEvents() throws Exception {
        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(
                d2.retrofit(), d2.databaseAdapter(), orgUnitUid, 50);

        List<Event> events = eventEndpointCall.call();

        for (Event event : events) {
            eventStore.insert(event);
        }

        assertThat(events.isEmpty()).isFalse();
    }

    private Event getEventFromDB() {
        Event event = null;
        List<Event> storedEvents = eventStore.selectAll();
        for(Event storedEvent : storedEvents) {
            if(storedEvent.uid().equals(eventUid1)) {
                event = storedEvent;
            }
        }
        return event;
    }

    private void pushDummyEvent() throws Exception {
        d2.eventModule().events.upload().call();
    }

    private void downloadMetadata() throws Exception {
        d2.userModule().logIn(user, password).blockingGet();
        d2.syncMetaData().blockingSubscribe();
    }

    private boolean verifyPushedEventIsInPullList(Event event, List<Event> eventList) {
        for(Event pullEvent : eventList){
            if (event.uid().equals(pullEvent.uid()) &&
                    event.attributeOptionCombo().equals(pullEvent.attributeOptionCombo())){
                return true;
            }
        }
        return false;
    }
}