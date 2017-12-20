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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static okhttp3.Credentials.basic;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.SqLiteTransaction;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;


@RunWith(JUnit4.class)
public class UserAuthenticateCallUnitShould {

    @Mock
    private UserService userService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private SqLiteTransaction sqLiteTransaction;

    @Mock
    private UserStore userStore;

    @Mock
    private UserCredentialsHandler userCredentialsHandler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private OrganisationUnitHandler organisationUnitHandler;

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
    private UserCredentials userCredentials;

    @Mock
    private User user;

    @Mock
    private Date created;

    List<OrganisationUnit> organisationUnits;

    @Mock
    private Date lastUpdated;

    @Mock
    private AuthenticatedUserModel authenticatedUser;

    // call we are testing
    private Call<Response<User>> userAuthenticateCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        userAuthenticateCall = new UserAuthenticateCall(userService, databaseAdapter, userStore,
                userCredentialsHandler, resourceHandler,
                authenticatedUserStore,
                organisationUnitHandler, "test_user_name", "test_user_password");

        when(userCredentials.uid()).thenReturn("test_user_credentials_uid");
        when(userCredentials.code()).thenReturn("test_user_credentials_code");
        when(userCredentials.name()).thenReturn("test_user_credentials_name");
        when(userCredentials.displayName()).thenReturn("test_user_credentials_display_name");
        when(userCredentials.created()).thenReturn(created);
        when(userCredentials.lastUpdated()).thenReturn(lastUpdated);
        when(userCredentials.username()).thenReturn("test_user_credentials_username");

        when(organisationUnit.uid()).thenReturn("test_organisation_unit_uid");
        when(organisationUnit.code()).thenReturn("test_organisation_unit_code");
        when(organisationUnit.name()).thenReturn("test_organisation_unit_name");
        when(organisationUnit.displayName()).thenReturn("test_organisation_unit_display_name");
        when(organisationUnit.created()).thenReturn(created);
        when(organisationUnit.lastUpdated()).thenReturn(lastUpdated);
        when(organisationUnit.shortName()).thenReturn("test_organisation_unit_short_name");
        when(organisationUnit.displayShortName()).thenReturn(
                "test_organisation_unit_display_short_name");
        when(organisationUnit.description()).thenReturn("test_organisation_unit_description");
        when(organisationUnit.displayDescription()).thenReturn(
                "test_organisation_unit_display_description");
        when(organisationUnit.path()).thenReturn("test_organisation_unit_path");
        when(organisationUnit.openingDate()).thenReturn(created);
        when(organisationUnit.closedDate()).thenReturn(lastUpdated);
        when(organisationUnit.level()).thenReturn(4);
        when(organisationUnit.parent()).thenReturn(null);

        organisationUnits = Arrays.asList(organisationUnit);

