package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventPostCallRealIntegrationShould extends AbsStoreTestCase {

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
    private String categoryComboUID;
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
        categoryComboUID = "nM3u9s5a52V";
        codeGenerator = new CodeGeneratorImpl();

        eventUid1 = codeGenerator.generate();
        eventUid2 = codeGenerator.generate();

    }

    @Test
    public void stub() throws Exception {

    }
    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void successful_response_after_sync_events() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1,
                dataElementUid, attributeOptionCombo, null);

        d2.eventModule().events.upload().call();
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1, dataElementUid,
                attributeOptionCombo, null);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeModule().wipeEverything();

        downloadMetadata();

        downloadEventsBy(categoryComboUID);

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_two_events_with_correct_category_combo_after_be_pushed() throws Exception {
        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid1, dataElementUid,
                attributeOptionCombo, null);

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid2, dataElementUid,
                attributeOptionCombo, null);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeModule().wipeEverything();

        downloadMetadata();

        downloadEventsBy(categoryComboUID);

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid,
            String programStageUid, String eventUid,
            String dataElementUid, String attributeOptionCombo, String trackedEntityInstance) {

        eventStore.insert(Event.builder().uid(eventUid).created(new Date()).lastUpdated(new Date())
                .status(EventStatus.ACTIVE).coordinate(Coordinates.create(13.21, 12.21)).program(programUid)
                .programStage(programStageUid).organisationUnit(orgUnitUid).eventDate(new Date())
                .completedDate(new Date()).dueDate(new Date()).state(State.TO_POST)
                .attributeOptionCombo(attributeOptionCombo).trackedEntityInstance(trackedEntityInstance).build());

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

    private void downloadEventsBy(String categoryComboUID) throws Exception {
        EventEndpointCall eventEndpointCall = EventCallFactory.create(
                d2.retrofit(), d2.databaseAdapter(), orgUnitUid, 50, categoryComboUID);

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
        d2.userModule().logIn(user, password).call();
        d2.syncMetaData().call();
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