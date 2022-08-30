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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class EventEndpointCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    @Throws(Exception::class)
    fun download_number_of_events_according_to_page_default_query() {
        enqueue("event/events_1.json")
        d2.eventModule().eventDownloader().blockingDownload()
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(1)
    }

    @Test
    @Throws(Exception::class)
    fun rollback_transaction_when_insert_a_event_with_wrong_foreign_key() {
        enqueue("event/two_events_first_good_second_wrong_foreign_key.json")
        d2.eventModule().eventDownloader().blockingDownload()
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(0)
        assertThat(d2.trackedEntityModule().trackedEntityDataValues().blockingCount())
            .isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun download_events_by_uid() {
        enqueue("event/events_with_uids.json")
        d2.eventModule().eventDownloader().byUid().`in`("wAiGPfJGMxt", "PpNGhvEYnXe").blockingDownload()
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(2)
    }

    @Throws(Exception::class)
    private fun checkOverwrite(state: State, finalStatus: EventStatus) {
        enqueue("event/events_1.json")
        d2.eventModule().eventDownloader().blockingDownload()

        val events = d2.eventModule().events().blockingGet()
        val event = events[0]
        assertThat(event.uid()).isEqualTo("V1CerIi3sdL")
        assertThat(events.size).isEqualTo(1)
        EventStoreImpl.create(d2.databaseAdapter()).update(
            event.toBuilder()
                .syncState(state)
                .aggregatedSyncState(state)
                .status(EventStatus.SKIPPED).build()
        )

        enqueue("event/events_1.json")
        d2.eventModule().eventDownloader().blockingDownload()

        val event1 = d2.eventModule().events().one().blockingGet()
        assertThat(event1.uid()).isEqualTo("V1CerIi3sdL")
        assertThat(event1.status()).isEqualTo(finalStatus)
    }

    @Test
    @Throws(Exception::class)
    fun overwrite_when_state_sync() {
        checkOverwrite(State.SYNCED, EventStatus.COMPLETED)
    }

    @Test
    @Throws(Exception::class)
    fun not_overwrite_when_state_to_post() {
        checkOverwrite(State.TO_POST, EventStatus.SKIPPED)
    }

    @Test
    @Throws(Exception::class)
    fun not_overwrite_when_state_error() {
        checkOverwrite(State.ERROR, EventStatus.SKIPPED)
    }

    @Test
    @Throws(Exception::class)
    fun not_overwrite_when_state_to_update() {
        checkOverwrite(State.TO_UPDATE, EventStatus.SKIPPED)
    }

    private fun enqueue(url: String) {
        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse(url)
    }
}
