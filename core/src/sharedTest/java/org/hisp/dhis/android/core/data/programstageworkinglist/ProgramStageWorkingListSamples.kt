/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.data.programstageworkinglist

import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableProperties
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageQueryCriteria
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList

object ProgramStageWorkingListSamples {

    fun getProgramStageWorkingList(): ProgramStageWorkingList {
        val builder = ProgramStageWorkingList.builder()
        fillIdentifiableProperties(builder)
        return builder
            .id(1L)
            .description("description")
            .program(ObjectWithUid.create("program"))
            .programStage(ObjectWithUid.create("programStage"))
            .programStageQueryCriteria(
                ProgramStageQueryCriteria.builder()
                    .status(EventStatus.COMPLETED)
                    .eventCreatedAt(
                        DateFilterPeriod.builder()
                            .startDate(parseDate("2014-05-01"))
                            .endDate(parseDate("2014-05-01"))
                            .type(DatePeriodType.ABSOLUTE)
                            .build()
                    )
                    .scheduledAt(
                        DateFilterPeriod.builder()
                            .startBuffer(-5)
                            .endBuffer(5)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .enrollmentStatus(EnrollmentStatus.ACTIVE)
                    .enrolledAt(
                        DateFilterPeriod.builder()
                            .period(RelativePeriod.TODAY)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .enrollmentOccurredAt(
                        DateFilterPeriod.builder()
                            .period(RelativePeriod.LAST_2_SIXMONTHS)
                            .type(DatePeriodType.RELATIVE)
                            .build()
                    )
                    .order("order")
                    .displayColumnOrder(
                        listOf(
                            "column1",
                            "column2"
                        )
                    )
                    .orgUnit("orgunit")
                    .ouMode(OrganisationUnitMode.ACCESSIBLE)
                    .assignedUserMode(AssignedUserMode.CURRENT)
                    .build()
            )
            .build()
    }
}
