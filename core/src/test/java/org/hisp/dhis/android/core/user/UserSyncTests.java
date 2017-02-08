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

import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceStore;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserSyncTests {

    @Mock
    private UserSyncService userSyncService;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private UserStore userStore;

    @Mock
    private UserCredentialsStore userCredentialsStore;

    @Mock
    private UserOrganisationUnitLinkStore userOrganisationUnitLinkStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private UserRoleStore userRoleStore;

    @Mock
    private UserRoleProgramLinkStore userRoleProgramLinkStore;

    @Mock
    private OrganisationUnitStore organisationUnitStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Call<User> userCall;

    @Captor
    private ArgumentCaptor<Filter<User>> filterCaptor;

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

    private org.hisp.dhis.android.core.common.Call<Response<User>> userSyncCall;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userSyncCall = new UserSyncCall(
                userSyncService, sqLiteDatabase, organisationUnitStore,
                userOrganisationUnitLinkStore, userCredentialsStore,
                userRoleStore, userStore, userRoleProgramLinkStore, resourceStore
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

        when(userSyncService.getUser(any(Filter.class))).thenReturn(userCall);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldInvokeServerWithCorrectParameters() throws Exception {
        when(userCall.execute()).thenReturn(Response.success(user));
        when(userSyncService.getUser(filterCaptor.capture())).thenReturn(userCall);

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
    public void call_shouldNotInvokeStoresOnException() throws IOException {
        when(userCall.execute()).thenThrow(IOException.class);

        try {
            userSyncCall.call();
            fail("Exception was not thrown");
        } catch (Exception ex) {

            // verify that database was not touched
            verify(sqLiteDatabase, never()).beginTransaction();
            verify(sqLiteDatabase, never()).setTransactionSuccessful();
            verify(sqLiteDatabase, never()).endTransaction();

            // verify that nothing was inserted into stores

            verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                    any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString());

            verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class), anyString(), anyString());

            verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class));

            verify(userRoleProgramLinkStore, never()).insert(anyString(), anyString());

            verify(organisationUnitStore, never()).insert(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                    anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                    anyString(), any(Integer.class));

            verify(userOrganisationUnitLinkStore, never()).insert(
                    anyString(), anyString(), anyString());

            // verify that nothing was updated into stores

            verify(userStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                    any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString());

            verify(userCredentialsStore, never()).update(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString());

            verify(userRoleStore, never()).update(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class), anyString());

            verify(userRoleProgramLinkStore, never()).update(anyString(), anyString(), anyString(), anyString());

            verify(organisationUnitStore, never()).update(anyString(), anyString(), anyString(),
                    anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                    anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                    anyString(), any(Integer.class), anyString());

            verify(userOrganisationUnitLinkStore, never()).update(
                    anyString(), anyString(), anyString(), anyString(), anyString());
        }
    }

    @Test
    public void call_shouldNotInvokeStoresIfRequestFails() throws Exception {
        // unauthorized
        when(userCall.execute()).thenReturn(Response.<User>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<User> response = userSyncCall.call();

        // checking that response failed due to unauthorized
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);

        // verify that database was not touched
        verify(sqLiteDatabase, never()).beginTransaction();
        verify(sqLiteDatabase, never()).setTransactionSuccessful();
        verify(sqLiteDatabase, never()).endTransaction();

        // verify that nothing was inserted into stores

        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

        verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class));

        verify(userRoleProgramLinkStore, never()).insert(anyString(), anyString());

        verify(organisationUnitStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class));

        verify(userOrganisationUnitLinkStore, never()).insert(
                anyString(), anyString(), anyString());

        // verify that nothing was updated into stores

        verify(userStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());

        verify(userCredentialsStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString());

        verify(userRoleStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString());

        verify(userRoleProgramLinkStore, never()).update(anyString(), anyString(), anyString(), anyString());

        verify(organisationUnitStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString());

        verify(userOrganisationUnitLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), anyString());

    }

    @Test
    public void call_shouldUpdateUserIfRequestSucceeds() throws Exception {
        // insert user first to check the updateWithSection mechanism
        when(userStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        User updatedUser = User.create(
                user.uid(), user.code(), user.name(), user.displayName(), created, lastUpdated,
                user.birthday(), user.education(), "transgender",
                user.jobTitle(), user.surname(), user.firstName(), user.introduction(),
                user.employer(), user.interests(), user.languages(), "superuser@email.com", "81549300",
                user.nationality(), user.userCredentials(),
                user.organisationUnits(), user.teiSearchOrganisationUnits(), user.dataViewOrganisationUnits(), false);


        when(userCall.execute()).thenReturn(Response.success(updatedUser));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        verify(userStore, times(1)).update(user.uid(), user.code(), user.name(), user.displayName(), user.created(),
                user.lastUpdated(), user.birthday(), user.education(), "transgender", user.jobTitle(),
                user.surname(), user.firstName(), user.introduction(), user.employer(),
                user.interests(), user.languages(), "superuser@email.com", "81549300",
                user.nationality(), user.uid());

        // user should have been updated and thus not inserted
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        // user should not have been deleted
        verify(userStore, never()).delete(anyString());
    }

    @Test
    public void call_shouldUpdateUserCredentialsIfRequestSucceeds() throws Exception {
        // insert userCredentials first to check the updateWithSection mechanism
        when(userCredentialsStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString())).thenReturn(1);

        UserCredentials updatedUserCredentials = UserCredentials.create(userCredentials.uid(),
                userCredentials.code(), "new_user_credentials_name", "new_user_credentials_display_name",
                userCredentials.created(), userCredentials.lastUpdated(),
                userCredentials.username(), userCredentials.userRoles(), Boolean.FALSE);
        when(user.userCredentials()).thenReturn(updatedUserCredentials);

        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that updateWithSection is called once
        verify(userCredentialsStore, times(1)).update(userCredentials.uid(), userCredentials.code(),
                "new_user_credentials_name", "new_user_credentials_display_name",
                userCredentials.created(), userCredentials.lastUpdated(), userCredentials.username(),
                user.uid(), userCredentials.uid());

        // verify that insert and delete is never called
        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

        verify(userCredentialsStore, never()).delete(anyString());
    }

    @Test
    public void call_shouldInsertUserRoleIfRequestSucceeds() throws Exception {
        // doesn't exist in database thus we should insert not updateWithSection
        when(userRoleStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(0);

        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that userRoleStore was called once with insert
        verify(userRoleStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class));

        // verify that updateWithSection was called once (Because we try to updateWithSection before we can insert)
        verify(userRoleStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString());

        // verify that delete was not called
        verify(userRoleStore, never()).delete(anyString());
    }

    @Test
    public void call_shouldUpdateUserRoleIfRequestSucceeds() throws Exception {
        UserRole updatedUserRole = UserRole.create(userRole.uid(), userRole.code(), "new_user_role_name",
                "new_user_role_display_name", userRole.created(), userRole.lastUpdated(),
                userRole.programs(), Boolean.FALSE);
        when(user.userCredentials().userRoles()).thenReturn(Collections.singletonList(updatedUserRole));

        when(userRoleStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString())).thenReturn(1);

        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        verify(userRoleStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString());

        verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class));

        verify(userRoleStore, never()).delete(anyString());
    }

    @Test
    public void call_shouldInsertUserRoleProgramLinkIfRequestSucceeds() throws Exception {
        when(userRoleProgramLinkStore.update(anyString(), anyString(), anyString(), anyString())).thenReturn(0);
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that insert is called once
        verify(userRoleProgramLinkStore, times(1)).insert(anyString(), anyString());

        // verify that updateWithSection is called once since we try to updateWithSection before we insert
        verify(userRoleProgramLinkStore, times(1)).update(anyString(), anyString(), anyString(), anyString());

        // verify that delete is never called
        verify(userRoleProgramLinkStore, never()).delete(anyString(), anyString());
    }

    @Test
    public void call_shouldUpdateUserRoleProgramLinkIfRequestSucceeds() throws Exception {
        when(userRole.uid()).thenReturn("new_user_role_uid");
        when(program.uid()).thenReturn("new_program_uid");

        when(userRoleProgramLinkStore.update(anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that updateWithSection is called once
        verify(userRoleProgramLinkStore, times(1)).update(anyString(), anyString(), anyString(), anyString());

        // verify that insert and delete is never called
        verify(userRoleProgramLinkStore, never()).insert(anyString(), anyString());
        verify(userRoleProgramLinkStore, never()).delete(anyString(), anyString());
    }

    @Test
    public void call_shouldInsertOrganisationUnitIfRequestSucceeds() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString())).thenReturn(0);

        when(userCall.execute()).thenReturn(Response.success(user));
        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that insert is called once
        verify(organisationUnitStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class));

        // verify that updateWithSection is called once since we updateWithSection before we insert
        verify(organisationUnitStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString());

        // verify that delete is never called
        verify(organisationUnitStore, never()).delete(anyString());
    }

    @Test
    public void call_shouldUpdateOrganisationUnitIfRequestSucceeds() throws Exception {
        when(organisationUnitStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString())).thenReturn(1);

        when(userCall.execute()).thenReturn(Response.success(user));
        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that updateWithSection is called once
        verify(organisationUnitStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString());

        // verify that insert is never called
        verify(organisationUnitStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class));

        verify(organisationUnitStore, never()).delete(anyString());

    }

    @Test
    public void call_shouldInsertUserOrganisationUnitLinkIfRequestSucceeds() throws Exception {
        when(userOrganisationUnitLinkStore.update(
                anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(0);

        when(userCall.execute()).thenReturn(Response.success(user));
        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that insert is called once
        verify(userOrganisationUnitLinkStore, times(1)).insert(anyString(), anyString(), anyString());

        // verify that updateWithSection is called once since we try to updateWithSection before we insert
        verify(userOrganisationUnitLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    public void call_shouldUpdateUserOrganisationUnitLinkIfRequestSucceeds() throws Exception {
        when(userOrganisationUnitLinkStore.update(
                anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(1);

        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();

        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that updateWithSection is called once
        verify(userOrganisationUnitLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), anyString()
        );

        // verify that insert is never called
        verify(userOrganisationUnitLinkStore, never()).insert(anyString(), anyString(), anyString());
    }

    @Test
    public void call_shouldDeleteOrganisationUnitIfMarkedAsDeleted() throws Exception {
        when(organisationUnit.deleted()).thenReturn(Boolean.TRUE);
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();
        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that delete is called once
        verify(organisationUnitStore, times(1)).delete(anyString());

        // verify that updateWithSection and insert is never called
        verify(organisationUnitStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class), anyString());

        verify(organisationUnitStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), any(Integer.class));

    }

    @Test
    public void call_shouldDeleteUserRoleIfMarkedAsDeleted() throws Exception {
        when(userRole.deleted()).thenReturn(Boolean.TRUE);
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();
        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that delete is called once
        verify(userRoleStore, times(1)).delete(anyString());

        // verify that updateWithSection and insert is never called
        verify(userRoleStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString());

        verify(userRoleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class));
    }

    @Test
    public void call_shouldDeleteUserCredentialsIfMarkedAsDeleted() throws Exception {
        when(userCredentials.deleted()).thenReturn(Boolean.TRUE);
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();
        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        verify(userCredentialsStore, times(1)).delete(anyString());

        // verify that insert and updateWithSection is never called

        verify(userCredentialsStore, never()).update(anyString(), anyString(),
                anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(),
                anyString(), anyString());

        verify(userCredentialsStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString());

    }

    @Test
    public void call_shouldDeleteUserIfMarkedAsDeleted() throws Exception {
        when(user.deleted()).thenReturn(Boolean.TRUE);
        when(userCall.execute()).thenReturn(Response.success(user));

        userSyncCall.call();
        InOrder transactionOrder = inOrder(sqLiteDatabase);

        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();

        // verify that delete is called once
        verify(userStore, times(1)).delete(anyString());

        // verify that insert and updateWithSection is never called
        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        verify(userStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    //TODO Figure out how to mock the response header so we can mock HeaderUtils.DATE
//    @Test
//    public void call_shouldUpdateIntoResourceStoreWhenUpdatingUser() throws Exception {
//        when(userStore.updateWithSection(anyString(), anyString(), anyString(), anyString(),
//                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(1);
//        when(userSyncCall.call()).
//
//        Date parsedServerDate = BaseIdentifiableObject.DATE_FORMAT.parse(HeaderUtils.DATE);
//
//        when(resourceStore.updateWithSection(
//                User.class.getSimpleName(), parsedServerDate, User.class.getSimpleName())
//        ).thenReturn(1);
//
//        when(userCall.execute()).thenReturn(Response.success(user));
//
//        userSyncCall.call();
//        InOrder transactionOrder = inOrder(sqLiteDatabase);
//
//        transactionOrder.verify(sqLiteDatabase, times(1)).beginTransaction();
//        transactionOrder.verify(sqLiteDatabase, times(1)).setTransactionSuccessful();
//        transactionOrder.verify(sqLiteDatabase, times(1)).endTransaction();
//
//        // verify that userStore.updateWithSection() is called once
//        verify(userStore, times(1)).updateWithSection(anyString(), anyString(), anyString(), anyString(),
//                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString(), anyString());
//
//        // verify that resource store is called once with User mime type
//        verify(resourceStore, times(1)).updateWithSection(
//                User.class.getSimpleName(), parsedServerDate, User.class.getSimpleName()
//        );
//
//        // verify that userStore.insert() and userStore.delete() is never called
//        verify(userStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
//                any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
//                anyString(), anyString(), anyString(), anyString());
//
//        verify(userStore, never()).delete(anyString());
//
//
//    }

    @Test
    public void call_shouldMarkCallAsExecutedOnSuccess() throws Exception {
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
    public void call_shouldMarkCallAsExecutedOnFailure() throws Exception {
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
}
