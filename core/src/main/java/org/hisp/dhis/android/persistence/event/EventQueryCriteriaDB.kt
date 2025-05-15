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

package org.hisp.dhis.android.persistence.event

import org.hisp.dhis.android.core.event.EventQueryCriteria
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.FilterQueryCriteriaDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyFilterQueryCriteriaFields
import org.hisp.dhis.android.persistence.common.toDB

internal data class EventQueryCriteriaDB(
    override val followUp: Boolean?,
    override val organisationUnit: String?,
    override val ouMode: String?,
    override val assignedUserMode: String?,
    override val orderProperty: String?,
    override val displayColumnOrder: StringListDB?,
    override val eventStatus: String?,
    override val eventDate: DateFilterPeriodDB?,
    override val lastUpdatedDate: DateFilterPeriodDB?,
    val events: StringListDB?,
    val dueDate: DateFilterPeriodDB?,
    val completedDate: DateFilterPeriodDB?,
) : EntityDB<EventQueryCriteria>, FilterQueryCriteriaDB {

    override fun toDomain(): EventQueryCriteria {
        return EventQueryCriteria.builder().apply {
            applyFilterQueryCriteriaFields(this@EventQueryCriteriaDB)
            events(events?.toDomain())
            dueDate(dueDate?.toDomain())
            completedDate(completedDate?.toDomain())
        }.build()
    }
}

internal fun EventQueryCriteria.toDB(): EventQueryCriteriaDB {
    return EventQueryCriteriaDB(
        followUp = followUp(),
        organisationUnit = organisationUnit(),
        ouMode = ouMode()?.name,
        assignedUserMode = assignedUserMode()?.name,
        orderProperty = order(),
        displayColumnOrder = displayColumnOrder()?.toDB(),
        eventStatus = eventStatus()?.name,
        eventDate = eventDate()?.toDB(),
        lastUpdatedDate = lastUpdatedDate()?.toDB(),
        events = events()?.toDB(),
        dueDate = dueDate()?.toDB(),
        completedDate = completedDate()?.toDB(),
    )
}
