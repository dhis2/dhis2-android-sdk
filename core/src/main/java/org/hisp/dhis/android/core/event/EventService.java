package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.imports.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventService {

    @POST("events")
    Call<WebResponse> postEvents(@Body EventPayload events);
}
