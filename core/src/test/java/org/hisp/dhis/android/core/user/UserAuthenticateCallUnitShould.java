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
import java.util.Date;
import java.util.List;
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
    private retrofit2.Call<User> userCall;

    @Captor
    private ArgumentCaptor<String> credentialsCaptor;

    @Captor
    private ArgumentCaptor<Fields<User>> filterCaptor;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private User user;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private Date created;

    List<OrganisationUnit> organisationUnits;

    @Mock
    private Date lastUpdated;

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

    // call we are testing
    private Call<User> userAuthenticateCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        UserAuthenticateCall.OrganisationUnitHandlerFactory organisationUnitHandlerFactory =
                new UserAuthenticateCall.OrganisationUnitHandlerFactory() {
                    @Override
                    public GenericHandler<OrganisationUnit, OrganisationUnitModel>
                    organisationUnitHandler(DatabaseAdapter databaseAdapter, User user) {
                        return organisationUnitHandler;
                    }
                };

        userAuthenticateCall = new UserAuthenticateCall(databaseAdapter, retrofit, systemInfoCallFactory,
                userService, userHandler, resourceHandler, authenticatedUserStore,
                systemInfoStore, userStore,
                organisationUnitHandlerFactory, dbWipeCall,"test_user_name", "test_user_password");


        organisationUnits = Arrays.asList(organisationUnit);

        when(user.uid()).thenReturn("test_user_uid");
        when(systemInfo.serverDate()).thenReturn(serverDate);

        when(userService.authenticate(any(String.class), any(Fields.class))).thenReturn(userCall);

        when(systemInfoCallFactory.create(databaseAdapter, retrofit)).thenReturn(systemInfoEndpointCall);
        when(systemInfoEndpointCall.call()).thenReturn(systemInfo);

        when(databaseAdapter.beginNewTransaction()).then(new Answer<Transaction>() {
            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                transaction.begin();
                return transaction;
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invoke_server_with_correct_parameters_after_call() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));
        when(userService.authenticate(
                credentialsCaptor.capture(), filterCaptor.capture())
        ).thenReturn(userCall);

        userAuthenticateCall.call();

        assertThat(basic("test_user_name", "test_user_password"))
                .isEqualTo(credentialsCaptor.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_exception_on_call() throws IOException {
        when(userCall.execute()).thenThrow(IOException.class);

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
        when(userCall.execute()).thenReturn(
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
    public void persist_objects_after_successful_call() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        verifyTransactionComplete();

        verify(authenticatedUserStore, times(1)).insert(
                "test_user_uid", base64("test_user_name", "test_user_password"));

        verify(userHandler, times(1)).handle(eq(user), any(UserModelBuilder.class));

        verify(organisationUnitHandler).handleMany(
                anyListOf(OrganisationUnit.class), any(OrganisationUnitModelBuilder.class));
    }


    @Test
    public void not_fail_after_call_a_user_without_organisation_unit() throws Exception {
        when(user.organisationUnits()).thenReturn(null);
        when(userCall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        verifyTransactionComplete();

        // stores must not be invoked
        verify(authenticatedUserStore, times(1)).insert(anyString(), anyString());
        verify(userHandler, times(1)).handle(eq(user), any(UserModelBuilder.class));

        verifyNoMoreInteractions(organisationUnitHandler);
    }

    @Test
    public void mark_as_executed_when_call_is_success() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        assertThat(userAuthenticateCall.isExecuted()).isEqualTo(true);

        try {
            userAuthenticateCall.call();

            fail("Invoking call second time should throw exception");
        } catch (IllegalStateException illegalStateException) {
            // swallow exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_when_call_is_failure() throws Exception {
        when(userCall.execute()).thenThrow(IOException.class);

        try {
            userAuthenticateCall.call();
        } catch (D2CallException ioException) {
            // swallow exception
        }

        assertThat(userAuthenticateCall.isExecuted()).isEqualTo(true);

        try {
            userAuthenticateCall.call();

            fail("Invoking call second time should throw exception");
        } catch (IllegalStateException illegalStateException) {
            // swallow exception
        }
    }

    @Test(expected = D2CallException.class)
    public void throw_d2_call_exception_state_exception_if_user_already_signed_in() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUser));
        userAuthenticateCall.call();
    }
}
