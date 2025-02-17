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
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryErrorCatcher
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackerQueryResult
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterNetworkHandler
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class NewTrackedEntityEndpointCallFactory(
    private val networkHandler: TrackerExporterNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val relationshipTypeRepository: RelationshipTypeCollectionRepository,
) : TrackedEntityEndpointCallFactory {

    override suspend fun getCollectionCall(query: TrackerAPIQuery): Payload<TrackedEntityInstance> {
        return networkHandler.getTrackedEntityCollectionCall(query)
    }

    override suspend fun getEntityCall(uid: String, query: TrackerAPIQuery): TrackedEntityInstance {
        return networkHandler.getTrackedEntityEntityCall(uid, query)
    }

    override suspend fun getRelationshipEntityCall(
        item: RelationshipItemRelative,
    ): Payload<TrackedEntityInstance> {
        val program = getRelatedProgramUid(item)

        return networkHandler.getTrackedEntityRelationshipEntityCall(item, program)
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
            networkHandler.getEventQueryForOrgunit(query, orgunit)
        }.getOrThrow()
    }

    private suspend fun getTrackedEntityQuery(query: TrackedEntityInstanceQueryOnline): List<TrackedEntityInstance> {
        return coroutineAPICallExecutor.wrap(
            errorCatcher = TrackedEntityInstanceQueryErrorCatcher(),
        ) {
            networkHandler.getTrackedEntityQuery(query)
        }.getOrThrow().items
    }

    private fun getPostEventTeiQuery(
        query: TrackedEntityInstanceQueryOnline,
        events: List<Event>,
    ): TrackedEntityInstanceQueryOnline {
        return TrackedEntityInstanceQueryOnline(
            page = query.page,
            pageSize = query.pageSize,
            paging = query.paging,
            orgUnits = query.orgUnits,
            orgUnitMode = query.orgUnitMode,
            program = query.program,
            uids = events.mapNotNull { EventInternalAccessor.accessTrackedEntityInstance(it) }.distinct(),
            order = query.order,
            trackedEntityType = query.trackedEntityType,
            includeDeleted = query.includeDeleted,
        )
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