        when(user.uid()).thenReturn("test_user_uid");
        when(user.code()).thenReturn("test_user_code");
        when(user.name()).thenReturn("test_user_name");
        when(user.displayName()).thenReturn("test_user_display_name");
        when(user.created()).thenReturn(created);
        when(user.lastUpdated()).thenReturn(lastUpdated);
        when(user.birthday()).thenReturn("test_user_birthday");
        when(user.education()).thenReturn("test_user_education");
        when(user.gender()).thenReturn("test_user_gender");
        when(user.jobTitle()).thenReturn("test_user_job_title");
        when(user.surname()).thenReturn("test_user_surname");
        when(user.firstName()).thenReturn("test_user_first_name");
        when(user.introduction()).thenReturn("test_user_introduction");
        when(user.employer()).thenReturn("test_user_employer");
        when(user.interests()).thenReturn("test_user_interests");
        when(user.languages()).thenReturn("test_user_languages");
        when(user.email()).thenReturn("test_user_email");
        when(user.phoneNumber()).thenReturn("test_user_phone_number");
        when(user.nationality()).thenReturn("test_user_nationality");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.organisationUnits()).thenReturn(organisationUnits);

        when(userService.authenticate(any(String.class), any(Fields.class))).thenReturn(userCall);

        when(databaseAdapter.beginNewTransaction()).then(new Answer<Transaction>() {
            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                sqLiteTransaction.begin();
                return sqLiteTransaction;
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

        assertThat(filterCaptor.getValue().fields())
                .contains(User.uid, User.code, User.name, User.displayName, User.created,
                        User.lastUpdated, User.birthday, User.education, User.gender, User.jobTitle,
                        User.surname, User.firstName, User.introduction, User.employer,
                        User.interests,
                        User.languages, User.email, User.phoneNumber, User.nationality,
                        User.userCredentials.with(
                                UserCredentials.uid,
                                UserCredentials.code,
                                UserCredentials.name,
                                UserCredentials.displayName,
                                UserCredentials.created,
                                UserCredentials.lastUpdated,
                                UserCredentials.username),
                        User.organisationUnits.with(
                                OrganisationUnit.uid,
                                OrganisationUnit.code,
                                OrganisationUnit.name,
                                OrganisationUnit.displayName,
                                OrganisationUnit.created,
                                OrganisationUnit.lastUpdated,
                                OrganisationUnit.shortName,
                                OrganisationUnit.displayShortName,
                                OrganisationUnit.description,
                                OrganisationUnit.displayDescription,
                                OrganisationUnit.path,
                                OrganisationUnit.openingDate,
                                OrganisationUnit.closedDate,
                                OrganisationUnit.level,
                                OrganisationUnit.parent.with(
                                        OrganisationUnit.uid)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_exception_on_call() throws IOException {
        when(userCall.execute()).thenThrow(IOException.class);

        try {
            userAuthenticateCall.call();

            fail("Expected exception was not thrown");
        } catch (Exception exception) {
            // ensure that transaction is not created
            verify(databaseAdapter, never()).beginNewTransaction();
            verify(sqLiteTransaction, never()).begin();
            verify(sqLiteTransaction, never()).setSuccessful();
            verify(sqLiteTransaction, never()).end();

            // stores must not be invoked
            verify(authenticatedUserStore, never()).insert(anyString(), anyString());
            verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                    any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString());
            verify(userCredentialsHandler, never()).handleUserCredentials(
                    any(UserCredentials.class), any(User.class));
            verify(organisationUnitHandler, never()).handleOrganisationUnits(any(ArrayList.class),
                    any(OrganisationUnitModel.Scope.class), anyString());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_exception_on_request_fail() throws Exception {
        when(userCall.execute()).thenReturn(
                Response.<User>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                        ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<User> userResponse = userAuthenticateCall.call();

        assertThat(userResponse.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);

        // ensure that transaction is not created
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(sqLiteTransaction, never()).begin();
        verify(sqLiteTransaction, never()).setSuccessful();
        verify(sqLiteTransaction, never()).end();

        // stores must not be invoked
        verify(authenticatedUserStore, never()).insert(anyString(), anyString());
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(userCredentialsHandler, never()).handleUserCredentials(
                any(UserCredentials.class), any(User.class));
        verify(organisationUnitHandler, never()).handleOrganisationUnits(any(ArrayList.class),
                any(OrganisationUnitModel.Scope.class), anyString());

    }

    @Test
    public void persist_objects_after_successful_call() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        InOrder transactionMethodsOrder = inOrder(databaseAdapter);
        transactionMethodsOrder.verify(databaseAdapter).beginNewTransaction();
        verify(sqLiteTransaction).begin();
        verify(sqLiteTransaction).setSuccessful();
        verify(sqLiteTransaction).end();

        verify(authenticatedUserStore, times(1)).insert(
                "test_user_uid", base64("test_user_name", "test_user_password"));

        verify(userStore, times(1)).insert(
                "test_user_uid", "test_user_code", "test_user_name", "test_user_display_name",
                created, lastUpdated, "test_user_birthday", "test_user_education",
                "test_user_gender", "test_user_job_title", "test_user_surname",
                "test_user_first_name", "test_user_introduction", "test_user_employer",
                "test_user_interests", "test_user_languages", "test_user_email",
                "test_user_phone_number", "test_user_nationality"
        );

        verify(userCredentialsHandler, times(1)).handleUserCredentials(
                userCredentials, user);

        verify(organisationUnitHandler, times(1)).handleOrganisationUnits(
                organisationUnits, OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user.uid());

    }


    @Test
    public void not_fail_after_call_a_user_without_organisation_unit() throws Exception {
        when(user.organisationUnits()).thenReturn(null);
        when(userCall.execute()).thenReturn(Response.success(user));

        userAuthenticateCall.call();

        // ensure that transaction is created
        verify(databaseAdapter).beginNewTransaction();
        verify(sqLiteTransaction).begin();
        verify(sqLiteTransaction).setSuccessful();
        verify(sqLiteTransaction).end();

        // stores must not be invoked
        verify(authenticatedUserStore, times(1)).insert(anyString(), anyString());
        verify(userStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userCredentialsHandler, times(1)).handleUserCredentials(
                userCredentials, user);
        verify(organisationUnitHandler, never()).handleOrganisationUnits(any(ArrayList.class),
                any(OrganisationUnitModel.Scope.class), anyString());
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
        } catch (IOException ioException) {
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

    @Test
    public void throw_illegal_state_exception_if_user_already_signed_in() throws Exception {
        when(authenticatedUserStore.query()).thenReturn(Arrays.asList(authenticatedUser));

        try {
            userAuthenticateCall.call();

            fail("IllegalStateException was expected");
        } catch (IllegalStateException illegalStateException) {
            // swallow exception
        }
    }
}
