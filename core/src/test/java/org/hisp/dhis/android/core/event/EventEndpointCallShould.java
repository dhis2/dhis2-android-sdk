package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.BaseCallShould;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.mockwebserver.RecordedRequest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

public class EventEndpointCallShould extends BaseCallShould {

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uids_size_exceeds_the_limit() {
        EventEndpointCall eventEndpointCall = givenAEventCallByUIds(MAX_UIDS + 1);
    }

    @Test
    public void create_event_call_if_uids_size_does_not_exceeds_the_limit() {
        EventEndpointCall eventEndpointCall = givenAEventCallByUIds(MAX_UIDS);
        assertThat(eventEndpointCall, is(notNullValue()));
    }

    @Test
    public void realize_request_with_page_filters_when_included_in_query()
            throws Exception {
        EventEndpointCall eventEndpointCall = givenAEventCallByPagination(2, 32);

        dhis2MockServer.enqueueMockResponse();

        eventEndpointCall.call();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("paging=true&page=2&pageSize=32"));
    }

    @Test
    public void realize_request_with_orgUnit_program_filters_when_included_in_query()
            throws Exception {
        EventEndpointCall eventEndpointCall = givenAEventCallByOrgUnitAndProgram("OU", "P");

        dhis2MockServer.enqueueMockResponse();

        eventEndpointCall.call();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("orgUnit=OU&program=P"));
    }

    private EventEndpointCall givenAEventCallByUIds(int numUIds) {
        Set<String> uIds = new HashSet<>();

        for (int i = 0; i < numUIds; i++) {
            uIds.add("uid" + i);
        }

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withUIds(uIds)
                .build();

        return EventEndpointCall.create(genericCallData.retrofit(), eventQuery);
    }

    private EventEndpointCall givenAEventCallByPagination(int page, int pageCount) {
        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withPage(page)
                .withPageSize(pageCount)
                .withPaging(true)
                .build();

        return EventEndpointCall.create(genericCallData.retrofit(), eventQuery);
    }

    private EventEndpointCall givenAEventCallByOrgUnitAndProgram(String orgUnit, String program) {
        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit(orgUnit)
                .withProgram(program)
                .build();

        return EventEndpointCall.create(genericCallData.retrofit(), eventQuery);
    }
}
