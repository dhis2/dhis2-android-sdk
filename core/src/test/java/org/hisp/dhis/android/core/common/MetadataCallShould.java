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

import static junit.framework.Assert.assertTrue;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.sqlite.SQLiteStatement;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.Category;
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
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramService;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeFactory;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfoService;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkStore;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@RunWith(JUnit4.class)
public class MetadataCallShould {
    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private Transaction transaction;

    @Mock
    private SQLiteStatement sqliteStatement;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<SystemInfo> systemInfoCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> userCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OrganisationUnit>> organisationUnitCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programCall;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<OptionSet>> optionSetCall;

    @Mock
    private SystemInfo systemInfo;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Category>> categoryInfo;

    @Mock
    private SystemInfoService systemInfoService;

    @Mock
    private SystemInfoHandler systemInfoHandler;

    @Mock
    private SystemInfoStore systemInfoStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private UserRoleStore userRoleStore;

    @Mock
    private UserRoleProgramLinkStore userRoleProgramLinkStore;

    @Mock
    private OrganisationUnitStore organisationUnitStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private UserStore userStore;

    @Mock
    private ProgramStore programStore;

    @Mock
    private TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    @Mock
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    @Mock
    private ProgramRuleVariableStore programRuleVariableStore;

    @Mock
    private ProgramIndicatorStore programIndicatorStore;

    @Mock
    private ProgramStageSectionProgramIndicatorLinkStore
            programStageSectionProgramIndicatorLinkStore;

    @Mock
    private ProgramRuleActionStore programRuleActionStore;

    @Mock
    private ProgramRuleStore programRuleStore;

    @Mock
    private ProgramStageDataElementStore programStageDataElementStore;

    @Mock
    private ProgramStageSectionStore programStageSectionStore;

    @Mock
    private ProgramStageStore programStageStore;

    @Mock
    private RelationshipTypeStore relationshipStore;

    @Mock
    private OrganisationUnitProgramLinkStore organisationUnitProgramLinkStore;

    @Mock
    private UserService userService;

    @Mock
    private ProgramService programService;

    @Mock
    private OrganisationUnitService organisationUnitService;

    @Mock
    private OptionSetService optionSetService;

    @Mock
    private Date serverDateTime;

    @Mock
    private User user;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private List<UserRole> userRoles;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private Payload<OrganisationUnit> organisationUnitPayload;

    @Mock
    private Payload<Program> programPayload;

    @Mock
    private Payload<OptionSet> optionSetPayload;

    @Mock
    private OptionSet optionSet;

    @Mock
    private Program program;

    @Mock
    private TrackedEntity trackedEntity;

    @Mock
    private CategoryQuery categoryQuery;

    private CategoryService categoryService;

    @Mock
    private CategoryHandler categoryHandler;


    private CategoryComboService comboService;

    @Mock
    private CategoryComboHandler mockCategoryComboHandler;

    private OptionSetFactory optionSetFactory;
    private TrackedEntityFactory trackedEntityFactory;
    private DataElementFactory dataElementFactory;
    private RelationshipTypeFactory relationshipTypeFactory;

    // object to test
    private MetadataCall metadataCall;


    private Response errorResponse;

    Dhis2MockServer dhis2MockServer;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
        when(databaseAdapter.compileStatement(anyString())).thenReturn(sqliteStatement);

        when(systemInfoService.getSystemInfo(any(Fields.class))).thenReturn(systemInfoCall);
        when(userService.getUser(any(Fields.class))).thenReturn(userCall);
        when(organisationUnitService.getOrganisationUnits(
                anyString(), any(Fields.class), any(Filter.class), anyBoolean(), anyBoolean())
        ).thenReturn(organisationUnitCall);
        when(programService.getPrograms(
                any(Fields.class), any(Filter.class), any(Filter.class), anyBoolean())
        ).thenReturn(programCall);

        when(optionSetService.optionSets(
                anyBoolean(), any(Fields.class), any(Filter.class))
        ).thenReturn(optionSetCall);


        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(userCredentials.userRoles()).thenReturn(userRoles);
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));
        when(organisationUnitPayload.items()).thenReturn(
                Collections.singletonList(organisationUnit));
        when(program.trackedEntity()).thenReturn(trackedEntity);
        when(programPayload.items()).thenReturn(Collections.singletonList(program));
        when(trackedEntity.uid()).thenReturn("nEenWmSyUEp");
        when(optionSetPayload.items()).thenReturn(Collections.singletonList(optionSet));

        when(resourceStore.getLastUpdated(any(ResourceModel.Type.class))).thenReturn("2017-01-01");

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();


        categoryService = retrofit.create(CategoryService.class);
        comboService = retrofit.create(CategoryComboService.class);

        optionSetFactory = new OptionSetFactory(retrofit, databaseAdapter, resourceHandler);

        trackedEntityFactory = new TrackedEntityFactory(retrofit, databaseAdapter, resourceHandler);

        dataElementFactory = new DataElementFactory(retrofit, databaseAdapter, resourceHandler);

        relationshipTypeFactory =
                new RelationshipTypeFactory(retrofit, databaseAdapter, resourceHandler);

        metadataCall = new MetadataCall(
                databaseAdapter, systemInfoService, userService,
                programService, organisationUnitService,
                systemInfoStore, resourceStore, userStore,
                userCredentialsStore, userRoleStore, userRoleProgramLinkStore,
                organisationUnitStore, userOrganisationUnitLinkStore, programStore,
                programTrackedEntityAttributeStore, programRuleVariableStore, programIndicatorStore,
                programStageSectionProgramIndicatorLinkStore, programRuleActionStore,
                programRuleStore, programStageDataElementStore, programStageSectionStore,
                programStageStore, organisationUnitProgramLinkStore, categoryQuery,
                categoryService, categoryHandler, CategoryComboQuery.defaultQuery(), comboService,
                mockCategoryComboHandler, optionSetFactory, trackedEntityFactory,
                trackedEntityAttributeFactory, dataElementFactory, relationshipTypeFactory);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));
        when(userCall.execute()).thenReturn(Response.success(user));
        when(organisationUnitCall.execute()).thenReturn(Response.success(organisationUnitPayload));
        when(programCall.execute()).thenReturn(Response.success(programPayload));
        when(optionSetCall.execute()).thenReturn(Response.success(optionSetPayload));
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void returns_category_combo_payload_when_execute_metadata_call() throws Exception {
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        Response response = metadataCall.call();
        // assert that last successful response is returned

        Payload<String> payload = (Payload<String>) response.body();

        assertTrue(!payload.items().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_system_info_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

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
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        final int expectedTransactions = 2;
        when(userCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions
                - 1)).setSuccessful();//ie last one is not marked as success...
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_organisation_unit_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        final int expectedTransactions = 4;
        when(organisationUnitCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions
                - 1)).setSuccessful(); //taking in account the sub-transactions
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_program_call_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        final int expectedTransactions = 6;
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
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 8;

        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_option_set_fail() throws Exception {
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("api_error.json", HttpURLConnection.HTTP_CONFLICT);

        final int expectedTransactions = 8;
        when(optionSetCall.execute()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CONFLICT);
        verify(databaseAdapter, times(expectedTransactions)).beginNewTransaction();
        verify(transaction, times(expectedTransactions)).end();
        verify(transaction, atMost(expectedTransactions - 1)).setSuccessful();
    }
}
