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

import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.toDB

internal data class EntityQueryCriteriaDB(
    val enrollmentStatus: String?,
    val followUp: Boolean?,
    val organisationUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
    val orderProperty: String?,
    val displayColumnOrder: StringListDB?,
    val eventStatus: String?,
    val eventDate: String?,
    val lastUpdatedDate: String?,
    val programStage: String?,
    val trackedEntityInstances: StringListDB?,
    val enrollmentIncidentDate: String?,
    val enrollmentCreatedDate: String?,
    val trackedEntityType: String?,
) : EntityDB<EntityQueryCriteria> {
    override fun toDomain(): EntityQueryCriteria {
        return EntityQueryCriteria.builder()
            .enrollmentStatus(enrollmentStatus?.let { EnrollmentStatus.valueOf(it) })
            .followUp(followUp)
            .organisationUnit(organisationUnit)
            .ouMode(ouMode?.let { OrganisationUnitMode.valueOf(it) })
            .assignedUserMode(assignedUserMode?.let { AssignedUserMode.valueOf(it) })
            .order(orderProperty)
            .displayColumnOrder(displayColumnOrder?.toDomain())
            .eventStatus(eventStatus?.let { EventStatus.valueOf(it) })
            .eventDate(eventDate?.let {
                KotlinxJsonParser.instance.decodeFromString<DateFilterPeriodDB>(it).toDomain()
            })
            .lastUpdatedDate(lastUpdatedDate?.let {
                KotlinxJsonParser.instance.decodeFromString<DateFilterPeriodDB>(it).toDomain()
            })
            .programStage(programStage)
            .trackedEntityInstances(trackedEntityInstances?.toDomain())
            .enrollmentIncidentDate(enrollmentIncidentDate?.let {
                KotlinxJsonParser.instance.decodeFromString<DateFilterPeriodDB>(it).toDomain()
            })
            .enrollmentCreatedDate(enrollmentCreatedDate?.let {
                KotlinxJsonParser.instance.decodeFromString<DateFilterPeriodDB>(it).toDomain()
            })
            .trackedEntityType(trackedEntityType)
            .build()
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
        eventDate = eventDate()?.let {
            KotlinxJsonParser.instance.encodeToString(
                DateFilterPeriodDB.serializer(),
                it.toDB(),
            )
        },
        lastUpdatedDate = lastUpdatedDate()?.let {
            KotlinxJsonParser.instance.encodeToString(
                DateFilterPeriodDB.serializer(),
                it.toDB(),
            )
        },
        programStage = programStage(),
        trackedEntityInstances = trackedEntityInstances()?.toDB(),
        enrollmentIncidentDate = enrollmentIncidentDate()?.let {
            KotlinxJsonParser.instance.encodeToString(
                DateFilterPeriodDB.serializer(),
                it.toDB(),
            )
        },
        enrollmentCreatedDate = enrollmentCreatedDate()?.let {
            KotlinxJsonParser.instance.encodeToString(
                DateFilterPeriodDB.serializer(),
                it.toDB(),
            )
        },
        trackedEntityType = trackedEntityType()
    )
}
