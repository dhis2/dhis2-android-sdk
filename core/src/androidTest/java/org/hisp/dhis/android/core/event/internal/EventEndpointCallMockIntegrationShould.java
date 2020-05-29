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

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(D2JunitRunner.class)
public class EventEndpointCallMockIntegrationShould extends BaseMockIntegrationTestMetadataEnqueable {

    @After
    public void tearDown() throws D2Error {
        d2.wipeModule().wipeData();
    }

    @Test
    public void download_events_according_to_default_query() throws Exception {
        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        assertThat(d2.eventModule().events().blockingCount(), is(1));
    }

    @Test
    public void download_number_of_events_according_to_page_size() throws Exception {
        int pageSize = 1;

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize);

        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        assertThat(d2.eventModule().events().blockingCount(), is(pageSize));
    }

    @Test
    public void rollback_transaction_when_insert_a_event_with_wrong_foreign_key() throws Exception {
        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse(
                "event/two_events_first_good_second_wrong_foreign_key.json");

        eventEndpointCall.call();

        assertThat(d2.eventModule().events().blockingCount(), is(0));
        assertThat(d2.trackedEntityModule().trackedEntityDataValues().blockingCount(), is(0));
    }

    @Test
    public void not_overwrite_events_marked_as_to_post_to_update_or_error() throws Exception {
        int pageSize = 1;

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize);
        dhis2MockServer.enqueueMockResponse("event/events_1.json");

        List<Event> events = eventEndpointCall.call();
        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        Event event = events.get(0);
        assertThat(event.uid(), is("V1CerIi3sdL"));
        assertThat(d2.eventModule().events().blockingCount(), is(pageSize));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.SYNCED).status(EventStatus.SKIPPED).build());

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        Event event1 = d2.eventModule().events().one().blockingGet();
        assertThat(event1.uid(), is("V1CerIi3sdL"));
        assertThat(event1.status(), is(EventStatus.COMPLETED)); // Because in Synced state should overwrite.

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.TO_UPDATE).status(EventStatus.SKIPPED).build());

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        Event event2 = d2.eventModule().events().one().blockingGet();
        assertThat(event2.uid(), is("V1CerIi3sdL"));
        assertThat(event2.status(), is(EventStatus.SKIPPED));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.ERROR).status(EventStatus.SKIPPED).build());

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        Event event3 = d2.eventModule().events().one().blockingGet();
        assertThat(event3.uid(), is("V1CerIi3sdL"));
        assertThat(event3.status(), is(EventStatus.SKIPPED));

        EventStoreImpl.create(d2.databaseAdapter()).update(event.toBuilder()
                .state(State.TO_POST).status(EventStatus.SKIPPED).build());

        ((EventModuleImpl) d2.eventModule()).eventPersistenceCallFactory.persistEvents(events).blockingGet();

        Event event4 = d2.eventModule().events().one().blockingGet();
        assertThat(event4.uid(), is("V1CerIi3sdL"));
        assertThat(event4.status(), is(EventStatus.SKIPPED));
    }
}