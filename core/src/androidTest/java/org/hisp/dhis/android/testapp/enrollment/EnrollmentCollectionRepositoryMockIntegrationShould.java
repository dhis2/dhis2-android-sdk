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

package org.hisp.dhis.android.testapp.enrollment;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class EnrollmentCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void allow_access_to_all_enrollments_without_children() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments.get();
        assertThat(enrollments.size(), is(2));

        Enrollment enrollment = enrollments.get(0);
        assertThat(enrollment.uid(), is("enroll1"));
        assertThat(enrollment.program(), is("lxAQ7Zs9VYR"));
        assertThat(enrollment.events() == null, is(true));
    }

    @Test
    public void allow_access_to_one_enrollment_without_children() {
        Enrollment enrollment = d2.enrollmentModule().enrollments.uid("enroll1").get();
        assertThat(enrollment.uid(), is("enroll1"));
        assertThat(enrollment.program(), is("lxAQ7Zs9VYR"));
        assertThat(enrollment.events() == null, is(true));
    }

    @Test
    public void include_events_as_children() {
        Enrollment enrollment1 = d2.enrollmentModule().enrollments
                .withEvents().uid("enroll1").get();
        Enrollment enrollment2 = d2.enrollmentModule().enrollments
                .withEvents().uid("enroll2").get();
        assertThat(enrollment1.events().size(), is(1));
        assertThat(enrollment2.events().size(), is(1));
    }

    @Test
    public void include_notes_as_children() {
        Enrollment enrollment1 = d2.enrollmentModule().enrollments
                .withNotes().uid("enroll1").get();
        Enrollment enrollment2 = d2.enrollmentModule().enrollments
                .withNotes().uid("enroll2").get();
        assertThat(enrollment1.notes().size(), is(2));
        assertThat(enrollment2.notes().size(), is(2));
    }

    @Test
    public void filter_by_uid() {
        Enrollment enrollment = d2.enrollmentModule().enrollments
                .byUid().eq("enroll1")
                .one().get();
        assertThat(enrollment.uid(), is("enroll1"));
        assertThat(enrollment.program(), is("lxAQ7Zs9VYR"));
    }

    @Test
    public void filter_by_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.322");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byCreated().eq(created)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.718");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byLastUpdated().eq(lastUpdated)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_created_as_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byCreatedAtClient().eq("2019-01-08T13:40:28.718")
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_last_updated_as_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byLastUpdatedAtClient().eq("2018-01-11T13:40:28.718")
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_organisation_unit() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byOrganisationUnit().eq("DiszpKrYNg8")
                .get();
        assertThat(enrollments.size(), is(2));
    }

    @Test
    public void filter_by_program() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byProgram().eq("lxAQ7Zs9VYR")
                .get();
        assertThat(enrollments.size(), is(2));
    }

    @Test
    public void filter_by_enrollment_date() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2018-01-10T00:00:00.000");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byEnrollmentDate().eq(created)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_incident_date() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T00:00:00.000");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byIncidentDate().eq(lastUpdated)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_follow_up() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byFollowUp().isTrue()
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_status() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byStatus().eq(EnrollmentStatus.ACTIVE)
                .get();
        assertThat(enrollments.size(), is(2));
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byTrackedEntityInstance().eq("nWrB0TfWlvD")
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_coordinate_latitude() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byCoordinateLatitude().eq(2.6)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_coordinate_longitude() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments
                .byCoordinateLongitude().eq(4.1)
                .get();
        assertThat(enrollments.size(), is(1));
    }

    @Test
    public void filter_by_state() {
        List<Enrollment> enrollments =
                d2.enrollmentModule().enrollments
                        .byState().eq(State.SYNCED)
                        .get();
        assertThat(enrollments.size(), is(4));
    }

    @Test
    public void add_enrollments_to_the_repository() throws D2Error {
        List<Enrollment> enrollments1 = d2.enrollmentModule().enrollments.get();
        assertThat(enrollments1.size(), is(2));

        String enrolmentUid = d2.enrollmentModule().enrollments.add(EnrollmentCreateProjection.create(
                "DiszpKrYNg8", "lxAQ7Zs9VYR", "nWrB0TfWlvh"));

        List<Enrollment> enrollments2 = d2.enrollmentModule().enrollments.get();
        assertThat(enrollments2.size(), is(3));

        Enrollment enrollment = d2.enrollmentModule().enrollments.uid(enrolmentUid).get();
        assertThat(enrollment.uid(), is(enrolmentUid));

        d2.enrollmentModule().enrollments.uid(enrolmentUid).delete();
    }
}