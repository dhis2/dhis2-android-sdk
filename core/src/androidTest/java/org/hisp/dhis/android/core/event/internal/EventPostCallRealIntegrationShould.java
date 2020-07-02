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

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.arch.helpers.UidGenerator;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.assertTrue;

public class EventPostCallRealIntegrationShould extends BaseRealIntegrationTest {

    private D2 d2;
    private UidGenerator uidGenerator;


    private EventStore eventStore;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    private String eventUid1;
    private String eventUid2;
    private String orgUnitUid;
    private String programUid;
    private String programStageUid;
    private String dataElementUid;
    private String attributeOptionCombo;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.forNewDatabase();

        eventStore = EventStoreImpl.create(d2.databaseAdapter());
        trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter());

        uidGenerator = new UidGeneratorImpl();

        eventUid1 = uidGenerator.generate();
        eventUid2 = uidGenerator.generate();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void successful_response_after_sync_events() throws Exception {
        downloadMetadata();

        createDummyDataToPost(eventUid1);

        d2.eventModule().events().blockingUpload();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {
        downloadMetadata();

        createDummyDataToPost(eventUid1);

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

        createDummyDataToPost(eventUid1);

        createDummyDataToPost(eventUid2);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeModule().wipeEverything();

        downloadMetadata();

        downloadEvents();

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    private void createDummyDataToPost(String eventUid) {
        eventStore.insert(Event.builder().uid(eventUid).created(new Date()).lastUpdated(new Date())
                .geometry(Geometry.builder().type(FeatureType.POINT).coordinates("[12.21, 13.21]").build())
                .status(EventStatus.ACTIVE).program(programUid)
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

    private void pushDummyEvent() {
        d2.eventModule().events().blockingUpload();
    }

    private void downloadMetadata() {
        d2.userModule().logIn(username, password, url).blockingGet();
        d2.metadataModule().blockingDownload();

        orgUnitUid = d2.organisationUnitModule().organisationUnits().one().blockingGet().uid();
        ProgramStage programStage = d2.programModule().programStages().one().blockingGet();
        programStageUid = programStage.uid();
        programUid = programStage.program().uid();
        dataElementUid = d2.dataElementModule().dataElements().one().blockingGet().uid();
        attributeOptionCombo = d2.categoryModule().categoryOptionCombos().one().blockingGet().uid();
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