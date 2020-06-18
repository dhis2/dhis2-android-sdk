/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.internal.EventWebResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface EventService {
    String ORG_UNIT = "orgUnit";
    String OU_MODE = "ouMode";
    String PROGRAM = "program";
    String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    String FIELDS = "fields";
    String PAGING = "paging";
    String PAGE_SIZE = "pageSize";
    String PAGE = "page";
    String EVENTS = "events";
    String STRATEGY = "strategy";
    String EVENT_UID = "eventUid";
    String LAST_UPDATED_START_DATE = "lastUpdatedStartDate";
    String INCLUDE_DELETED = "includeDeleted";

    @POST(EVENTS)
    Call<EventWebResponse> postEvents(@Body EventPayload events, @Query(STRATEGY) String strategy);

    @GET(EVENTS)
    Call<Payload<Event>> getEvents(
            @Query(ORG_UNIT) String orgUnit,
            @Query(OU_MODE) String orgUnitMode,
            @Query(PROGRAM) String program,
            @Query(FIELDS) @Which Fields<Event> fields,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize,
            @Query(LAST_UPDATED_START_DATE) String lastUpdatedStartDate,
            @Query(INCLUDE_DELETED) Boolean includeDeleted);

    @GET(EVENTS + "/{" + EVENT_UID + "}")
    Call<Event> getEvent(
            @Path(EVENT_UID) String eventUid,
            @Query(FIELDS) @Which Fields<Event> fields);

    @GET(EVENTS + "/{" + EVENT_UID + "}")
    Single<Event> getEventSingle(
            @Path(EVENT_UID) String eventUid,
            @Query(FIELDS) @Which Fields<Event> fields);
}
