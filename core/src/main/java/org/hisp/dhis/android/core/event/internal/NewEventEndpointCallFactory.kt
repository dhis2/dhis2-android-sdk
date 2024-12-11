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
import org.hisp.dhis.android.core.arch.api.payload.internal.TrackerPayload
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent
import org.hisp.dhis.android.core.event.NewTrackerImporterEventTransformer
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelative
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterParameterManager
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.koin.core.annotation.Singleton

@Singleton
internal class NewEventEndpointCallFactory(
    private val service: TrackerExporterService,
    private val parameterManager: TrackerExporterParameterManager,
) : EventEndpointCallFactory() {

    override suspend fun getCollectionCall(eventQuery: TrackerAPIQuery): PayloadJackson<Event> {
        return service.getEvents(
            fields = NewEventFields.allFields,
            orgUnit = eventQuery.orgUnit,
            orgUnitMode = parameterManager.getOrgunitModeParameter(eventQuery.commonParams.ouMode),
            program = eventQuery.commonParams.program,
            occurredAfter = getEventStartDate(eventQuery),
            paging = true,
            page = eventQuery.page,
            pageSize = eventQuery.pageSize,
            updatedAfter = eventQuery.lastUpdatedStr,
            includeDeleted = true,
            eventUid = parameterManager.getEventsParameter(eventQuery.uids),
        ).let { mapPayload(it) }
    }

    override suspend fun getRelationshipEntityCall(item: RelationshipItemRelative): PayloadJackson<Event> {
        return service.getEventSingle(
            eventUid = parameterManager.getEventsParameter(listOf(item.itemUid)),
            fields = NewEventFields.asRelationshipFields,
            orgUnitMode = parameterManager.getOrgunitModeParameter(OrganisationUnitMode.ACCESSIBLE),
        ).let { mapPayload(it) }
    }

    private fun mapPayload(payload: TrackerPayload<NewTrackerImporterEvent>): PayloadJackson<Event> {
        val newItems = payload.items().map { t -> NewTrackerImporterEventTransformer.deTransform(t) }
        return PayloadJackson(newItems)
    }
}
