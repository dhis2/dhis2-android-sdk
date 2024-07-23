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

import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.koin.core.annotation.Singleton

@Singleton
internal class EventService(private val client: HttpServiceClient) {

    suspend fun postEvents(events: EventPayload, strategy: String): EventWebResponse {
        return client.post {
            url(EVENTS)
            parameters {
                attribute(STRATEGY to strategy)
            }
            body(events)
        }
    }

    @SuppressWarnings("LongParameterList")
    suspend fun getEvents(
        fields: Fields<Event>,
        orgUnit: String? = null,
        orgUnitMode: String? = null,
        status: String? = null,
        program: String? = null,
        programStage: String? = null,
        programStatus: String? = null,
        filter: List<String>? = null,
        followUp: Boolean? = null,
        startDate: String? = null,
        endDate: String? = null,
        dueDateStart: String? = null,
        dueDateEnd: String? = null,
        order: String? = null,
        assignedUserMode: String? = null,
        paging: Boolean,
        page: Int?,
        pageSize: Int?,
        lastUpdatedStartDate: String? = null,
        lastUpdatedEndDate: String? = null,
        includeDeleted: Boolean,
        eventUid: String? = null,
    ): Payload<Event> {
        return client.get {
            url(EVENTS)
            parameters {
                fields(fields)
                attribute(ORG_UNIT to orgUnit)
                attribute(OU_MODE to orgUnitMode)
                attribute(STATUS to status)
                attribute(PROGRAM to program)
                attribute(PROGRAM_STAGE to programStage)
                attribute(PROGRAM_STATUS to programStatus)
                attribute(FOLLOW_UP to followUp)
                attribute(EVENT_START_DATE to startDate)
                attribute(EVENT_END_DATE to endDate)
                attribute(DUE_START_DATE to dueDateStart)
                attribute(DUE_END_DATE to dueDateEnd)
                attribute(ORDER to order)
                attribute(ASSIGNED_USER_MODE to assignedUserMode)
                attribute(LAST_UPDATED_START_DATE to lastUpdatedStartDate)
                attribute(LAST_UPDATED_END_DATE to lastUpdatedEndDate)
                attribute(INCLUDE_DELETED to includeDeleted)
                attribute(EVENT to eventUid)
                filter(filter)
                paging(paging)
                page(page)
                pageSize(pageSize)
            }
        }
    }

    suspend fun getEvent(
        eventUid: String,
        fields: Fields<Event>,
        orgUnitMode: String,
    ): Event {
        return client.get {
            url("$EVENTS/$eventUid")
            parameters {
                fields(fields)
                attribute(OU_MODE to orgUnitMode)
            }
        }
    }

    suspend fun getEventSingle(
        eventUid: String,
        fields: Fields<Event>,
        orgUnitMode: String,
    ): Payload<Event> {
        return client.get {
            url(EVENTS)
            parameters {
                fields(fields)
                attribute(EVENT to eventUid)
                attribute(OU_MODE to orgUnitMode)
            }
        }
    }

    companion object {
        private const val ORG_UNIT = "orgUnit"
        private const val OU_MODE = "ouMode"
        private const val STATUS = "status"
        private const val PROGRAM = "program"
        private const val PROGRAM_STAGE = "programStage"
        private const val PROGRAM_STATUS = "programStatus"
        private const val FOLLOW_UP = "followUp"
        private const val EVENT_START_DATE = "startDate"
        private const val EVENT_END_DATE = "endDate"
        private const val DUE_START_DATE = "dueDateStart"
        private const val DUE_END_DATE = "dueDateEnd"
        private const val ORDER = "order"
        private const val ASSIGNED_USER_MODE = "assignedUserMode"
        private const val EVENTS = "events"
        private const val STRATEGY = "strategy"
        private const val EVENT = "event"
        private const val LAST_UPDATED_START_DATE = "lastUpdatedStartDate"
        private const val LAST_UPDATED_END_DATE = "lastUpdatedEndDate"
        private const val INCLUDE_DELETED = "includeDeleted"
    }
}
