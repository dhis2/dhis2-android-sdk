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

package org.hisp.dhis.android.network.programstageworkinglist

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageQueryCriteria
import org.hisp.dhis.android.network.common.dto.DateFilterPeriodDTO

@Serializable
internal data class ProgramStageQueryCriteriaDTO(
    val eventStatus: String?,
    val eventCreatedAt: DateFilterPeriodDTO?,
    val eventOccurredAt: DateFilterPeriodDTO?,
    val eventScheduledAt: DateFilterPeriodDTO?,
    val enrollmentStatus: String?,
    val enrolledAt: DateFilterPeriodDTO?,
    val enrollmentOccurredAt: DateFilterPeriodDTO?,
    val order: String?,
    val displayColumnOrder: List<String>?,
    val orgUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
    val dataFilters: List<ProgramStageWorkingListEventDataFilterDTO>?,
    val attributeValueFilters: List<ProgramStageWorkingListAttributeValueFilterDTO>?,
) {
    fun toDomain(programStageWorkingList: String): ProgramStageQueryCriteria {
        return ProgramStageQueryCriteria.builder()
            .eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
            .eventCreatedAt(eventCreatedAt?.toDomain())
            .eventOccurredAt(eventOccurredAt?.toDomain())
            .eventScheduledAt(eventScheduledAt?.toDomain())
            .enrollmentStatus(enrollmentStatus?.let { EnrollmentStatus.valueOf(it) })
            .enrolledAt(enrolledAt?.toDomain())
            .enrollmentOccurredAt(enrollmentOccurredAt?.toDomain())
            .order(order)
            .displayColumnOrder(displayColumnOrder)
            .orgUnit(orgUnit)
            .ouMode(ouMode?.let { OrganisationUnitMode.valueOf(it) })
            .assignedUserMode(assignedUserMode?.let { AssignedUserMode.valueOf(it) })
            .dataFilters(dataFilters?.map { it.toDomain(programStageWorkingList) })
            .attributeValueFilters(attributeValueFilters?.map { it.toDomain(programStageWorkingList) })
            .build()
    }
}
