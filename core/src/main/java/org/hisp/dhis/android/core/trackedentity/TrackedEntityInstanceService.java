package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.api.responses.HttpMessageResponse;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.search.SearchGrid;

import java.util.List;

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
    String INCLUDE_ALL_ATTRIBUTES = "includeAllAttributes";
    String FILTER = "filter";
    String STRATEGY = "strategy";
    String LAST_UPDATED_START_DATE = "lastUpdatedStartDate";
    String REASON = "reason";

    @POST(TRACKED_ENTITY_INSTANCES)
    Call<WebResponse> postTrackedEntityInstances(
            @Body TrackedEntityInstancePayload trackedEntityInstances,
            @Query(STRATEGY) String strategy);

    @GET(TRACKED_ENTITY_INSTANCES)
    Call<Payload<TrackedEntityInstance>> getTrackedEntityInstance(
            @Query(TRACKED_ENTITY_INSTACE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes);

    @GET(TRACKED_ENTITY_INSTANCES + "/{" + TRACKED_ENTITY_INSTACE + "}")
    Call<TrackedEntityInstance> getTrackedEntityInstanceByProgram(
            @Path(TRACKED_ENTITY_INSTACE) String trackedEntityInstanceUid,
            @Query(PROGRAM) String program,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes);

    @GET(TRACKED_ENTITY_INSTANCES)
    Call<Payload<TrackedEntityInstance>> getTrackedEntityInstances(
            @Query(OU) String orgUnits,
            @Query(OU_MODE) String orgUnitMode,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize,
            @Query(LAST_UPDATED_START_DATE) String lastUpdatedStartDate,
            @Query(INCLUDE_ALL_ATTRIBUTES) boolean includeAllAttributes);

    @GET(TRACKED_ENTITY_INSTANCES + "/query")
    Call<SearchGrid> query(
            @Query(OU) String orgUnit,
            @Query(OU_MODE) String orgUnitMode,
            @Query(PROGRAM) String program,
            @Query(PROGRAM_START_DATE) String programStartDate,
            @Query(PROGRAM_END_DATE) String programEndDate,
            @Query(QUERY) String query,
            @Query(ATTRIBUTE) List<String> attribute,
            @Query(FILTER) List<String> filter,
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