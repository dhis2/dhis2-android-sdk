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

package org.hisp.dhis.android.network.event

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventNetworkHandler
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIOrderFormat
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerQueryHelper.getOrgunits
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.koin.core.annotation.Singleton

@Singleton
internal class EventNetworkHandlerImpl(
    httpClient: HttpServiceClient,
) : EventNetworkHandler {
    private val service = EventService(httpClient)

    override suspend fun postEvents(events: List<Event>, strategy: String): EventWebResponse {
        val payload = EventPayload(items = events.map { it.toDto() })
        val response = service.postEvents(payload, strategy)
        return response.toDomain()
    }

    override suspend fun getCollectionCall(eventQuery: TrackerAPIQuery): Payload<Event> {
        val apiPayload = service.getEvents(
            fields = EventFields.allFields,
            orgUnit = getOrgunits(eventQuery)?.firstOrNull(),
            orgUnitMode = eventQuery.commonParams.ouMode.name,
            program = eventQuery.commonParams.program,
            startDate = eventQuery.getEventStartDate(),
            paging = true,
            page = eventQuery.page,
            pageSize = eventQuery.pageSize,
            lastUpdatedStartDate = eventQuery.lastUpdatedStr,
            includeDeleted = true,
            eventUid = eventQuery.getUidStr(),
        )
        return apiPayload.mapItems { it.toDomain() }
    }

    override suspend fun getEventQueryForOrgunit(
        query: TrackedEntityInstanceQueryOnline,
        orgunit: String?,
    ): Payload<Event> {
        val apiPayload = service.getEvents(
            fields = EventFields.teiQueryFields,
            orgUnit = getOrgunits(orgunit, query.orgUnitMode)?.firstOrNull(),
            orgUnitMode = query.orgUnitMode?.toString(),
            status = query.eventStatus?.toString(),
            program = query.program,
            programStage = query.programStage,
            programStatus = query.enrollmentStatus?.toString(),
            filter = toAPIFilterFormat(query.dataValueFilter, upper = true),
            followUp = query.followUp,
            startDate = query.eventStartDate.simpleDateFormat(),
            endDate = query.eventEndDate.simpleDateFormat(),
            dueDateStart = query.dueStartDate.simpleDateFormat(),
            dueDateEnd = query.dueEndDate.simpleDateFormat(),
            order = toAPIOrderFormat(query.order, TrackerExporterVersion.V1),
            assignedUserMode = query.assignedUserMode?.toString(),
            paging = query.paging,
            pageSize = query.pageSize.takeIf { query.paging },
            page = query.page.takeIf { query.paging },
            lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
            lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
            includeDeleted = query.includeDeleted,
        )
        return apiPayload.mapItems { it.toDomain() }
    }

    override suspend fun getRelationshipEntityCall(item: RelationshipItemRelative): Payload<Event> {
        val payload = service.getEventSingle(
            eventUid = item.itemUid,
            fields = EventFields.asRelationshipFields,
            orgUnitMode = OrganisationUnitMode.ACCESSIBLE.name,
        )
        return payload.mapItems { it.toDomain() }
    }

    override suspend fun getEvent(
        eventUid: String,
        orgUnitMode: String,
    ): Event {
        return service.getEvent(
            eventUid = eventUid,
            fields = EventFields.allFields,
            orgUnitMode = orgUnitMode,
        ).toDomain()
    }
}
