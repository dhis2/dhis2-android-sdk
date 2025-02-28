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

package org.hisp.dhis.android.network.trackedentityinstance

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceNetworkHandler
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIOrderFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerQueryHelper.getOrgunits
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityInstanceNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val dhisVersionManager: DHISVersionManager,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) : TrackedEntityInstanceNetworkHandler {
    private val service = TrackedEntityInstanceService(httpClient)

    override suspend fun postTrackedEntityInstances(
        instances: List<TrackedEntityInstance>,
        strategy: String?,
    ): Result<TEIWebResponse, D2Error> {
        val payload = TrackedEntityInstancePayload(items = instances.map { it.toDto() })
        return coroutineAPICallExecutor.wrap(
            storeError = true,
            acceptedErrorCodes = listOf(HttpStatusCodes.CONFLICT),
            errorClassParser = TEIWebResponseDTO::toErrorClass,
        ) {
            service.postTrackedEntityInstances(payload, strategy).toDomain()
        }
    }

    override suspend fun getRelationshipEntityCall(item: RelationshipItemRelative): Payload<TrackedEntityInstance> {
        val payload = service.getTrackedEntityInstance(
            fields = TrackedEntityInstanceFields.asRelationshipFields,
            trackedEntityInstance = item.itemUid,
            orgUnitMode = OrganisationUnitMode.ACCESSIBLE.name,
            includeAllAttributes = true,
            includeDeleted = true,
        )
        return payload.mapItems { it.toDomain() }
    }

    override suspend fun getTrackedEntityInstance(uid: String, query: TrackerAPIQuery): TrackedEntityInstance {
        return service.getSingleTrackedEntityInstance(
            fields = TrackedEntityInstanceFields.allFields,
            trackedEntityInstanceUid = uid,
            orgUnitMode = query.commonParams.ouMode.name,
            program = query.commonParams.program,
            programStatus = query.getProgramStatus(),
            programStartDate = query.getProgramStartDate(),
            includeAllAttributes = true,
            includeDeleted = true,
        ).toDomain()
    }

    override suspend fun getTrackedEntityInstances(query: TrackerAPIQuery): Payload<TrackedEntityInstance> {
        val payload = service.getTrackedEntityInstances(
            fields = TrackedEntityInstanceFields.allFields,
            trackedEntityInstances = query.getUidStr(),
            orgUnits = query.getOrgunitStr(),
            orgUnitMode = query.commonParams.ouMode.name,
            program = query.commonParams.program,
            programStatus = query.getProgramStatus(),
            programStartDate = query.getProgramStartDate(),
            order = DEFAULT_TRACKER_ORDER.toAPIString(TrackerExporterVersion.V1),
            paging = true,
            page = query.page,
            pageSize = query.pageSize,
            lastUpdatedStartDate = query.lastUpdatedStr,
            includeAllAttributes = true,
            includeDeleted = true,
        )
        return payload.mapItems { it.toDomain() }
    }

    override suspend fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        return service.query(
            trackedEntityInstance = query.uids?.joinToString(";"),
            orgUnit = getOrgunits(query)?.joinToString(";"),
            orgUnitMode = query.orgUnitMode?.toString(),
            program = query.program,
            programStage = query.programStage,
            programStartDate = query.programStartDate.simpleDateFormat(),
            programEndDate = query.programEndDate.simpleDateFormat(),
            enrollmentStatus = query.enrollmentStatus?.toString(),
            programIncidentStartDate = query.incidentStartDate.simpleDateFormat(),
            programIncidentEndDate = query.incidentEndDate.simpleDateFormat(),
            followUp = query.followUp,
            eventStartDate = query.eventStartDate.simpleDateFormat(),
            eventEndDate = query.eventEndDate.simpleDateFormat(),
            eventStatus = getEventStatus(query),
            trackedEntityType = query.trackedEntityType,
            filter = toAPIFilterFormat(query.attributeFilter, upper = true),
            assignedUserMode = query.assignedUserMode?.toString(),
            lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
            lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
            order = toAPIOrderFormat(query.order, TrackerExporterVersion.V1),
            paging = query.paging,
            pageSize = query.pageSize.takeIf { query.paging },
            page = query.page.takeIf { query.paging },
        ).toDomain()
    }

    private fun getEventStatus(query: TrackedEntityInstanceQueryOnline): String? {
        return if (query.eventStatus == null) {
            null
        } else if (!dhisVersionManager.isGreaterThan(DHISVersion.V2_33) && query.eventStatus == EventStatus.ACTIVE) {
            EventStatus.VISITED.toString()
        } else {
            query.eventStatus.toString()
        }
    }
}
