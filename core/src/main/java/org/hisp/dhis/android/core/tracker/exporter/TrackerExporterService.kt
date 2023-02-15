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
package org.hisp.dhis.android.core.tracker.exporter

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.arch.api.payload.internal.NTIPayload
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import retrofit2.http.*

@Suppress("LongParameterList")
internal interface TrackerExporterService {
    @GET(TRACKED_ENTITY_INSTANCES)
    fun getTrackedEntityInstance(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterTrackedEntity>,
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstance: String?,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): Single<NTIPayload<NewTrackerImporterTrackedEntity>>

    @GET("$TRACKED_ENTITY_INSTANCES/{$TRACKED_ENTITY_INSTACE}")
    suspend fun getSingleTrackedEntityInstance(
        @Path(TRACKED_ENTITY_INSTACE) trackedEntityInstanceUid: String,
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterTrackedEntity>,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STATUS) programStatus: String?,
        @Query(PROGRAM_START_DATE) programStartDate: String?,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean
    ): NewTrackerImporterTrackedEntity

    @GET(TRACKED_ENTITY_INSTANCES)
    suspend fun getTrackedEntityInstances(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterTrackedEntity>,
        @Query(TRACKED_ENTITY_INSTACE) trackedEntityInstances: String? = null,
        @Query(OU) orgUnits: String? = null,
        @Query(OU_MODE) orgUnitMode: String? = null,
        @Query(PROGRAM) program: String? = null,
        @Query(PROGRAM_STAGE) programStage: String? = null,
        @Query(PROGRAM_START_DATE) programStartDate: String? = null,
        @Query(PROGRAM_END_DATE) programEndDate: String? = null,
        @Query(PROGRAM_STATUS) programStatus: String? = null,
        @Query(PROGRAM_INCIDENT_START_DATE) programIncidentStartDate: String? = null,
        @Query(PROGRAM_INCIDENT_END_DATE) programIncidentEndDate: String? = null,
        @Query(FOLLOW_UP) followUp: Boolean? = null,
        @Query(EVENT_START_DATE) eventStartDate: String? = null,
        @Query(EVENT_END_DATE) eventEndDate: String? = null,
        @Query(EVENT_STATUS) eventStatus: String? = null,
        @Query(TRACKED_ENTITY_TYPE) trackedEntityType: String? = null,
        @Query(QUERY) query: String? = null,
        @Query(ATTRIBUTE) attribute: List<String?>? = null,
        @Query(FILTER) filter: List<String?>? = null,
        @Query(ASSIGNED_USER_MODE) assignedUserMode: String? = null,
        @Query(LAST_UPDATED_START_DATE) lastUpdatedStartDate: String? = null,
        @Query(LAST_UPDATED_END_DATE) lastUpdatedEndDate: String? = null,
        @Query(ORDER) order: String? = null,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(INCLUDE_ALL_ATTRIBUTES) includeAllAttributes: Boolean,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean = false
    ): NTIPayload<NewTrackerImporterTrackedEntity>

    @GET("$ENROLLMENTS/{$ENROLLMENT}")
    fun getEnrollmentSingle(
        @Path(ENROLLMENT) enrollmentUid: String,
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEnrollment>
    ): Single<NewTrackerImporterEnrollment>

    @GET(EVENTS)
    fun getEvents(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEvent>,
        @Query(OU) orgUnit: String?,
        @Query(OU_MODE) orgUnitMode: String?,
        @Query(PROGRAM) program: String?,
        @Query(OCCURRED_AFTER) startDate: String?,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(LAST_UPDATED_START_DATE) lastUpdatedStartDate: String?,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean,
        @Query(EVENT) eventUid: String?
    ): Single<NTIPayload<NewTrackerImporterEvent>>

    @GET(EVENTS)
    fun getEventSingle(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEvent>,
        @Query(EVENT) eventUid: String,
        @Query(OU_MODE) orgUnitMode: String
    ): Single<NTIPayload<NewTrackerImporterEvent>>

    companion object {
        const val TRACKED_ENTITY_INSTANCES = "tracker/trackedEntities"
        const val ENROLLMENTS = "tracker/enrollments"
        const val EVENTS = "tracker/events"
        const val TRACKED_ENTITY_INSTACE = "trackedEntity"
        const val ENROLLMENT = "enrollment"
        const val EVENT = "event"
        const val OU = "orgUnit"
        const val OU_MODE = "ouMode"
        const val FIELDS = "fields"
        const val QUERY = "query"
        const val ATTRIBUTE = "attribute"
        const val PAGING = "paging"
        const val PAGE = "page"
        const val PAGE_SIZE = "pageSize"
        const val PROGRAM = "program"
        const val PROGRAM_STAGE = "programStage"
        const val PROGRAM_START_DATE = "enrollmentEnrolledAfter"
        const val PROGRAM_END_DATE = "enrollmentEnrolledBefore"
        const val PROGRAM_STATUS = "programStatus"
        const val PROGRAM_INCIDENT_START_DATE = "enrollmentOccurredAfter"
        const val PROGRAM_INCIDENT_END_DATE = "enrollmentOccurredBefore"
        const val FOLLOW_UP = "followUp"
        const val EVENT_STATUS = "eventStatus"
        const val EVENT_START_DATE = "eventOccurredAfter"
        const val EVENT_END_DATE = "eventOccurredBefore"
        const val OCCURRED_AFTER = "occurredAfter"
        const val TRACKED_ENTITY_TYPE = "trackedEntityType"
        const val INCLUDE_ALL_ATTRIBUTES = "includeAllAttributes"
        const val FILTER = "filter"
        const val LAST_UPDATED_START_DATE = "updatedAfter"
        const val LAST_UPDATED_END_DATE = "updatedBefore"
        const val INCLUDE_DELETED = "includeDeleted"
        const val ASSIGNED_USER_MODE = "assignedUserMode"
        const val ORDER = "order"
        const val DEFAULT_PAGE_SIZE = 20
    }
}
