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

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.MetadataCall;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataset.DataSetParentCall;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserRole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould extends BaseCallShould {
    @Mock
    private SystemInfo systemInfo;

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
    private Payload<Category> categoryPayload;

    @Mock
    private Payload<CategoryCombo> categoryComboPayload;

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
    private Category category;

    @Mock
    private CategoryCombo categoryCombo;

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
    private Call<Response<SystemInfo>> systemInfoEndpointCall;

    @Mock
    private Call<Response<User>> userCall;

    @Mock
    private Call<Response<Payload<Category>>> categoryEndpointCall;

    @Mock
    private Call<Response<Payload<CategoryCombo>>> categoryComboEndpointCall;

    @Mock
    private Call<Response<Payload<Program>>> programAccessEndpointCall;

    @Mock
    private Call<Response<Payload<Program>>> programEndpointCall;

    @Mock
    private Call<Response<Payload<ProgramStage>>> programStageEndpointCall;

    @Mock
    private Call<Response<Payload<TrackedEntity>>> trackedEntityCall;

    @Mock
    private Call<Response<Payload<OptionSet>>> optionSetCall;

    @Mock
    private Call<Response> dataSetParentCall;

    @Mock
    private Call<Response<Payload<OrganisationUnit>>> organisationUnitEndpointCall;

    @Mock
    private SimpleCallFactory<SystemInfo> systemInfoCallFactory;

    @Mock
    private SimpleCallFactory<User> userCallFactory;

    @Mock
    private SimpleCallFactory<Payload<Program>> programAccessCallFactory;

    @Mock
    private SimpleCallFactory<Payload<Category>> categoryCallFactory;

    @Mock
    private SimpleCallFactory<Payload<CategoryCombo>> categoryComboCallFactory;

    @Mock
    private UidsCallFactory<Program> programCallFactory;

    @Mock
    private UidsCallFactory<ProgramStage> programStageCallFactory;

    @Mock
    private UidsCallFactory<TrackedEntity> trackedEntityCallFactory;

    @Mock
    private OrganisationUnitCall.Factory organisationUnitCallFactory;

    @Mock
    private UidsCallFactory<OptionSet> optionSetCallFactory;

    @Mock
    private DataSetParentCall.Factory dataSetParentCallFactory;

    // object to test
    private MetadataCall metadataCall;


    private Response errorResponse;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        // Payload data
        when(systemInfo.serverDate()).thenReturn(serverDateTime);
        when(userCredentials.userRoles()).thenReturn(Collections.singletonList(userRole));
        when(organisationUnit.uid()).thenReturn("unit");
        when(organisationUnit.path()).thenReturn("path/to/org/unit");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(Collections.singletonList(organisationUnit));
        when(dataAccess.read()).thenReturn(true);
        when(access.data()).thenReturn(dataAccess);
        when(programWithAccess.access()).thenReturn(access);
        when(program.trackedEntity()).thenReturn(trackedEntity);
        when(program.access()).thenReturn(access);
        when(program.programStages()).thenReturn(Collections.singletonList(programStageWithUid));
        when(programStageWithUid.uid()).thenReturn("program_stage_uid");
        when(trackedEntity.uid()).thenReturn("test_tracked_entity_uid");

        // Payloads
        when(programWithAccessPayload.items()).thenReturn(Collections.singletonList(programWithAccess));
        when(categoryPayload.items()).thenReturn(Collections.singletonList(category));
        when(categoryComboPayload.items()).thenReturn(Collections.singletonList(categoryCombo));
        when(programPayload.items()).thenReturn(Collections.singletonList(program));
        when(trackedEntityPayload.items()).thenReturn(Collections.singletonList(trackedEntity));
        when(organisationUnitPayload.items()).thenReturn(Collections.singletonList(organisationUnit));
        when(optionSetPayload.items()).thenReturn(Collections.singletonList(optionSet));
        when(dataElementPayload.items()).thenReturn(Collections.singletonList(dataElement));

        // Call factories
        when(systemInfoCallFactory.create(genericCallData)).thenReturn(systemInfoEndpointCall);
        when(userCallFactory.create(genericCallData)).thenReturn(userCall);
        when(programAccessCallFactory.create(genericCallData)).thenReturn(programAccessEndpointCall);
        when(categoryCallFactory.create(genericCallData)).thenReturn(categoryEndpointCall);
        when(categoryComboCallFactory.create(genericCallData)).thenReturn(categoryComboEndpointCall);
        when(programCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(programEndpointCall);
        when(programStageCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(programStageEndpointCall);
        when(trackedEntityCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(trackedEntityCall);
        when(organisationUnitCallFactory.create(same(genericCallData), same(user), anySetOf(String.class)))
                .thenReturn(organisationUnitEndpointCall);
        when(optionSetCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(optionSetCall);
        when(dataSetParentCallFactory.create(same(user), same(genericCallData), any(List.class)))
                .thenReturn(dataSetParentCall);

        // Calls
        when(systemInfoEndpointCall.call()).thenReturn(Response.success(systemInfo));
        when(userCall.call()).thenReturn(Response.success(user));
        when(categoryEndpointCall.call()).thenReturn(Response.success(categoryPayload));
        when(categoryComboEndpointCall.call()).thenReturn(Response.success(categoryComboPayload));
        when(programEndpointCall.call()).thenReturn(Response.success(programPayload));
        when(programAccessEndpointCall.call()).thenReturn(Response.success(programWithAccessPayload));
        when(trackedEntityCall.call()).thenReturn(Response.success(trackedEntityPayload));
        when(optionSetCall.call()).thenReturn(Response.success(optionSetPayload));
        when(organisationUnitEndpointCall.call()).thenReturn(Response.success(organisationUnitPayload));
        when(dataSetParentCall.call()).thenReturn(Response.success(dataElementPayload));
        when(programStageEndpointCall.call()).thenReturn(Response.success(programStagePayload));

        // Metadata call
        metadataCall = new MetadataCall(
                genericCallData,
                systemInfoCallFactory,
                userCallFactory,
                categoryCallFactory,
                categoryComboCallFactory,
                programAccessCallFactory,
                programCallFactory,
                programStageCallFactory,
                trackedEntityCallFactory,
                organisationUnitCallFactory,
                optionSetCallFactory,
                dataSetParentCallFactory);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
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
        when(systemInfoEndpointCall.call()).thenReturn(errorResponse);

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
        when(userCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_organisation_unit_call_fail() throws Exception {
        when(organisationUnitEndpointCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_program_call_fail() throws Exception {
        when(programEndpointCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_tracked_entity_call_fail() throws Exception {
        when(trackedEntityCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void verify_transaction_fail_when_option_set_fail() throws Exception {
        when(optionSetCall.call()).thenReturn(errorResponse);

        Response response = metadataCall.call();

        assertThat(response).isEqualTo(errorResponse);
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        verify(databaseAdapter).beginNewTransaction();
        verify(transaction).end();
        verify(transaction, never()).setSuccessful();
    }
}
