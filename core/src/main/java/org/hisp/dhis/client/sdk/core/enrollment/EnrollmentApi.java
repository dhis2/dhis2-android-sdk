package org.hisp.dhis.client.sdk.core.enrollment;

import org.hisp.dhis.client.sdk.core.commons.Payload;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface EnrollmentApi {
    @GET("enrollments")
    Call<Payload<Enrollment>> list(@QueryMap Map<String, String> queryMap);
}
