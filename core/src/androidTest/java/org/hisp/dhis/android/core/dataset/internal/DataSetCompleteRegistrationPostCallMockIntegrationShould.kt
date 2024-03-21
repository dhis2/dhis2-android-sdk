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

package org.hisp.dhis.android.core.dataset.internal

import org.hisp.dhis.android.core.arch.helpers.DateUtils.SIMPLE_DATE_FORMAT
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class DataSetCompleteRegistrationPostCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        d2.wipeModule().wipeData()
    }

    @Test
    fun post_dataset_complete_registration_success() {
        // Given a DatasetComplete registration to post
        dhis2MockServer.enqueueMockResponse(200, "imports/data_value_import_summary_web_response.json")
        val dataSetCompleteRegistration = provideDataSetCompleteRegistration()
        d2.dataSetModule().dataSetCompleteRegistrations().value(
            period = dataSetCompleteRegistration.period(),
            organisationUnit = dataSetCompleteRegistration.organisationUnit(),
            dataSet = dataSetCompleteRegistration.dataSet(),
            attributeOptionCombo = dataSetCompleteRegistration.attributeOptionCombo(),
        ).blockingSet()

        val toPostRepository = d2.dataSetModule()
            .dataSetCompleteRegistrations()
            .bySyncState().eq(State.TO_POST)

        val syncedRepository = d2.dataSetModule()
            .dataSetCompleteRegistrations()
            .bySyncState().eq(State.SYNCED)

        assertEquals(toPostRepository.blockingGet().size, 1)
        assertEquals(syncedRepository.blockingGet().size, 0)

        // When the object is uploaded
        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()

        // Then the status is synced
        assertEquals(toPostRepository.blockingGet().size, 0)
        assertEquals(syncedRepository.blockingGet().size, 1)
    }

    @Test
    fun post_dataset_delete_complete_registration_success() {
        val toPostRepository = d2.dataSetModule()
            .dataSetCompleteRegistrations()
            .bySyncState().eq(State.TO_POST)

        val syncedRepository = d2.dataSetModule()
            .dataSetCompleteRegistrations()
            .bySyncState().eq(State.SYNCED)

        val toUpdateRepository = d2.dataSetModule()
            .dataSetCompleteRegistrations()
            .bySyncState().eq(State.TO_UPDATE)

        val dataSetCompleteRegistration = provideDataSetCompleteRegistration()
        var dObject = d2.dataSetModule().dataSetCompleteRegistrations().value(
            period = dataSetCompleteRegistration.period(),
            organisationUnit = dataSetCompleteRegistration.organisationUnit(),
            dataSet = dataSetCompleteRegistration.dataSet(),
            attributeOptionCombo = dataSetCompleteRegistration.attributeOptionCombo(),
        )

        dhis2MockServer.enqueueMockResponse(200, "imports/data_value_import_summary_web_response.json")
        dObject.blockingSet()
        assertEquals(toPostRepository.blockingGet().size, 1)
        assertEquals(syncedRepository.blockingGet().size, 0)

        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()

        assertEquals(toPostRepository.blockingGet().size, 0)
        assertEquals(syncedRepository.blockingGet().size, 1)

        dhis2MockServer.enqueueMockResponseWithEmptyBody(204)
        dObject = d2.dataSetModule().dataSetCompleteRegistrations().value(
            period = dataSetCompleteRegistration.period(),
            organisationUnit = dataSetCompleteRegistration.organisationUnit(),
            dataSet = dataSetCompleteRegistration.dataSet(),
            attributeOptionCombo = dataSetCompleteRegistration.attributeOptionCombo(),
        )
        dObject.blockingDelete()
        assertEquals(toUpdateRepository.blockingGet().size, 1)

        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()

        assertEquals(toUpdateRepository.blockingGet().size, 0)
    }

    private fun provideDataSetCompleteRegistration() = DataSetCompleteRegistration.builder()
        .period("2018")
        .dataSet("BfMAe6Itzgt")
        .attributeOptionCombo("bRowv6yZOF2")
        .organisationUnit("DiszpKrYNg8")
        .date(SIMPLE_DATE_FORMAT.parse("2010-03-02"))
        .storedBy("android")
        .build()
}
