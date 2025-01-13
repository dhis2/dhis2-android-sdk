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

package org.hisp.dhis.android.network.eventFilter

import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.FilterQueryCriteria
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.network.common.dto.DateFilterPeriodDTO

internal interface FilterQueryCriteriaDTO {
    val followUp: Boolean?
    val organisationUnit: String?
    val ouMode: String?
    val assignedUserMode: String?
    val order: String?
    val displayColumnOrder: List<String>?
    val eventStatus: String?
    val eventDate: DateFilterPeriodDTO?
    val lastUpdatedDate: DateFilterPeriodDTO?
}

internal fun <T> T.applyFilterQueryCriteriaFields(item: FilterQueryCriteriaDTO): T where
      T : FilterQueryCriteria.Builder<T> {
    followUp(item.followUp)
    organisationUnit(item.organisationUnit)
    item.ouMode?.let { ouMode(OrganisationUnitMode.valueOf(it)) }
    item.assignedUserMode?.let { assignedUserMode(AssignedUserMode.valueOf(it)) }
    order(item.order)
    displayColumnOrder(item.displayColumnOrder)
    item.eventStatus?.let { eventStatus(EventStatus.valueOf(it)) }
    eventDate(item.eventDate?.toDomain())
    lastUpdatedDate(item.lastUpdatedDate?.toDomain())
    return this
}
