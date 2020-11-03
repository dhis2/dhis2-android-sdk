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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventCallFactory.create
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
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
    fun download_events_according_to_default_query() {
        val eventEndpointCall = create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0)
        dhis2MockServer.enqueueMockResponse("event/events_1.json")
        val events = eventEndpointCall.call()
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        Truth.assertThat(d2.eventModule().events().blockingCount()).isEqualTo(1)
    }

    @Test
    @Throws(Exception::class)
    fun download_number_of_events_according_to_page_size() {
        val pageSize = 1
        val eventEndpointCall = create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize)
        dhis2MockServer.enqueueMockResponse("event/events_1.json")
        val events = eventEndpointCall.call()
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        Truth.assertThat(d2.eventModule().events().blockingCount()).isEqualTo(pageSize)
    }

    @Test
    @Throws(Exception::class)
    fun rollback_transaction_when_insert_a_event_with_wrong_foreign_key() {
        val eventEndpointCall = create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0)
        dhis2MockServer.enqueueMockResponse(
            "event/two_events_first_good_second_wrong_foreign_key.json"
        )
        eventEndpointCall.call()
        Truth.assertThat(d2.eventModule().events().blockingCount()).isEqualTo(0)
        Truth.assertThat(d2.trackedEntityModule().trackedEntityDataValues().blockingCount())
            .isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun not_overwrite_events_marked_as_to_post_to_update_or_error() {
        val pageSize = 1
        val eventEndpointCall = create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", pageSize)
        dhis2MockServer.enqueueMockResponse("event/events_1.json")
        val events = eventEndpointCall.call()
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        val event = events[0]
        Truth.assertThat(event.uid()).isEqualTo("V1CerIi3sdL")
        Truth.assertThat(d2.eventModule().events().blockingCount()).isEqualTo(pageSize)
        EventStoreImpl.create(d2.databaseAdapter()).update(
            event.toBuilder()
                .state(State.SYNCED).status(EventStatus.SKIPPED).build()
        )
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        val event1 = d2.eventModule().events().one().blockingGet()
        Truth.assertThat(event1.uid()).isEqualTo("V1CerIi3sdL")
        Truth.assertThat(event1.status())
            .isEqualTo(EventStatus.COMPLETED) // Because in Synced state should overwrite.
        EventStoreImpl.create(d2.databaseAdapter()).update(
            event.toBuilder()
                .state(State.TO_UPDATE).status(EventStatus.SKIPPED).build()
        )
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        val event2 = d2.eventModule().events().one().blockingGet()
        Truth.assertThat(event2.uid()).isEqualTo("V1CerIi3sdL")
        Truth.assertThat(event2.status()).isEqualTo(EventStatus.SKIPPED)
        EventStoreImpl.create(d2.databaseAdapter()).update(
            event.toBuilder()
                .state(State.ERROR).status(EventStatus.SKIPPED).build()
        )
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        val event3 = d2.eventModule().events().one().blockingGet()
        Truth.assertThat(event3.uid()).isEqualTo("V1CerIi3sdL")
        Truth.assertThat(event3.status()).isEqualTo(EventStatus.SKIPPED)
        EventStoreImpl.create(d2.databaseAdapter()).update(
            event.toBuilder()
                .state(State.TO_POST).status(EventStatus.SKIPPED).build()
        )
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        val event4 = d2.eventModule().events().one().blockingGet()
        Truth.assertThat(event4.uid()).isEqualTo("V1CerIi3sdL")
        Truth.assertThat(event4.status()).isEqualTo(EventStatus.SKIPPED)
    }

    @Test
    @Throws(Exception::class)
    fun download_events_by_uids() {
        val eventEndpointCall =
            create(d2.retrofit(), d2.databaseAdapter(), "DiszpKrYNg8", 0, listOf("wAiGPfJGMxt", "PpNGhvEYnXe"))
        dhis2MockServer.enqueueMockResponse("event/events_with_uids.json")
        val events = eventEndpointCall.call()
        (d2.eventModule() as EventModuleImpl).eventPersistenceCallFactory.persistEvents(
            events,
            RelationshipItemRelatives()
        ).blockingGet()
        Truth.assertThat(d2.eventModule().events().blockingCount()).isEqualTo(2)
    }
}
