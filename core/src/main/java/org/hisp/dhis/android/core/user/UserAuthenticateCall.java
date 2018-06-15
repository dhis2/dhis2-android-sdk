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

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.calls.factories.BasicCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfoStore;

import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Retrofit;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.data.api.ApiUtils.base64;

public final class UserAuthenticateCall extends SyncCall<User> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private final BasicCallFactory<SystemInfo> systemInfoCallFactory;

    // retrofit service
    private final UserService userService;

    private final GenericHandler<User, UserModel> userHandler;
    private final ResourceHandler resourceHandler;
    private final AuthenticatedUserStore authenticatedUserStore;
    private final ObjectWithoutUidStore<SystemInfoModel> systemInfoStore;
    private final IdentifiableObjectStore<UserModel> userStore;
    private final Callable<Unit> dbWipe;

    // username and password of candidate
    private final String username;
    private final String password;
    private final String apiURL;

    UserAuthenticateCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull BasicCallFactory<SystemInfo> systemInfoCallFactory,
            @NonNull UserService userService,
            @NonNull GenericHandler<User, UserModel> userHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull AuthenticatedUserStore authenticatedUserStore,
            @NonNull ObjectWithoutUidStore<SystemInfoModel> systemInfoStore,
            @NonNull IdentifiableObjectStore<UserModel> userStore,
            @NonNull Callable<Unit> dbWipe,
            @NonNull String username,
            @NonNull String password,
            @NonNull String apiURL) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;

        this.systemInfoCallFactory = systemInfoCallFactory;
        this.userService = userService;

        this.userHandler = userHandler;
        this.resourceHandler = resourceHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.systemInfoStore = systemInfoStore;
        this.userStore = userStore;
        this.dbWipe = dbWipe;

        this.username = username;
        this.password = password;

        this.apiURL = apiURL;
    }

    @Override
    public User call() throws D2CallException {
        setExecuted();
        throwExceptionIfUsernameNull();
        throwExceptionIfPasswordNull();
        throwExceptionIfAlreadyAuthenticated();

        Call<User> authenticateCall = userService.authenticate(basic(username, password), User.allFieldsWithoutOrgUnit);
        User authenticatedUser = new APICallExecutor().executeObjectCall(authenticateCall);

        if (wasLoggedAndUserIsNew(authenticatedUser)) {
            new D2CallExecutor().executeD2Call(dbWipe);
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            authenticatedUserStore.insert(authenticatedUser.uid(), base64(username, password));
            SystemInfo systemInfo = new D2CallExecutor().executeD2Call(systemInfoCallFactory
                    .create(databaseAdapter, retrofit));
            handleUser(authenticatedUser, GenericCallData.create(databaseAdapter, retrofit, systemInfo.serverDate()));
            transaction.setSuccessful();
            return authenticatedUser;
        } finally {
            transaction.end();
        }
    }

    private void throwExceptionIfUsernameNull() throws D2CallException {
        if (username == null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.LOGIN_USERNAME_NULL)
                    .errorDescription("Username is null")
                    .isHttpError(false)
                    .build();
        }
    }

    private void throwExceptionIfPasswordNull() throws D2CallException {
        if (password == null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.LOGIN_PASSWORD_NULL)
                    .errorDescription("Password is null")
                    .isHttpError(false)
                    .build();
        }
    }

    private void throwExceptionIfAlreadyAuthenticated() throws D2CallException {
        List<AuthenticatedUserModel> authenticatedUsers = authenticatedUserStore.query();
        if (!authenticatedUsers.isEmpty()) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                    .errorDescription("A user is already authenticated: " + authenticatedUsers.get(0).user())
                    .isHttpError(false)
                    .build();
        }
    }

    private boolean wasLoggedAndUserIsNew(User newUser) {
        SystemInfoModel lastSystemInfo = systemInfoStore.selectFirst(SystemInfoModel.factory);
        UserModel lastUser = userStore.selectFirst(UserModel.factory);
        return lastUser != null && lastSystemInfo != null && (
                !lastUser.uid().equals(newUser.uid()) ||
                        !(lastSystemInfo.contextPath() + "/api/").equals(apiURL));
    }

    private void handleUser(User user, GenericCallData genericCallData) {
        userHandler.handle(user, new UserModelBuilder());
        resourceHandler.handleResource(ResourceModel.Type.USER, genericCallData.serverDate());
        resourceHandler.handleResource(ResourceModel.Type.USER_CREDENTIALS, genericCallData.serverDate());
        resourceHandler.handleResource(ResourceModel.Type.AUTHENTICATED_USER, genericCallData.serverDate());
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
                ResourceHandler.create(databaseAdapter),
                new AuthenticatedUserStoreImpl(databaseAdapter),
                SystemInfoStore.create(databaseAdapter),
                UserStore.create(databaseAdapter),
                LogOutUserCallable.createToWipe(databaseAdapter),
                username,
                password,
                retrofit.baseUrl().toString()
        );
    }
}