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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EventEndpointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void download_events_according_to_default_query() throws Exception {
        givenAMetadataInDatabase();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        assertThat(d2.eventModule().events.count(), is(1));
    }

    @Test
    public void download_number_of_events_according_to_page_size() throws Exception {
        givenAMetadataInDatabase();

        int pageSize = 1;

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize);

        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        assertThat(d2.eventModule().events.count(), is(pageSize));
    }

    @Test
    public void rollback_transaction_when_insert_a_event_with_wrong_foreign_key()
            throws Exception {
        givenAMetadataInDatabase();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse(
                "event/two_events_first_good_second_wrong_foreign_key.json");

        eventEndpointCall.call();

        assertThat(d2.eventModule().events.count(), is(0));
        assertThat(d2.trackedEntityModule().trackedEntityDataValues.count(), is(0));
    }

    @Test
    public void not_overwrite_events_marked_as_to_post_to_update_or_error() throws Exception {
        givenAMetadataInDatabase();

        int pageSize = 1;

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize);
        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();
        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        Event event = events.get(0);
        assertThat(event.uid(), is("V1CerIi3sdL"));
        assertThat(d2.eventModule().events.count(), is(pageSize));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.SYNCED).status(EventStatus.SKIPPED).build());

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        Event event1 = d2.eventModule().events.one().get();
        assertThat(event1.uid(), is("V1CerIi3sdL"));
        assertThat(event1.status(), is(EventStatus.COMPLETED)); // Because in Synced state should overwrite.

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.TO_UPDATE).status(EventStatus.SKIPPED).build());

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        Event event2 = d2.eventModule().events.one().get();
        assertThat(event2.uid(), is("V1CerIi3sdL"));
        assertThat(event2.status(), is(EventStatus.SKIPPED));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.ERROR).status(EventStatus.SKIPPED).build());

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        Event event3 = d2.eventModule().events.one().get();
        assertThat(event3.uid(), is("V1CerIi3sdL"));
        assertThat(event3.status(), is(EventStatus.SKIPPED));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.TO_POST).status(EventStatus.SKIPPED).build());

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        Event event4 = d2.eventModule().events.one().get();
        assertThat(event4.uid(), is("V1CerIi3sdL"));
        assertThat(event4.status(), is(EventStatus.SKIPPED));
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }
}