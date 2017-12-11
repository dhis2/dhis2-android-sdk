package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventService {

    @POST("events")
    Call<WebResponse> postEvents(@Body EventPayload events);

    @GET("events")
    Call<Payload<Event>> getEvents(@Query("orgUnit") String orgUnit,
            @Query("program") String program,
            @Query("trackedEntityInstance") String trackedEntityInstance,
            @Query("fields") @Which Fields<Event> fields,
            @Query("filter") @Where Filter<Event, String> lastUpdated,
            @Query("filter") @Where Filter<Event, String> uids,
            @Query("paging") Boolean paging, @Query("page") int page,
            @Query("pageSize") int pageSize);
}
