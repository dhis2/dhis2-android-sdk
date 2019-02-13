/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.event;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EventCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
        downloadTrackedEntityInstances();
        downloadEvents();
    }

    @Test
    public void find_all() {
        List<Event> events =
                d2.eventModule().events
                        .get();

        assertThat(events.size(), is(3));
    }

    @Test
    public void filter_by_uid() {
        List<Event> events =
                d2.eventModule().events
                        .byUid().eq("V1CerIi3sdL")
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_enrollment() {
        List<Event> events =
                d2.eventModule().events
                        .byEnrollmentUid().eq("JILLTkO4LKQ")
                        .get();

        assertThat(events.size(), is(2));
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<Event> events =
                d2.eventModule().events
                        .byCreated().eq(BaseNameableObject.DATE_FORMAT.parse("2017-08-07T15:47:25.959"))
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        List<Event> events =
                d2.eventModule().events
                        .byLastUpdated().eq(BaseNameableObject.DATE_FORMAT.parse("2018-09-14T22:26:39.094"))
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_created_at_client() {
        List<Event> events =
                d2.eventModule().events
                        .byCreatedAtClient().eq("2018-02-28T00:00:00.000")
                        .get();

        assertThat(events.size(), is(0));
    }

    @Test
    public void filter_by_last_updated_at_client() {
        List<Event> events =
                d2.eventModule().events
                        .byLastUpdatedAtClient().eq("2018-02-28T00:00:00.000")
                        .get();

        assertThat(events.size(), is(0));
    }

    @Test
    public void filter_by_status() {
        List<Event> events =
                d2.eventModule().events
                        .byStatus().eq(EventStatus.ACTIVE)
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_latitude() {
        List<Event> events =
                d2.eventModule().events
                        .byLatitude().eq("43.0")
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_longitude() {
        List<Event> events =
                d2.eventModule().events
                        .byLongitude().eq("21.0")
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_program() {
        List<Event> events =
                d2.eventModule().events
                        .byProgramUid().eq("lxAQ7Zs9VYR")
                        .get();

        assertThat(events.size(), is(3));
    }

    @Test
    public void filter_by_program_stage() {
        List<Event> events =
                d2.eventModule().events
                        .byProgramStageUid().eq("dBwrot7S420")
                        .get();

        assertThat(events.size(), is(3));
    }

    @Test
    public void filter_by_organization_unit() {
        List<Event> events =
                d2.eventModule().events
                        .byOrganisationUnitUid().eq("DiszpKrYNg8")
                        .get();

        assertThat(events.size(), is(3));
    }

    @Test
    public void filter_by_event_date() throws ParseException {
        List<Event> events =
                d2.eventModule().events
                        .byEventDate().eq(BaseNameableObject.DATE_FORMAT.parse("2017-02-27T00:00:00.000"))
                        .get();

        assertThat(events.size(), is(2));
    }

    @Test
    public void filter_by_complete_date() throws ParseException {
        List<Event> events =
                d2.eventModule().events
                        .byCompleteDate().eq(BaseNameableObject.DATE_FORMAT.parse("2016-02-27T00:00:00.000"))
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_due_date() throws ParseException {
        List<Event> events =
                d2.eventModule().events
                        .byDueDate().eq(BaseNameableObject.DATE_FORMAT.parse("2017-01-28T00:00:00.000"))
                        .get();

        assertThat(events.size(), is(1));
    }

    @Test
    public void filter_by_state() {
        List<Event> events =
                d2.eventModule().events
                        .byState().eq(State.SYNCED)
                        .get();

        assertThat(events.size(), is(3));
    }

    @Test
    public void filter_by_attribute_option_combo() {
        List<Event> events =
                d2.eventModule().events
                        .byAttributeOptionComboUid().eq("bRowv6yZOF2")
                        .get();

        assertThat(events.size(), is(2));
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<Event> events =
                d2.eventModule().events
                        .byTrackedEntityInstaceUid().eq("nWrB0TfWlvh")
                        .get();

        assertThat(events.size(), is(1));
    }



}
