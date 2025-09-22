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

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.trackedentity.ReservedValueSummary
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueStoreImpl

class TrackedEntityAttributeReservedValueEndpointCallRealIntegrationShould : BaseRealIntegrationTest() {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private val numberToReserve = 5
    private val orgunitUid = "DiszpKrYNg8"

    private fun reserveValues() {
        d2.trackedEntityModule().reservedValueManager().blockingDownloadReservedValues("xs8A6tQJY0s", numberToReserve)
    }

    private val value: String
        get() = d2.trackedEntityModule().reservedValueManager().blockingGetValue("xs8A6tQJY0s", orgunitUid)

    // @Test
    fun reserve_and_download() {
        login()
        syncMetadata()
        reserveValues()
        val value = value
    }

    // @Test
    fun download_and_persist_reserved_values() = runTest {
        login()
        syncMetadata()
        reserveValues()

        val reservedValues = TrackedEntityAttributeReservedValueStoreImpl(
            d2.databaseAdapter(),
        ).selectAll()

        Truth.assertThat(reservedValues.size).isEqualTo(numberToReserve)
    }

    // @Test
    fun download_and_persist_all_reserved_values() = runTest {
        login()
        syncMetadata()
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(20)

        val reservedValues = TrackedEntityAttributeReservedValueStoreImpl(
            d2.databaseAdapter(),
        ).selectAll()

        val value = d2.trackedEntityModule().reservedValueManager().blockingGetValue("xs8A6tQJY0s", orgunitUid)
    }

    // @Test
    fun reserve_and_count() {
        login()
        syncMetadata()
        val trackedEntityAttribute =
            d2.trackedEntityModule().trackedEntityAttributes().byGenerated().isTrue.one().blockingGet()
        d2.trackedEntityModule().reservedValueManager()
            .blockingDownloadReservedValues(trackedEntityAttribute!!.uid(), numberToReserve)
        val attributeCount = d2.trackedEntityModule().reservedValueManager()
            .blockingCount(trackedEntityAttribute.uid(), null)
        val attributeAndOrgUnitCount = d2.trackedEntityModule().reservedValueManager()
            .blockingCount(trackedEntityAttribute.uid(), orgunitUid)

        Truth.assertThat(attributeCount).isEqualTo(numberToReserve)
        Truth.assertThat(attributeAndOrgUnitCount).isEqualTo(numberToReserve)
    }

    // @Test
    fun retrieve_the_reserved_value_summaries() {
        login()
        syncMetadata()
        d2.trackedEntityModule().reservedValueManager().blockingDownloadAllReservedValues(5)

        val reservedValueSummaries: List<ReservedValueSummary?> =
            d2.trackedEntityModule().reservedValueManager().blockingGetReservedValueSummaries()

        Truth.assertThat(reservedValueSummaries).isNotEmpty()
    }

    private fun login() {
        if (!d2.userModule().isLogged().blockingGet()) {
            d2.userModule().logIn(username, password, url).blockingGet()
        }
    }

    private fun syncMetadata() {
        d2.metadataModule().blockingDownload()
    }
}
