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

package org.hisp.dhis.android.testapp.enrollment;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class EnrollmentCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_all_enrollments_without_children() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments().blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);

        Enrollment enrollment = enrollments.get(0);
        assertThat(enrollment.uid()).isEqualTo("enroll1");
        assertThat(enrollment.program()).isEqualTo("lxAQ7Zs9VYR");
    }

    @Test
    public void allow_access_to_one_enrollment_without_children() {
        Enrollment enrollment = d2.enrollmentModule().enrollments().uid("enroll1").blockingGet();
        assertThat(enrollment.uid()).isEqualTo("enroll1");
        assertThat(enrollment.program()).isEqualTo("lxAQ7Zs9VYR");
    }

    @Test
    public void include_notes_as_children() {
        Enrollment enrollment1 = d2.enrollmentModule().enrollments()
                .withNotes().uid("enroll1").blockingGet();
        Enrollment enrollment2 = d2.enrollmentModule().enrollments()
                .withNotes().uid("enroll2").blockingGet();
        assertThat(enrollment1.notes().size()).isEqualTo(2);
        assertThat(enrollment2.notes().size()).isEqualTo(2);
    }

    @Test
    public void filter_by_uid() {
        Enrollment enrollment = d2.enrollmentModule().enrollments()
                .byUid().eq("enroll1")
                .one().blockingGet();
        assertThat(enrollment.uid()).isEqualTo("enroll1");
        assertThat(enrollment.program()).isEqualTo("lxAQ7Zs9VYR");
    }

    @Test
    public void filter_by_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.322");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byCreated().eq(created)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T13:40:28.718");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byLastUpdated().eq(lastUpdated)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_created_as_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byCreatedAtClient().eq("2018-01-08T13:40:28.718")
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated_as_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byLastUpdatedAtClient().eq("2018-01-11T13:40:28.718")
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_organisation_unit() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byOrganisationUnit().eq("DiszpKrYNg8")
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byProgram().eq("lxAQ7Zs9VYR")
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_enrollment_date() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2018-01-10T13:45:00.000");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byEnrollmentDate().eq(created)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_incident_date() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2019-01-10T12:23:00.000");
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byIncidentDate().eq(lastUpdated)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_follow_up() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byFollowUp().isTrue()
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_status() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byStatus().eq(EnrollmentStatus.ACTIVE)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq("nWrB0TfWlvD")
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_aggregated_sync_state() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byAggregatedSyncState().eq(State.SYNCED)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_sync_state() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .bySyncState().eq(State.SYNCED)
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_deleted() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .byDeleted().isFalse()
                .blockingGet();
        assertThat(enrollments.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_geometry_type() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                        .byGeometryType().eq(FeatureType.POLYGON)
                        .blockingGet();

        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_geometry_coordinates() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                        .byGeometryCoordinates().eq("[4.1, 2.6]")
                        .blockingGet();

        assertThat(enrollments.size()).isEqualTo(1);
    }

    @Test
    public void order_by_created() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll1");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll2");
    }

    @Test
    public void order_by_created_at_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByCreatedAtClient(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll2");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll1");
    }

    @Test
    public void order_by_last_updated() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByLastUpdated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll1");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll2");
    }

    @Test
    public void order_by_last_updated_at_client() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll2");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll1");
    }

    @Test
    public void order_by_enrollment_date() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByEnrollmentDate(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll1");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll2");
    }

    @Test
    public void order_by_incident_date() {
        List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                .orderByIncidentDate(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(enrollments.get(0).uid()).isEqualTo("enroll1");
        assertThat(enrollments.get(1).uid()).isEqualTo("enroll2");
    }

    @Test
    public void add_enrollments_to_the_repository() throws D2Error {
        List<Enrollment> enrollments1 = d2.enrollmentModule().enrollments().blockingGet();
        assertThat(enrollments1.size()).isEqualTo(2);

        String enrolmentUid = d2.enrollmentModule().enrollments().blockingAdd(EnrollmentCreateProjection.create(
                "DiszpKrYNg8", "lxAQ7Zs9VYR", "nWrB0TfWlvh"));

        List<Enrollment> enrollments2 = d2.enrollmentModule().enrollments().blockingGet();
        assertThat(enrollments2.size()).isEqualTo(3);

        Enrollment enrollment = d2.enrollmentModule().enrollments().uid(enrolmentUid).blockingGet();
        assertThat(enrollment.uid()).isEqualTo(enrolmentUid);

        d2.enrollmentModule().enrollments().uid(enrolmentUid).blockingDelete();
    }
}