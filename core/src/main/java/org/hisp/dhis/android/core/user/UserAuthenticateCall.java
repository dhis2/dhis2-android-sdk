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

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.repositories.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.calls.WipeDBCallable;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Retrofit;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.utils.UserUtils.base64;
import static org.hisp.dhis.android.core.utils.UserUtils.md5;

public final class UserAuthenticateCall extends SyncCall<User> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;

    private final D2InternalModules internalModules;

    // retrofit service
    private final UserService userService;

    private final SyncHandler<User> userHandler;
    private final ResourceHandler resourceHandler;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final ReadOnlyObjectRepository<SystemInfo> systemInfoRepository;
    private final IdentifiableObjectStore<User> userStore;
    private final Callable<Unit> dbWipe;

    // username and password of candidate
    private final String username;
    private final String password;
    private final String apiURL;

    UserAuthenticateCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull UserService userService,
            @NonNull SyncHandler<User> userHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull ReadOnlyObjectRepository<SystemInfo> systemInfoRepository,
            @NonNull IdentifiableObjectStore<User> userStore,
            @NonNull Callable<Unit> dbWipe,
            @NonNull String username,
            @NonNull String password,
            @NonNull String apiURL) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;

        this.internalModules = internalModules;
        this.userService = userService;

        this.userHandler = userHandler;
        this.resourceHandler = resourceHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.systemInfoRepository = systemInfoRepository;
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

        Call<User> authenticateCall =
                userService.authenticate(basic(username, password), UserFields.allFieldsWithoutOrgUnit);

        try {
            User authenticatedUser = new APICallExecutor().executeObjectCall(authenticateCall);
            return loginOnline(authenticatedUser);
        } catch (D2CallException d2Exception) {
            if (d2Exception.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR ||
                    d2Exception.errorCode() == D2ErrorCode.SOCKET_TIMEOUT) {
                return loginOffline();
            } else {
                throw d2Exception;
            }
        }
    }

    private User loginOnline(User authenticatedUser) throws D2CallException {
        if (wasLoggedAndUserIsNew(authenticatedUser) || wasLoggedAndServerIsNew()) {
            new D2CallExecutor().executeD2Call(dbWipe);
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            AuthenticatedUserModel authenticatedUserModel = buildAuthenticatedUserModel(authenticatedUser.uid());
            authenticatedUserStore.updateOrInsertWhere(authenticatedUserModel);
            SystemInfo systemInfo = new D2CallExecutor().executeD2Call(internalModules.systemInfo.callFactory.create());
            handleUser(authenticatedUser, GenericCallData.create(databaseAdapter, retrofit, systemInfo.serverDate(),
                    internalModules.systemInfo.publicModule.versionManager));
            transaction.setSuccessful();
            return authenticatedUser;
        } finally {
            transaction.end();
        }
    }

    private User loginOffline() throws D2CallException {
        if (wasLoggedAndServerIsNew()) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.DIFFERENT_SERVER_OFFLINE)
                    .errorDescription("Cannot switch servers offline.")
                    .isHttpError(false)
                    .build();
        }

        AuthenticatedUserModel existingUser = authenticatedUserStore.selectFirst(AuthenticatedUserModel.factory);

        if (existingUser == null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE)
                    .errorDescription("No user has been previously authenticated. Cannot login offline.")
                    .isHttpError(false)
                    .build();
        }

        if (!md5(username, password).equals(existingUser.hash())) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.DIFFERENT_AUTHENTICATED_USER_OFFLINE)
                    .errorDescription("Credentials do not match authenticated user. Cannot switch users offline.")
                    .isHttpError(false)
                    .build();
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            AuthenticatedUserModel authenticatedUserModel = buildAuthenticatedUserModel(existingUser.user());
            authenticatedUserStore.updateOrInsertWhere(authenticatedUserModel);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }

        return userStore.selectByUid(existingUser.user(), User.factory);
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
        AuthenticatedUserModel authenticatedUser = authenticatedUserStore.selectFirst(AuthenticatedUserModel.factory);
        if (authenticatedUser != null && authenticatedUser.credentials() != null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                    .errorDescription("A user is already authenticated: " + authenticatedUser.user())
                    .isHttpError(false)
                    .build();
        }
    }

    private boolean wasLoggedAndUserIsNew(User newUser) {
        User lastUser = userStore.selectFirst(User.factory);
        return lastUser != null && !lastUser.uid().equals(newUser.uid());
    }

    private boolean wasLoggedAndServerIsNew() {
        SystemInfo lastSystemInfo = systemInfoRepository.get();
        return lastSystemInfo != null && !(lastSystemInfo.contextPath() + "/api/").equals(apiURL);
    }

    private void handleUser(User user, GenericCallData genericCallData) {
        userHandler.handle(user);
        resourceHandler.handleResource(ResourceModel.Type.USER, genericCallData.serverDate());
        resourceHandler.handleResource(ResourceModel.Type.USER_CREDENTIALS, genericCallData.serverDate());
        resourceHandler.handleResource(ResourceModel.Type.AUTHENTICATED_USER, genericCallData.serverDate());
    }

    private AuthenticatedUserModel buildAuthenticatedUserModel(String uid) {
        return AuthenticatedUserModel.builder()
                .user(uid)
                .credentials(base64(username, password))
                .hash(md5(username, password))
                .build();
    }

    public static UserAuthenticateCall create(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull String username,
            @NonNull String password) {
        return new UserAuthenticateCall(
                databaseAdapter,
                retrofit,
                internalModules,
                retrofit.create(UserService.class),
                UserHandler.create(databaseAdapter),
                ResourceHandler.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                internalModules.systemInfo.publicModule.systemInfo,
                UserStore.create(databaseAdapter),
                WipeDBCallable.create(databaseAdapter, internalModules),
                username,
                password,
                retrofit.baseUrl().toString()
        );
    }
}