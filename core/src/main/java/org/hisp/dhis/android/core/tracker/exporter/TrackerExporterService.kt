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
package org.hisp.dhis.android.core.tracker.exporter

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.arch.api.payload.internal.TrackerPayload
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import retrofit2.http.*

@Suppress("LongParameterList")
internal interface TrackerExporterService {
    @GET("$TRACKED_ENTITIES_API/{$TRACKED_ENTITY}")
    suspend fun getSingleTrackedEntityInstance(
        @Path(TRACKED_ENTITY) trackedEntityInstanceUid: String,
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterTrackedEntity>,
        @QueryMap orgUnitMode: Map<String, String> = emptyMap(),
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STATUS) programStatus: String?,
        @Query(ENROLLMENT_ENROLLED_AFTER) programStartDate: String?,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean,
    ): NewTrackerImporterTrackedEntity

    @GET(TRACKED_ENTITIES_API)
    suspend fun getTrackedEntityInstances(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterTrackedEntity>,
        @QueryMap trackedEntityInstances: Map<String, String> = emptyMap(),
        @QueryMap orgUnits: Map<String, String> = emptyMap(),
        @QueryMap orgUnitMode: Map<String, String> = emptyMap(),
        @Query(PROGRAM) program: String? = null,
        @Query(PROGRAM_STAGE) programStage: String? = null,
        @Query(ENROLLMENT_ENROLLED_AFTER) programStartDate: String? = null,
        @Query(ENROLLMENT_ENROLLED_BEFORE) programEndDate: String? = null,
        @Query(PROGRAM_STATUS) programStatus: String? = null,
        @Query(ENROLLMENT_OCCURRED_AFTER) programIncidentStartDate: String? = null,
        @Query(ENROLLMENT_OCCURRED_BEFORE) programIncidentEndDate: String? = null,
        @Query(FOLLOW_UP) followUp: Boolean? = null,
        @Query(EVENT_START_DATE) eventStartDate: String? = null,
        @Query(EVENT_END_DATE) eventEndDate: String? = null,
        @Query(EVENT_STATUS) eventStatus: String? = null,
        @Query(TRACKED_ENTITY_TYPE) trackedEntityType: String? = null,
        @Query(FILTER) filter: List<String?>? = null,
        @Query(ASSIGNED_USER_MODE) assignedUserMode: String? = null,
        @Query(UPDATED_AFTER) lastUpdatedStartDate: String? = null,
        @Query(UPDATED_BEFORE) lastUpdatedEndDate: String? = null,
        @Query(ORDER) order: String? = null,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int?,
        @Query(PAGE_SIZE) pageSize: Int?,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean = false,
    ): TrackerPayload<NewTrackerImporterTrackedEntity>

    @GET("$ENROLLMENTS_API/{$ENROLLMENT}")
    suspend fun getEnrollmentSingle(
        @Path(ENROLLMENT) enrollmentUid: String,
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEnrollment>,
    ): NewTrackerImporterEnrollment

    @GET(EVENTS_API)
    suspend fun getEvents(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEvent>,
        @Query(ORG_UNIT) orgUnit: String?,
        @QueryMap orgUnitMode: Map<String, String> = emptyMap(),
        @Query(STATUS) status: String? = null,
        @Query(PROGRAM) program: String?,
        @Query(PROGRAM_STAGE) programStage: String? = null,
        @Query(PROGRAM_STATUS) programStatus: String? = null,
        @Query(FILTER) filter: List<String?>? = null,
        @Query(FILTER_ATTRIBUTES) filterAttributes: List<String?>? = null,
        @Query(FOLLOW_UP) followUp: Boolean? = null,
        @Query(OCCURRED_AFTER) occurredAfter: String? = null,
        @Query(OCCURRED_BEFORE) occurredBefore: String? = null,
        @Query(SCHEDULED_AFTER) scheduledAfter: String? = null,
        @Query(SCHEDULED_BEFORE) scheduledBefore: String? = null,
        @Query(ENROLLMENT_ENROLLED_AFTER) enrollmentEnrolledAfter: String? = null,
        @Query(ENROLLMENT_ENROLLED_BEFORE) enrollmentEnrolledBefore: String? = null,
        @Query(ENROLLMENT_OCCURRED_AFTER) enrollmentOccurredAfter: String? = null,
        @Query(ENROLLMENT_OCCURRED_BEFORE) enrollmentOccurredBefore: String? = null,
        @Query(ORDER) order: String? = null,
        @Query(ASSIGNED_USER_MODE) assignedUserMode: String? = null,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int?,
        @Query(PAGE_SIZE) pageSize: Int?,
        @Query(UPDATED_AFTER) updatedAfter: String?,
        @Query(UPDATED_BEFORE) updatedBefore: String? = null,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean,
        @QueryMap eventUid: Map<String, String> = emptyMap(),
    ): TrackerPayload<NewTrackerImporterEvent>

    @GET(EVENTS_API)
    suspend fun getEventSingle(
        @Query(FIELDS) @Which fields: Fields<NewTrackerImporterEvent>,
        @QueryMap eventUid: Map<String, String>? = null,
        @QueryMap orgUnitMode: Map<String, String>? = null,
    ): TrackerPayload<NewTrackerImporterEvent>

    companion object {
        const val TRACKED_ENTITIES_API = "tracker/trackedEntities"
        const val ENROLLMENTS_API = "tracker/enrollments"
        const val EVENTS_API = "tracker/events"
        const val TRACKED_ENTITY = "trackedEntity"
        const val TRACKED_ENTITIES = "trackedEntities"
        const val ENROLLMENT = "enrollment"
        const val EVENT = "event"
        const val EVENTS = "events"
        const val ORG_UNIT = "orgUnit"
        const val ORG_UNITS = "orgUnits"
        const val OU_MODE = "orgUnitMode"
        const val OU_MODE_BELOW_41 = "ouMode"
        const val FIELDS = "fields"
        const val PAGING = "paging"
        const val PAGE = "page"
        const val PAGE_SIZE = "pageSize"
        const val PROGRAM = "program"
        const val PROGRAM_STAGE = "programStage"
        const val ENROLLMENT_ENROLLED_AFTER = "enrollmentEnrolledAfter"
        const val ENROLLMENT_ENROLLED_BEFORE = "enrollmentEnrolledBefore"
        const val PROGRAM_STATUS = "programStatus"
        const val ENROLLMENT_OCCURRED_AFTER = "enrollmentOccurredAfter"
        const val ENROLLMENT_OCCURRED_BEFORE = "enrollmentOccurredBefore"
        const val FOLLOW_UP = "followUp"
        const val STATUS = "status"
        const val EVENT_STATUS = "eventStatus"
        const val EVENT_START_DATE = "eventOccurredAfter"
        const val EVENT_END_DATE = "eventOccurredBefore"
        const val OCCURRED_AFTER = "occurredAfter"
        const val OCCURRED_BEFORE = "occurredBefore"
        const val SCHEDULED_AFTER = "scheduledAfter"
        const val SCHEDULED_BEFORE = "scheduledBefore"
        const val TRACKED_ENTITY_TYPE = "trackedEntityType"
        const val FILTER = "filter"
        const val FILTER_ATTRIBUTES = "filterAttributes"
        const val UPDATED_AFTER = "updatedAfter"
        const val UPDATED_BEFORE = "updatedBefore"
        const val INCLUDE_DELETED = "includeDeleted"
        const val ASSIGNED_USER_MODE = "assignedUserMode"
        const val ORDER = "order"
        const val DEFAULT_PAGE_SIZE = 20
    }
}
