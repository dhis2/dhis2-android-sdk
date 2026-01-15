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

package org.hisp.dhis.android.persistence.common

import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.FilterQueryCriteria
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

internal interface FilterQueryCriteriaDB {
    val followUp: Boolean?
    val organisationUnit: String?
    val ouMode: String?
    val assignedUserMode: String?
    val orderProperty: String?
    val displayColumnOrder: StringListDB?
    val eventDate: DateFilterPeriodDB?
    val lastUpdatedDate: DateFilterPeriodDB?
}

internal fun <T> T.applyFilterQueryCriteriaFields(item: FilterQueryCriteriaDB): T where
      T : FilterQueryCriteria.Builder<T> {
    followUp(item.followUp)
    organisationUnit(item.organisationUnit)
    item.ouMode?.let { ouMode(OrganisationUnitMode.valueOf(it)) }
    item.assignedUserMode?.let { assignedUserMode(AssignedUserMode.valueOf(it)) }
    order(item.orderProperty)
    item.displayColumnOrder?.let { displayColumnOrder(it.toDomain()) }
    item.eventDate?.let { eventDate(it.toDomain()) }
    item.lastUpdatedDate?.let { lastUpdatedDate(it.toDomain()) }
    return this
}
