package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrackedEntityInstanceService {

    @POST("trackedEntityInstances")
    Call<WebResponse> postTrackedEntityInstances(
            @Body TrackedEntityInstancePayload trackedEntityInstances);


    @GET("trackedEntityInstances/{trackedEntityInstanceUid}")
    Call<TrackedEntityInstance> trackedEntityInstance(
            @Path("trackedEntityInstanceUid") String trackedEntityInstanceUid,
            @Query("fields") @Which Fields<TrackedEntityInstance> fields,
            @Query("includeDeleted") boolean includeDeleted);
}