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

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCallFactory
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnline
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER
import org.hisp.dhis.android.core.trackedentity.search.TrackerQueryResult
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.koin.core.annotation.Singleton

@Singleton
internal class OldTrackedEntityEndpointCallFactory(
    private val trackedEntityInstanceService: TrackedEntityInstanceService,
    private val queryCallFactory: TrackedEntityInstanceQueryCallFactory,
) : TrackedEntityEndpointCallFactory() {

    override suspend fun getCollectionCall(query: TrackerAPIQuery): Payload<TrackedEntityInstance> {
        return trackedEntityInstanceService.getTrackedEntityInstances(
            fields = TrackedEntityInstanceFields.allFields,
            trackedEntityInstances = getUidStr(query),
            orgUnits = getOrgunitStr(query),
            orgUnitMode = query.commonParams.ouMode.name,
            program = query.commonParams.program,
            programStatus = getProgramStatus(query),
            programStartDate = getProgramStartDate(query),
            order = DEFAULT_TRACKER_ORDER.toAPIString(TrackerExporterVersion.V1),
            paging = true,
            page = query.page,
            pageSize = query.pageSize,
            lastUpdatedStartDate = query.lastUpdatedStr,
            includeAllAttributes = true,
            includeDeleted = true,
        )
    }

    override suspend fun getEntityCall(uid: String, query: TrackerAPIQuery): TrackedEntityInstance {
        return trackedEntityInstanceService.getSingleTrackedEntityInstance(
            fields = TrackedEntityInstanceFields.allFields,
            trackedEntityInstanceUid = uid,
            orgUnitMode = query.commonParams.ouMode.name,
            program = query.commonParams.program,
            programStatus = getProgramStatus(query),
            programStartDate = getProgramStartDate(query),
            includeAllAttributes = true,
            includeDeleted = true,
        )
    }

    override suspend fun getRelationshipEntityCall(item: RelationshipItemRelative): Payload<TrackedEntityInstance> {
        return trackedEntityInstanceService.getTrackedEntityInstance(
            fields = TrackedEntityInstanceFields.asRelationshipFields,
            trackedEntityInstance = item.itemUid,
            orgUnitMode = OrganisationUnitMode.ACCESSIBLE.name,
            includeAllAttributes = true,
            includeDeleted = true,
        )
    }

    override suspend fun getQueryCall(query: TrackedEntityInstanceQueryOnline): TrackerQueryResult {
        return queryCallFactory.getCall(query)
    }
}
