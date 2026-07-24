/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.event

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.FilterQueryCriteria
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

@ModelBuilder
data class EventQueryCriteria(
    override val followUp: Boolean?,
    override val organisationUnit: String?,
    override val ouMode: OrganisationUnitMode?,
    override val assignedUserMode: AssignedUserMode?,
    override val order: String?,
    override val displayColumnOrder: List<String>?,
    override val eventDate: DateFilterPeriod?,
    override val lastUpdatedDate: DateFilterPeriod?,
    val status: EventStatus?,
    val dataFilters: List<EventDataFilter>?,
    val events: List<String>?,
    val dueDate: DateFilterPeriod?,
    val completedDate: DateFilterPeriod?,
) : FilterQueryCriteria, CoreObject {

    fun status(): EventStatus? = status

    @Deprecated("Use status() instead")
    fun eventStatus(): EventStatus? = status()

    fun dataFilters(): List<EventDataFilter>? = dataFilters
    fun events(): List<String>? = events
    fun dueDate(): DateFilterPeriod? = dueDate
    fun completedDate(): DateFilterPeriod? = completedDate

    fun toBuilder(): Builder = EventQueryCriteriaBuilder.from(this)

    class Builder : EventQueryCriteriaBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
