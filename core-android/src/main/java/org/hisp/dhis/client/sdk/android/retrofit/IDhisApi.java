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

package org.hisp.dhis.client.sdk.android.retrofit;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Response;

import org.hisp.dhis.client.sdk.models.category.Category;
import org.hisp.dhis.client.sdk.models.category.CategoryCombo;
import org.hisp.dhis.client.sdk.models.category.CategoryOption;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.dataset.DataSet;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

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


public interface IDhisApi {

    /////////////////////////////////////////////////////////////////////////
    // Strings for referencing endpoints in the DHIS 2 Web API
    /////////////////////////////////////////////////////////////////////////

    String DASHBOARDS = "dashboards";
    String DASHBOARD_ITEMS = "dashboardItems";

    String ORGANISATION_UNITS = "organisationUnits";
    String PROGRAMS = "programs";
    String OPTION_SETS = "optionSets";
    String TRACKED_ENTITY_ATTRIBUTES = "trackedEntityAttributes";
    String CONSTANTS = "constants";
    String PROGRAM_RULES = "programRules";
    String PROGRAM_RULE_VARIABLES = "programRuleVariables";
    String PROGRAM_RULE_ACTIONS = "programRuleActions";
    String RELATION_SHIP_TYPES = "relationshipTypes";
    String EVENTS = "events";
    String TRACKED_ENTITY_INSTANCES = "trackedEntityInstances";
    String ENROLLMENTS = "enrollments";

    /////////////////////////////////////////////////////////////////////////
    // Methods for working with Interpretations
    /////////////////////////////////////////////////////////////////////////

    @GET("/interpretations/?paging=false")
    Call<Map<String, List<Interpretation>>> getInterpretations(@QueryMap Map<String, String> queryMap);

    @GET("/interpretations/{uid}")
    Call<Interpretation> getInterpretation(@Path("uid") String uId, @QueryMap Map<String, String> queryMap);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/chart/{uid}")
    Call<Response> postChartInterpretation(@Path("uid") String elementUid,
                                           @Body String interpretationText);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/map/{uid}")
    Call<Response> postMapInterpretation(@Path("uid") String elementUid,
                                         @Body String interpretationText);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/reportTable/{uid}")
    Call<Response> postReportTableInterpretation(@Path("uid") String elementUid,
                                                 @Body String interpretationText);

    @Headers("Content-Type: text/plain")
    @PUT("/interpretations/{uid}")
    Call<Response> putInterpretationText(@Path("uid") String interpretationUid,
                                         @Body String interpretationText);

    @DELETE("/interpretations/{uid}")
    Call<Response> deleteInterpretation(@Path("uid") String interpretationUid);

