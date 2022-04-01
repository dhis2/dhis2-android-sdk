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

package org.hisp.dhis.android.testapp.event;

import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.event.EventFilter;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class EventFilterCollectionRepositoryMockIntegrationShould
        extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byProgram().eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program_stage() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byProgramStage().eq("dBwrot7S420")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_description() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byDescription().eq("Simple Filter for TB events")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_follow_up() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byFollowUp().eq(Boolean.FALSE)
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_organisation_unit() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byOrganisationUnit().eq("DiszpKrYNg8")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_ou_mode() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byOuMode().eq(OrganisationUnitMode.ACCESSIBLE)
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_assigned_user_mode() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byAssignedUserMode().eq(AssignedUserMode.CURRENT)
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_order() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byOrder().eq("dueDate:asc,createdDate:desc")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_display_column_order() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byDisplayColumnOrder().like("assignedUser")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_events() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byEvents().like("event2Uid")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_event_status() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byEventStatus().eq(EventStatus.ACTIVE)
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_event_date() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byEventDate().like("2014-05-01")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_due_date() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byDueDate().like("LAST_2_SIXMONTHS")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated_date() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byLastUpdatedDate().like("-5")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_completed_date() {
        List<EventFilter> eventFilters = d2.eventModule().eventFilters()
                        .byCompletedDate().like("RELATIVE")
                        .blockingGet();

        assertThat(eventFilters.size()).isEqualTo(1);
    }

    @Test
    public void include_event_data_filters_as_children() {
        EventFilter eventFilter = d2.eventModule().eventFilters()
                .withEventDataFilters().one().blockingGet();

        assertThat(eventFilter.eventQueryCriteria().dataFilters().size()).isEqualTo(4);
        assertThat(eventFilter.eventQueryCriteria().dataFilters().get(0).dataItem()).isEqualTo("abcDataElementUid");
    }
}