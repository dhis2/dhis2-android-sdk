package org.hisp.dhis.android.core.event;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class EventCallShould {
    private Call<Response<Payload<Event>>> eventCall;

    @Mock
    private EventService eventService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private EventHandler eventHandler;

    @Mock
    private Date serverDate;

    Dhis2MockServer dhis2MockServer;
    Retrofit retrofit;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uids_size_exceeds_the_limit() {
        EventCall eventCall = givenAEventCallByUIds(MAX_UIDS + 1);
    }

    @Test
    public void create_event_call_if_uids_size_does_not_exceeds_the_limit() {
        EventCall eventCall = givenAEventCallByUIds(MAX_UIDS);
        assertThat(eventCall, is(notNullValue()));
    }

    @Test
    public void realize_request_with_page_filters_when_included_in_query()
            throws Exception {
        EventCall eventCall = givenAEventCallByPagination(2, 32);

        dhis2MockServer.enqueueMockResponse();

        eventCall.call();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("paging=true&page=2&pageSize=32"));
    }

    @Test
    public void realize_request_with_orgUnit_program_filters_when_included_in_query()
            throws Exception {
        EventCall eventCall = givenAEventCallByOrgUnitAndProgram("OU", "P");

        dhis2MockServer.enqueueMockResponse();

        eventCall.call();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("orgUnit=OU&program=P"));
    }

    private EventCall givenAEventCallByUIds(int numUIds) {
        Set<String> uIds = new HashSet<>();

        for (int i = 0; i < numUIds; i++) {
            uIds.add("uid" + i);
        }

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withUIds(uIds)
                .build();

        EventCall eventCall =
                new EventCall(eventService, databaseAdapter, resourceHandler,
                        eventHandler, serverDate, eventQuery);

        return eventCall;
    }

    private EventCall givenAEventCallByPagination(int page, int pageCount) {
        EventService eventService = retrofit.create(EventService.class);

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withPage(page)
                .withPageSize(pageCount)
                .withPaging(true)
                .build();


        EventCall eventCall =
                new EventCall(eventService, databaseAdapter, resourceHandler,
                        eventHandler, serverDate, eventQuery);

        return eventCall;
    }

    private EventCall givenAEventCallByOrgUnitAndProgram(String orgUnit, String program) {
        EventService eventService = retrofit.create(EventService.class);

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withProgram(program)
                .build();


        EventCall eventCall =
                new EventCall(eventService, databaseAdapter, resourceHandler,
                        eventHandler, serverDate, eventQuery);

        return eventCall;
    }
}
