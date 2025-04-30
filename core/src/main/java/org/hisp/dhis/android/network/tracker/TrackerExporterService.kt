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
package org.hisp.dhis.android.network.tracker

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.network.common.fields.Fields

@Suppress("LongParameterList")
internal class TrackerExporterService(private val client: HttpServiceClient) {
    suspend fun getSingleTrackedEntityInstance(
        trackedEntityInstanceUid: String,
        fields: Fields<NewTrackerImporterTrackedEntity>,
        orgUnitMode: Map<String, String> = emptyMap(),
        program: String?,
        programStatus: String?,
        programStartDate: String?,
        includeDeleted: Boolean,
    ): NewTrackedEntityDTO {
        return client.get {
            url("$TRACKED_ENTITIES_API/$trackedEntityInstanceUid")
            parameters {
                fields(fields)
                attribute(PROGRAM, program)
                attribute(PROGRAM_STATUS, programStatus)
                attribute(ENROLLMENT_ENROLLED_AFTER, programStartDate)
                attribute(INCLUDE_DELETED, includeDeleted)
                orgUnitMode.forEach { (key, value) -> attribute(key, value) }
            }
        }
    }

    suspend fun getTrackedEntityInstances(
        fields: Fields<NewTrackerImporterTrackedEntity>,
        trackedEntityInstances: Map<String, String> = emptyMap(),
        orgUnits: Map<String, String> = emptyMap(),
        orgUnitMode: Map<String, String> = emptyMap(),
        program: String? = null,
        programStage: String? = null,
        programStartDate: String? = null,
        programEndDate: String? = null,
        programStatus: String? = null,
        programIncidentStartDate: String? = null,
        programIncidentEndDate: String? = null,
        followUp: Boolean? = null,
        eventStartDate: String? = null,
        eventEndDate: String? = null,
        eventStatus: String? = null,
        trackedEntityType: String? = null,
        filter: List<String?>? = null,
        assignedUserMode: String? = null,
        lastUpdatedStartDate: String? = null,
        lastUpdatedEndDate: String? = null,
        order: String? = null,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
        includeDeleted: Boolean = false,
    ): NewTrackedEntityPayload {
        return client.get {
            url(TRACKED_ENTITIES_API)
            parameters {
                fields(fields)
                trackedEntityInstances.forEach { (key, value) -> attribute(key, value) }
                orgUnits.forEach { (key, value) -> attribute(key, value) }
                orgUnitMode.forEach { (key, value) -> attribute(key, value) }
                attribute(PROGRAM, program)
                attribute(PROGRAM_STAGE, programStage)
                attribute(ENROLLMENT_ENROLLED_AFTER, programStartDate)
                attribute(ENROLLMENT_ENROLLED_BEFORE, programEndDate)
                attribute(PROGRAM_STATUS, programStatus)
                attribute(ENROLLMENT_OCCURRED_AFTER, programIncidentStartDate)
                attribute(ENROLLMENT_OCCURRED_BEFORE, programIncidentEndDate)
                attribute(FOLLOW_UP, followUp)
                attribute(EVENT_START_DATE, eventStartDate)
                attribute(EVENT_END_DATE, eventEndDate)
                attribute(EVENT_STATUS, eventStatus)
                attribute(TRACKED_ENTITY_TYPE, trackedEntityType)
                attribute(FILTER, filter)
                attribute(ASSIGNED_USER_MODE, assignedUserMode)
                attribute(UPDATED_AFTER, lastUpdatedStartDate)
                attribute(UPDATED_BEFORE, lastUpdatedEndDate)
                attribute(ORDER, order)
                attribute(INCLUDE_DELETED, includeDeleted)
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    suspend fun getEnrollmentSingle(
        enrollmentUid: String,
        fields: Fields<NewTrackerImporterEnrollment>,
    ): NewEnrollmentDTO {
        return client.get {
            url("$ENROLLMENTS_API/$enrollmentUid")
            parameters {
                fields(fields)
            }
        }
    }

    suspend fun getEvents(
        fields: Fields<NewTrackerImporterEvent>,
        orgUnit: String?,
        orgUnitMode: Map<String, String> = emptyMap(),
        status: String? = null,
        program: String?,
        programStage: String? = null,
        programStatus: String? = null,
        filter: List<String?>? = null,
        filterAttributes: List<String?>? = null,
        followUp: Boolean? = null,
        occurredAfter: String? = null,
        occurredBefore: String? = null,
        scheduledAfter: String? = null,
        scheduledBefore: String? = null,
        enrollmentEnrolledAfter: String? = null,
        enrollmentEnrolledBefore: String? = null,
        enrollmentOccurredAfter: String? = null,
        enrollmentOccurredBefore: String? = null,
        order: String? = null,
        assignedUserMode: String? = null,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
        updatedAfter: String?,
        updatedBefore: String? = null,
        includeDeleted: Boolean,
        eventUid: Map<String, String> = emptyMap(),
    ): NewEventPayload {
        return client.get {
            url(EVENTS_API)
            parameters {
                fields(fields)
                attribute(ORG_UNIT, orgUnit)
                orgUnitMode.forEach { (key, value) -> attribute(key, value) }
                attribute(STATUS, status)
                attribute(PROGRAM, program)
                attribute(PROGRAM_STAGE, programStage)
                attribute(PROGRAM_STATUS, programStatus)
                attribute(FILTER, filter)
                attribute(FILTER_ATTRIBUTES, filterAttributes)
                attribute(FOLLOW_UP, followUp)
                attribute(OCCURRED_AFTER, occurredAfter)
                attribute(OCCURRED_BEFORE, occurredBefore)
                attribute(SCHEDULED_AFTER, scheduledAfter)
                attribute(SCHEDULED_BEFORE, scheduledBefore)
                attribute(ENROLLMENT_ENROLLED_AFTER, enrollmentEnrolledAfter)
                attribute(ENROLLMENT_ENROLLED_BEFORE, enrollmentEnrolledBefore)
                attribute(ENROLLMENT_OCCURRED_AFTER, enrollmentOccurredAfter)
                attribute(ENROLLMENT_OCCURRED_BEFORE, enrollmentOccurredBefore)
                attribute(ORDER, order)
                attribute(ASSIGNED_USER_MODE, assignedUserMode)
                attribute(UPDATED_AFTER, updatedAfter)
                attribute(UPDATED_BEFORE, updatedBefore)
                attribute(INCLUDE_DELETED, includeDeleted)
                eventUid.forEach { (key, value) -> attribute(key, value) }
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    suspend fun getEventSingle(
        fields: Fields<NewTrackerImporterEvent>,
        eventUid: Map<String, String>? = null,
        orgUnitMode: Map<String, String>? = null,
    ): NewEventPayload {
        return client.get {
            url(EVENTS_API)
            parameters {
                fields(fields)
                eventUid?.forEach { (key, value) -> attribute(key, value) }
                orgUnitMode?.forEach { (key, value) -> attribute(key, value) }
            }
        }
    }

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
