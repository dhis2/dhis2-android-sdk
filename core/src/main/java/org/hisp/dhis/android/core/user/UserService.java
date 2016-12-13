package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.models.user.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface UserService {
    @GET("me")
    Call<User> authenticate(@QueryMap Map<String, String> queryMap);
}
