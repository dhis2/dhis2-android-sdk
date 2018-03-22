/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryComboService;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.category.CategoryService;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageEndpointCall;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleStore;
import org.hisp.dhis.android.core.user.UserService;
import org.hisp.dhis.android.core.user.UserStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould {
    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private Transaction transaction;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<SystemInfo> systemInfoCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> userCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programWithAccessCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<TrackedEntity>> trackedEntityCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OptionSet>> optionSetCall;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private SystemInfoService systemInfoService;

    @Mock
    private SystemInfoStore systemInfoStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private UserRoleStore userRoleStore;

    @Mock
    private UserStore userStore;

    @Mock
    private ProgramStore programStore;

    @Mock
    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    @Mock
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    @Mock
    private ProgramRuleVariableStore programRuleVariableStore;

    @Mock
    private ProgramIndicatorStore programIndicatorStore;

    @Mock
    private ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;

    @Mock
    private ProgramRuleActionStore programRuleActionStore;

    @Mock
    private ProgramRuleStore programRuleStore;

    @Mock
    private RelationshipTypeStore relationshipStore;

    @Mock
    private TrackedEntityStore trackedEntityStore;

    @Mock
    private UserService userService;

    @Mock
    private ProgramService programService;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private OptionSetService optionSetService;

    @Mock
    private Date serverDateTime;

    @Mock
    private User user;

    @Mock
    private DataElement dataElement;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private UserRole userRole;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private Payload<OrganisationUnit> organisationUnitPayload;

    @Mock
    private Payload<Program> programWithAccessPayload;

    @Mock
    private Payload<Program> programPayload;

    @Mock
    private Payload<ProgramStage> programStagePayload;

    @Mock
    private Payload<TrackedEntity> trackedEntityPayload;

    @Mock
    private Payload<OptionSet> optionSetPayload;

    @Mock
    private Payload<DataElement> dataElementPayload;

    @Mock
    private OptionSet optionSet;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private Program programWithAccess;

    @Mock
    private Program program;

    @Mock
    private ObjectWithUid programStageWithUid;

    @Mock
    private TrackedEntity trackedEntity;

    @Mock
    private CategoryQuery categoryQuery;

    @Mock
    private CategoryHandler categoryHandler;

    @Mock
    private CategoryComboHandler mockCategoryComboHandler;

    @Mock
    private GenericHandler<OptionSet, OptionSetModel> optionSetHandler;

    @Mock
    private OrganisationUnitCall.Factory organisationUnitCallFactory;

    @Mock
    private ProgramStageEndpointCall.Factory programStageCallFactory;

    @Mock
    private Call<Response<Payload<ProgramStage>>> programStageEndpointCall;

    @Mock
    private DataSetParentCall.Factory dataSetParentCallFactory;

    @Mock
    private Call<Response> dataSetParentCall;

    @Mock
    private Call<Response<Payload<OrganisationUnit>>> organisationUnitEndpointCall;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    private Response<Payload<DataElement>> dataSetParentCallResponse;

    private Response<Payload<ProgramStage>> programStageResponse;


    // object to test
    private MetadataCall metadataCall;


    private Response errorResponse;

    private Dhis2MockServer  dhis2MockServer;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        when(systemInfoService.getSystemInfo(any(Fields.class))).thenReturn(systemInfoCall);
        when(userService.getUser(any(Fields.class))).thenReturn(userCall);
        when(programService.getProgramsForAccess(any(Fields.class), any(Filter.class), anyBoolean())
        ).thenReturn(programWithAccessCall);
        when(programService.getPrograms(
                any(Fields.class), any(Filter.class), any(Filter.class), anyBoolean())
        ).thenReturn(programCall);
        when(trackedEntityService.trackedEntities(
                any(Fields.class), any(Filter.class), any(Filter.class), anyBoolean())
        ).thenReturn(trackedEntityCall);
        when(optionSetService.optionSets(
                anyBoolean(), any(Fields.class), any(Filter.class))
        ).thenReturn(optionSetCall);

        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(userRole);

        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(userCredentials.userRoles()).thenReturn(userRoles);
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));
        when(organisationUnitPayload.items()).thenReturn(Collections.singletonList(organisationUnit));
        when(dataAccess.read()).thenReturn(true);
        when(access.data()).thenReturn(dataAccess);
        when(programWithAccess.access()).thenReturn(access);
        when(program.trackedEntity()).thenReturn(trackedEntity);
        when(program.access()).thenReturn(access);
        when(program.programStages()).thenReturn(Collections.singletonList(programStageWithUid));
        when(programStageWithUid.uid()).thenReturn("program_stage_uid");
        when(programWithAccessPayload.items()).thenReturn(Collections.singletonList(programWithAccess));
        when(programPayload.items()).thenReturn(Collections.singletonList(program));
        when(trackedEntityPayload.items()).thenReturn(Collections.singletonList(trackedEntity));
        when(trackedEntity.uid()).thenReturn("test_tracked_entity_uid");
        when(optionSetPayload.items()).thenReturn(Collections.singletonList(optionSet));
        when(dataElementPayload.items()).thenReturn(Collections.singletonList(dataElement));

        when(resourceStore.getLastUpdated(any(ResourceModel.Type.class))).thenReturn("2017-01-01");

        when(dataSetParentCallFactory.create(any(User.class), any(GenericCallData.class), any(List.class)))
                .thenReturn(dataSetParentCall);
        when(organisationUnitCallFactory.create(any(GenericCallData.class), any(User.class), anySetOf(String.class)))
                .thenReturn(organisationUnitEndpointCall);
        Response<Payload<DataElement>> dataSetParentCallResponse = Response.success(dataElementPayload);
        when(dataSetParentCall.call()).thenReturn(dataSetParentCallResponse);

        when(programStageCallFactory.create(any(GenericCallData.class), any(Set.class)))
                .thenReturn(programStageEndpointCall);
        programStageResponse = Response.success(programStagePayload);
        when(programStageEndpointCall.call()).thenReturn(programStageResponse);

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        CategoryService categoryService = retrofit.create(CategoryService.class);
        CategoryComboService comboService = retrofit.create(CategoryComboService.class);

        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");

        metadataCall = new MetadataCall(
                databaseAdapter, systemInfoService, userService, programService, trackedEntityService,
                optionSetService, systemInfoStore, resourceStore, userStore, userCredentialsStore, userRoleStore,
                programStore, trackedEntityAttributeStore, programTrackedEntityAttributeStore, programRuleVariableStore,
                programIndicatorStore,programStageSectionProgramIndicatorLinkStore, programRuleActionStore,
                programRuleStore, relationshipStore, trackedEntityStore, categoryQuery, categoryService,
                categoryHandler, CategoryComboQuery.defaultQuery(), comboService, mockCategoryComboHandler,
                optionSetHandler, organisationUnitCallFactory, dataSetParentCallFactory, styleHandler,
                renderTypeHandler, programStageCallFactory, retrofit);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));
        when(userCall.execute()).thenReturn(Response.success(user));
        when(programCall.execute()).thenReturn(Response.success(programPayload));
        when(programWithAccessCall.execute()).thenReturn(Response.success(programWithAccessPayload));
        when(trackedEntityCall.execute()).thenReturn(Response.success(trackedEntityPayload));
        when(optionSetCall.execute()).thenReturn(Response.success(optionSetPayload));
        when(organisationUnitEndpointCall.call()).thenReturn(Response.success(organisationUnitPayload));
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void return_last_response_successful() throws Exception {
        Response response = metadataCall.call();
        assertTrue(response.isSuccessful());
    }

    @Test
    public void return_last_response_items_returned() throws Exception {
        Response response = metadataCall.call();
        Payload<DataElement> payload = (Payload<DataElement>) response.body();
        assertTrue(!payload.items().isEmpty());
        assertThat(payload.items().get(0)).isEqualTo(dataElement);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_system_info_call_fail() throws Exception {
        final int expectedTransactions = 1;
        when(systemInfoCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, times(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_user_call_fail() throws Exception {
        final int expectedTransactions = 2;
        when(userCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();//ie last one is not marked as success...
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_organisation_unit_call_fail() throws Exception {
        final int expectedTransactions = 7;
        when(organisationUnitEndpointCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful(); //taking in account the sub-transactions
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_program_call_fail() throws Exception {
        final int expectedTransactions = 5;
        when(programCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_tracked_entity_call_fail() throws Exception {
        final int expectedTransactions = 7;
        when(trackedEntityCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_option_set_fail() throws Exception {
        final int expectedTransactions = 7;
        when(optionSetCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }
}
