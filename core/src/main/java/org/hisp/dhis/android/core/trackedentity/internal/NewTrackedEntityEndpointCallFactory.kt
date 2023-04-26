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

import dagger.Reusable
import io.reactivex.Single
import java.util.concurrent.Callable
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.api.payload.internal.NTIPayload
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.internal.NewEventFields
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityTransformer
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryErrorCatcher
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem
import org.hisp.dhis.android.core.trackedentity.search.TrackerQueryResult
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.hisp.dhis.android.core.util.simpleDateFormat

@Reusable
internal class NewTrackedEntityEndpointCallFactory @Inject constructor(
    private val trackedExporterService: TrackerExporterService,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val rxAPICallExecutor: RxAPICallExecutor
) : TrackedEntityEndpointCallFactory() {

    override fun getCollectionCall(query: TrackerAPIQuery): Single<Payload<TrackedEntityInstance>> {
        return rxSingle {
            val payload = trackedExporterService.getTrackedEntityInstances(
                fields = NewTrackedEntityInstanceFields.allFields,
                trackedEntityInstances = getUidStr(query),
                orgUnits = query.orgUnit,
                orgUnitMode = query.commonParams.ouMode.name,
                program = query.commonParams.program,
                programStatus = getProgramStatus(query),
                programStartDate = getProgramStartDate(query),
                order = TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER.toAPIString(),
                paging = true,
                page = query.page,
                pageSize = query.pageSize,
                lastUpdatedStartDate = query.lastUpdatedStr,
                includeAllAttributes = true,
                includeDeleted = true
            )

            mapPayload(payload)
        }
    }

    override suspend fun getEntityCall(uid: String, query: TrackerAPIQuery): TrackedEntityInstance {
        val tei = trackedExporterService.getSingleTrackedEntityInstance(
            fields = NewTrackedEntityInstanceFields.allFields,
            trackedEntityInstanceUid = uid,
            orgUnitMode = query.commonParams.ouMode.name,
            program = query.commonParams.program,
            programStatus = getProgramStatus(query),
            programStartDate = getProgramStartDate(query),
            includeAllAttributes = true,
            includeDeleted = true
        )

        return NewTrackerImporterTrackedEntityTransformer.deTransform(tei)
    }

    override fun getRelationshipEntityCall(uid: String): Single<Payload<TrackedEntityInstance>> {
        return trackedExporterService.getTrackedEntityInstance(
            trackedEntityInstance = uid,
            fields = NewTrackedEntityInstanceFields.asRelationshipFields,
            orgUnitMode = OrganisationUnitMode.ACCESSIBLE.name,
            includeAllAttributes = true,
            includeDeleted = true
        ).map { mapPayload(it) }
    }

    override fun getQueryCall(query: TrackedEntityInstanceQueryOnline): Callable<TrackerQueryResult> {
        return Callable {
            runBlocking {
                val shouldCallEventsFirst = query.dataValueFilter.isNotEmpty() ||
                    query.dueStartDate != null || query.dueEndDate != null

                if (shouldCallEventsFirst) {
                    val events = getEventQuery(query)
                    if (events.isEmpty()) {
                        TrackerQueryResult(
                            trackedEntities = emptyList(),
                            exhausted = true
                        )
                    } else {
                        val teiQuery = getPostEventTeiQuery(query, events)
                        val instances = getTrackedEntityQuery(teiQuery)
                        TrackerQueryResult(
                            trackedEntities = instances,
                            exhausted = events.size < query.pageSize
                        )
                    }
                } else {
                    val instances = getTrackedEntityQuery(query)
                    TrackerQueryResult(
                        trackedEntities = instances,
                        exhausted = instances.size < query.pageSize
                    )
                }
            }
        }
    }

    private fun getEventQuery(query: TrackedEntityInstanceQueryOnline): List<NewTrackerImporterEvent> {
        return if (query.orgUnits.size <= 1) {
            getEventQueryForOrgunit(query, query.orgUnits.firstOrNull())
        } else {
            query.orgUnits.foldRight(emptyList()) { orgunit: String, events: List<NewTrackerImporterEvent> ->
                events + getEventQueryForOrgunit(query, orgunit)
            }
        }
    }

    private fun getEventQueryForOrgunit(
        query: TrackedEntityInstanceQueryOnline,
        orgunit: String?
    ): List<NewTrackerImporterEvent> {
        return rxAPICallExecutor.wrapSingle(
            trackedExporterService.getEvents(
                fields = NewEventFields.teiQueryFields,
                orgUnit = orgunit,
                orgUnitMode = query.orgUnitMode?.toString(),
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
                order = query.order,
                assignedUserMode = query.assignedUserMode?.toString(),
                paging = query.paging,
                pageSize = query.pageSize,
                page = query.page,
                updatedAfter = query.lastUpdatedStartDate.simpleDateFormat(),
                updatedBefore = query.lastUpdatedEndDate.simpleDateFormat(),
                includeDeleted = query.includeDeleted
            ),
            storeError = false
        ).blockingGet().instances
    }

    private suspend fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        return coroutineAPICallExecutor.wrap(
            errorCatcher = TrackedEntityInstanceQueryErrorCatcher()
        ) {
            val uidsStr = query.uids?.joinToString(";")

            val payload = trackedExporterService.getTrackedEntityInstances(
                fields = NewTrackedEntityInstanceFields.asRelationshipFields,
                trackedEntityInstances = uidsStr,
                orgUnits = getOrgunits(query.orgUnits),
                orgUnitMode = query.orgUnitMode?.toString(),
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
                query = query.query,
                filter = toAPIFilterFormat(query.attributeFilter, upper = true),
                assignedUserMode = query.assignedUserMode?.toString(),
                lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
                lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
                order = query.order,
                paging = query.paging,
                page = query.page,
                pageSize = query.pageSize,
                includeAllAttributes = true
            )

            mapPayload(payload)
        }.getOrThrow().items()
    }

    private fun getPostEventTeiQuery(
        query: TrackedEntityInstanceQueryOnline,
        events: List<NewTrackerImporterEvent>
    ): TrackedEntityInstanceQueryOnline {
        return TrackedEntityInstanceQueryOnline(
            page = query.page,
            pageSize = query.pageSize,
            paging = query.paging,
            orgUnits = query.orgUnits,
            orgUnitMode = query.orgUnitMode,
            program = query.program,
            uids = events.mapNotNull { it.trackedEntity() }.distinct(),
            query = query.query,
            attributeFilter = query.attributeFilter,
            order = query.order,
            trackedEntityType = query.trackedEntityType,
            includeDeleted = query.includeDeleted
        )
    }

    private fun mapPayload(payload: NTIPayload<NewTrackerImporterTrackedEntity>): Payload<TrackedEntityInstance> {
        val newItems = payload.instances.map { t -> NewTrackerImporterTrackedEntityTransformer.deTransform(t) }
        return Payload(newItems)
    }

    private fun getOrgunits(orgUnits: List<String>): String? {
        return if (orgUnits.isEmpty()) null
        else orgUnits.joinToString(";")
    }
}
