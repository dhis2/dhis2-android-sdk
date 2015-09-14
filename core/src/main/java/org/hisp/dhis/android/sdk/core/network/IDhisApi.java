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

package org.hisp.dhis.android.sdk.core.network;

import org.hisp.dhis.android.sdk.models.common.SystemInfo;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.category.Category;
import org.hisp.dhis.android.sdk.models.categoryCombo.CategoryCombo;
import org.hisp.dhis.android.sdk.models.categoryOption.CategoryOption;
import org.hisp.dhis.android.sdk.models.dataset.DataSet;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.sdk.models.user.UserAccount;

import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedString;


public interface IDhisApi {

    /////////////////////////////////////////////////////////////////////////
    // Methods for getting user information
    /////////////////////////////////////////////////////////////////////////

    @GET("/system/info/")
    SystemInfo getSystemInfo();

    @GET("/me/")
    UserAccount getCurrentUserAccount(@QueryMap Map<String, String> queryParams);


    /////////////////////////////////////////////////////////////////////////
    // Methods for getting Dashboard and DashboardItems
    /////////////////////////////////////////////////////////////////////////

    @GET("/dashboards?paging=false")
    Map<String, List<Dashboard>> getDashboards(@QueryMap Map<String, String> queryMap);

    @GET("/dashboards/{uid}")
    Dashboard getDashboard(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @POST("/dashboards/")
    Response postDashboard(@Body Dashboard dashboard);

    @DELETE("/dashboards/{uid}")
    Response deleteDashboard(@Path("uid") String dashboardUId);

    @PUT("/dashboards/{uid}")
    Response putDashboard(@Path("uid") String uid, @Body Dashboard dashboard);

    @GET("/dashboardItems?paging=false")
    Map<String, List<DashboardItem>> getDashboardItems(@QueryMap Map<String, String> queryMap);

    @GET("/dashboardItems/{uid}")
    DashboardItem getDashboardItem(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @POST("/dashboards/{dashboardUId}/items/content")
    Response postDashboardItem(@Path("dashboardUId") String dashboardUId,
                               @Query("type") String type,
                               @Query("id") String uid,
                               @Body String stubBody);

    @DELETE("/dashboards/{dashboardUId}/items/{itemUId}")
    Response deleteDashboardItem(@Path("dashboardUId") String dashboardUId,
                                 @Path("itemUId") String itemUId);

    @DELETE("/dashboards/{dashboardUid}/items/{itemUid}/content/{contentUid}")
    Response deleteDashboardItemContent(@Path("dashboardUid") String dashboardUid,
                                        @Path("itemUid") String itemUid,
                                        @Path("contentUid") String contentUid);


    /////////////////////////////////////////////////////////////////////////
    // Methods for getting DashboardItemContent
    /////////////////////////////////////////////////////////////////////////

    @GET("/charts?paging=false")
    Map<String, List<DashboardItemContent>> getCharts(@QueryMap Map<String, String> queryParams);

    @GET("/eventCharts?paging=false")
    Map<String, List<DashboardItemContent>> getEventCharts(@QueryMap Map<String, String> queryParams);

    @GET("/maps?paging=false")
    Map<String, List<DashboardItemContent>> getMaps(@QueryMap Map<String, String> queryParams);

    @GET("/reportTables?paging=false")
    Map<String, List<DashboardItemContent>> getReportTables(@QueryMap Map<String, String> queryParams);

    @Headers("Accept: application/text")
    @GET("/reportTables/{id}/data.html")
    Response getReportTableData(@Path("id") String id);

    @GET("/eventReports?paging=false")
    Map<String, List<DashboardItemContent>> getEventReports(@QueryMap Map<String, String> queryParams);

    @GET("/users?paging=false")
    Map<String, List<DashboardItemContent>> getUsers(@QueryMap Map<String, String> queryParams);

    @GET("/reports?paging=false")
    Map<String, List<DashboardItemContent>> getReports(@QueryMap Map<String, String> queryMap);

    @GET("/documents?paging=false")
    Map<String, List<DashboardItemContent>> getResources(@QueryMap Map<String, String> queryMap);


    /////////////////////////////////////////////////////////////////////////
    // Methods for working with Interpretations
    /////////////////////////////////////////////////////////////////////////

    @GET("/interpretations/?paging=false")
    Map<String, List<Interpretation>> getInterpretations(@QueryMap Map<String, String> queryMap);

    @GET("/interpretations/{uid}")
    Interpretation getInterpretation(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/chart/{uid}")
    Response postChartInterpretation(@Path("uid") String elementUid,
                                     @Body TypedString interpretationText);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/map/{uid}")
    Response postMapInterpretation(@Path("uid") String elementUid,
                                   @Body TypedString interpretationText);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/reportTable/{uid}")
    Response postReportTableInterpretation(@Path("uid") String elementUid,
                                           @Body TypedString interpretationText);

    @Headers("Content-Type: text/plain")
    @PUT("/interpretations/{uid}")
    Response putInterpretationText(@Path("uid") String interpretationUid,
                                   @Body TypedString interpretationText);

    @DELETE("/interpretations/{uid}")
    Response deleteInterpretation(@Path("uid") String interpretationUid);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/{interpretationUid}/comments")
    Response postInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                       @Body TypedString commentText);

    @Headers("Content-Type: text/plain")
    @PUT("/interpretations/{interpretationUid}/comments/{commentUid}")
    Response putInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                      @Path("commentUid") String commentUid,
                                      @Body TypedString commentText);

    @DELETE("/interpretations/{interpretationUid}/comments/{commentUid}")
    Response deleteInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                         @Path("commentUid") String commentUid);


    /////////////////////////////////////////////////////////////////////////
    // Methods for working with data capture meta data
    /////////////////////////////////////////////////////////////////////////

    @GET("/organisationUnits?paging=false")
    Map<String, List<OrganisationUnit>> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets?paging=false")
    Map<String, List<DataSet>> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("/dataElements?paging=false")
    Map<String, List<DataElement>> getDataElements(@QueryMap Map<String, String> queryParams);

    @GET("/categoryCombos?paging=false")
    Map<String, List<CategoryCombo>> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("/categories?paging=false")
    Map<String, List<Category>> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("/categoryOptions?paging=false")
    Map<String, List<CategoryOption>> getCategoryOptions(@QueryMap Map<String, String> queryMap);
}