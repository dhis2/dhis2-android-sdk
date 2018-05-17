package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
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
    String TRACKED_ENTITY_INSTANCE_UID = "trackedEntityInstanceUid";
    String OU = "ou";
    String OU_MODE = "ouMode";
    String FIELDS = "fields";
    String QUERY = "query";
    String ATTRIBUTE = "attribute";
    String PAGING = "paging";
    String PAGE = "page";
    String PAGE_SIZE = "pageSize";
    String PROGRAM = "program";
    String INCLUDE_DELETED = "includeDeleted";
    String FILTER = "filter";

    @POST(TRACKED_ENTITY_INSTANCES)
    Call<WebResponse> postTrackedEntityInstances(
            @Body TrackedEntityInstancePayload trackedEntityInstances);

    @GET(TRACKED_ENTITY_INSTANCES + "/{" + TRACKED_ENTITY_INSTANCE_UID + "}")
    Call<TrackedEntityInstance> trackedEntityInstance(
            @Path(TRACKED_ENTITY_INSTANCE_UID) String trackedEntityInstanceUid,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(INCLUDE_DELETED) boolean includeDeleted);

    @GET(TRACKED_ENTITY_INSTANCES)
    Call<Payload<TrackedEntityInstance>> getTEIs(
            @Query(OU) String orgUnits,
            @Query(OU_MODE) String orgUnitMode,
            @Query(FIELDS) @Which Fields<TrackedEntityInstance> fields,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize);

    @GET(TRACKED_ENTITY_INSTANCES + "/query")
    Call<SearchGrid> query(
            @Query(OU) String orgUnit,
            @Query(OU_MODE) String orgUnitMode,
            @Query(PROGRAM) String program,
            @Query(QUERY) String query,
            @Query(ATTRIBUTE) List<String> attribute,
            @Query(FILTER) List<String> filter,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize);
}