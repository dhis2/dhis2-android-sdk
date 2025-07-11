/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.event.internal.EventCallFactory.create
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl

class EventEndpointCallRealIntegrationShould : BaseRealIntegrationTest() {
    // This test is commented because technically it is flaky.
    // It depends on a live server to operate and the login is hardcoded here.
    // Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.
    // @Test
    @Throws(Exception::class)
    fun download_number_of_events_according_to_default_limit() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        val eventEndpointCall =
            create(d2.httpServiceClient(), d2.coroutineAPICallExecutor(), "DiszpKrYNg8", 0, emptyList())
        val events = eventEndpointCall.items()

        assertThat(events.isEmpty()).isFalse()

        // TODO: we should create dependant server data verifications in other test suite
        /* verifyNumberOfDownloadedEvents(49);
        verifyNumberOfDownloadedTrackedEntityDataValue(335);
         */
    }

    // @Test
    @Throws(Exception::class)
    fun download_event_with_category_combo_option() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()
        create(d2.httpServiceClient(), d2.coroutineAPICallExecutor(), "DiszpKrYNg8", 0, emptyList())

        assertThat(verifyAtLeastOneEventWithOptionCombo()).isTrue()
    }

    private fun verifyAtLeastOneEventWithOptionCombo(): Boolean {
        val eventStore = EventStoreImpl(d2.databaseAdapter())
        val downloadedEvents = eventStore.querySingleEvents()
        return downloadedEvents.any { it.attributeOptionCombo() != null }
    }

    private fun verifyNumberOfDownloadedEvents(numEvents: Int) {
        val eventStore = EventStoreImpl(d2.databaseAdapter())
        val downloadedEvents = eventStore.querySingleEvents()

        assertThat(downloadedEvents.size).isEqualTo(numEvents)
    }

    private fun verifyNumberOfDownloadedTrackedEntityDataValue(num: Int) {
        val trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl(d2.databaseAdapter())
        val numPersisted = trackedEntityDataValueStore.selectAll().size

        assertThat(numPersisted).isEqualTo(num)
    }
}
