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
package org.hisp.dhis.android.core.programstageworkinglist

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.network.programstageworkinglist.ProgramStageWorkingListDTO
import org.junit.Test

class ProgramStageWorkingListShould :
    BaseObjectKotlinxShould("programstageworkinglist/program_stage_working_list.json"),
    ObjectShould {

    @Test
    override fun map_from_json_string() {
        val workingListDTO = deserialize(ProgramStageWorkingListDTO.serializer())
        val workingList = workingListDTO.toDomain()

        assertThat(workingList.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2023-01-26T19:16:58.712"))
        assertThat(workingList.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2023-01-26T19:16:58.712"))
        assertThat(workingList.uid()).isEqualTo("NAgjOfWMXg6")
        assertThat(workingList.name()).isEqualTo("Test WL")
        assertThat(workingList.displayName()).isEqualTo("Test WL display")
        assertThat(workingList.program()!!.uid()).isEqualTo("uy2gU8kT1jF")
        assertThat(workingList.programStage()!!.uid()).isEqualTo("oRySG82BKE6")
        assertThat(workingList.description()).isEqualTo("Test WL definition")
        assertThat(workingList.programStageQueryCriteria()?.eventStatus()).isEqualTo(EventStatus.ACTIVE)
        assertThat(workingList.programStageQueryCriteria()?.eventCreatedAt()).isNotNull()
        assertThat(workingList.programStageQueryCriteria()?.eventOccurredAt()).isNotNull()
        assertThat(workingList.programStageQueryCriteria()?.eventScheduledAt()).isNotNull()
        assertThat(workingList.programStageQueryCriteria()?.enrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED)
        assertThat(workingList.programStageQueryCriteria()?.enrolledAt()).isNotNull()
        assertThat(workingList.programStageQueryCriteria()?.enrollmentOccurredAt()).isNotNull()
        assertThat(workingList.programStageQueryCriteria()?.orgUnit()).isEqualTo("Rp268JB6Ne4")
        assertThat(workingList.programStageQueryCriteria()?.ouMode()).isEqualTo(OrganisationUnitMode.SELECTED)
        assertThat(workingList.programStageQueryCriteria()?.assignedUserMode()).isEqualTo(AssignedUserMode.PROVIDED)
        assertThat(workingList.programStageQueryCriteria()?.order()).isEqualTo("w75KJ2mc4zz:asc")
        assertThat(workingList.programStageQueryCriteria()?.displayColumnOrder()?.size).isEqualTo(2)
        assertThat(workingList.programStageQueryCriteria()?.dataFilters()?.size).isEqualTo(1)
        assertThat(workingList.programStageQueryCriteria()?.attributeValueFilters()?.size).isEqualTo(1)
    }
}
