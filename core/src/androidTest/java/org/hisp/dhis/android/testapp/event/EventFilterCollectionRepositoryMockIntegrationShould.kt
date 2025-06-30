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
package org.hisp.dhis.android.testapp.event

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class EventFilterCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val eventFilters = d2.eventModule().eventFilters().blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_program() {
        val eventFilters = d2.eventModule().eventFilters()
            .byProgram().eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_program_stage() {
        val eventFilters = d2.eventModule().eventFilters()
            .byProgramStage().eq("dBwrot7S420")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_description() {
        val eventFilters = d2.eventModule().eventFilters()
            .byDescription().eq("Simple Filter for TB events")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_follow_up() {
        val eventFilters = d2.eventModule().eventFilters()
            .byFollowUp().isFalse
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit() {
        val eventFilters = d2.eventModule().eventFilters()
            .byOrganisationUnit().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_ou_mode() {
        val eventFilters = d2.eventModule().eventFilters()
            .byOuMode().eq(OrganisationUnitMode.ACCESSIBLE)
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_assigned_user_mode() {
        val eventFilters = d2.eventModule().eventFilters()
            .byAssignedUserMode().eq(AssignedUserMode.CURRENT)
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_order() {
        val eventFilters = d2.eventModule().eventFilters()
            .byOrder().eq("dueDate:asc,createdDate:desc")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_display_column_order() {
        val eventFilters = d2.eventModule().eventFilters()
            .byDisplayColumnOrder().like("assignedUser")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(2)
    }

    @Test
    fun filter_by_events() {
        val eventFilters = d2.eventModule().eventFilters()
            .byEvents().like("event2Uid")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_event_status() {
        val eventFilters = d2.eventModule().eventFilters()
            .byEventStatus().eq(EventStatus.ACTIVE)
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_event_date() {
        val eventFilters = d2.eventModule().eventFilters()
            .byEventDate().like("2014-05-01")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_due_date() {
        val eventFilters = d2.eventModule().eventFilters()
            .byDueDate().like("LAST_2_SIXMONTHS")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated_date() {
        val eventFilters = d2.eventModule().eventFilters()
            .byLastUpdatedDate().like("-5")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun filter_by_completed_date() {
        val eventFilters = d2.eventModule().eventFilters()
            .byCompletedDate().like("RELATIVE")
            .blockingGet()

        assertThat(eventFilters.size).isEqualTo(1)
    }

    @Test
    fun include_event_data_filters_as_children() {
        val eventFilter = d2.eventModule().eventFilters()
            .withEventDataFilters().one().blockingGet()

        assertThat(eventFilter!!.eventQueryCriteria()!!.dataFilters()!!.size).isEqualTo(4)
        assertThat(eventFilter.eventQueryCriteria()!!.dataFilters()!![0].dataItem()).isEqualTo("g9eOBujte1U")
    }
}
