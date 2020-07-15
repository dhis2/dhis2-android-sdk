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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.SearchGrid;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrackedEntityInstanceService {
    String TRACKED_ENTITY_INSTANCES = "trackedEntityInstances";

    String TRACKED_ENTITY_INSTACE = "trackedEntityInstance";
    String OU = "ou";
    String OU_MODE = "ouMode";
    String FIELDS = "fields";
    String QUERY = "query";
    String ATTRIBUTE = "attribute";
    String PAGING = "paging";
    String PAGE = "page";
    String PAGE_SIZE = "pageSize";
    String PROGRAM = "program";
    String PROGRAM_START_DATE = "programStartDate";
    String PROGRAM_END_DATE = "programEndDate";
    String PROGRAM_STATUS = "programStatus";
    String EVENT_STATUS = "eventStatus";
    String EVENT_START_DATE = "eventStartDate";
    String EVENT_END_DATE = "eventEndDate";
    String TRACKED_ENTITY_TYPE = "trackedEntityType";
    String INCLUDE_ALL_ATTRIBUTES = "includeAllAttributes";
    String FILTER = "filter";
    String STRATEGY = "strategy";
    String LAST_UPDATED_START_DATE = "lastUpdatedStartDate";
    String REASON = "reason";
    String INCLUDE_DELETED = "includeDeleted";
    String ASSIGNED_USER_MODE = "assignedUserMode";
    String ORDER = "order";

    @POST(TRACKED_ENTITY_INSTANCES)
    Call<TEIWebResponse> postTrackedEntityInstances(
            @Body TrackedEntityInstancePayload trackedEntityInstances,
            @Query(STRATEGY) String strategy);

    @GET(TRACKED_ENTITY_INSTANCES)
    Single<Payload<TrackedEntityInstance>> getTrackedEntityInstance(
            @Query(TRACKED_ENTITY_INSTACE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes,
            @Query(INCLUDE_DELETED) boolean includeDeleted);

    @GET(TRACKED_ENTITY_INSTANCES)
    Call<Payload<TrackedEntityInstance>> getTrackedEntityInstanceAsCall(
            @Query(TRACKED_ENTITY_INSTACE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes,
            @Query(INCLUDE_DELETED) boolean includeDeleted);

    @GET(TRACKED_ENTITY_INSTANCES + "/{" + TRACKED_ENTITY_INSTACE + "}")
    Call<TrackedEntityInstance> getTrackedEntityInstanceByProgram(
            @Path(TRACKED_ENTITY_INSTACE) String trackedEntityInstanceUid,
            @Query(PROGRAM) String program,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes,
            @Query(INCLUDE_DELETED) boolean includeDeleted);

    @GET(TRACKED_ENTITY_INSTANCES)
    Single<Payload<TrackedEntityInstance>> getTrackedEntityInstances(
            @Query(TRACKED_ENTITY_INSTACE) String trackedEntityInstances,
            @Query(OU) String orgUnits,
            @Query(OU_MODE) String orgUnitMode,
            @Query(PROGRAM) String program,
            @Query(PROGRAM_STATUS) String programStatus,
            @Query(PROGRAM_START_DATE) String programStartDate,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize,
            @Query(LAST_UPDATED_START_DATE) String lastUpdatedStartDate,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes,
            @Query(INCLUDE_DELETED) boolean includeDeleted);

    @GET(TRACKED_ENTITY_INSTANCES + "/query")
    Call<SearchGrid> query(
            @Query(OU) String orgUnit,
            @Query(OU_MODE) String orgUnitMode,
            @Query(PROGRAM) String program,
            @Query(PROGRAM_START_DATE) String programStartDate,
            @Query(PROGRAM_END_DATE) String programEndDate,
            @Query(PROGRAM_STATUS) String enrollmentStatus,
            @Query(EVENT_START_DATE) String eventStartDate,
            @Query(EVENT_END_DATE) String eventEndDate,
            @Query(EVENT_STATUS) String eventStatus,
            @Query(TRACKED_ENTITY_TYPE) String trackedEntityType,
            @Query(QUERY) String query,
            @Query(ATTRIBUTE) List<String> attribute,
            @Query(FILTER) List<String> filter,
            @Query(ASSIGNED_USER_MODE) String assignedUserMode,
            @Query(ORDER) String order,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize);

    @POST("tracker/ownership/override")
    Call<HttpMessageResponse> breakGlass(
            @Query(TRACKED_ENTITY_INSTACE) String trackedEntityInstance,
            @Query(PROGRAM) String program,
            @Query(REASON) String reason
    );
}