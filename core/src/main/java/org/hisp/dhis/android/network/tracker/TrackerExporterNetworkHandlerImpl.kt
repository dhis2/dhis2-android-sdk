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

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.NewEventFields
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIOrderFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterNetworkHandler
import org.hisp.dhis.android.core.tracker.exporter.TrackerQueryHelper.getOrgunits
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.hisp.dhis.android.network.common.HttpServiceClientKotlinx
import org.hisp.dhis.android.network.common.PayloadJson
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerExporterNetworkHandlerImpl(
    httpServiceClient: HttpServiceClientKotlinx,
    private val parameterManager: TrackerExporterParameterManager,
) : TrackerExporterNetworkHandler {
    private val service = TrackerExporterService(httpServiceClient)

    override suspend fun getTrackedEntityCollectionCall(
        query: TrackerAPIQuery,
        programStatus: String?,
        programStartDate: String?,
    ): Payload<TrackedEntityInstance> {
        val apiPayload = service.getTrackedEntityInstances(
            fields = NewTrackedEntityInstanceFields.allFields,
            trackedEntityInstances = parameterManager.getTrackedEntitiesParameter(query.uids),
            orgUnits = parameterManager.getOrgunitsParameter(getOrgunits(query)),
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.commonParams.ouMode),
            program = query.commonParams.program,
            programStatus = programStatus,
            programStartDate = programStartDate,
            order = DEFAULT_TRACKER_ORDER.toAPIString(TrackerExporterVersion.V2),
            paging = true,
            page = query.page,
            pageSize = query.pageSize,
            lastUpdatedStartDate = query.lastUpdatedStr,
            includeDeleted = true,
        )
        return apiPayload.mapItems(NewTrackedEntityDTO::toDomain)
    }

    override suspend fun getTrackedEntityEntityCall(
        uid: String,
        query: TrackerAPIQuery,
        programStatus: String?,
        programStartDate: String?,
    ): TrackedEntityInstance {
        val apiDto = service.getSingleTrackedEntityInstance(
            fields = NewTrackedEntityInstanceFields.allFields,
            trackedEntityInstanceUid = uid,
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.commonParams.ouMode),
            program = query.commonParams.program,
            programStatus = programStatus,
            programStartDate = programStartDate,
            includeDeleted = true,
        )
        return apiDto.toDomain()
    }

    override suspend fun getTrackedEntityRelationshipEntityCall(
        item: RelationshipItemRelative,
        program: String?,
    ): Payload<TrackedEntityInstance> {
        val apiPayload = service.getSingleTrackedEntityInstance(
            fields = NewTrackedEntityInstanceFields.asRelationshipFields,
            trackedEntityInstanceUid = item.itemUid,
            orgUnitMode = parameterManager.getOrgunitModeParameter(OrganisationUnitMode.ACCESSIBLE),
            program = program,
            programStatus = null,
            programStartDate = null,
            includeDeleted = true,
        )
        return PayloadJson(listOf(apiPayload.toDomain()))
    }

    override suspend fun getEventQueryForOrgunit(
        query: TrackedEntityInstanceQueryOnline,
        orgunit: String?,
    ): List<Event> {
        val apiPayload = service.getEvents(
            fields = NewEventFields.teiQueryFields,
            orgUnit = orgunit,
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.orgUnitMode),
            status = query.eventStatus?.toString(),
            program = query.program,
            programStage = query.programStage,
            programStatus = query.enrollmentStatus?.toString(),
            filter = toAPIFilterFormat(query.dataValueFilter, upper = false),
            filterAttributes = toAPIFilterFormat(query.attributeFilter, upper = false),
            followUp = query.followUp,
            occurredAfter = query.eventStartDate.simpleDateFormat(),
            occurredBefore = query.eventStartDate.simpleDateFormat(),
            scheduledAfter = query.dueStartDate.simpleDateFormat(),
            scheduledBefore = query.dueEndDate.simpleDateFormat(),
            enrollmentEnrolledAfter = query.programStartDate.simpleDateFormat(),
            enrollmentEnrolledBefore = query.programEndDate.simpleDateFormat(),
            enrollmentOccurredAfter = query.incidentStartDate.simpleDateFormat(),
            enrollmentOccurredBefore = query.incidentEndDate.simpleDateFormat(),
            order = toAPIOrderFormat(query.order, TrackerExporterVersion.V2),
            assignedUserMode = query.assignedUserMode?.toString(),
            paging = query.paging,
            pageSize = query.pageSize.takeIf { query.paging },
            page = query.page.takeIf { query.paging },
            updatedAfter = query.lastUpdatedStartDate.simpleDateFormat(),
            updatedBefore = query.lastUpdatedEndDate.simpleDateFormat(),
            includeDeleted = query.includeDeleted,
        )

        return apiPayload.mapItems(NewEventDTO::toDomain).items
    }

    override suspend fun getTrackedEntityQuery(
        query: TrackedEntityInstanceQueryOnline,
    ): Payload<TrackedEntityInstance> {
        val apiPayload = service.getTrackedEntityInstances(
            fields = NewTrackedEntityInstanceFields.asRelationshipFields,
            trackedEntityInstances = parameterManager.getTrackedEntitiesParameter(query.uids),
            orgUnits = parameterManager.getOrgunitsParameter(getOrgunits(query)),
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.orgUnitMode),
            program = query.program,
            programStage = query.programStage,
            programStartDate = query.programStartDate.simpleDateFormat(),
            programEndDate = query.programEndDate.simpleDateFormat(),
            programStatus = query.enrollmentStatus?.toString(),
            programIncidentStartDate = query.incidentStartDate.simpleDateFormat(),
            programIncidentEndDate = query.incidentEndDate.simpleDateFormat(),
            followUp = query.followUp,
            eventStartDate = query.eventStartDate.simpleDateFormat(),
            eventEndDate = query.eventEndDate.simpleDateFormat(),
            eventStatus = query.eventStatus?.toString(),
            trackedEntityType = query.trackedEntityType,
            filter = toAPIFilterFormat(query.attributeFilter, upper = true),
            assignedUserMode = query.assignedUserMode?.toString(),
            lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
            lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
            order = toAPIOrderFormat(query.order, TrackerExporterVersion.V2),
            paging = query.paging,
            page = query.page.takeIf { query.paging },
            pageSize = query.pageSize.takeIf { query.paging },
        )
        return apiPayload.mapItems(NewTrackedEntityDTO::toDomain)
    }

    override suspend fun getEnrollmentRelationshipEntityCall(item: RelationshipItemRelative): Enrollment {
        val apiPayload = service.getEnrollmentSingle(
            enrollmentUid = item.itemUid,
            fields = NewEnrollmentFields.asRelationshipFields,
        )
        return apiPayload.toDomain()
    }

    override suspend fun getEventCollectionCall(eventQuery: TrackerAPIQuery): Payload<Event> {
        val apiPayload = service.getEvents(
            fields = NewEventFields.allFields,
            orgUnit = eventQuery.orgUnit,
            orgUnitMode = parameterManager.getOrgunitModeParameter(eventQuery.commonParams.ouMode),
            program = eventQuery.commonParams.program,
            occurredAfter = eventQuery.getEventStartDate(),
            paging = true,
            page = eventQuery.page,
            pageSize = eventQuery.pageSize,
            updatedAfter = eventQuery.lastUpdatedStr,
            includeDeleted = true,
            eventUid = parameterManager.getEventsParameter(eventQuery.uids),
        )
        return apiPayload.mapItems(NewEventDTO::toDomain)
    }

    override suspend fun getEventRelationshipEntityCall(item: RelationshipItemRelative): Payload<Event> {
        val apiPayload = service.getEventSingle(
            eventUid = parameterManager.getEventsParameter(listOf(item.itemUid)),
            fields = NewEventFields.asRelationshipFields,
            orgUnitMode = parameterManager.getOrgunitModeParameter(OrganisationUnitMode.ACCESSIBLE),
        )
        return apiPayload.mapItems(NewEventDTO::toDomain)
    }
}
