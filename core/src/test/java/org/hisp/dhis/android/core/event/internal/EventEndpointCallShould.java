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

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.api.testutils.RetrofitFactory;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer;
import org.hisp.dhis.android.core.event.Event;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventEndpointCallShould {

    private static Retrofit retrofit;

    private static Dhis2MockServer mockWebServer;

    @Mock
    protected DatabaseAdapter databaseAdapter;

    @BeforeClass
    public static void setUpClass() throws IOException {
        mockWebServer = new Dhis2MockServer(0);
        retrofit = RetrofitFactory.fromDHIS2MockServer(mockWebServer);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void realize_request_with_page_filters_when_included_in_query() throws Exception {
        Callable<List<Event>> eventEndpointCall = givenAEventCallByPagination(2, 32);

        mockWebServer.enqueueMockResponse();

        eventEndpointCall.call();

        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getPath(), containsString("paging=true&page=2&pageSize=32"));
    }

    @Test
    public void realize_request_with_orgUnit_program_filters_when_included_in_query() throws Exception {
        Callable<List<Event>> eventEndpointCall = givenAEventCallByOrgUnitAndProgram("OU", "P");

        mockWebServer.enqueueMockResponse();

        eventEndpointCall.call();

        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getPath(), containsString("orgUnit=OU"));
        assertThat(request.getPath(), containsString("program=P"));
    }

    private Callable<List<Event>> givenAEventCallByPagination(int page, int pageCount) {
        EventQuery eventQuery = EventQuery.builder()
                .page(page)
                .pageSize(pageCount)
                .paging(true)
                .build();

        return new EventEndpointCallFactory(retrofit.create(EventService.class), APICallExecutorImpl.create(databaseAdapter)).getCall(eventQuery);
    }

    private Callable<List<Event>> givenAEventCallByOrgUnitAndProgram(String orgUnit, String program) {
        EventQuery eventQuery = EventQuery.builder()
                .orgUnit(orgUnit)
                .program(program)
                .build();

        return new EventEndpointCallFactory(retrofit.create(EventService.class), APICallExecutorImpl.create(databaseAdapter)).getCall(eventQuery);
    }
}
