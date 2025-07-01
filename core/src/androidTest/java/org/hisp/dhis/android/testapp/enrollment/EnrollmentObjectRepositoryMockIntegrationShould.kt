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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.data.enrollment.EnrollmentSamples
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Assert
import org.junit.Test
import java.util.Date

class EnrollmentObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun update_organisation_unit() = runTest {
        val orgUnitUid = "new_org_unit"
        koin.get<OrganisationUnitStore>().insert(OrganisationUnit.builder().uid(orgUnitUid).build())

        val repository = objectRepository()

        repository.setOrganisationUnitUid(orgUnitUid)
        assertThat(repository.blockingGet()!!.organisationUnit()).isEqualTo(orgUnitUid)

        repository.blockingDelete()
        koin.get<OrganisationUnitStore>().delete(orgUnitUid)
    }

    @Test(expected = D2Error::class)
    @Throws(D2Error::class)
    fun not_update_organisation_unit_if_not_exists() {
        val orgUnitUid = "new_org_unit"

        val repository = objectRepository()

        try {
            repository.setOrganisationUnitUid(orgUnitUid)
        } finally {
            repository.blockingDelete()
        }
    }

    @Test
    fun update_enrollment_date() {
        val enrollmentDate = Date()

        val repository = objectRepository()

        repository.setEnrollmentDate(enrollmentDate)
        assertThat(repository.blockingGet()!!.enrollmentDate()).isEqualTo(enrollmentDate)

        repository.blockingDelete()
    }

    @Test
    fun not_update_status_when_passing_same_value() {
        val enrollmentUid = "enrollment_uid"

        d2.databaseAdapter().insert(
            EnrollmentTableInfo.TABLE_INFO.name(),
            null,
            EnrollmentSamples.get(
                enrollmentUid,
                "DiszpKrYNg8",
                "lxAQ7Zs9VYR",
                "nWrB0TfWlvh",
                Date(),
            ).toBuilder()
                .id(null)
                .aggregatedSyncState(State.SYNCED)
                .syncState(State.SYNCED)
                .status(EnrollmentStatus.ACTIVE)
                .build()
                .toContentValues(),
        )

        val repository = d2.enrollmentModule().enrollments().uid(enrollmentUid)

        repository.setStatus(EnrollmentStatus.ACTIVE)
        assertThat(repository.blockingGet()!!.aggregatedSyncState()).isEqualTo(State.SYNCED)

        repository.setStatus(EnrollmentStatus.COMPLETED)
        assertThat(repository.blockingGet()!!.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

        d2.databaseAdapter().delete(
            EnrollmentTableInfo.TABLE_INFO.name(),
            "${IdentifiableColumns.UID} = ?",
            arrayOf(enrollmentUid),
        )

        assertThat(repository.blockingExists()).isFalse()
    }

    @Test
    fun update_incident_date() {
        val incidentDate = Date()

        val repository = objectRepository()

        repository.setIncidentDate(incidentDate)
        assertThat(repository.blockingGet()!!.incidentDate()).isEqualTo(incidentDate)

        repository.blockingDelete()
    }

    @Test
    fun update_completed_date() {
        val completedDate = Date()

        val repository = objectRepository()

        repository.setCompletedDate(completedDate)
        assertThat(repository.blockingGet()!!.completedDate()).isEqualTo(completedDate)

        repository.blockingDelete()
    }

    @Test
    fun update_follow_up() {
        val repository = objectRepository()

        repository.setFollowUp(true)
        assertThat(repository.blockingGet()!!.followUp()).isTrue()

        repository.blockingDelete()
    }

    @Test
    fun update_enrollment_status_completed() {
        val enrollmentStatus = EnrollmentStatus.COMPLETED

        val repository = objectRepository()

        repository.setStatus(enrollmentStatus)
        assertThat(repository.blockingGet()!!.status()).isEqualTo(enrollmentStatus)
        assertThat(repository.blockingGet()!!.completedDate()).isNotNull()

        repository.blockingDelete()
    }

    @Test
    fun update_enrollment_status_active() {
        val enrollmentStatus = EnrollmentStatus.ACTIVE

        val repository = objectRepository()

        repository.setStatus(enrollmentStatus)
        assertThat(repository.blockingGet()!!.status()).isEqualTo(enrollmentStatus)
        assertThat(repository.blockingGet()!!.completedDate()).isNull()

        repository.blockingDelete()
    }

    @Test
    fun update_geometry() {
        val geometry = Geometry.builder()
            .type(FeatureType.POINT)
            .coordinates("[10.00, 11.00]")
            .build()

        val repository = objectRepository()

        repository.setGeometry(geometry)
        assertThat(repository.blockingGet()!!.geometry()).isEqualTo(geometry)

        repository.blockingDelete()
    }

    @Test
    fun update_invalid_geometry() {
        val geometry = Geometry.builder()
            .type(FeatureType.POINT)
            .build()

        val repository = objectRepository()

        try {
            repository.setGeometry(geometry)
            Assert.fail("Invalid geometry should fail")
        } catch (d2Error: D2Error) {
            assertThat(d2Error.errorCode()).isEquivalentAccordingToCompareTo(D2ErrorCode.INVALID_GEOMETRY_VALUE)
        } finally {
            repository.blockingDelete()
        }
    }

    @Throws(D2Error::class)
    private fun objectRepository(): EnrollmentObjectRepository {
        val enrollmentUid = d2.enrollmentModule().enrollments().blockingAdd(
            EnrollmentCreateProjection.create(
                "DiszpKrYNg8",
                "lxAQ7Zs9VYR",
                "nWrB0TfWlvh",
            ),
        )
        return d2.enrollmentModule().enrollments().uid(enrollmentUid)
    }
}
