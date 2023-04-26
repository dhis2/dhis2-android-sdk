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

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Which
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import retrofit2.Call
import retrofit2.http.*

internal interface EventService {

    @POST(EVENTS)
    fun postEvents(@Body events: EventPayload, @Query(STRATEGY) strategy: String): Call<EventWebResponse>

    @SuppressWarnings("LongParameterList")
    @GET(EVENTS)
    fun getEvents(
        @Query(FIELDS) @Which fields: Fields<Event>,
        @Query(ORG_UNIT) orgUnit: String? = null,
        @Query(OU_MODE) orgUnitMode: String? = null,
        @Query(STATUS) status: String? = null,
        @Query(PROGRAM) program: String? = null,
        @Query(PROGRAM_STAGE) programStage: String? = null,
        @Query(PROGRAM_STATUS) programStatus: String? = null,
        @Query(FILTER) filter: List<String>? = null,
        @Query(FOLLOW_UP) followUp: Boolean? = null,
        @Query(EVENT_START_DATE) startDate: String? = null,
        @Query(EVENT_END_DATE) endDate: String? = null,
        @Query(DUE_START_DATE) dueDateStart: String? = null,
        @Query(DUE_END_DATE) dueDateEnd: String? = null,
        @Query(ORDER) order: String? = null,
        @Query(ASSIGNED_USER_MODE) assignedUserMode: String? = null,
        @Query(PAGING) paging: Boolean,
        @Query(PAGE) page: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(LAST_UPDATED_START_DATE) lastUpdatedStartDate: String? = null,
        @Query(LAST_UPDATED_END_DATE) lastUpdatedEndDate: String? = null,
        @Query(INCLUDE_DELETED) includeDeleted: Boolean,
        @Query(EVENT) eventUid: String? = null,
    ): Single<Payload<Event>>

    @GET("$EVENTS/{$EVENT_UID}")
    fun getEvent(
        @Path(EVENT_UID) eventUid: String,
        @Query(FIELDS) @Which fields: Fields<Event>,
        @Query(OU_MODE) orgUnitMode: String
    ): Call<Event>

    @GET(EVENTS)
    fun getEventSingle(
        @Query(EVENT) eventUid: String,
        @Query(FIELDS) @Which fields: Fields<Event>,
        @Query(OU_MODE) orgUnitMode: String
    ): Single<Payload<Event>>

    companion object {
        const val ORG_UNIT = "orgUnit"
        const val OU_MODE = "ouMode"
        const val STATUS = "status"
        const val PROGRAM = "program"
        const val PROGRAM_STAGE = "programStage"
        const val PROGRAM_STATUS = "programStatus"
        const val TRACKED_ENTITY_INSTANCE = "trackedEntityInstance"
        const val FILTER = "filter"
        const val FOLLOW_UP = "followUp"
        const val EVENT_START_DATE = "startDate"
        const val EVENT_END_DATE = "endDate"
        const val DUE_START_DATE = "dueDateStart"
        const val DUE_END_DATE = "dueDateEnd"
        const val ORDER = "order"
        const val ASSIGNED_USER_MODE = "assignedUserMode"
        const val FIELDS = "fields"
        const val PAGING = "paging"
        const val PAGE_SIZE = "pageSize"
        const val PAGE = "page"
        const val EVENTS = "events"
        const val STRATEGY = "strategy"
        const val EVENT_UID = "eventUid"
        const val EVENT = "event"
        const val LAST_UPDATED_START_DATE = "lastUpdatedStartDate"
        const val LAST_UPDATED_END_DATE = "lastUpdatedEndDate"
        const val INCLUDE_DELETED = "includeDeleted"
    }
}
