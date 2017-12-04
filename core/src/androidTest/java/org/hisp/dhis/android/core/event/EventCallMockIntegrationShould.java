package org.hisp.dhis.android.core.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@RunWith(AndroidJUnit4.class)
public class EventCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        deleteDatabase();
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse(dhis2MockServer.getBaseEndpoint()))
                .build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        d2 = new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter())
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter()))
                                .addInterceptor(loggingInterceptor)
                                .build()
                ).build();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void download_number_of_events_according_to_default_limit() throws Exception {
        givenAMetadataInDatabase();

        EventCall eventCall = givenADefaultEventCall();

        dhis2MockServer.enqueueMockResponse("events_1.json");

        eventCall.call();

        verifyDownloadedEvents("events_1.json");
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        d2.syncMetaData().call();
    }

    private void verifyDownloadedEvents(String file) throws IOException {
        String expectedEventsResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper();

        Payload<Event> expectedEventsResponse =
                objectMapper.readValue(expectedEventsResponseJson,
                        new TypeReference<Payload<Event>>() {
                        });

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(expectedEventsResponse.items().size()));
        assertThat(downloadedEvents, is(expectedEventsResponse.items()));
    }

    private EventCall givenADefaultEventCall() {
        //TODO - create a internal factory of dependencies separate of d2
        // for avoid to expose retrofit to the outside of library
        // and for avoid create instances duplication on every test
        EventService eventService = d2.retrofit().create(EventService.class);

        EventStore eventStore = new EventStoreImpl(databaseAdapter());
        EventHandler eventHandler = new EventHandler(eventStore);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        EventCall eventCall = new EventCall(eventService, databaseAdapter(), resourceHandler,
                eventHandler,
                new Date(), EventQuery.Builder.create().build());

        return eventCall;
    }
}
