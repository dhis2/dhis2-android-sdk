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


import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public final class UserCall implements Call<Response<User>> {
    // retrofit service
    private final UserService userService;
    // databaseAdapter and handlers
    private final DatabaseAdapter databaseAdapter;
    private final UserCredentialsStore userCredentialsStore;
    private final UserRoleStore userRoleStore;
    private final UserStore userStore;
    private final UserRoleProgramLinkStore userRoleProgramLinkStore;
    private final ResourceStore resourceStore;
    // server date time
    private final Date serverDate;
    private boolean isExecuted;

    public UserCall(UserService userService,
                    DatabaseAdapter databaseAdapter,
                    UserStore userStore,
                    UserCredentialsStore userCredentialsStore,
                    UserRoleStore userRoleStore,
                    ResourceStore resourceStore,
                    Date serverDate,
                    UserRoleProgramLinkStore userRoleProgramLinkStore) {
        this.userService = userService;
        this.databaseAdapter = databaseAdapter;
        this.userCredentialsStore = userCredentialsStore;
        this.userRoleStore = userRoleStore;
        this.userStore = userStore;
        this.resourceStore = resourceStore;
        this.serverDate = new Date(serverDate.getTime());
        this.userRoleProgramLinkStore = userRoleProgramLinkStore;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<User> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }
        Response<User> response = getUser();
        if (response.isSuccessful()) {
            UserHandler userHandler = new UserHandler(userStore);
            UserCredentialsHandler userCredentialsHandler = new UserCredentialsHandler(userCredentialsStore);
            UserRoleHandler userRoleHandler = new UserRoleHandler(userRoleStore, userRoleProgramLinkStore);
            ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

            Transaction transaction = databaseAdapter.beginNewTransaction();
            try {
                User user = response.body();
                // TODO: check that this is user is authenticated and is persisted in db
                userHandler.handleUser(user);
                UserCredentials userCredentials = user.userCredentials();
                userCredentialsHandler.handleUserCredentials(userCredentials, user);

                List<UserRole> userRoles = userCredentials.userRoles();
                userRoleHandler.handleUserRoles(userRoles);

                resourceHandler.handleResource(ResourceModel.Type.USER, serverDate);

                transaction.setSuccessful();
            } catch (SQLiteConstraintException constraintException) {
                //constraintException.printStackTrace();
                Log.d("CAll", "call: constraintException");
            } finally {
                transaction.end();
            }
        }
        return response;
    }

    private Response<User> getUser() throws IOException {
        Fields<User> fields = Fields.<User>builder().fields(
                User.uid, User.code, User.name, User.displayName,
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
                        UserCredentials.userRoles.with(UserRole.uid, UserRole.programs.with(Program.uid))
                ),
                User.organisationUnits.with(
                        OrganisationUnit.uid,
                        OrganisationUnit.path,
                        OrganisationUnit.programs.with(Program.uid)
                ),
                User.teiSearchOrganisationUnits.with(OrganisationUnit.uid)
        ).build();
        return userService.getUser(fields).execute();
    }

}
