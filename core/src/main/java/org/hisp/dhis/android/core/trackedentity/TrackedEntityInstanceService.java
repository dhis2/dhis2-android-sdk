package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrackedEntityInstanceService {
    @GET("trackedEntityInstances")
    Call<List<TrackedEntityInstance>> trackedEntityInstances(
            @Query("fields") @Which Fields<TrackedEntityInstance> fields,
            @Query("filter") @Where Filter<TrackedEntityInstance, Date> timestamp,
            @Query("ouMode") OuMode ouMode,
            @Query("includeDeleted") Boolean includeDeleted);

    @POST("trackedEntityInstances")
    Call<WebResponse> postTrackedEntityInstances(
            @Body TrackedEntityInstancePayload trackedEntityInstances);
}