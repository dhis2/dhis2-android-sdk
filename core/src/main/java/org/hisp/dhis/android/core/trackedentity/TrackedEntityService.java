package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrackedEntityService {

    @GET("trackedEntities")
    Call<Payload<TrackedEntity>> trackedEntities(@Query("paging") boolean paging,
                                                 @Query("fields") @Fields Filter<TrackedEntity> filter);
}
