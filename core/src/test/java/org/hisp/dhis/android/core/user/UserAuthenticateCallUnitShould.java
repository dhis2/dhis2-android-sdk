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

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.common.BasicCallFactory;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModelBuilder;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static okhttp3.Credentials.basic;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class UserAuthenticateCallUnitShould extends BaseCallShould {

    @Mock
    private UserService userService;

    @Mock
    private GenericHandler<User, UserModel> userHandler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler;

    @Mock
    private AuthenticatedUserStore authenticatedUserStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> authenticateAPICall;

    @Captor
    private ArgumentCaptor<String> credentialsCaptor;

    @Captor
    private ArgumentCaptor<Fields<User>> filterCaptor;

    @Mock
    private User user;

    @Mock
    private UserModel userModel;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private SystemInfoModel systemInfoModel;

    @Mock
    private AuthenticatedUserModel authenticatedUser;

    @Mock
    private IdentifiableObjectStore<UserModel> userStore;

    @Mock
    private ObjectWithoutUidStore<SystemInfoModel> systemInfoStore;

    @Mock
    private BasicCallFactory<SystemInfo> systemInfoCallFactory;

    @Mock
    private Call<SystemInfo> systemInfoEndpointCall;

    @Mock
    private Callable<Void> dbWipeCall;

    private UserAuthenticateCall.OrganisationUnitHandlerFactory organisationUnitHandlerFactory;

    private String baseEndpoint;

    // call we are testing
    private Call<User> userAuthenticateCall;

    private static final String USERNAME = "test_username";
    private static final String UID = "test_uid";
    private static final String PASSWORD = "test_password";

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        organisationUnitHandlerFactory = new UserAuthenticateCall.OrganisationUnitHandlerFactory() {
            @Override
            public GenericHandler<OrganisationUnit, OrganisationUnitModel>
            organisationUnitHandler(DatabaseAdapter databaseAdapter, User user) {
                return organisationUnitHandler;
            }
        };

        userAuthenticateCall = instantiateCall(USERNAME, PASSWORD);

        when(user.uid()).thenReturn(UID);
        when(userModel.uid()).thenReturn(UID);
        when(systemInfo.serverDate()).thenReturn(serverDate);

        baseEndpoint = "https://dhis-instance.org";
        when(systemInfo.contextPath()).thenReturn(baseEndpoint);

        when(userService.authenticate(any(String.class), any(Fields.class))).thenReturn(authenticateAPICall);

        when(systemInfoCallFactory.create(databaseAdapter, retrofit)).thenReturn(systemInfoEndpointCall);
        when(systemInfoEndpointCall.call()).thenReturn(systemInfo);
        when(authenticateAPICall.execute()).thenReturn(Response.success(user));

        when(databaseAdapter.beginNewTransaction()).then(new Answer<Transaction>() {
            @Override
            public Transaction answer(InvocationOnMock invocation) {
                transaction.begin();
                return transaction;
            }
        });
    }

    private UserAuthenticateCall instantiateCall(String username, String password) {
        return new UserAuthenticateCall(databaseAdapter, retrofit, systemInfoCallFactory,
                userService, userHandler, resourceHandler, authenticatedUserStore,
                systemInfoStore, userStore, organisationUnitHandlerFactory, dbWipeCall,
                username, password, baseEndpoint + "/api/");
    }

    @Test(expected = D2CallException.class)
    public void throw_d2_call_exception_for_null_username() throws Exception {
        instantiateCall(null, PASSWORD).call();
    }

    @Test(expected = D2CallException.class)
    public void throw_d2_call_exception_for_null_password() throws Exception {
        instantiateCall(USERNAME, null).call();
    }

    @Test
    public void invoke_server_with_correct_parameters_after_call() throws Exception {
        when(authenticateAPICall.execute()).thenReturn(Response.success(user));
        when(userService.authenticate(
                credentialsCaptor.capture(), filterCaptor.capture())
        ).thenReturn(authenticateAPICall);

        userAuthenticateCall.call();

        assertThat(basic(USERNAME, PASSWORD))
                .isEqualTo(credentialsCaptor.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_exception_on_call() throws IOException {
        when(authenticateAPICall.execute()).thenThrow(IOException.class);

        try {
            userAuthenticateCall.call();

            fail("Expected exception was not thrown");
        } catch (Exception exception) {
            verifyNoTransactionStarted();

            // stores must not be invoked
            verify(authenticatedUserStore, never()).insert(anyString(), anyString());
            verifyNoMoreInteractions(userHandler);
            verifyNoMoreInteractions(organisationUnitHandler);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_exception_on_request_fail() throws Exception {
        when(authenticateAPICall.execute()).thenReturn(
                Response.<User>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                        ResponseBody.create(MediaType.parse("application/json"), "{}")));

        try {
            userAuthenticateCall.call();
        } catch (D2CallException d2e) {

        }

        verifyNoTransactionStarted();

        // stores must not be invoked
        verify(authenticatedUserStore).query();
        verifyNoMoreInteractions(authenticatedUserStore);
        verifyNoMoreInteractions(userHandler);
        verifyNoMoreInteractions(organisationUnitHandler);
    }

    @Test
    public void succeed_when_no_previous_user_or_system_info() throws Exception {
        userAuthenticateCall.call();
        verifySuccess();
    }

    @Test
    public void not_wipe_db_when_no_previous_user_or_system_info() throws Exception {
        userAuthenticateCall.call();
        verify(dbWipeCall, never()).call();
        verifySuccess();
    }

    @Test
    public void wipe_db_when_previously_another_user() throws Exception {
        userAuthenticateCall.call();
        verify(dbWipeCall, never()).call();
        verifySuccess();
    }

    @Test
    public void wipe_db_when_previously_equal_user_but_different_server() throws Exception {
        when(userStore.selectFirst(any(CursorModelFactory.class))).thenReturn(userModel);
        when(systemInfoStore.selectFirst(any(CursorModelFactory.class))).thenReturn(systemInfoModel);
        when(systemInfoModel.contextPath()).thenReturn("https://another-instance.org/");

        userAuthenticateCall.call();

        verify(dbWipeCall).call();
        verifySuccess();
    }

    @Test
    public void not_wipe_db_when_previously_same_user() throws Exception {
        when(userStore.selectFirst(any(CursorModelFactory.class))).thenReturn(userModel);
        when(systemInfoStore.selectFirst(any(CursorModelFactory.class))).thenReturn(systemInfoModel);

        userAuthenticateCall.call();

        verify(dbWipeCall, never()).call();
        verifySuccess();
    }

    @Test
    public void wipe_db_when_previously_different_user() throws Exception {
        when(userModel.uid()).thenReturn("previous_user");
        when(userStore.selectFirst(any(CursorModelFactory.class))).thenReturn(userModel);
        when(systemInfoStore.selectFirst(any(CursorModelFactory.class))).thenReturn(systemInfoModel);

        userAuthenticateCall.call();

        verify(dbWipeCall).call();
        verifySuccess();
    }

    @Test
    public void not_fail_after_call_a_user_without_organisation_unit() throws Exception {
        when(user.organisationUnits()).thenReturn(null);

        userAuthenticateCall.call();

        verifyTransactionComplete();

        // stores must not be invoked
        verify(authenticatedUserStore, times(1)).insert(anyString(), anyString());
        verify(userHandler, times(1)).handle(eq(user), any(UserModelBuilder.class));

        verifyNoMoreInteractions(organisationUnitHandler);
    }

    @Test
    public void thrown_d2_call_exception_on_consecutive_calls() throws Exception {
        when(authenticateAPICall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        assertThat(userAuthenticateCall.isExecuted()).isEqualTo(true);

        try {
            userAuthenticateCall.call();

            fail("Invoking call second time should throw exception");
        } catch (D2CallException illegalStateException) {
            // swallow exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_when_call_is_failure() throws Exception {
        when(authenticateAPICall.execute()).thenThrow(IOException.class);

        try {
            userAuthenticateCall.call();
        } catch (D2CallException ioException) {
            // swallow exception
        }

        assertThat(userAuthenticateCall.isExecuted()).isEqualTo(true);
    }

    @Test(expected = D2CallException.class)
    public void throw_d2_call_exception_state_exception_if_user_already_signed_in() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUser));
        userAuthenticateCall.call();
    }

    private void verifySuccess() {
        verifyTransactionComplete();
        verify(authenticatedUserStore).insert(UID, base64(USERNAME, PASSWORD));
        verify(userHandler).handle(eq(user), any(UserModelBuilder.class));
        verify(organisationUnitHandler).handleMany(
                anyListOf(OrganisationUnit.class), any(OrganisationUnitModelBuilder.class));
    }
}
