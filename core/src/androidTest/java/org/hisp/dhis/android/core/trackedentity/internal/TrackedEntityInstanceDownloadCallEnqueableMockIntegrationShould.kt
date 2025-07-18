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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityInstanceDownloadCallEnqueableMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @Before
    fun setUp() = runTest {
        d2.wipeModule().wipeData()
    }

    @Test
    fun should_continue_on_page_error() {
        val programTeis = "trackedentity/new_tracker_importer_tracked_entities.json"

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse(403)
        dhis2MockServer.enqueueMockResponse(programTeis)

        d2.trackedEntityModule().trackedEntityInstanceDownloader().blockingDownload()

        val existingTeisInDB = d2.trackedEntityModule().trackedEntityInstances().blockingGet()

        assertThat(existingTeisInDB).isNotEmpty()
    }

    @After
    suspend fun tearDown() {
        d2.wipeModule().wipeData()
    }
}
