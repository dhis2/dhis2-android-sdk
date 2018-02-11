package org.hisp.dhis.android.core.trackedentity;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.mockito.Mockito.when;


import android.support.annotation.NonNull;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.data.server.RetrofitFactory;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

public class TrackedEntityInstanceEndPointCallShould {
    @Mock
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private Transaction transaction;


    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    @Mock
    private Date serverDate;

    private Dhis2MockServer dhis2MockServer;

    private Retrofit retrofit;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        retrofit = RetrofitFactory.build(dhis2MockServer.getBaseEndpoint());
        MockitoAnnotations.initMocks(this);
        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uid_is_null() {
        givenATrackedEntityInstanceEndPointCall(null);
    }

    @Test
    public void create_call_if_uid_is_not_null() {
        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                givenATrackedEntityInstanceEndPointCall("PgmUFEQYZdt");
        assertThat(trackedEntityInstanceEndPointCall, is(notNullValue()));
    }

    @Test
    public void append_translation_variables_to_the_query_string()
            throws Exception {

        whenCallTrackedEntityInstanceEndpoindWithMockWebservice();

        thenAssertTranslationParametersAreInclude();
    }

    private void thenAssertTranslationParametersAreInclude() throws InterruptedException {
        RecordedRequest request = dhis2MockServer.takeRequest();

        MatcherAssert.assertThat(request.getPath(), containsString(
                "translation=" + DEFAULT_IS_TRANSLATION_ON + "&locale="
                        + DEFAULT_TRANSLATION_LOCALE));
    }

    private void whenCallTrackedEntityInstanceEndpoindWithMockWebservice() throws Exception {

        TrackedEntityInstanceEndPointCall callWithMockWebservice =
                provideTrackedEntityInstanceEndPointCall();


        dhis2MockServer.enqueueMockResponse("tracked_entity_instance.json");
        callWithMockWebservice.call();
    }

    @NonNull
    private TrackedEntityInstanceEndPointCall provideTrackedEntityInstanceEndPointCall() {

        TrackedEntityInstanceService mockService = retrofit.create(TrackedEntityInstanceService
                .class);

        return new TrackedEntityInstanceEndPointCall(
                mockService, databaseAdapter, trackedEntityInstanceHandler,
                resourceHandler, serverDate, "trackedEntityInstanceUid",
                DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE);
    }

    private TrackedEntityInstanceEndPointCall givenATrackedEntityInstanceEndPointCall(
            String trackedEntityInstanceUid) {
        return new TrackedEntityInstanceEndPointCall(
                trackedEntityInstanceService, databaseAdapter, trackedEntityInstanceHandler,
                resourceHandler, serverDate, trackedEntityInstanceUid,
                DEFAULT_IS_TRANSLATION_ON, DEFAULT_TRANSLATION_LOCALE);
    }
}
