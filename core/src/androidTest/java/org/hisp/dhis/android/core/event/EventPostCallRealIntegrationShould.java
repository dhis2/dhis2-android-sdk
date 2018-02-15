package org.hisp.dhis.android.core.event;

import static com.google.common.truth.Truth.assertThat;

import android.support.test.filters.LargeTest;
import static junit.framework.Assert.assertTrue;

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

@RunWith(AndroidJUnit4.class)
public class EventPostCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;
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
        CodeGenerator codeGenerator = new CodeGeneratorImpl();

        eventUid = codeGenerator.generate();

    }

    @Test
    @LargeTest
    public void successful_response_after_sync_events() throws Exception {
        retrofit2.Response response;
        response = d2.logIn(user, password).call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo, null);

        Call<Response<WebResponse>> call = d2.syncSingleEvents();
        response = call.call();
        assertThat(response.isSuccessful()).isTrue();


    }

    @Test
    @LargeTest
    public void pull_event_with_correct_category_combo_after_be_pushed() throws Exception {

        downloadMetadata();

        createDummyDataToPost(orgUnitUid, programUid, programStageUid, eventUid, dataElementUid, attributeCategoryOption, attributeOptionCombo, null);

        pushDummyEvent();

        Event pushedEvent = getEventFromDB();

        d2.wipeDB().call();

        downloadMetadata();

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
        Response response;

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), orgUnitUid, 0,categoryComboUID, categoryOptionUID);

        response = eventEndPointCall.call();

        assertThat(response.isSuccessful()).isTrue();
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

    private void downloadMetadata() throws Exception {
        Response response;
        response = d2.logIn(user, password).call();
        assertThat(response.isSuccessful()).isTrue();

        response = d2.syncMetaData().call();
        assertThat(response.isSuccessful()).isTrue();
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
