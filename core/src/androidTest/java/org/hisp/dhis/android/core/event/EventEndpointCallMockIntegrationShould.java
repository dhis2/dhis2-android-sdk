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

import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EventEndpointCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private EventStore eventStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
        eventStore = EventStoreImpl.create(databaseAdapter());
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

        verifyDownloadedEvents("event/events_1.json");
    }

    @Test
    public void download_number_of_events_according_to_page_size() throws Exception {
        givenAMetadataInDatabase();

        int pageSize = 3;

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize);

        dhis2MockServer.enqueueMockResponse("event/events_2.json");

        List<Event> events = eventEndpointCall.call();

        d2.eventModule().eventPersistenceCallFactory.getCall(events).call();

        List<Event> downloadedEvents = eventStore.selectAll();

        assertThat(downloadedEvents.size(), is(pageSize));
    }

    //@Test
    //TODO Pendding
    public void rollback_transaction_when_insert_a_event_with_wrong_foreign_key()
            throws Exception {
        givenAMetadataInDatabase();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        dhis2MockServer.enqueueMockResponse(
                "event/two_events_first_good_second_wrong_foreign_key.json");

        eventEndpointCall.call();

        verifyNumberOfDownloadedEvents(1);
        verifyNumberOfDownloadedTrackedEntityDataValue(6);
        verifyDownloadedEvents("event/event_1_with_all_data_values.json");
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    private void verifyNumberOfDownloadedEvents(int numEvents) {
        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(numEvents));
    }

    private void verifyNumberOfDownloadedTrackedEntityDataValue(int num) {
        TrackedEntityDataValueStore eventStore = TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter());

        int numPersisted = eventStore.selectAll().size();

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

        List<Event> downloadedEventsWithoutValues = eventStore.querySingleEvents();

        TrackedEntityDataValueStore trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter());

        for (int i = 0; i < downloadedEventsWithoutValues.size(); ++i) {

            Event event = downloadedEventsWithoutValues.get(i);

            List<TrackedEntityDataValue> trackedEntityDataValues =
                    trackedEntityDataValueStore.queryTrackedEntityDataValuesByEventUid(event.uid());
            List<TrackedEntityDataValue> trackedEntityDataValuesWithNullIdsAndEvents = new ArrayList<>();

            for (TrackedEntityDataValue trackedEntityDataValue : trackedEntityDataValues) {
                trackedEntityDataValuesWithNullIdsAndEvents.add(
                        trackedEntityDataValue.toBuilder().id(null).event(null).build());
            }

            downloadedEvents.add(event.toBuilder()
                    .id(null)
                    .state(null)
                    .deleted(false)
                    .trackedEntityDataValues(trackedEntityDataValuesWithNullIdsAndEvents).build());
        }

        return downloadedEvents;
    }

    private Payload<Event> parseEventResponse(String file) throws IOException {
        String expectedEventsResponseJson = new ResourcesFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedEventsResponseJson,
                new TypeReference<Payload<Event>>() {
                });
    }
}