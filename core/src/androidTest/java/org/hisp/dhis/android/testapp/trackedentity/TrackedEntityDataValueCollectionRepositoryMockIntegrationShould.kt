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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Ignore
import org.junit.Test

class TrackedEntityDataValueCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_tracked_entity_data_values() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues().blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(13)
    }

    @Test
    fun filter_by_event() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byEvent().eq("single1")
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(6)
    }

    @Test
    fun filter_by_created() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byCreated().eq("2015-02-28T12:05:00.333".toJavaDate())
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byLastUpdated().eq("2015-02-28T12:05:00.222".toJavaDate())
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_element() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byDataElement().eq("bx6fsa0t90x")
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(2)
    }

    @Ignore("Pending to fix mapping ANDROSDK-1643")
    @Test
    fun filter_by_stored_by() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byStoredBy().eq("storer")
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_value() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byValue().eq("11")
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(1)
    }

    @Test
    fun filter_by_provided_elsewhere() {
        val trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byProvidedElsewhere().eq(true)
            .blockingGet()

        assertThat(trackedEntityDataValues.size).isEqualTo(2)
    }

    @Test
    fun filter_by_deleted() {
        val repository = d2.trackedEntityModule().trackedEntityDataValues()
            .value("single1", "g9eOBujte1U")

        val value = repository.blockingGet()

        var trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byDeleted().isTrue
            .blockingGet()
        assertThat(trackedEntityDataValues.size).isEqualTo(0)

        repository.blockingDelete()

        trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues()
            .byDeleted().isTrue
            .blockingGet()
        assertThat(trackedEntityDataValues.size).isEqualTo(1)

        repository.blockingSet(value!!.value())
    }

    @Test
    fun return_tracked_entity_data_value_object_repository() {
        val objectRepository = d2.trackedEntityModule().trackedEntityDataValues()
            .value("single1", "g9eOBujte1U")

        assertThat(objectRepository.blockingExists()).isTrue()
        assertThat(objectRepository.blockingGet()!!.value()).isEqualTo("1")
    }
}