    @Headers("Content-Type: text/plain")
    @POST("/interpretations/{interpretationUid}/comments")
    Call<Response> postInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                             @Body String commentText);

    @Headers("Content-Type: text/plain")
    @PUT("/interpretations/{interpretationUid}/comments/{commentUid}")
    Call<Response> putInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                            @Path("commentUid") String commentUid,
                                            @Body String commentText);

    @DELETE("/interpretations/{interpretationUid}/comments/{commentUid}")
    Call<Response> deleteInterpretationComment(@Path("interpretationUid") String interpretationUid,
                                               @Path("commentUid") String commentUid);


    /////////////////////////////////////////////////////////////////////////
    // Methods for working with data capture meta data
    /////////////////////////////////////////////////////////////////////////

    @GET("/organisationUnits?paging=false")
    Call<Map<String, List<OrganisationUnit>>> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets?paging=false")
    Call<Map<String, List<DataSet>>> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("/dataElements?paging=false")
    Call<Map<String, List<DataElement>>> getDataElements(@QueryMap Map<String, String> queryParams);

    @GET("/categoryCombos?paging=false")
    Call<Map<String, List<CategoryCombo>>> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("/categories?paging=false")
    Call<Map<String, List<Category>>> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("/categoryOptions?paging=false")
    Call<Map<String, List<CategoryOption>>> getCategoryOptions(@QueryMap Map<String, String> queryMap);

    /////////////////////////////////////////////////////////////////////////
    // Methods for working with tracker meta data
    /////////////////////////////////////////////////////////////////////////

    @GET("/" + TRACKED_ENTITY_ATTRIBUTES + "?paging=false")
    Call<Map<String, List<TrackedEntityAttribute>>> getTrackedEntityAttributes(@QueryMap Map<String, String> queryParams);

    @GET("/" + CONSTANTS + "?paging=false")
    Call<Map<String, List<Constant>>> getConstants(@QueryMap Map<String, String> queryParams);

    @GET("/" + PROGRAM_RULES + "?paging=false")
    Call<Map<String, List<ProgramRule>>> getProgramRules(@QueryMap Map<String, String> queryParams);

    @GET("/" + PROGRAM_RULE_VARIABLES + "?paging=false")
    Call<Map<String, List<ProgramRuleVariable>>> getProgramRuleVariables(@QueryMap Map<String, String> queryParams);

    @GET("/" + PROGRAM_RULE_ACTIONS + "?paging=false")
    Call<Map<String, List<ProgramRuleAction>>> getProgramRuleActions(@QueryMap Map<String, String> queryParams);

    @GET("/" + RELATION_SHIP_TYPES + "?paging=false")
    Call<Map<String, List<RelationshipType>>> getRelationshipTypes(@QueryMap Map<String, String> queryParams);

    @GET("/" + OPTION_SETS + "?paging=false")
    Call<Map<String, List<OptionSet>>> getOptionSets(@QueryMap Map<String, String> queryParams);

    @GET("/" + PROGRAMS + "/{programUid}")
    Call<Program> getProgram(@Path("programUid") String programUid, @QueryMap Map<String, String> queryMap);

    @GET("/" + PROGRAMS + "/?paging=false")
    Call<Map<String, List<Program>>> getPrograms(@QueryMap Map<String, String> queryMap);

    @GET("/me/programs/")
    Call<Response> getAssignedPrograms(@QueryMap Map<String, String> queryMap);

    /////////////////////////////////////////////////////////////////////////
    // Methods for working with Tracker Data Values
    /////////////////////////////////////////////////////////////////////////
    @GET("/" + EVENTS + "?page=0")
    Call<JsonNode> getEvents(@Query("program") String programUid,
                             @Query("orgUnit") String organisationUnitUid,
                             @Query("pageSize") int eventLimit,
                             @QueryMap Map<String, String> queryParams);

    @GET("/" + EVENTS + "?paging=false&ouMode=ACCESSIBLE")
    Call<JsonNode> getEventsForEnrollment(@Query("program") String programUid,
                                          @Query("programStatus") String programStatus,
                                          @Query("trackedEntityInstance") String
                                                  trackedEntityInstanceUid,
                                          @QueryMap Map<String, String> queryParams);

    @GET("/" + EVENTS + "/{eventUid}")
    Call<Event> getEvent(@Path("eventUid") String eventUid,
                         @QueryMap Map<String, String> queryMap);

    @POST("/" + EVENTS + "/")
    Call<Response> postEvent(@Body Event event);

    @PUT("/" + EVENTS + "/{eventUid}")
    Call<Response> putEvent(@Path("eventUid") String eventUid,
                            @Body Event event);

    @GET("/" + ENROLLMENTS + "/{enrollmentUid}")
    Call<Enrollment> getEnrollment(@Path("enrollmentUid") String enrollmentUid,
                                   @QueryMap Map<String, String> queryMap);

    @GET("/" + ENROLLMENTS + "?ouMode=ACCESSIBLE")
    Call<Map<String, List<Enrollment>>> getEnrollments(@Query("trackedEntityInstance") String trackedEntityInstanceUid,
                                                       @QueryMap Map<String, String> queryMap);

    @POST("/" + ENROLLMENTS + "/")
    Call<Response> postEnrollment(@Body Enrollment enrollment);

    @PUT("/" + ENROLLMENTS + "/{enrollmentUid}")
    Call<Response> putEnrollment(@Path("enrollmentUid") String enrollmentUid,
                                 @Body Enrollment enrollment);

    @GET("/" + TRACKED_ENTITY_INSTANCES + "/{trackedEntityInstanceUid}")
    Call<TrackedEntityInstance> getTrackedEntityInstance(@Path("trackedEntityInstanceUid") String trackedEntityInstanceUid,
                                                         @QueryMap Map<String, String> queryMap);

    @GET("/" + TRACKED_ENTITY_INSTANCES)
    Call<Map<String, List<TrackedEntityInstance>>> getTrackedEntityInstances(@Query("ou") String organisationUnitUid,
                                                                             @QueryMap Map<String, String> queryMap);

    @POST("/" + TRACKED_ENTITY_INSTANCES + "/")
    Call<Response> postTrackedEntityInstance(@Body TrackedEntityInstance trackedEntityInstance);

    @PUT("/" + TRACKED_ENTITY_INSTANCES + "/{trackedEntityInstanceUid}")
    Call<Response> putTrackedEntityInstance(@Path("trackedEntityInstanceUid") String trackedEntityInstanceUid,
                                            @Body TrackedEntityInstance trackedEntityInstance);
}
