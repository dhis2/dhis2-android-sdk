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

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class EventCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Event> events =
                d2.eventModule().events()
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_uid() {
        List<Event> events =
                d2.eventModule().events()
                        .byUid().eq("single1")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_enrollment() {
        List<Event> events =
                d2.eventModule().events()
                        .byEnrollmentUid().eq("enroll1")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<Event> events =
                d2.eventModule().events()
                        .byCreated().eq(BaseNameableObject.DATE_FORMAT.parse("2017-08-07T15:47:25.959"))
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        List<Event> events =
                d2.eventModule().events()
                        .byLastUpdated().eq(BaseNameableObject.DATE_FORMAT.parse("2019-01-01T22:26:39.094"))
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_created_at_client() {
        List<Event> events =
                d2.eventModule().events()
                        .byCreatedAtClient().eq("2018-02-28T00:00:00.000")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(0);
    }

    @Test
    public void filter_by_last_updated_at_client() {
        List<Event> events =
                d2.eventModule().events()
                        .byLastUpdatedAtClient().eq("2018-02-28T00:00:00.000")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(0);
    }

    @Test
    public void filter_by_status() {
        List<Event> events =
                d2.eventModule().events()
                        .byStatus().eq(EventStatus.ACTIVE)
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_geometry_type() {
        List<Event> events =
                d2.eventModule().events()
                        .byGeometryType().eq(FeatureType.POINT)
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_geometry_coordinates() {
        List<Event> events =
                d2.eventModule().events()
                        .byGeometryCoordinates().eq("[21.0, 43.0]")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program() {
        List<Event> events =
                d2.eventModule().events()
                        .byProgramUid().eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_program_stage() {
        List<Event> events =
                d2.eventModule().events()
                        .byProgramStageUid().eq("dBwrot7S420")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_organization_unit() {
        List<Event> events =
                d2.eventModule().events()
                        .byOrganisationUnitUid().eq("DiszpKrYNg8")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_event_date() throws ParseException {
        List<Period> periods = new ArrayList<>();
        periods.add(Period.builder()
                .startDate(BaseNameableObject.DATE_FORMAT.parse("2017-02-27T00:00:00.000"))
                .endDate(BaseNameableObject.DATE_FORMAT.parse("2017-02-27T00:00:00.000"))
                .build());
        List<Event> events =
                d2.eventModule().events()
                        .byEventDate().inPeriods(periods)
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_complete_date() throws ParseException {
        List<Event> events =
                d2.eventModule().events()
                        .byCompleteDate().eq(BaseNameableObject.DATE_FORMAT.parse("2016-02-27T14:34:00.000"))
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_due_date() throws ParseException {
        List<Event> events =
                d2.eventModule().events()
                        .byDueDate()
                        .afterOrEqual(BaseNameableObject.DATE_FORMAT.parse("2017-01-28T12:35:00.000"))
                        .blockingGet();

        assertThat(events.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_state() {
        List<Event> events =
                d2.eventModule().events()
                        .bySyncState().eq(State.SYNCED)
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_attribute_option_combo() {
        List<Event> events =
                d2.eventModule().events()
                        .byAttributeOptionComboUid().eq("bRowv6yZOF2")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_deleted() {
        List<Event> events =
                d2.eventModule().events()
                        .byDeleted().isFalse()
                        .blockingGet();

        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<Event> events =
                d2.eventModule().events()
                        .byTrackedEntityInstanceUids(Collections.singletonList("nWrB0TfWlvh"))
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_data_value() {
        List<Event> events =
                d2.eventModule().events()
                        .byDataValue("hB9F8vKFmlk").lt("3843")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_follow_up() {
        List<Event> events =
                d2.eventModule().events()
                        .byFollowUp(true)
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_assigned_user() {
        List<Event> events =
                d2.eventModule().events()
                        .byAssignedUser().eq("aTwqot2S410")
                        .blockingGet();

        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void count_tracked_entity_instances_unrestricted() {
        int count = d2.eventModule().events().countTrackedEntityInstances();

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void count_tracked_entity_instances_restricted() {
        int count = d2.eventModule().events().byUid().eq("event1").countTrackedEntityInstances();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void include_tracked_entity_data_values_as_children() {
        Event event = d2.eventModule().events()
                .withTrackedEntityDataValues().uid("single1").blockingGet();
        assertThat(event.trackedEntityDataValues().size()).isEqualTo(6);
    }

    @Test
    public void include_notes_as_children() {
        Event event = d2.eventModule().events()
                .withNotes().uid("single1").blockingGet();
        assertThat(event.notes().size()).isEqualTo(2);
    }

    @Test
    public void order_by_due_date() {
        List<Event> events = d2.eventModule().events()
                .orderByDueDate(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");
        assertThat(events.get(1).uid()).isEqualTo("event2");
        assertThat(events.get(2).uid()).isEqualTo("single1");
        assertThat(events.get(3).uid()).isEqualTo("single2");
    }

    @Test
    public void order_by_created() {
        List<Event> events = d2.eventModule().events()
                .orderByCreated(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");
        assertThat(events.get(1).uid()).isEqualTo("event2");
        assertThat(events.get(2).uid()).isEqualTo("single1");
        assertThat(events.get(3).uid()).isEqualTo("single2");
    }

    @Test
    public void order_by_created_at_client() {
        List<Event> events = d2.eventModule().events()
                .orderByCreatedAtClient(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");
        assertThat(events.get(1).uid()).isEqualTo("event2");
        assertThat(events.get(2).uid()).isEqualTo("single1");
        assertThat(events.get(3).uid()).isEqualTo("single2");
    }

    @Test
    public void order_by_last_updated() {
        List<Event> events = d2.eventModule().events()
                .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");
        assertThat(events.get(1).uid()).isEqualTo("event2");
        assertThat(events.get(2).uid()).isEqualTo("single2");
        assertThat(events.get(3).uid()).isEqualTo("single1");
    }

    @Test
    public void order_by_last_updated_at_client() {
        List<Event> events = d2.eventModule().events()
                .orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");
        assertThat(events.get(1).uid()).isEqualTo("event2");
        assertThat(events.get(2).uid()).isEqualTo("single1");
        assertThat(events.get(3).uid()).isEqualTo("single2");
    }

    @Test
    public void order_by_event_date_and_last_updated() {
        List<Event> events = d2.eventModule().events()
                .orderByEventDate(RepositoryScope.OrderByDirection.ASC)
                .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event2");
        assertThat(events.get(1).uid()).isEqualTo("event1");
        assertThat(events.get(2).uid()).isEqualTo("single2");
        assertThat(events.get(3).uid()).isEqualTo("single1");
    }

    @Test
    public void order_by_complete_date() {
        List<Event> events = d2.eventModule().events()
                .orderByCompleteDate(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event2");
        assertThat(events.get(1).uid()).isEqualTo("single1");
        assertThat(events.get(2).uid()).isEqualTo("single2");
        assertThat(events.get(3).uid()).isEqualTo("event1");
    }

    @Test
    public void order_by_organisation_unit_name() {
        List<Event> events = d2.eventModule().events()
                .orderByOrganisationUnitName(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.size()).isEqualTo(4);
    }

    @Test
    public void order_by_timeline() {
        List<Event> events = d2.eventModule().events()
                .orderByTimeline(RepositoryScope.OrderByDirection.ASC)
                .blockingGet();
        assertThat(events.get(0).uid()).isEqualTo("event1");  // eventDate
        assertThat(events.get(1).uid()).isEqualTo("event2");  // dueDate
        assertThat(events.get(2).uid()).isEqualTo("single2"); // eventDate
        assertThat(events.get(3).uid()).isEqualTo("single1"); // eventDate
    }

    @Test
    public void order_by_data_element() {
        List<Event> events = d2.eventModule().events()
                .byEnrollmentUid().isNull()
                .orderByDataElement(RepositoryScope.OrderByDirection.DESC, "hB9F8vKFmlk")
                .withTrackedEntityDataValues()
                .blockingGet();
        assertThat(events.size()).isEqualTo(2);
        assertThat(events.get(0).uid()).isEqualTo("single2");  // 3843
        assertThat(events.get(1).uid()).isEqualTo("single1");  // 3842
    }

    @Test
    public void add_events_to_the_repository() throws D2Error {
        List<Event> events1 = d2.eventModule().events().blockingGet();
        assertThat(events1.size()).isEqualTo(4);

        String eventUid = d2.eventModule().events().blockingAdd(
                EventCreateProjection.create("enroll1", "lxAQ7Zs9VYR", "dBwrot7S420",
                        "DiszpKrYNg8", "bRowv6yZOF2"));

        List<Event> events2 = d2.eventModule().events().blockingGet();
        assertThat(events2.size()).isEqualTo(5);

        Event event = d2.eventModule().events().uid(eventUid).blockingGet();
        assertThat(event.uid()).isEqualTo(eventUid);

        d2.eventModule().events().uid(eventUid).blockingDelete();
    }
}