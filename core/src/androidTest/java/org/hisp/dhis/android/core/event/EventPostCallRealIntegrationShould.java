package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.imports.WebResponse;
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

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventPostCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
    Exception e;
    CodeGenerator codeGenerator;


    private EventStore eventStore;
    private TrackedEntityDataValueStore trackedEntityDataValueStore;

    private String eventUid;
    private String orgUnitUid;
    private String programUid;
    private String programStageUid;
    private String dataElementUid;
    private String attributeCategoryOption;
    private String attributeOptionCombo;
    private String categoryComboUID;
    private String user = "admin";
    private String password = "district";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        eventStore = new EventStoreImpl(databaseAdapter());
        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl(databaseAdapter());

        orgUnitUid = "ImspTQPwCqd";
        programUid = "kla3mAPgvCH";
        programStageUid = "aNLq9ZYoy9W";
        dataElementUid = "b6dOUjAarHD";
        attributeCategoryOption = "C6nZpLKjEJr";
        attributeOptionCombo = "nvLjum6Xbv5";
        categoryComboUID = "nM3u9s5a52V";
        codeGenerator = new CodeGeneratorImpl();

        eventUid = codeGenerator.generate();

    }

    @Test
    public void stub() throws Exception {

    }
    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void successful_response_after_sync_events() throws Exception {
        dowloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo, null);

        Call<Response<WebResponse>> call = d2.syncSingleEvents();
        Response<WebResponse> response = call.call();
        assertThat(response.isSuccessful()).isTrue();


    }

    // commented out since it is a flaky test that works against a real server.
    //@Test
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {
        retrofit2.Response response = null;

        dowloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo, null);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeDB().call();

        dowloadMetadata();

        downloadEventsBy(categoryComboUID,attributeCategoryOption);

        assertThatEventPushedIsDownloaded(pushedEvent);
    }

    private void createDummyDataToPost(String orgUnitUid, String programUid,
            String programStageUid, String eventUid,
            String dataElementUid, String attributeCategoryOption, String attributeOptionCombo, String trackedEntityInstance) {
        eventStore.insert(
                eventUid, null, new Date(), new Date(), null, null,
                EventStatus.ACTIVE, "13.21", "12.21", programUid, programStageUid, orgUnitUid,
                new Date(), new Date(), new Date(), State.TO_POST, attributeCategoryOption, attributeOptionCombo, trackedEntityInstance
        );

        trackedEntityDataValueStore.insert(
                eventUid, new Date(), new Date(), dataElementUid, "user_name", "12", Boolean.FALSE
        );
    }

    private void assertThatEventPushedIsDownloaded(Event pushedEvent) {
        eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertTrue(verifyPushedEventIsInPullList(pushedEvent, downloadedEvents));
    }

    private void downloadEventsBy(String categoryComboUID,String categoryOptionUID) throws Exception {
        EventEndpointCall eventEndpointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), orgUnitUid, 0,categoryComboUID, categoryOptionUID);

        List<Event> events = eventEndpointCall.call();

        assertThat(events.isEmpty()).isFalse();
    }

    private Event getEventFromDB() {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());
        Event event = null;
        List<Event> storedEvents = eventStore.queryAll();
        for(Event storedEvent : storedEvents) {
            if(storedEvent.uid().equals(eventUid)) {
                event = storedEvent;
            }
        }
        return event;
    }

    private void pushDummyEvent() throws Exception {
        Response response;Call<Response<WebResponse>> call = d2.syncSingleEvents();
        response = call.call();
        assertThat(response.isSuccessful()).isTrue();
    }

    private void dowloadMetadata() throws Exception {
        d2.logIn(user, password).call();
        d2.syncMetaData().call();
    }

    private boolean verifyPushedEventIsInPullList(Event event, List<Event> eventList) {
        for(Event pullEvent : eventList){
            if(event.uid().equals(pullEvent.uid()) && event.attributeOptionCombo().equals(pullEvent.attributeOptionCombo()) && event.attributeCategoryOptions().equals(pullEvent.attributeCategoryOptions())){
                return true;
            }
        }
        return false;
    }
}
