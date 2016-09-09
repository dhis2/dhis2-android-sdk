package org.hisp.dhis.client.sdk.android.enrollment;

import org.hisp.dhis.client.sdk.core.common.network.ApiMessage;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface EnrollmentApiClientRetrofit {
    @DELETE("enrollments/{uid}")
    Call<ApiMessage> deleteEnrollment(@Path("uid") String enrollmentUid);

    @POST("enrollments")
    Call<ApiMessage> postEnrollments(@Body Map<String, List<Enrollment>> enrollments);

    @GET("enrollments")
    Call<Map<String, List<Enrollment>>> getEnrollments(@QueryMap Map<String, String> queryMap);
}
