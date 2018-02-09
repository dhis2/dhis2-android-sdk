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

import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

import static okhttp3.Credentials.basic;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

// ToDo: ask about API changes
// ToDo: performance tests? Try to feed in a user instance with thousands organisation units
public final class UserAuthenticateCall implements Call<Response<User>> {
    // retrofit service
    private final UserService userService;

    // stores and databaseAdapter related dependencies
    private final DatabaseAdapter databaseAdapter;
    private final UserHandler userHandler;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final OrganisationUnitHandler organisationUnitHandler;

    // username and password of candidate
    private final String username;
    private final String password;

    private boolean isExecuted;

    public UserAuthenticateCall(
            @NonNull UserService userService,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull UserHandler userHandler,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull OrganisationUnitHandler organisationUnitHandler,
            @NonNull String username,
            @NonNull String password) {
        this.userService = userService;

        this.databaseAdapter = databaseAdapter;
        this.userHandler = userHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitHandler = organisationUnitHandler;

        // credentials
        this.username = username;
        this.password = password;
    }

    @Override
    public Response<User> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        List<AuthenticatedUserModel> authenticatedUsers = authenticatedUserStore.query();
        if (!authenticatedUsers.isEmpty()) {
            throw new IllegalStateException("Another user has already been authenticated: " +
                    authenticatedUsers.get(0));
        }

        Response<User> response = authenticate(basic(username, password));
        if (response.isSuccessful()) {
            saveUser(response);
        }

        return response;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    private Response<User> authenticate(String credentials) throws IOException {
        return userService.authenticate(credentials, Fields.<User>builder().fields(
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
                                OrganisationUnit.uid))
        ).build()).execute();
    }

    private void saveUser(Response<User> response) throws Exception {
        Transaction transaction = databaseAdapter.beginNewTransaction();

        // enclosing transaction in try-finally block in
        // order to make sure that databaseAdapter transaction won't be leaked
        try {
            User user = response.body();

            Date serverDateTime = response.headers().getDate(HeaderUtils.DATE);

            handleUser(user, serverDateTime);

            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    @NonNull
    private void handleUser(User user, Date serverDate) {
        userHandler.handleUser(user, serverDate);

        authenticatedUserStore.insert(user.uid(), base64(username, password));

        if (user.organisationUnits() != null) {
            organisationUnitHandler.handleOrganisationUnits(
                    user.organisationUnits(),
                    OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE,
                    user.uid(), serverDate);
        }
    }
}