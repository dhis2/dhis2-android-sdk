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
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserCallShould {

    @Mock
    private UserService userService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private UserStore userStore;

    @Mock
    private Transaction transaction;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private UserRoleStore userRoleStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<User> userCall;

    @Captor
    private ArgumentCaptor<Fields<User>> filterCaptor;

    @Mock
    private OrganisationUnit organisationUnit;

    @Mock
    private UserCredentials userCredentials;

    @Mock
    private User user;

    @Mock
    private UserRole userRole;

    @Mock
    private Program program;

    @Mock
    private Date created;

    @Mock
    private Date lastUpdated;

    @Mock
    private Date serverDate;

    private Call<Response<User>> userSyncCall;

    @Mock
    private UserRoleProgramLinkStore userRoleProgramLinkStore;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userSyncCall = new UserCall(
                userService, databaseAdapter,
                userStore, userCredentialsStore, userRoleStore, resourceStore,
                serverDate, userRoleProgramLinkStore
        );

        when(userCredentials.uid()).thenReturn("user_credentials_uid");
        when(userCredentials.code()).thenReturn("user_credentials_code");
        when(userCredentials.name()).thenReturn("user_credentials_name");
        when(userCredentials.displayName()).thenReturn("user_credentials_displayName");
        when(userCredentials.created()).thenReturn(created);
        when(userCredentials.lastUpdated()).thenReturn(lastUpdated);
        when(userCredentials.username()).thenReturn("user_credentials_username");

        when(program.uid()).thenReturn("program_uid");

        when(userRole.uid()).thenReturn("user_role_uid");
        when(userRole.code()).thenReturn("user_role_code");
        when(userRole.name()).thenReturn("user_role_name");
        when(userRole.displayName()).thenReturn("user_role_display_name");
        when(userRole.created()).thenReturn(created);
        when(userRole.lastUpdated()).thenReturn(lastUpdated);
        when(userRole.programs()).thenReturn(Collections.singletonList(program));

        when(organisationUnit.uid()).thenReturn("organisation_unit_uid");
        when(organisationUnit.code()).thenReturn("organisation_unit_code");
        when(organisationUnit.name()).thenReturn("organisation_unit_name");
        when(organisationUnit.displayName()).thenReturn("organisation_unit_displayName");
        when(organisationUnit.created()).thenReturn(created);
        when(organisationUnit.lastUpdated()).thenReturn(lastUpdated);
        when(organisationUnit.shortName()).thenReturn("organisation_unit_shortName");
        when(organisationUnit.shortName()).thenReturn("organisation_unit_displayShortName");
        when(organisationUnit.description()).thenReturn("organisation_unit_description");
        when(organisationUnit.displayDescription()).thenReturn("organisation_unit_displayDescription");
        when(organisationUnit.path()).thenReturn("organisation/unit/path");
        when(organisationUnit.openingDate()).thenReturn(created);
        when(organisationUnit.closedDate()).thenReturn(created);
        when(organisationUnit.level()).thenReturn(3);
        when(organisationUnit.parent()).thenReturn(null);

        List<OrganisationUnit> organisationUnits = Collections.singletonList(organisationUnit);
        when(user.uid()).thenReturn("user_uid");
        when(user.code()).thenReturn("user_code");
        when(user.name()).thenReturn("user_name");
        when(user.displayName()).thenReturn("user_display_name");
        when(user.created()).thenReturn(created);
        when(user.lastUpdated()).thenReturn(lastUpdated);
        when(user.birthday()).thenReturn("user_birthday");
        when(user.education()).thenReturn("user_education");
        when(user.gender()).thenReturn("male");
        when(user.jobTitle()).thenReturn("job_title");
        when(user.surname()).thenReturn("user_surname");
        when(user.firstName()).thenReturn("user_first_name");
        when(user.introduction()).thenReturn("user_introduction");
        when(user.employer()).thenReturn("user_employer");
        when(user.interests()).thenReturn("user_interests");
        when(user.languages()).thenReturn("user_languages");
        when(user.email()).thenReturn("user_email");
        when(user.phoneNumber()).thenReturn("user_phone_number");
        when(user.nationality()).thenReturn("user_nationality");
        when(user.userCredentials()).thenReturn(userCredentials);
        when(user.userCredentials().userRoles()).thenReturn(Collections.singletonList(userRole));
        when(user.organisationUnits()).thenReturn(organisationUnits);
        when(user.teiSearchOrganisationUnits()).thenReturn(organisationUnits);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(userService.getUser(any(Fields.class))).thenReturn(userCall);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_correct_fields_after_invoke_user_sync_call() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));
        when(userService.getUser(filterCaptor.capture())).thenReturn(userCall);

        // fake call to api
        userSyncCall.call();

        assertThat(filterCaptor.getValue().fields())
                .contains(User.uid, User.code, User.name, User.displayName,
                        User.created, User.lastUpdated, User.birthday, User.education,
                        User.gender, User.jobTitle, User.surname, User.firstName,
                        User.introduction, User.employer, User.interests, User.languages,
                        User.email, User.phoneNumber, User.nationality,
                        User.userCredentials.with(
                                UserCredentials.uid,
                                UserCredentials.code,
                                UserCredentials.name,
                                UserCredentials.displayName,
                                UserCredentials.created,
                                UserCredentials.lastUpdated,
                                UserCredentials.username,
                                UserCredentials.userRoles.with(
                                        UserRole.uid, UserRole.programs.with(
                                                Program.uid
                                        )
                                )
                        ),
                        User.organisationUnits.with(
                                OrganisationUnit.uid,
                                OrganisationUnit.path,
                                OrganisationUnit.programs.with(
                                        Program.uid
                                )
                        ),
                        User.teiSearchOrganisationUnits.with(
                                OrganisationUnit.uid
                        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void not_invoke_stores_on_call_io_exception() throws IOException {
        when(userCall.execute()).thenThrow(IOException.class);

        try {
            userSyncCall.call();
            fail("Exception was not thrown");
        } catch (Exception ex) {

            // verify that handlers was not touched
            verify(databaseAdapter, never()).beginNewTransaction();
            verify(transaction, never()).setSuccessful();
            verify(transaction, never()).end();

            verify(userStore, never()).insert(
                    anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
            verify(userStore, never()).update(
                    anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
            verify(userStore, never()).delete(anyString());

            verify(userCredentialsStore, never()).insert(
                    anyString(), anyString(), anyString(), anyString(),
                    any(Date.class), any(Date.class), anyString(), anyString()
            );
            verify(userCredentialsStore, never()).update(
                    anyString(), anyString(), anyString(), anyString(),
                    any(Date.class), any(Date.class), anyString(), anyString(), anyString()
            );

            verify(userCredentialsStore, never()).delete(anyString());

            verify(userRoleStore, never()).insert(
                    anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class)
            );
            verify(userRoleStore, never()).update(
                    anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class), anyString()
            );
            verify(userRoleStore, never()).delete(anyString());
        }
    }

    @Test
    public void not_invoke_stores_after_call_failure() throws Exception {
        // unauthorized
        when(userCall.execute()).thenReturn(Response.<User>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<User> response = userSyncCall.call();

        // checking that response failed due to unauthorized
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);

        // verify that database was not touched
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();

        verify(userStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userStore, never()).delete(anyString());

        verify(userCredentialsStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString()
        );
        verify(userCredentialsStore, never()).update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString()
        );

        verify(userCredentialsStore, never()).delete(anyString());

        verify(userRoleStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class)
        );
        verify(userRoleStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class), anyString()
        );
        verify(userRoleStore, never()).delete(anyString());
    }

    @Test
    public void mark_as_executed_on_success() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        assertThat(userSyncCall.isExecuted()).isEqualTo(true);

        try {
            userSyncCall.call();

            fail("Two calls to the userSyncCall should throw exception");
        } catch (Exception exception) {
            // ignore exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mark_as_executed_on_failure() throws Exception {
        when(userCall.execute()).thenThrow(IOException.class);

        try {
            userSyncCall.call();
        } catch (IOException ioException) {
            // swallow exception
        }

        assertThat(userSyncCall.isExecuted()).isEqualTo(true);

        try {
            userSyncCall.call();

            fail("Two calls to the userSyncCall should throw exception");
        } catch (Exception exception) {
            // ignore exception
        }
    }

    @Test
    public void invoke_handlers_on_success() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        verify(userStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );

        verify(userCredentialsStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString()
        );

        verify(userRoleStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class)
        );

        verify(userRoleProgramLinkStore, times(1)).insert(anyString(), anyString());

        verify(resourceStore, times(1)).insert(anyString(), any(Date.class));

    }
}
