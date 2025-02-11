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

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.PayloadJackson
import org.hisp.dhis.android.core.arch.api.payload.internal.TrackerPayload
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.internal.NewEventFields
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityTransformer
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryErrorCatcher
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIOrderFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER
import org.hisp.dhis.android.core.trackedentity.search.TrackerQueryResult
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterParameterManager
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.hisp.dhis.android.core.tracker.exporter.TrackerQueryHelper.getOrgunits
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class NewTrackedEntityEndpointCallFactory(
    private val trackedExporterService: TrackerExporterService,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val relationshipTypeRepository: RelationshipTypeCollectionRepository,
    private val parameterManager: TrackerExporterParameterManager,
) : TrackedEntityEndpointCallFactory {

    override suspend fun getCollectionCall(query: TrackerAPIQuery): PayloadJackson<TrackedEntityInstance> {
        return trackedExporterService.getTrackedEntityInstances(
            fields = NewTrackedEntityInstanceFields.allFields,
            trackedEntityInstances = parameterManager.getTrackedEntitiesParameter(query.uids),
            orgUnits = parameterManager.getOrgunitsParameter(getOrgunits(query)),
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.commonParams.ouMode),
            program = query.commonParams.program,
            programStatus = query.getProgramStatus(),
            programStartDate = query.getProgramStartDate(),
            order = DEFAULT_TRACKER_ORDER.toAPIString(TrackerExporterVersion.V2),
            paging = true,
            page = query.page,
            pageSize = query.pageSize,
            lastUpdatedStartDate = query.lastUpdatedStr,
            includeDeleted = true,
        ).let { mapPayload(it) }
    }

    override suspend fun getEntityCall(uid: String, query: TrackerAPIQuery): TrackedEntityInstance {
        return trackedExporterService.getSingleTrackedEntityInstance(
            fields = NewTrackedEntityInstanceFields.allFields,
            trackedEntityInstanceUid = uid,
            orgUnitMode = parameterManager.getOrgunitModeParameter(query.commonParams.ouMode),
            program = query.commonParams.program,
            programStatus = query.getProgramStatus(),
            programStartDate = query.getProgramStartDate(),
            includeDeleted = true,
        ).let { NewTrackerImporterTrackedEntityTransformer.deTransform(it) }
    }

    override suspend fun getRelationshipEntityCall(
        item: RelationshipItemRelative,
    ): PayloadJackson<TrackedEntityInstance> {
        return trackedExporterService.getSingleTrackedEntityInstance(
            fields = NewTrackedEntityInstanceFields.asRelationshipFields,
            trackedEntityInstanceUid = item.itemUid,
            orgUnitMode = parameterManager.getOrgunitModeParameter(OrganisationUnitMode.ACCESSIBLE),
            program = getRelatedProgramUid(item),
            programStatus = null,
            programStartDate = null,
            includeDeleted = true,
        ).let { PayloadJackson(listOf(NewTrackerImporterTrackedEntityTransformer.deTransform(it))) }
    }

    override suspend fun getQueryCall(query: TrackedEntityInstanceQueryOnline): TrackerQueryResult {
        return if (query.shouldCallEventFirst()) {
            val events = getEventQuery(query)
            if (events.isEmpty()) {
                TrackerQueryResult(
                    trackedEntities = emptyList(),
                    exhausted = true,
                )
            } else {
                val teiQuery = getPostEventTeiQuery(query, events)
                val instances = getTrackedEntityQuery(teiQuery)
                TrackerQueryResult(
                    trackedEntities = instances,
                    exhausted = events.size < query.pageSize || !query.paging,
                )
            }
        } else {
            val instances = getTrackedEntityQuery(query)
            TrackerQueryResult(
                trackedEntities = instances,
                exhausted = instances.size < query.pageSize || !query.paging,
            )
        }
    }

    private suspend fun getEventQuery(query: TrackedEntityInstanceQueryOnline): List<NewTrackerImporterEvent> {
        return if (query.orgUnits.size <= 1) {
            getEventQueryForOrgunit(query, query.orgUnits.firstOrNull())
        } else {
            query.orgUnits.foldRight(emptyList()) { orgunit: String, events: List<NewTrackerImporterEvent> ->
                events + getEventQueryForOrgunit(query, orgunit)
            }
        }
    }

    private suspend fun getEventQueryForOrgunit(
        query: TrackedEntityInstanceQueryOnline,
        orgunit: String?,
    ): List<NewTrackerImporterEvent> {
        return coroutineAPICallExecutor.wrap(storeError = false) {
            trackedExporterService.getEvents(
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
        }.getOrThrow().items()
    }

    private suspend fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        return coroutineAPICallExecutor.wrap(
            errorCatcher = TrackedEntityInstanceQueryErrorCatcher(),
        ) {
            val payload = trackedExporterService.getTrackedEntityInstances(
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

            mapPayload(payload)
        }.getOrThrow().items()
    }

    private fun getPostEventTeiQuery(
        query: TrackedEntityInstanceQueryOnline,
        events: List<NewTrackerImporterEvent>,
    ): TrackedEntityInstanceQueryOnline {
        return TrackedEntityInstanceQueryOnline(
            page = query.page,
            pageSize = query.pageSize,
            paging = query.paging,
            orgUnits = query.orgUnits,
            orgUnitMode = query.orgUnitMode,
            program = query.program,
            uids = events.mapNotNull { it.trackedEntity() }.distinct(),
            order = query.order,
            trackedEntityType = query.trackedEntityType,
            includeDeleted = query.includeDeleted,
        )
    }

    private fun mapPayload(
        payload: TrackerPayload<NewTrackerImporterTrackedEntity>,
    ): PayloadJackson<TrackedEntityInstance> {
        val newItems = payload.items().map { t -> NewTrackerImporterTrackedEntityTransformer.deTransform(t) }
        return PayloadJackson(newItems)
    }

    private fun getRelatedProgramUid(item: RelationshipItemRelative): String? {
        val relationshipType = relationshipTypeRepository
            .withConstraints()
            .uid(item.relationshipTypeUid)
            .blockingGet()

        val constraint = when (item.constraintType) {
            RelationshipConstraintType.FROM -> relationshipType?.fromConstraint()
            RelationshipConstraintType.TO -> relationshipType?.toConstraint()
        }

        return constraint?.program()?.uid()
    }
}
