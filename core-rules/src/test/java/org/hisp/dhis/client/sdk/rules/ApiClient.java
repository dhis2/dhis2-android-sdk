/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.rules;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static okhttp3.Credentials.basic;

public class ApiClient {
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000;   // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;      // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;     // 20s

    private static ApiClient apiClient;

    private final DhisApi dhisApi;

    private ApiClient(String serverUrl, String username, String password) {
        AuthInterceptor authInterceptor = new AuthInterceptor(username, password);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(
                MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS,
                MapperFeature.AUTO_DETECT_SETTERS);

        // Extracting base url
        HttpUrl url = HttpUrl.parse(serverUrl)
                .newBuilder()
                .addPathSegment("api")
                .build();

        HttpUrl modifiedUrl = HttpUrl.parse(url.toString() + "/"); // EW!!!
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(modifiedUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        dhisApi = retrofit.create(DhisApi.class);
    }

    public static void init(String serverUrl, String username, String password) {
        apiClient = new ApiClient(serverUrl, username, password);
    }

    public static List<Program> getPrograms() throws IOException {
        UserAccount userAccount = instance().getUserAccount();
        List<Program> assignedPrograms = userAccount.getPrograms();

        return instance().getPrograms(ModelUtils.toUidSet(assignedPrograms));
    }

    public static List<ProgramRule> getProgramRules(
            Collection<Program> programs) throws IOException {
        Set<String> programUids = ModelUtils.toUidSet(programs);

        return null;
    }

    public static List<Constant> getConstants() {
        return null;
    }

    private static ApiClient instance() {
        if (apiClient == null) {
            throw new IllegalArgumentException("Call init() first");
        }

        return apiClient;
    }

    private UserAccount getUserAccount() throws IOException {
        Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "userCredentials[userRoles[programs[id]]]");

        return dhisApi.getCurrentUserAccount(QUERY_PARAMS).execute().body();
    }

    private List<Program> getPrograms(Set<String> uids) throws IOException {
        Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", getProgramFields());
        QUERY_PARAMS.put("paging", "false");

        Map<String, List<Program>> response = dhisApi
                .getPrograms(QUERY_PARAMS, buildIdFilter(uids)).execute().body();
        return response.get("programs");
    }

    private List<ProgramRule> getProgramRules(Set<String> programUids) throws IOException {
        return null;
    }

    // NICE!
    private static String getProgramFields() {
        return "id,created,lastUpdated,name,shortName,ignoreOverdueEvents," +
                "skipOffline,dataEntryMethod,enrollmentDateLabel,onlyEnrollOnce,version," +
                "selectIncidentDatesInFuture,incidentDateLabel,selectEnrollmentDatesInFuture," +
                "displayName,displayShortName,externalAccess,displayFrontPageList,programType," +
                "relationshipFromA,relationshipText,displayIncidentDate,trackedEntity[created," +
                "lastUpdated,name,id,displayDescription,externalAccess],programIndicators[" +
                "lastUpdated,id,created,name,shortName,aggregationType,dimensionType,displayName," +
                "displayInForm,publicAccess,description,displayShortName,externalAccess," +
                "displayDescription,expression,decimals,program[id]]," +
                "programTrackedEntityAttributes[" +
                "lastUpdated,id,created,name,shortName,displayName,mandatory,displayShortName," +
                "externalAccess,valueType,allowFutureDate,dimensionItem,displayInList," +
                "program[id]," +
                "trackedEntityAttribute[lastUpdated,id,created,name,shortName,dimensionType," +
                "programScope,displayInListNoProgram,displayName,description,displayShortName," +
                "externalAccess,sortOrderInListNoProgram,displayOnVisitSchedule,valueType," +
                "sortOrderInVisitSchedule,orgunitScope,confidential,displayDescription," +
                "dimensionItem,unique,inherit,optionSetValue,optionSet[created,lastUpdated,name," +
                "id,displayName,version,externalAccess,valueType,options[code,created," +
                "lastUpdated,name,id,displayName,externalAccess]]]],programStages[lastUpdated,id," +
                "created,name,executionDateLabel,allowGenerateNextVisit,validCompleteOnly," +
                "pregenerateUid,displayName,description,externalAccess,openAfterEnrollment," +
                "repeatable,captureCoordinates,formType,remindCompleted,displayGenerateEventBox," +
                "generatedByEnrollmentDate,defaultTemplateMessage,autoGenerateEvent,sortOrder," +
                "hideDueDate,blockEntryForm,minDaysFromStart,program[id]," +
                "programStageDataElements[" +
                "created,lastUpdated,id,displayInReports,externalAccess,compulsory," +
                "allowProvidedElsewhere,sortOrder,allowFutureDates,programStage[id],dataElement[" +
                "code,lastUpdated,id,created,name,shortName,aggregationType,dimensionType," +
                "domainType,displayName,publicAccess,displayShortName,externalAccess,valueType," +
                "formName,dimensionItem,displayFormName,zeroIsSignificant,url,optionSetValue," +
                "optionSet[created,lastUpdated,name,id,displayName,version,externalAccess," +
                "valueType,options[code,created,lastUpdated,name,id,displayName,externalAccess" +
                "]]]],programStageSections[created,lastUpdated,name,id,displayName," +
                "externalAccess," +
                "sortOrder,programStage[id],programIndicators[id],programStageDataElements[id]]]";
    }

    private static String buildIdFilter(Set<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            return "id:in:[" + join(ids, ",") + "]";
        }

        return "";
    }

    private static String join(Collection<String> strings, String delimiter) {
        Preconditions.isNull(delimiter, "String delimiter must not be null");

        StringBuilder buffer = new StringBuilder();

        if (strings != null) {
            Iterator<? extends String> iterator = strings.iterator();

            if (iterator.hasNext()) {
                buffer.append(iterator.next());

                while (iterator.hasNext()) {
                    buffer.append(delimiter).append(iterator.next());
                }
            }
        }

        return buffer.toString();
    }

    private interface DhisApi {

        @GET("me/")
        Call<UserAccount> getCurrentUserAccount(@QueryMap Map<String, String> queryParams);

        @GET("programs/")
        Call<Map<String, List<Program>>> getPrograms(
                @QueryMap Map<String, String> queryParams, @Query("filter") String filter);
    }

    private static class AuthInterceptor implements Interceptor {
        private final String username;
        private final String password;

        public AuthInterceptor(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            String base64Credentials = basic(username, password);
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", base64Credentials)
                    .build();

            return chain.proceed(request);
        }
    }
}
