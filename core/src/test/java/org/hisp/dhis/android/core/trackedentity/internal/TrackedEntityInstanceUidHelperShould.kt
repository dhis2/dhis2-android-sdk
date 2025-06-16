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
package org.hisp.dhis.android.core.trackedentity.internal

import com.google.common.truth.Truth
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TrackedEntityInstanceUidHelperShould {
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val tei1: TrackedEntityInstance = mock()
    private val tei2: TrackedEntityInstance = mock()
    private val enrollment: Enrollment = mock()
    private val event: Event = mock()

    private lateinit var uidHelper: TrackedEntityInstanceUidHelper

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        whenever(organisationUnitStore.selectUids()).thenReturn(listOf("ou1", "ou2"))
        uidHelper = TrackedEntityInstanceUidHelperImpl(organisationUnitStore)
    }

    @Test
    fun call_organisation_unit_select_uids() = runTest {
        uidHelper.getMissingOrganisationUnitUids(ArrayList())
        Mockito.verify(organisationUnitStore).selectUids()
    }

    @Test
    fun return_tei_org_unit_if_not_in_store() = runTest {
        whenever(tei1.organisationUnit()).thenReturn("ou3")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(1)
        Truth.assertThat(missingUids.iterator().next()).isEqualTo("ou3")
    }

    @Test
    fun not_return_tei_org_unit_if_in_store() = runTest {
        whenever(tei1.organisationUnit()).thenReturn("ou2")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(0)
    }

    @Test
    fun return_2_tei_org_unit_if_not_in_store() = runTest {
        whenever(tei1.organisationUnit()).thenReturn("ou3")
        whenever(tei2.organisationUnit()).thenReturn("ou4")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1, tei2))
        Truth.assertThat(missingUids.size).isEqualTo(2)
        Truth.assertThat(missingUids.contains("ou3")).isTrue()
        Truth.assertThat(missingUids.contains("ou4")).isTrue()
    }

    @Test
    fun return_enrollment_org_unit_if_not_in_store() = runTest {
        addToEnrollment("ou3")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(1)
        Truth.assertThat(missingUids.iterator().next()).isEqualTo("ou3")
    }

    @Test
    fun not_return_enrollment_org_unit_if_in_store() = runTest {
        addToEnrollment("ou2")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(0)
    }

    @Test
    fun return_event_org_unit_if_not_in_store() = runTest {
        addToEvent("ou3")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(1)
        Truth.assertThat(missingUids.iterator().next()).isEqualTo("ou3")
    }

    @Test
    fun not_return_event_org_unit_if_in_store() = runTest {
        addToEvent("ou2")
        val missingUids = uidHelper.getMissingOrganisationUnitUids(listOf(tei1))
        Truth.assertThat(missingUids.size).isEqualTo(0)
    }

    private fun addToEnrollment(organisationUnitId: String) {
        whenever(enrollment.organisationUnit()).thenReturn(organisationUnitId)
        whenever(TrackedEntityInstanceInternalAccessor.accessEnrollments(tei1)).thenReturn(listOf(enrollment))
    }

    private fun addToEvent(organisationUnitId: String) {
        whenever(event.organisationUnit()).thenReturn(organisationUnitId)
        whenever(EnrollmentInternalAccessor.accessEvents(enrollment)).thenReturn(listOf(event))
        whenever(TrackedEntityInstanceInternalAccessor.accessEnrollments(tei1)).thenReturn(listOf(enrollment))
    }
}
