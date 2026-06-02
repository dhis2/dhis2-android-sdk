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

package org.hisp.dhis.android.core.trackedentity

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.FilterQueryCriteria
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

@ModelBuilder
data class EntityQueryCriteria(
    override val followUp: Boolean?,
    override val organisationUnit: String?,
    override val ouMode: OrganisationUnitMode?,
    override val assignedUserMode: AssignedUserMode?,
    override val order: String?,
    override val displayColumnOrder: List<String>?,
    override val eventDate: DateFilterPeriod?,
    override val lastUpdatedDate: DateFilterPeriod?,
    val eventStatus: EventStatus?,
    val programStage: String?,
    val trackedEntityInstances: List<String>?,
    val trackedEntityType: String?,
    val enrollmentStatus: EnrollmentStatus?,
    val enrollmentIncidentDate: DateFilterPeriod?,
    val enrollmentCreatedDate: DateFilterPeriod?,
    val attributeValueFilters: List<AttributeValueFilter>?,
) : FilterQueryCriteria, CoreObject {

    fun eventStatus(): EventStatus? = eventStatus
    fun programStage(): String? = programStage
    fun trackedEntityInstances(): List<String>? = trackedEntityInstances
    fun trackedEntityType(): String? = trackedEntityType
    fun enrollmentStatus(): EnrollmentStatus? = enrollmentStatus
    fun enrollmentIncidentDate(): DateFilterPeriod? = enrollmentIncidentDate
    fun enrollmentCreatedDate(): DateFilterPeriod? = enrollmentCreatedDate
    fun attributeValueFilters(): List<AttributeValueFilter>? = attributeValueFilters

    fun toBuilder(): Builder = EntityQueryCriteriaBuilder.from(this)

    class Builder : EntityQueryCriteriaBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
