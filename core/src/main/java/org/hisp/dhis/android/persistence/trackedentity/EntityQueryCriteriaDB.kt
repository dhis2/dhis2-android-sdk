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

package org.hisp.dhis.android.persistence.trackedentity

import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.FilterQueryCriteriaDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyFilterQueryCriteriaFields
import org.hisp.dhis.android.persistence.common.toDB

internal data class EntityQueryCriteriaDB(
    val enrollmentStatus: String?,
    override val followUp: Boolean?,
    override val organisationUnit: String?,
    override val ouMode: String?,
    override val assignedUserMode: String?,
    override val orderProperty: String?,
    override val displayColumnOrder: StringListDB?,
    override val eventDate: DateFilterPeriodDB?,
    override val lastUpdatedDate: DateFilterPeriodDB?,
    val eventStatus: String?,
    val programStage: String?,
    val trackedEntityInstances: StringListDB?,
    val enrollmentIncidentDate: DateFilterPeriodDB?,
    val enrollmentCreatedDate: DateFilterPeriodDB?,
    val trackedEntityType: String?,
) : EntityDB<EntityQueryCriteria>, FilterQueryCriteriaDB {

    override fun toDomain(): EntityQueryCriteria {
        return EntityQueryCriteria.builder().apply {
            applyFilterQueryCriteriaFields(this@EntityQueryCriteriaDB)
            eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
            enrollmentStatus(enrollmentStatus?.let { EnrollmentStatus.valueOf(it) })
            programStage(programStage)
            trackedEntityInstances(trackedEntityInstances?.toDomain())
            enrollmentIncidentDate(enrollmentIncidentDate?.toDomain())
            enrollmentCreatedDate(enrollmentCreatedDate?.toDomain())
            trackedEntityType(trackedEntityType)
        }.build()
    }
}

internal fun EntityQueryCriteria.toDB(): EntityQueryCriteriaDB {
    return EntityQueryCriteriaDB(
        enrollmentStatus = enrollmentStatus()?.name,
        followUp = followUp(),
        organisationUnit = organisationUnit(),
        ouMode = ouMode()?.name,
        assignedUserMode = assignedUserMode()?.name,
        orderProperty = order(),
        displayColumnOrder = displayColumnOrder()?.toDB(),
        eventStatus = eventStatus()?.name,
        eventDate = eventDate()?.toDB(),
        lastUpdatedDate = lastUpdatedDate()?.toDB(),
        programStage = programStage(),
        trackedEntityInstances = trackedEntityInstances()?.toDB(),
        enrollmentIncidentDate = enrollmentIncidentDate()?.toDB(),
        enrollmentCreatedDate = enrollmentCreatedDate()?.toDB(),
        trackedEntityType = trackedEntityType(),
    )
}
