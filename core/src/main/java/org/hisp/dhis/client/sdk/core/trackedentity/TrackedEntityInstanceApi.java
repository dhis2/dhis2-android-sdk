package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.commons.Payload;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface TrackedEntityInstanceApi {
    @GET("trackedEntityInstances")
    Call<Payload<TrackedEntityInstance>> list(@QueryMap Map<String, String> queryMap);
}
