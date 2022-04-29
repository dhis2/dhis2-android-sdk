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
package org.hisp.dhis.android.core

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class EventWithLimitCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun download_events() {
        val eventLimitByOrgUnit = 1
        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("event/events_1.json")
        d2.eventModule().eventDownloader().limit(eventLimitByOrgUnit).blockingDownload()
        val eventStore = EventStoreImpl.create(databaseAdapter)
        val downloadedEvents = eventStore.querySingleEvents()
        Truth.assertThat(downloadedEvents.size).isEqualTo(eventLimitByOrgUnit)
    }

    // @Test TODO https://jira.dhis2.org/browse/ANDROSDK-1328
    fun download_events_by_uid_limited_by_one() {
        val eventLimitByOrgUnit = 1
        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("event/events_with_uids.json")
        d2.eventModule().eventDownloader()
            .byUid()
            .`in`("wAiGPfJGMxt", "PpNGhvEYnXe")
            .limit(eventLimitByOrgUnit)
            .blockingDownload()
        val eventStore = EventStoreImpl.create(databaseAdapter)
        val downloadedEvents = eventStore.querySingleEvents()
        Truth.assertThat(downloadedEvents.size).isEqualTo(eventLimitByOrgUnit)
    }
}
