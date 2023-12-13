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
package org.hisp.dhis.android.testapp.trackedentity.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackedEntityInstanceQueryCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_query() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byQuery().eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_attributes() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byAttribute("cejWyOfXge6").eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_filter() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byFilter("cejWyOfXge6").eq("4081507")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_data_value() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byDataValue("g9eOBujte1U").eq("false")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_program() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(2)
    }

    @Test
    fun find_uids_by_program() {
        val trackedEntityInstanceUids = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .blockingGetUids()

        assertThat(trackedEntityInstanceUids.size).isEqualTo(2)
    }

    @Test
    fun find_by_enrollment_date() {
        val refDate = DateUtils.DATE_FORMAT.parse("2018-01-10T00:00:00.000")
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byProgramDate().afterOrEqual(refDate)
            .byProgramDate().beforeOrEqual(refDate)
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_incident_date() {
        val refDate = DateUtils.DATE_FORMAT.parse("2018-01-10T00:00:00.000")
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byIncidentDate().afterOrEqual(refDate)
            .byIncidentDate().beforeOrEqual(refDate)
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_event_date() {
        val refDate = DateUtils.DATE_FORMAT.parse("2015-05-01T00:00:00.000")
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byEventDate().afterOrEqual(refDate)
            .byEventDate().beforeOrEqual(refDate)
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_uid() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byTrackedEntities().eq("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun exclude_uids() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .excludeUids().`in`("nWrB0TfWlvh")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }

    @Test
    fun find_by_working_list() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgramStageWorkingList().eq("NAgjOfWMXg6")
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(0)
    }

    @Test
    fun get_scope() {
        val scope = d2.trackedEntityModule().trackedEntityInstanceQuery().scope

        assertThat(scope.filter()).isNotNull()
    }

    @Test
    fun find_by_transferred_orgunit() {
        val originalOu = d2.trackedEntityModule()
            .trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byOrgUnits().eq("DiszpKrYNg8")
        val transferredOu = d2.trackedEntityModule()
            .trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byOrgUnits().eq("g8upMTyEZGZ")

        assertThat(originalOu.blockingCount()).isEqualTo(2)
        assertThat(transferredOu.blockingCount()).isEqualTo(0)

        // Transfer ownership
        val teiUid = originalOu.blockingGet()[0].uid()
        d2.trackedEntityModule().ownershipManager()
            .blockingTransfer(teiUid, "IpHINAT79UW", "g8upMTyEZGZ")
        assertThat(originalOu.blockingCount()).isEqualTo(1)
        assertThat(transferredOu.blockingCount()).isEqualTo(1)

        // Undo change
        d2.trackedEntityModule().ownershipManager()
            .blockingTransfer(teiUid, "IpHINAT79UW", "DiszpKrYNg8")
    }

    @Test
    fun find_by_event_status() {
        val trackedEntityInstances = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byProgram().eq("IpHINAT79UW")
            .byEventStatus().eq(EventStatus.COMPLETED)
            .blockingGet()

        assertThat(trackedEntityInstances.size).isEqualTo(1)
    }
}
