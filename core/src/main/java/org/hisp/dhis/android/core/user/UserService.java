package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Fields;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface UserService {

    @GET("me")
    Call<User> authenticate(@Header("Authorization") String credentials,
            @Query("fields") @Fields Filter<User> filter);
}
