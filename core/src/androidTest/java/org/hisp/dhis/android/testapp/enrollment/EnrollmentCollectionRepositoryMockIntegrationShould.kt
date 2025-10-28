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
package org.hisp.dhis.android.testapp.enrollment

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter.convertServerToClient
import org.hisp.dhis.android.core.arch.helpers.DateTimezoneConverter.convertServerToClientAsString
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class EnrollmentCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun allow_access_to_all_enrollments_without_children() {
        val enrollments = d2.enrollmentModule().enrollments().blockingGet()
        assertThat(enrollments.size).isEqualTo(2)

        val enrollment = enrollments[0]
        assertThat(enrollment.uid()).isEqualTo("enroll1")
        assertThat(enrollment.program()).isEqualTo("IpHINAT79UW")
    }

    @Test
    fun allow_access_to_one_enrollment_without_children() {
        val enrollment = d2.enrollmentModule().enrollments().uid("enroll1").blockingGet()
        assertThat(enrollment!!.uid()).isEqualTo("enroll1")
        assertThat(enrollment.program()).isEqualTo("IpHINAT79UW")
    }

    @Test
    fun include_notes_as_children() {
        val enrollment1 = d2.enrollmentModule().enrollments()
            .withNotes().uid("enroll1")
            .blockingGet()
        val enrollment2 = d2.enrollmentModule().enrollments()
            .withNotes().uid("enroll2")
            .blockingGet()

        assertThat(enrollment1!!.notes()!!.size).isEqualTo(2)
        assertThat(enrollment2!!.notes()!!.size).isEqualTo(2)
    }

    @Test
    fun filter_by_uid() {
        val enrollment = d2.enrollmentModule().enrollments()
            .byUid().eq("enroll1")
            .one().blockingGet()

        assertThat(enrollment!!.uid()).isEqualTo("enroll1")
        assertThat(enrollment.program()).isEqualTo("IpHINAT79UW")
    }

    @Test
    fun filter_by_created() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byCreated().eq(convertServerToClient("2019-01-10T13:40:28.322"))
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byLastUpdated().eq(convertServerToClient("2019-01-10T13:40:28.718"))
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created_as_client() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byCreatedAtClient().eq(convertServerToClientAsString("2018-01-08T13:40:28.718"))
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated_as_client() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byLastUpdatedAtClient().eq(convertServerToClientAsString("2018-01-11T13:40:28.718"))
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byOrganisationUnit().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_program() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byProgram().eq("IpHINAT79UW")
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_enrollment_date() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byEnrollmentDate().eq("2018-01-10T13:45:00.000".toJavaDate())
            .blockingGet()
        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_incident_date() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byIncidentDate().eq("2019-01-10T12:23:00.000".toJavaDate())
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_follow_up() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byFollowUp().isTrue
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_status() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byStatus().eq(EnrollmentStatus.ACTIVE)
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_tracked_entity_instance() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byTrackedEntityInstance().eq("nWrB0TfWlvD")
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_aggregated_sync_state() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byAggregatedSyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_sync_state() {
        val enrollments = d2.enrollmentModule().enrollments()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_deleted() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byDeleted().isFalse
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(2)
    }

    @Test
    fun filter_by_geometry_type() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byGeometryType().eq(FeatureType.POLYGON)
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun filter_by_geometry_coordinates() {
        val enrollments = d2.enrollmentModule().enrollments()
            .byGeometryCoordinates().eq("[4.1,2.6]")
            .blockingGet()

        assertThat(enrollments.size).isEqualTo(1)
    }

    @Test
    fun order_by_created() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByCreated(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll1")
        assertThat(enrollments[1].uid()).isEqualTo("enroll2")
    }

    @Test
    fun order_by_created_at_client() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByCreatedAtClient(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll2")
        assertThat(enrollments[1].uid()).isEqualTo("enroll1")
    }

    @Test
    fun order_by_last_updated() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByLastUpdated(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll1")
        assertThat(enrollments[1].uid()).isEqualTo("enroll2")
    }

    @Test
    fun order_by_last_updated_at_client() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByLastUpdatedAtClient(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll2")
        assertThat(enrollments[1].uid()).isEqualTo("enroll1")
    }

    @Test
    fun order_by_enrollment_date() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByEnrollmentDate(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll1")
        assertThat(enrollments[1].uid()).isEqualTo("enroll2")
    }

    @Test
    fun order_by_incident_date() {
        val enrollments = d2.enrollmentModule().enrollments()
            .orderByIncidentDate(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(enrollments[0].uid()).isEqualTo("enroll1")
        assertThat(enrollments[1].uid()).isEqualTo("enroll2")
    }

    @Test
    fun add_enrollments_to_the_repository() {
        val enrollments1 = d2.enrollmentModule().enrollments().blockingGet()
        assertThat(enrollments1.size).isEqualTo(2)

        val enrolmentUid = d2.enrollmentModule().enrollments()
            .blockingAdd(EnrollmentCreateProjection.create("DiszpKrYNg8", "lxAQ7Zs9VYR", "nWrB0TfWlvh"))

        val enrollments2 = d2.enrollmentModule().enrollments().blockingGet()
        assertThat(enrollments2.size).isEqualTo(3)

        val enrollment = d2.enrollmentModule().enrollments().uid(enrolmentUid).blockingGet()
        assertThat(enrollment!!.uid()).isEqualTo(enrolmentUid)

        d2.enrollmentModule().enrollments().uid(enrolmentUid).blockingDelete()
    }
}
