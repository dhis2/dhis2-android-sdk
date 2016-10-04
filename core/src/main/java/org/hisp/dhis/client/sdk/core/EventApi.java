package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.models.common.Payload;
import org.hisp.dhis.client.sdk.models.event.Event;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface EventApi {
    @GET("events")
    Call<Payload<Event>> list(@QueryMap Map<String, String> queryMap);
}
