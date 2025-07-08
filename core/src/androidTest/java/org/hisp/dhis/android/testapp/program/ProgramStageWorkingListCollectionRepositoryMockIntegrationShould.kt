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
package org.hisp.dhis.android.testapp.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramStageWorkingListCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val lists = d2.programModule().programStageWorkingLists()
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val lists = d2.programModule().programStageWorkingLists()
            .byProgram().eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program_stage() {
        val lists = d2.programModule().programStageWorkingLists()
            .byProgramStage().eq("dBwrot7S420")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_description() {
        val lists = d2.programModule().programStageWorkingLists()
            .byDescription().like("Test")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_event_status() {
        val lists = d2.programModule().programStageWorkingLists()
            .byEventStatus().eq(EventStatus.ACTIVE)
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_enrollment_status() {
        val lists = d2.programModule().programStageWorkingLists()
            .byEnrollmentStatus().eq(EnrollmentStatus.COMPLETED)
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit() {
        val lists = d2.programModule().programStageWorkingLists()
            .byOrganisationUnit().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_ou_mode() {
        val lists = d2.programModule().programStageWorkingLists()
            .byOuMode().eq(OrganisationUnitMode.SELECTED)
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_assigned_user_mode() {
        val lists = d2.programModule().programStageWorkingLists()
            .byAssignedUserMode().eq(AssignedUserMode.PROVIDED)
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_order() {
        val lists = d2.programModule().programStageWorkingLists()
            .byOrder().like("asc")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_column_order() {
        val lists = d2.programModule().programStageWorkingLists()
            .byDisplayColumnOrder().like("w75KJ2mc4zz")
            .blockingGet()

        assertThat(lists.size).isEqualTo(1)
    }

    @Test
    fun with_data_filters() {
        val lists = d2.programModule().programStageWorkingLists()
            .withDataFilters()
            .one()
            .blockingGet()!!

        assertThat(lists.programStageQueryCriteria()?.dataFilters()?.size).isEqualTo(1)
    }

    @Test
    fun with_attribute_value_filters() {
        val lists = d2.programModule().programStageWorkingLists()
            .withAttributeValueFilters()
            .one()
            .blockingGet()!!

        assertThat(lists.programStageQueryCriteria()?.attributeValueFilters()?.size).isEqualTo(1)
    }
}
