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
package org.hisp.dhis.android.core.event.internal

import org.hisp.dhis.android.core.arch.api.payload.internal.PayloadJackson
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerQueryHelper.getOrgunits
import org.koin.core.annotation.Singleton

@Singleton
internal class OldEventEndpointCallFactory(
    private val service: EventService,
) : EventEndpointCallFactory() {

    override suspend fun getCollectionCall(eventQuery: TrackerAPIQuery): PayloadJackson<Event> {
        return service.getEvents(
            fields = EventFields.allFields,
            orgUnit = getOrgunits(eventQuery)?.firstOrNull(),
            orgUnitMode = eventQuery.commonParams.ouMode.name,
            program = eventQuery.commonParams.program,
            startDate = getEventStartDate(eventQuery),
            paging = true,
            page = eventQuery.page,
            pageSize = eventQuery.pageSize,
            lastUpdatedStartDate = eventQuery.lastUpdatedStr,
            includeDeleted = true,
            eventUid = getUidStr(eventQuery),
        )
    }

    override suspend fun getRelationshipEntityCall(item: RelationshipItemRelative): PayloadJackson<Event> {
        return service.getEventSingle(
            eventUid = item.itemUid,
            fields = EventFields.asRelationshipFields,
            orgUnitMode = OrganisationUnitMode.ACCESSIBLE.name,
        )
    }
}
