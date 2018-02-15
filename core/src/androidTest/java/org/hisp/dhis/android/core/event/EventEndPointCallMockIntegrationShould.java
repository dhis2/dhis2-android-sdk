package org.hisp.dhis.android.core.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.EventCallFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.responses.BasicMetadataMockResponseList;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class EventEndPointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void download_events_according_to_default_query() throws Exception {
        givenAMetadataInDatabase();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("events_1.json");

        eventEndPointCall.call();

        verifyDownloadedEvents("events_1.json");
    }

    @Test
    @MediumTest
    public void download_number_of_events_according_to_page_limit() throws Exception {
        givenAMetadataInDatabase();

        int pageLimit = 12;

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", pageLimit);

        dhis2MockServer.enqueueMockResponse("events_1.json");

        eventEndPointCall.call();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(pageLimit));
    }

    @Test
    @MediumTest
    public void remove_data_values_removed_in_server_after_second_events_download()
            throws Exception {
        givenAMetadataInDatabase();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event_1_with_all_data_values.json");

        eventEndPointCall.call();

        eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event_1_with_only_one_data_values.json");

        eventEndPointCall.call();

        verifyDownloadedEvents("event_1_with_only_one_data_values.json");
    }

    @Test
    @MediumTest
    public void rollback_transaction_when_insert_a_event_with_wrong_foreign_key()
            throws Exception {
        givenAMetadataInDatabase();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse(
                "two_events_first_good_second_wrong_foreign_key.json");

        eventEndPointCall.call();

        verifyNumberOfDownloadedEvents(1);
        verifyNumberOfDownloadedTrackedEntityDataValue(6);
        verifyDownloadedEvents("event_1_with_all_data_values.json");
    }

    @Test
    @MediumTest
    public void remove_event_removed_in_server_after_second_events_download()
            throws Exception {
        givenAMetadataInDatabase();

        EventEndPointCall eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event_1_with_all_data_values.json");

        eventEndPointCall.call();

        eventEndPointCall = EventCallFactory.create(
                d2.retrofit(), databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event_1_with_delete_true.json");

        eventEndPointCall.call();

        verifyDownloadedEventsIsZero();
    }

    private void verifyDownloadedEventsIsZero() {

        List<Event> downloadedEvents = getDownloadedEvents();

        assertThat(downloadedEvents.size(), is(0));
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        Response response = d2.syncMetaData().call();
    }

    private void verifyNumberOfDownloadedEvents(int numEvents) {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(numEvents));
    }

    private void verifyNumberOfDownloadedTrackedEntityDataValue(int num) {
        TrackedEntityDataValueStoreImpl eventStore = new TrackedEntityDataValueStoreImpl(
                d2.databaseAdapter());

        int numPersisted = eventStore.countAll();

        assertThat(numPersisted, is(num));
    }

    private void verifyDownloadedEvents(String file) throws IOException {
        Payload<Event> expectedEventsResponse = parseEventResponse(file);

        List<Event> downloadedEvents = getDownloadedEvents();

        assertThat(downloadedEvents.size(), is(expectedEventsResponse.items().size()));
        assertThat(downloadedEvents, is(expectedEventsResponse.items()));
    }

    private List<Event> getDownloadedEvents() {
        List<Event> downloadedEvents = new ArrayList<>();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEventsWithoutValues = eventStore.querySingleEvents();

        TrackedEntityDataValueStoreImpl trackedEntityDataValue =
                new TrackedEntityDataValueStoreImpl(databaseAdapter());

        Map<String, List<TrackedEntityDataValue>> downloadedValues =
                trackedEntityDataValue.queryTrackedEntityDataValues();


        for (Event event : downloadedEventsWithoutValues) {
            event = event.toBuilder()
                    .trackedEntityDataValues(downloadedValues.get(event.uid())).build();

            downloadedEvents.add(event);
        }

        return downloadedEvents;
    }

    private Payload<Event> parseEventResponse(String file) throws IOException {
        String expectedEventsResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedEventsResponseJson,
                new TypeReference<Payload<Event>>() {
                });
    }
}
