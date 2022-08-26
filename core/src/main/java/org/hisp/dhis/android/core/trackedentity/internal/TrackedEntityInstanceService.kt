/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.SearchGrid
import retrofit2.Call
import retrofit2.http.*

@Suppress("LongParameterList")
internal interface TrackedEntityInstanceService {
    @POST(TRACKED_ENTITY_INSTANCES)
    fun postTrackedEntityInstances(
        @Body trackedEntityInstances: TrackedEntityInstancePayload?,
        @Query(STRATEGY) strategy: String?
    ): Call<TEIWebResponse>

    @GET(TRACKED_ENTITY_INSTANCES)
    fun getTrackedEntityInstance(
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstance: String?,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(FIELDS) @Which fields: Fields<TrackedEntityInstance?>?,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): Single<Payload<TrackedEntityInstance>>

    @GET(TRACKED_ENTITY_INSTANCES)
    fun getTrackedEntityInstanceAsCall(
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstance: String?,
        @Query(FIELDS) @Which fields: Fields<TrackedEntityInstance?>?,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): Call<Payload<TrackedEntityInstance>>

    @GET("$TRACKED_ENTITY_INSTANCES/{$TRACKED_ENTITY_INSTACE}")
    fun getSingleTrackedEntityInstance(
        @Path(TRACKED_ENTITY_INSTACE) trackedEntityInstanceUid: String,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STATUS) programStatus: String?,
        @Query(PROGRAM_START_DATE) programStartDate: String?,
        @Query(FIELDS) @Which fields: Fields<TrackedEntityInstance>,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): Call<TrackedEntityInstance>

    @GET(TRACKED_ENTITY_INSTANCES)
    fun getTrackedEntityInstances(
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstances: String?,
        @Query(OU) orgUnits: String?,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STATUS) programStatus: String?,
        @Query(PROGRAM_START_DATE) programStartDate: String?,
        @Query(FIELDS) @Which fields: Fields<TrackedEntityInstance>,
        @Query(ORDER) order: String?,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(LAST_UPDATED_START_DATE) lastUpdatedStartDate: String?,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): Single<Payload<TrackedEntityInstance>>

    @GET("$TRACKED_ENTITY_INSTANCES/query")
    fun query(
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstance: String?,
        @Query(OU) orgUnit: String?,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STAGE) programStage: String?,
        @Query(PROGRAM_START_DATE) programStartDate: String?,
        @Query(PROGRAM_END_DATE) programEndDate: String?,
        @Query(PROGRAM_STATUS) enrollmentStatus: String?,
        @Query(PROGRAM_INCIDENT_START_DATE) programIncidentStartDate: String?,
        @Query(PROGRAM_INCIDENT_END_DATE) programIncidentEndDate: String?,
        @Query(FOLLOW_UP) followUp: Boolean?,
        @Query(EVENT_START_DATE) eventStartDate: String?,
        @Query(EVENT_END_DATE) eventEndDate: String?,
        @Query(EVENT_STATUS) eventStatus: String?,
        @Query(TRACKED_ENTITY_TYPE) trackedEntityType: String?,
        @Query(QUERY) query: String?,
        @Query(ATTRIBUTE) attribute: List<String?>?,
        @Query(FILTER) filter: List<String?>?,
        @Query(ASSIGNED_USER_MODE) assignedUserMode: String?,
        @Query(LAST_UPDATED_START_DATE) lastUpdatedStartDate: String?,
        @Query(LAST_UPDATED_END_DATE) lastUpdatedEndDate: String?,
        @Query(ORDER) order: String?,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int,
        @Query(PAGE_SIZE) pageSize: Int
    ): Call<SearchGrid>

    companion object {
        const val TRACKED_ENTITY_INSTANCES = "trackedEntityInstances"
        const val TRACKED_ENTITY_INSTACE = "trackedEntityInstance"
        const val OU = "ou"
        const val OU_MODE = "ouMode"
        const val FIELDS = "fields"
        const val QUERY = "query"
        const val ATTRIBUTE = "attribute"
        const val PAGING = "paging"
        const val PAGE = "page"
        const val PAGE_SIZE = "pageSize"
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
