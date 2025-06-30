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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.event.EventObjectRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Assert
import org.junit.Test
import java.util.Date

class EventObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun update_organisation_unit() = runTest {
        val orgUnitUid = "new_org_unit"
        koin.get<OrganisationUnitStore>().insert(
            OrganisationUnit.builder().uid(orgUnitUid).build(),
        )

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
    fun update_event_date() {
        val eventDate = Date()

        val repository = objectRepository()

        repository.setEventDate(eventDate)
        assertThat(repository.blockingGet()!!.eventDate()).isEqualTo(eventDate)

        repository.blockingDelete()
    }

    @Test
    fun update_event_status_completed() {
        val eventStatus = EventStatus.COMPLETED

        val repository = objectRepository()

        repository.setStatus(eventStatus)
        assertThat(repository.blockingGet()!!.status()).isEqualTo(eventStatus)
        assertThat(repository.blockingGet()!!.completedDate()).isNotNull()
        assertThat(repository.blockingGet()!!.completedBy()).isNotNull()

        repository.blockingDelete()
    }

    @Test
    fun update_event_status_active() {
        val eventStatus = EventStatus.ACTIVE

        val repository = objectRepository()

        repository.setStatus(eventStatus)
        assertThat(repository.blockingGet()!!.status()).isEqualTo(eventStatus)
        assertThat(repository.blockingGet()!!.completedDate()).isNull()
        assertThat(repository.blockingGet()!!.completedBy()).isNull()

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
    fun update_completed_by() {
        val repository = objectRepository()

        repository.setCompletedBy("admin")
        assertThat(repository.blockingGet()!!.completedBy()).isEqualTo("admin")

        repository.blockingDelete()
    }

    @Test
    fun update_due_date() {
        val dueDate = Date()

        val repository = objectRepository()

        repository.setDueDate(dueDate)
        assertThat(repository.blockingGet()!!.dueDate()).isEqualTo(dueDate)

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

    @Test
    fun update_attribute_option_combo() = runTest {
        val attributeOptionCombo = "new_att_opt_comb"
        koin.get<CategoryOptionComboStore>()
            .insert(CategoryOptionCombo.builder().uid(attributeOptionCombo).build())

        val repository = objectRepository()

        repository.setAttributeOptionComboUid(attributeOptionCombo)
        assertThat(repository.blockingGet()!!.attributeOptionCombo())
            .isEqualTo(attributeOptionCombo)

        repository.delete()
        koin.get<CategoryOptionComboStore>().delete(attributeOptionCombo)
    }

    @Test(expected = D2Error::class)
    @Throws(D2Error::class)
    fun not_update_attribute_option_combo_if_not_exists() {
        val attributeOptionCombo = "new_att_opt_comb"

        val repository = objectRepository()

        try {
            repository.setAttributeOptionComboUid(attributeOptionCombo)
        } finally {
            repository.blockingDelete()
        }
    }

    @Test
    fun update_assigned_user() {
        val assignedUser = "aTwqot2S410"

        val repository = objectRepository()

        repository.setAssignedUser(assignedUser)
        assertThat(repository.blockingGet()!!.assignedUser()).isEqualTo(assignedUser)

        repository.blockingDelete()
    }

    @Throws(D2Error::class)
    private fun objectRepository(): EventObjectRepository {
        val eventUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                "enroll1",
                "lxAQ7Zs9VYR",
                "dBwrot7S420",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            ),
        )
        return d2.eventModule().events().uid(eventUid)
    }
}
