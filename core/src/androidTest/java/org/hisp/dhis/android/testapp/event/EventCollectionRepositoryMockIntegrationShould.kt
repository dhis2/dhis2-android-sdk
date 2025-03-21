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
package org.hisp.dhis.android.testapp.event

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test
import java.text.ParseException

class EventCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val events = d2.eventModule().events()
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_uid() {
        val events = d2.eventModule().events()
            .byUid().eq("single1")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_enrollment() {
        val events = d2.eventModule().events()
            .byEnrollmentUid().eq("enroll1")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created() {
        val events = d2.eventModule().events()
            .byCreated().eq("2017-08-07T15:47:25.959".toJavaDate())
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val events = d2.eventModule().events()
            .byLastUpdated().eq("2019-01-01T22:26:39.094".toJavaDate())
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created_at_client() {
        val events = d2.eventModule().events()
            .byCreatedAtClient().eq("2018-02-28T00:00:00.000")
            .blockingGet()

        assertThat(events.size).isEqualTo(0)
    }

    @Test
    fun filter_by_last_updated_at_client() {
        val events = d2.eventModule().events()
            .byLastUpdatedAtClient().eq("2018-02-28T00:00:00.000")
            .blockingGet()

        assertThat(events.size).isEqualTo(0)
    }

    @Test
    fun filter_by_status() {
        val events = d2.eventModule().events()
            .byStatus().eq(EventStatus.ACTIVE)
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_geometry_type() {
        val events = d2.eventModule().events()
            .byGeometryType().eq(FeatureType.POINT)
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_geometry_coordinates() {
        val events = d2.eventModule().events()
            .byGeometryCoordinates().eq("[21.0,43.0]")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val events = d2.eventModule().events()
            .byProgramUid().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(events.size).isEqualTo(2)
    }

    @Test
    fun filter_by_program_stage() {
        val events = d2.eventModule().events()
            .byProgramStageUid().eq("dBwrot7S420")
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_organization_unit() {
        val events = d2.eventModule().events()
            .byOrganisationUnitUid().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_event_date() {
        val periods: MutableList<Period> = ArrayList()
        periods.add(
            Period.builder()
                .startDate("2017-02-27T00:00:00.000".toJavaDate())
                .endDate("2017-02-27T00:00:00.000".toJavaDate())
                .build(),
        )
        val events = d2.eventModule().events()
            .byEventDate().inPeriods(periods)
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_complete_date() {
        val events = d2.eventModule().events()
            .byCompleteDate()
            .eq("2016-02-27T14:34:00.000".toJavaDate())
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_complete_by() {
        val events = d2.eventModule().events()
            .byCompletedBy().eq("android")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_by_due_date() {
        val events = d2.eventModule().events()
            .byDueDate()
            .afterOrEqual("2017-01-28T12:35:00.000".toJavaDate()!!)
            .blockingGet()

        assertThat(events.size).isEqualTo(2)
    }

    @Test
    fun filter_by_state() {
        val events = d2.eventModule().events()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_aggregated_sync_state() {
        val events = d2.eventModule().events()
            .byAggregatedSyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_attribute_option_combo() {
        val events = d2.eventModule().events()
            .byAttributeOptionComboUid().eq("bRowv6yZOF2")
            .blockingGet()

        assertThat(events.size).isEqualTo(2)
    }

    @Test
    fun filter_by_deleted() {
        val events = d2.eventModule().events()
            .byDeleted().isFalse
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun filter_by_tracked_entity_instance() {
        val events = d2.eventModule().events()
            .byTrackedEntityInstanceUids(listOf("nWrB0TfWlvh"))
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_value() {
        val events = d2.eventModule().events()
            .byDataValue("hB9F8vKFmlk").lt("3843")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_follow_up() {
        val events = d2.eventModule().events()
            .byFollowUp(true)
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun filter_by_assigned_user() {
        val events = d2.eventModule().events()
            .byAssignedUser().eq("aTwqot2S410")
            .blockingGet()

        assertThat(events.size).isEqualTo(1)
    }

    @Test
    fun count_tracked_entity_instances_unrestricted() {
        val count = d2.eventModule().events().countTrackedEntityInstances()

        assertThat(count).isEqualTo(2)
    }

    @Test
    fun count_tracked_entity_instances_restricted() {
        val count = d2.eventModule().events().byUid().eq("event1").countTrackedEntityInstances()

        assertThat(count).isEqualTo(1)
    }

    @Test
    fun include_tracked_entity_data_values_as_children() {
        val event = d2.eventModule().events()
            .withTrackedEntityDataValues().uid("single1")
            .blockingGet()
        assertThat(event!!.trackedEntityDataValues()!!.size).isEqualTo(6)
    }

    @Test
    fun include_notes_as_children() {
        val event = d2.eventModule().events()
            .withNotes().uid("single1")
            .blockingGet()

        assertThat(event!!.notes()!!.size).isEqualTo(2)
    }

    @Test
    fun order_by_due_date() {
        val events = d2.eventModule().events()
            .orderByDueDate(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1")
        assertThat(events[1].uid()).isEqualTo("event2")
        assertThat(events[2].uid()).isEqualTo("single1")
        assertThat(events[3].uid()).isEqualTo("single2")
    }

    @Test
    fun order_by_created() {
        val events = d2.eventModule().events()
            .orderByCreated(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1")
        assertThat(events[1].uid()).isEqualTo("event2")
        assertThat(events[2].uid()).isEqualTo("single1")
        assertThat(events[3].uid()).isEqualTo("single2")
    }

    @Test
    fun order_by_created_at_client() {
        val events = d2.eventModule().events()
            .orderByCreatedAtClient(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1")
        assertThat(events[1].uid()).isEqualTo("event2")
        assertThat(events[2].uid()).isEqualTo("single1")
        assertThat(events[3].uid()).isEqualTo("single2")
    }

    @Test
    fun order_by_last_updated() {
        val events = d2.eventModule().events()
            .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1")
        assertThat(events[1].uid()).isEqualTo("event2")
        assertThat(events[2].uid()).isEqualTo("single2")
        assertThat(events[3].uid()).isEqualTo("single1")
    }

    @Test
    fun order_by_last_updated_at_client() {
        val events = d2.eventModule().events()
            .orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1")
        assertThat(events[1].uid()).isEqualTo("event2")
        assertThat(events[2].uid()).isEqualTo("single1")
        assertThat(events[3].uid()).isEqualTo("single2")
    }

    @Test
    fun order_by_event_date_and_last_updated() {
        val events = d2.eventModule().events()
            .orderByEventDate(RepositoryScope.OrderByDirection.ASC)
            .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event2")
        assertThat(events[1].uid()).isEqualTo("event1")
        assertThat(events[2].uid()).isEqualTo("single2")
        assertThat(events[3].uid()).isEqualTo("single1")
    }

    @Test
    fun order_by_complete_date() {
        val events = d2.eventModule().events()
            .orderByCompleteDate(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event2")
        assertThat(events[1].uid()).isEqualTo("single1")
        assertThat(events[2].uid()).isEqualTo("single2")
        assertThat(events[3].uid()).isEqualTo("event1")
    }

    @Test
    fun order_by_organisation_unit_name() {
        val events = d2.eventModule().events()
            .orderByOrganisationUnitName(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun order_by_timeline() {
        val events = d2.eventModule().events()
            .orderByTimeline(RepositoryScope.OrderByDirection.ASC)
            .blockingGet()

        assertThat(events[0].uid()).isEqualTo("event1") // eventDate
        assertThat(events[1].uid()).isEqualTo("event2") // dueDate
        assertThat(events[2].uid()).isEqualTo("single2") // eventDate
        assertThat(events[3].uid()).isEqualTo("single1") // eventDate
    }

    @Test
    fun order_by_data_element() {
        val events = d2.eventModule().events()
            .byEnrollmentUid().isNull
            .orderByDataElement(RepositoryScope.OrderByDirection.DESC, "hB9F8vKFmlk")
            .withTrackedEntityDataValues()
            .blockingGet()

        assertThat(events.size).isEqualTo(2)
        assertThat(events[0].uid()).isEqualTo("single2") // 3843
        assertThat(events[1].uid()).isEqualTo("single1") // 3842
    }

    @Test
    @Throws(D2Error::class)
    fun add_events_to_the_repository() {
        val events1 = d2.eventModule().events().blockingGet()
        assertThat(events1.size).isEqualTo(4)

        val eventUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                "enroll1",
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )

        val events2 = d2.eventModule().events().blockingGet()
        assertThat(events2.size).isEqualTo(5)

        val event = d2.eventModule().events().uid(eventUid).blockingGet()
        assertThat(event!!.uid()).isEqualTo(eventUid)

        d2.eventModule().events().uid(eventUid).blockingDelete()
    }
}
