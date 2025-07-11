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
package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.internal.EventNetworkHandler
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceNetworkHandler
import org.koin.core.annotation.Singleton
import java.text.ParseException

@Singleton
internal class TrackedEntityInstanceQueryCallFactory(
    private val networkHandler: TrackedEntityInstanceNetworkHandler,
    private val eventNetworkHandler: EventNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) {
    suspend fun getCall(query: TrackedEntityInstanceQueryOnline): TrackerQueryResult {
        return queryTrackedEntityInstances(query)
    }

    private suspend fun queryTrackedEntityInstances(query: TrackedEntityInstanceQueryOnline): TrackerQueryResult {
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
                    exhausted = events.size < query.pageSize,
                )
            }
        } else {
            val instances = getTrackedEntityQuery(query)
            TrackerQueryResult(
                trackedEntities = instances,
                exhausted = instances.size < query.pageSize,
            )
        }
    }

    private suspend fun getEventQuery(query: TrackedEntityInstanceQueryOnline): List<Event> {
        return if (query.orgUnits.size <= 1) {
            getEventQueryForOrgunit(query, query.orgUnits.firstOrNull())
        } else {
            query.orgUnits.foldRight(emptyList()) { orgunit: String, events: List<Event> ->
                events + getEventQueryForOrgunit(query, orgunit)
            }
        }
    }

    private suspend fun getEventQueryForOrgunit(
        query: TrackedEntityInstanceQueryOnline,
        orgunit: String?,
    ): List<Event> {
        return coroutineAPICallExecutor.wrap(storeError = false) {
            eventNetworkHandler.getEventQueryForOrgunit(query, orgunit)
        }.getOrThrow().items
    }

    private suspend fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        return try {
            coroutineAPICallExecutor.wrap(
                storeError = false,
                errorCatcher = TrackedEntityInstanceQueryErrorCatcher(),
            ) {
                networkHandler.getTrackedEntityQuery(query)
            }.getOrThrow()
        } catch (pe: ParseException) {
            throw D2Error.builder()
                .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                .errorComponent(D2ErrorComponent.SDK)
                .errorDescription("Search Grid mapping exception")
                .originalException(pe)
                .build()
        }
    }

    companion object {
        internal fun getPostEventTeiQuery(
            query: TrackedEntityInstanceQueryOnline,
            events: List<Event>,
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
