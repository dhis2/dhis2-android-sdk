/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.trackedentity.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackedEntitySearchCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_query() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byQuery().eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_attributes() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byAttribute("cejWyOfXge6").eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_filter() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byFilter("cejWyOfXge6").eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_data_value() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byProgram().eq("IpHINAT79UW")
            .byDataValue("g9eOBujte1U").eq("false")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun should_return_header() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byProgram().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(trackedEntityInstances[0].header).isEqualTo("4081507, befryEfXge5")
        assertThat(trackedEntityInstances[1].header).isEqualTo("654321, aefryrfXge5")
    }

    @Test
    fun should_return_header_in_object_repository() {
        val trackedEntity = d2.trackedEntityModule().trackedEntitySearch()
            .byProgram().eq("IpHINAT79UW")
            .uid("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackedEntity?.header).isEqualTo("4081507, befryEfXge5")
    }

    @Test
    fun should_return_ordered_attributes() {
        val trackedEntity = d2.trackedEntityModule().trackedEntitySearch()
            .uid("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackedEntity).isNotNull()
        assertThat(trackedEntity!!.attributeValues!![0].attribute).isEqualTo("cejWyOfXge6")
        assertThat(trackedEntity.attributeValues[1].attribute).isEqualTo("aejWyOfXge6")
    }

    @Test
    fun should_return_program_owners() {
        val trackedEntity = d2.trackedEntityModule().trackedEntitySearch()
            .uid("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackedEntity).isNotNull()
        assertThat(trackedEntity!!.programOwners).isNotNull()
        assertThat(trackedEntity.programOwners!!.size).isEqualTo(1)
        assertThat(trackedEntity.programOwners[0].program).isEqualTo("IpHINAT79UW")
        assertThat(trackedEntity.programOwners[0].ownerOrgUnit).isEqualTo("DiszpKrYNg8")
    }

    @Test
    fun find_by_in_data_value() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntitySearch()
            .byProgram().eq("IpHINAT79UW")
            .byDataValue("g9eOBujte1U").`in`(listOf("false"))
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }
}
