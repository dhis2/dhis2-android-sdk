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
package org.hisp.dhis.android.testapp.trackedentity

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.testapp.trackedentity.TrackedEntityAttributeValueObjectRepositoryMockIntegrationShould.setDataValueState
import org.junit.Test
import org.junit.runner.RunWith
import java.text.ParseException

@RunWith(D2JunitRunner::class)
class TrackedEntityAttributeValueCollectionRepositoryMockIntegrationShould :

    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_tracked_entity_data_values() {
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues().blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(4)
    }

    @Test
    fun filter_by_tracked_entity_attribute() {
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityAttribute().eq("cejWyOfXge6")
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_value() {
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byValue().eq("4081507")
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(1)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_by_created() {
        val date = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.000")
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byCreated().eq(date)
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(1)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_by_last_updated() {
        val date = BaseIdentifiableObject.DATE_FORMAT.parse("2018-01-10T13:40:28.000")
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byLastUpdated().eq(date)
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tracked_entity_instance() {
        val trackedEntityAttributeValues =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq("nWrB0TfWlvh")
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(2)
    }

    @Test
    @Throws(D2Error::class)
    fun filter_by_deleted() {
        val teiUid = "nWrB0TfWlvh"
        val repository = d2.trackedEntityModule().trackedEntityAttributeValues()
            .value("cejWyOfXge6", teiUid)

        val value = repository.blockingGet()

        var trackedEntityAttributeValues: List<TrackedEntityAttributeValue?> =
            d2.trackedEntityModule().trackedEntityAttributeValues()
                .byDeleted().isTrue
                .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(0)

        repository.blockingDelete()

        trackedEntityAttributeValues = d2.trackedEntityModule().trackedEntityAttributeValues()
            .byDeleted().isTrue
            .blockingGet()
        Truth.assertThat(trackedEntityAttributeValues.size).isEqualTo(1)

        repository.blockingSet(value!!.value())
        setDataValueState(value, State.SYNCED)
        restoreTeiState(teiUid, State.SYNCED)
    }

    @Test
    fun return_tracked_entity_attribute_value_object_repository() {
        val objectRepository = d2.trackedEntityModule().trackedEntityAttributeValues()
            .value("cejWyOfXge6", "nWrB0TfWlvh")
        Truth.assertThat(objectRepository.blockingExists()).isEqualTo(java.lang.Boolean.TRUE)
        Truth.assertThat(objectRepository.blockingGet()!!.value()).isEqualTo("4081507")
    }

    private fun restoreTeiState(teiUid: String, syncState: State) {
        val store = DhisAndroidSdkKoinContext.koin.get<TrackedEntityInstanceStore>()
        store.setAggregatedSyncState(teiUid, syncState)
        store.setSyncState(teiUid, syncState)
    }
}
