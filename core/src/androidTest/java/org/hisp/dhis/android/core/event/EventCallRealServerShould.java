package org.hisp.dhis.android.core.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class EventCallRealServerShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        deleteDatabase();
        super.setUp();


        ConfigurationModel config = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse(RealServerMother.url))
                .build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

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

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    //@Test
    public void download_number_of_events_according_to_default_limit() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        EventCall eventCall = givenAEventCallByOrgUnit("DiszpKrYNg8");
        eventCall.call();

        verifyDownloadedEvents(50);
    }


    private void verifyDownloadedEvents(int numEvents) {
        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(numEvents));
    }

    private EventCall givenAEventCallByOrgUnit(String orgUnit) {
        //TODO - create a factory of dependencies separate of d2
        // for avoid to expose retrofit to the outside of library
        // and for avoid create instances duplication on every test
        EventService eventService = d2.retrofit().create(EventService.class);

        EventStore eventStore = new EventStoreImpl(databaseAdapter());
        EventHandler eventHandler = new EventHandler(eventStore);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        EventCall eventCall = new EventCall(eventService, databaseAdapter(), resourceHandler,
                eventHandler,
                new Date(), EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .build());

        return eventCall;
    }
}
