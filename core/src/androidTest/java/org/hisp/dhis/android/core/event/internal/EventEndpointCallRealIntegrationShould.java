/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.event.internal;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class EventEndpointCallRealIntegrationShould extends BaseRealIntegrationTest {

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    //@Test
    public void download_number_of_events_according_to_default_limit() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0, Collections.emptyList());

        List<Event> events = eventEndpointCall.call();
        assertThat(events.isEmpty()).isFalse();

        //TODO: we should create dependant server data verifications in other test suite
       /* verifyNumberOfDownloadedEvents(49);
        verifyNumberOfDownloadedTrackedEntityDataValue(335);*/
    }


    //@Test
    public void download_event_with_category_combo_option() throws Exception {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.metadataModule().blockingDownload();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0, Collections.emptyList());

        eventEndpointCall.call();

        assertThat(verifyAtLeastOneEventWithOptionCombo()).isTrue();
    }

    private boolean verifyAtLeastOneEventWithOptionCombo() {
        EventStore eventStore = EventStoreImpl.create(d2.databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();
        for (Event event : downloadedEvents) {
            if (event.attributeOptionCombo() != null) {
                return true;
            }
        }
        return false;
    }

    private void verifyNumberOfDownloadedEvents(int numEvents) {
        EventStore eventStore = EventStoreImpl.create(d2.databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size()).isEqualTo(numEvents);
    }

    private void verifyNumberOfDownloadedTrackedEntityDataValue(int num) {
        TrackedEntityDataValueStore trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter());

        int numPersisted = trackedEntityDataValueStore.selectAll().size();

        assertThat(numPersisted).isEqualTo(num);
    }
}
