package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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