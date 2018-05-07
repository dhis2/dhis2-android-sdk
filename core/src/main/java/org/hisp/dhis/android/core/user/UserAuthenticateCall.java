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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BlockCallFactory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModelBuilder;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

// ToDo: ask about API changes
// ToDo: performance tests? Try to feed in a user instance with thousands organisation units
public final class UserAuthenticateCall extends SyncCall<Response<User>> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private final BlockCallFactory<SystemInfo> systemInfoCallFactory;

    // retrofit service
    private final UserService userService;

    private final UserHandler userHandler;
    private final UserCredentialsHandler userCredentialsHandler;
    private final ResourceHandler resourceHandler;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final OrganisationUnitHandlerFactory organisationUnitHandlerFactory;

    // username and password of candidate
    private final String username;
    private final String password;

    UserAuthenticateCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull BlockCallFactory<SystemInfo> systemInfoCallFactory,
            @NonNull UserService userService,
            @NonNull UserHandler userHandler,
            @NonNull UserCredentialsHandler userCredentialsHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull OrganisationUnitHandlerFactory organisationUnitHandlerFactory,
            @NonNull String username,
            @NonNull String password) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;

        this.systemInfoCallFactory = systemInfoCallFactory;
        this.userService = userService;

        this.userHandler = userHandler;
        this.userCredentialsHandler = userCredentialsHandler;
        this.resourceHandler = resourceHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitHandlerFactory = organisationUnitHandlerFactory;

        // credentials
        this.username = username;
        this.password = password;
    }

    @Override
    public Response call() throws Exception {
        this.setExecuted();

        List<AuthenticatedUserModel> authenticatedUsers = authenticatedUserStore.query();
        if (!authenticatedUsers.isEmpty()) {
            throw new IllegalStateException("Another user has already been authenticated: " +
                    authenticatedUsers.get(0));
        }

        Response<User> authenticateResponse = authenticate(basic(username, password));

        if (!authenticateResponse.isSuccessful()) {
            return authenticateResponse;
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();

        // enclosing transaction in try-finally block in
        // order to make sure that databaseAdapter transaction won't be leaked
        try {
            User user = authenticateResponse.body();
            authenticatedUserStore.insert(user.uid(), base64(username, password));

            Response<SystemInfo> systemCallResponse = systemInfoCallFactory.create(databaseAdapter, retrofit).call();
            if (!systemCallResponse.isSuccessful()) {
                return systemCallResponse;
            }

            SystemInfo systemInfo = systemCallResponse.body();
            handleUser(user, GenericCallData.create(databaseAdapter, retrofit, systemInfo.serverDate()));

            transaction.setSuccessful();

            return authenticateResponse;
        } finally {
            transaction.end();
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
                        OrganisationUnit.parent.with(OrganisationUnit.uid),
                        OrganisationUnit.ancestors.with(OrganisationUnit.uid, OrganisationUnit.displayName))
                ).build()).execute();
    }

    @NonNull
    private void handleUser(User user, GenericCallData genericCallData) {

        userHandler.handleUser(user);

        resourceHandler.handleResource(ResourceModel.Type.USER, genericCallData.serverDate());

        userCredentialsHandler.handleUserCredentials(user.userCredentials(), user);

        resourceHandler.handleResource(ResourceModel.Type.USER_CREDENTIALS, genericCallData.serverDate());

        resourceHandler.handleResource(ResourceModel.Type.AUTHENTICATED_USER, genericCallData.serverDate());

        if (user.organisationUnits() != null) {
            organisationUnitHandlerFactory.organisationUnitHandler(databaseAdapter, user)
                    .handleMany(user.organisationUnits(), new OrganisationUnitModelBuilder());

            // TODO: This is introduced to download all descendants
            // resourceHandler.handleResource(ResourceModel.Type.ORGANISATION_UNIT, serverDateTime);
        }
    }

    public static UserAuthenticateCall create(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull String username,
            @NonNull String password) {
        return new UserAuthenticateCall(
                databaseAdapter,
                retrofit,
                SystemInfoCall.FACTORY,
                retrofit.create(UserService.class),
                UserHandler.create(databaseAdapter),
                UserCredentialsHandler.create(databaseAdapter),
                ResourceHandler.create(databaseAdapter),
                new AuthenticatedUserStoreImpl(databaseAdapter),
                FACTORY,
                username,
                password
        );
    }

    public interface OrganisationUnitHandlerFactory {
        GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler(
                DatabaseAdapter databaseAdapter,
                User user);
    }

    private static final UserAuthenticateCall.OrganisationUnitHandlerFactory FACTORY =
            new UserAuthenticateCall.OrganisationUnitHandlerFactory() {
                @Override
                public GenericHandler<OrganisationUnit, OrganisationUnitModel> organisationUnitHandler(
                        DatabaseAdapter databaseAdapter, User user) {
                    return OrganisationUnitHandler.create(databaseAdapter, new HashSet<String>(),
                            OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, user);
                }
            };
}