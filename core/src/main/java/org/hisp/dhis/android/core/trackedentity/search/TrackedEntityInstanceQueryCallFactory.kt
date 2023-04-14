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
package org.hisp.dhis.android.core.trackedentity.search

import dagger.Reusable
import java.text.ParseException
import java.util.concurrent.Callable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.core.event.internal.EventService
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
import org.hisp.dhis.android.core.util.simpleDateFormat

@Reusable
internal class TrackedEntityInstanceQueryCallFactory @Inject constructor(
    private val trackedEntityService: TrackedEntityInstanceService,
    private val eventService: EventService,
    private val mapper: SearchGridMapper,
    private val apiCallExecutor: APICallExecutor,
    private val rxAPICallExecutor: RxAPICallExecutor,
    private val dhisVersionManager: DHISVersionManager
) {
    fun getCall(query: TrackedEntityInstanceQueryOnline): Callable<TrackerQueryResult> {
        return Callable { queryTrackedEntityInstances(query) }
    }

    private fun queryTrackedEntityInstances(query: TrackedEntityInstanceQueryOnline): TrackerQueryResult {
        val shouldCallEventsFirst = query.dataValueFilter.isNotEmpty() ||
            query.dueStartDate != null || query.dueEndDate != null

        return if (shouldCallEventsFirst) {
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

    private fun getEventQuery(query: TrackedEntityInstanceQueryOnline): List<Event> {
        return if (query.orgUnits.size <= 1) {
            getEventQueryForOrgunit(query, query.orgUnits.firstOrNull())
        } else {
            query.orgUnits.foldRight(emptyList()) { orgunit: String, events: List<Event> ->
                events + getEventQueryForOrgunit(query, orgunit)
            }
        }
    }

    private fun getEventQueryForOrgunit(query: TrackedEntityInstanceQueryOnline, orgunit: String?): List<Event> {
        return rxAPICallExecutor.wrapSingle(
            eventService.getEvents(
                fields = EventFields.teiQueryFields,
                orgUnit = orgunit,
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
                order = query.order,
                assignedUserMode = query.assignedUserMode?.toString(),
                paging = query.paging,
                pageSize = query.pageSize,
                page = query.page,
                lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
                lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
                includeDeleted = query.includeDeleted
            ),
            storeError = false
        ).blockingGet().items()
    }

    private fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        val uidsStr = query.uids?.joinToString(";")

        val searchGridCall = trackedEntityService.query(
            trackedEntityInstance = uidsStr,
            orgUnit = getOrgunits(query.orgUnits),
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
            query = query.query,
            filter = toAPIFilterFormat(query.attributeFilter, upper = true),
            assignedUserMode = query.assignedUserMode?.toString(),
            lastUpdatedStartDate = query.lastUpdatedStartDate.simpleDateFormat(),
            lastUpdatedEndDate = query.lastUpdatedEndDate.simpleDateFormat(),
            order = query.order,
            paging = query.paging,
            pageSize = query.pageSize,
            page = query.page,
        )

        return try {
            val searchGrid = apiCallExecutor.executeObjectCallWithErrorCatcher(
                searchGridCall,
                TrackedEntityInstanceQueryErrorCatcher()
            )
            mapper.transform(searchGrid)
        } catch (pe: ParseException) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                .errorComponent(D2ErrorComponent.SDK)
                .errorDescription("Search Grid mapping exception")
                .originalException(pe)
                .build()
        }
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

    private fun getOrgunits(orgUnits: List<String>): String? {
        return if (orgUnits.isEmpty()) null
        else orgUnits.joinToString(";")
    }

    companion object {
        internal fun getPostEventTeiQuery(
            query: TrackedEntityInstanceQueryOnline,
            events: List<Event>
        ): TrackedEntityInstanceQueryOnline {
            return query.copy(
                uids = events.mapNotNull { EventInternalAccessor.accessTrackedEntityInstance(it) }.distinct(),
                dataValueFilter = emptyList(),
                eventStatus = null,
                eventStartDate = null,
                eventEndDate = null,
                dueStartDate = null,
                dueEndDate = null,
                programStage = null,
            )
        }
    }
}
