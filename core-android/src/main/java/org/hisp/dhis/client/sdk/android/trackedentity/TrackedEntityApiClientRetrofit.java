package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TrackedEntityApiClientRetrofit {
    @GET("trackedEntities")
    Call<Map<String, List<TrackedEntity>>> getTrackedEntities(@QueryMap Map<String, String> queryMap,
                                                                      @Query("filter") List<String> filters);
}
