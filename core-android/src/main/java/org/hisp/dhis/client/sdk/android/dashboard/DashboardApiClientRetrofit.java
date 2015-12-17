/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.dashboard;

import com.squareup.okhttp.Response;

import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface DashboardApiClientRetrofit {

    /////////////////////////////////////////////////////////////////////////
    // Methods for getting Dashboard and DashboardItems
    /////////////////////////////////////////////////////////////////////////

    @GET("/dashboards?paging=false")
    Call<Map<String, List<Dashboard>>> getDashboards(@QueryMap Map<String, String> queryMap);

    @GET("/dashboards/{uid}")
    Call<Dashboard> getDashboard(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @POST("/dashboards/")
    Call<Response> postDashboard(@Body Dashboard dashboard);

    @DELETE("/dashboards/{uid}")
    Call<Response> deleteDashboard(@Path("uid") String dashboardUId);

    @PUT("/dashboards/{uid}")
    Call<Response> putDashboard(@Path("uid") String uid, @Body Dashboard dashboard);

    @GET("/dashboardItems?paging=false")
    Call<Map<String, List<DashboardItem>>> getDashboardItems(@QueryMap Map<String, String> queryMap);

    @GET("/dashboardItems/{uid}")
    Call<DashboardItem> getDashboardItem(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @POST("/dashboards/{dashboardUId}/items/content")
    Call<Response> postDashboardItem(@Path("dashboardUId") String dashboardUId,
                                     @Query("type") String type,
                                     @Query("id") String uid,
                                     @Body String stubBody);

    @DELETE("/dashboards/{dashboardUId}/items/{itemUId}")
    Call<Response> deleteDashboardItem(@Path("dashboardUId") String dashboardUId,
                                       @Path("itemUId") String itemUId);

    @DELETE("/dashboards/{dashboardUid}/items/{itemUid}/content/{contentUid}")
    Call<Response> deleteDashboardItemContent(@Path("dashboardUid") String dashboardUid,
                                              @Path("itemUid") String itemUid,
                                              @Path("contentUid") String contentUid);


    /////////////////////////////////////////////////////////////////////////
    // Methods for getting DashboardContent
    /////////////////////////////////////////////////////////////////////////

    @GET("/charts?paging=false")
    Call<Map<String, List<DashboardContent>>> getCharts(@QueryMap Map<String, String> queryParams);

    @GET("/eventCharts?paging=false")
    Call<Map<String, List<DashboardContent>>> getEventCharts(@QueryMap Map<String, String> queryParams);

    @GET("/maps?paging=false")
    Call<Map<String, List<DashboardContent>>> getMaps(@QueryMap Map<String, String> queryParams);

    @GET("/reportTables?paging=false")
    Call<Map<String, List<DashboardContent>>> getReportTables(@QueryMap Map<String, String> queryParams);

    @Headers("Accept: application/text")
    @GET("/reportTables/{id}/data.html")
    Call<Response> getReportTableData(@Path("id") String id);

    @GET("/eventReports?paging=false")
    Call<Map<String, List<DashboardContent>>> getEventReports(@QueryMap Map<String, String> queryParams);

    @GET("/users?paging=false")
    Call<Map<String, List<DashboardContent>>> getUsers(@QueryMap Map<String, String> queryParams);

    @GET("/reports?paging=false")
    Call<Map<String, List<DashboardContent>>> getReports(@QueryMap Map<String, String> queryMap);

    @GET("/documents?paging=false")
    Call<Map<String, List<DashboardContent>>> getResources(@QueryMap Map<String, String> queryMap);
}
