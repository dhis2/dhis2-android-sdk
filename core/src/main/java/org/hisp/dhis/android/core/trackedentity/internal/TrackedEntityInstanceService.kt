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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.internal.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.SearchGrid
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class TrackedEntityInstanceService(private val client: HttpServiceClient) {
    suspend fun postTrackedEntityInstances(
        trackedEntityInstances: TrackedEntityInstancePayload?,
        strategy: String?,
    ): TEIWebResponse {
        return client.post {
            url(TRACKED_ENTITY_INSTANCES)
            parameters {
                attribute(STRATEGY to strategy)
            }
            body(trackedEntityInstances)
        }
    }

    suspend fun getTrackedEntityInstance(
        trackedEntityInstance: String,
        orgUnitMode: String?,
        fields: Fields<TrackedEntityInstance>,
        includeAllAttributes: Boolean,
        includeDeleted: Boolean,
    ): Payload<TrackedEntityInstance> {
        return client.get {
            url(TRACKED_ENTITY_INSTANCES)
            parameters {
                fields(fields)
                attribute(TRACKED_ENTITY_INSTACE to trackedEntityInstance)
                attribute(OU_MODE to orgUnitMode)
                attribute(INCLUDE_ALL_ATTRIBUTES to includeAllAttributes)
                attribute(INCLUDE_DELETED to includeDeleted)
            }
        }
    }

    suspend fun getSingleTrackedEntityInstance(
        trackedEntityInstanceUid: String,
        orgUnitMode: String?,
        program: String?,
        programStatus: String?,
        programStartDate: String?,
        fields: Fields<TrackedEntityInstance>,
        includeAllAttributes: Boolean,
        includeDeleted: Boolean,
    ): TrackedEntityInstance {
        return client.get {
            url("$TRACKED_ENTITY_INSTANCES/$trackedEntityInstanceUid")
            parameters {
                fields(fields)
                attribute(OU_MODE to orgUnitMode)
                attribute(PROGRAM to program)
                attribute(PROGRAM_STATUS to programStatus)
                attribute(PROGRAM_START_DATE to programStartDate)
                attribute(INCLUDE_ALL_ATTRIBUTES to includeAllAttributes)
                attribute(INCLUDE_DELETED to includeDeleted)
            }
        }
    }

    suspend fun getTrackedEntityInstances(
        trackedEntityInstances: String?,
        orgUnits: String?,
        orgUnitMode: String?,
        program: String?,
        programStatus: String?,
        programStartDate: String?,
        fields: Fields<TrackedEntityInstance>,
        order: String?,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
        lastUpdatedStartDate: String?,
        includeAllAttributes: Boolean,
        includeDeleted: Boolean,
    ): Payload<TrackedEntityInstance> {
        return client.get {
            url(TRACKED_ENTITY_INSTANCES)
            parameters {
                fields(fields)
                attribute(TRACKED_ENTITY_INSTACE to trackedEntityInstances)
                attribute(OU to orgUnits)
                attribute(OU_MODE to orgUnitMode)
                attribute(PROGRAM to program)
                attribute(PROGRAM_STATUS to programStatus)
                attribute(PROGRAM_START_DATE to programStartDate)
                attribute(ORDER to order)
                attribute(LAST_UPDATED_START_DATE to lastUpdatedStartDate)
                attribute(INCLUDE_ALL_ATTRIBUTES to includeAllAttributes)
                attribute(INCLUDE_DELETED to includeDeleted)
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    suspend fun query(
        trackedEntityInstance: String?,
        orgUnit: String?,
        orgUnitMode: String?,
        program: String?,
        programStage: String?,
        programStartDate: String?,
        programEndDate: String?,
        enrollmentStatus: String?,
        programIncidentStartDate: String?,
        programIncidentEndDate: String?,
        followUp: Boolean?,
        eventStartDate: String?,
        eventEndDate: String?,
        eventStatus: String?,
        trackedEntityType: String?,
        filter: List<String?>?,
        assignedUserMode: String?,
        lastUpdatedStartDate: String?,
        lastUpdatedEndDate: String?,
        order: String?,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
    ): SearchGrid {
        return client.get {
            url("$TRACKED_ENTITY_INSTANCES/query")
            parameters {
                filter(filter)
                attribute(TRACKED_ENTITY_INSTACE to trackedEntityInstance)
                attribute(OU to orgUnit)
                attribute(OU_MODE to orgUnitMode)
                attribute(PROGRAM to program)
                attribute(PROGRAM_STAGE to programStage)
                attribute(PROGRAM_START_DATE to programStartDate)
                attribute(PROGRAM_END_DATE to programEndDate)
                attribute(PROGRAM_STATUS to enrollmentStatus)
                attribute(PROGRAM_INCIDENT_START_DATE to programIncidentStartDate)
                attribute(PROGRAM_INCIDENT_END_DATE to programIncidentEndDate)
                attribute(FOLLOW_UP to followUp)
                attribute(EVENT_START_DATE to eventStartDate)
                attribute(EVENT_END_DATE to eventEndDate)
                attribute(EVENT_STATUS to eventStatus)
                attribute(TRACKED_ENTITY_TYPE to trackedEntityType)
                attribute(ASSIGNED_USER_MODE to assignedUserMode)
                attribute(LAST_UPDATED_START_DATE to lastUpdatedStartDate)
                attribute(LAST_UPDATED_END_DATE to lastUpdatedEndDate)
                attribute(ORDER to order)
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    companion object {
        const val TRACKED_ENTITY_INSTANCES = "trackedEntityInstances"
        const val TRACKED_ENTITY_INSTACE = "trackedEntityInstance"
        const val OU = "ou"
        const val OU_MODE = "ouMode"
        const val PROGRAM = "program"
        const val PROGRAM_STAGE = "programStage"
        const val PROGRAM_START_DATE = "programStartDate"
        const val PROGRAM_END_DATE = "programEndDate"
        const val PROGRAM_STATUS = "programStatus"
        const val PROGRAM_INCIDENT_START_DATE = "programIncidentStartDate"
        const val PROGRAM_INCIDENT_END_DATE = "programIncidentEndDate"
        const val FOLLOW_UP = "followUp"
        const val EVENT_STATUS = "eventStatus"
        const val EVENT_START_DATE = "eventStartDate"
        const val EVENT_END_DATE = "eventEndDate"
        const val TRACKED_ENTITY_TYPE = "trackedEntityType"
        const val INCLUDE_ALL_ATTRIBUTES = "includeAllAttributes"
        const val FILTER = "filter"
        const val STRATEGY = "strategy"
        const val LAST_UPDATED_START_DATE = "lastUpdatedStartDate"
        const val LAST_UPDATED_END_DATE = "lastUpdatedEndDate"
        const val INCLUDE_DELETED = "includeDeleted"
        const val ASSIGNED_USER_MODE = "assignedUserMode"
        const val ORDER = "order"
        const val DEFAULT_PAGE_SIZE = 20
    }
}
