package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.models.common.Property;
import org.hisp.dhis.android.models.user.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("me")
    Call<User> me(@Query("fields") @Fields Property... properties);
}
