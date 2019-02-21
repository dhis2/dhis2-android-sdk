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

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.junit.Before;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EventEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    //@Test
    public void download_number_of_events_according_to_default_limit() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        List<Event> events = eventEndpointCall.call();
        Truth.assertThat(events.isEmpty()).isFalse();

        //TODO: we should create dependant server data verifications in other test suite
       /* verifyNumberOfDownloadedEvents(49);
        verifyNumberOfDownloadedTrackedEntityDataValue(335);*/
    }


    //@Test
    public void download_event_with_category_combo_option() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        Callable<List<Event>> eventEndpointCall = EventCallFactory.create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0);

        eventEndpointCall.call();

        assertTrue(verifyAtLeastOneEventWithOptionCombo());
    }

    private boolean verifyAtLeastOneEventWithOptionCombo() {
        EventStore eventStore = EventStoreImpl.create(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();
        for(Event event : downloadedEvents){
            if (event.attributeOptionCombo() != null) {
                return true;
            }
        }
        return false;
    }

    private void verifyNumberOfDownloadedEvents(int numEvents) {
        EventStore eventStore = EventStoreImpl.create(databaseAdapter());

        List<Event> downloadedEvents = eventStore.querySingleEvents();

        assertThat(downloadedEvents.size(), is(numEvents));
    }

    private void verifyNumberOfDownloadedTrackedEntityDataValue(int num) {
        TrackedEntityDataValueStore trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter());

        int numPersisted = trackedEntityDataValueStore.selectAll().size();

        assertThat(numPersisted, is(num));
    }
}
