/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.internal.APIUrlProvider;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.wipe.WipeModule;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import io.reactivex.Single;
import retrofit2.Call;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.utils.UserUtils.base64;
import static org.hisp.dhis.android.core.utils.UserUtils.md5;

@Reusable
final class UserAuthenticateCallFactory {

    private final DatabaseAdapter databaseAdapter;
    private final APICallExecutor apiCallExecutor;

    private final UserService userService;

    private final Handler<User> userHandler;
    private final ResourceHandler resourceHandler;
    private final ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final IdentifiableObjectStore<User> userStore;
    private final WipeModule wipeModule;

    private final APIUrlProvider apiUrlProvider;

    @Inject
    UserAuthenticateCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull UserService userService,
            @NonNull Handler<User> userHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore,
            @NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            @NonNull IdentifiableObjectStore<User> userStore,
            @NonNull WipeModule wipeModule,
            @NonNull APIUrlProvider apiUrlProvider) {
        this.databaseAdapter = databaseAdapter;
        this.apiCallExecutor = apiCallExecutor;

        this.userService = userService;

        this.userHandler = userHandler;
        this.resourceHandler = resourceHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.systemInfoRepository = systemInfoRepository;
        this.userStore = userStore;
        this.wipeModule = wipeModule;

        this.apiUrlProvider = apiUrlProvider;
    }

    public Single<User> logIn(final String username, final String password) {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(loginInternal(username, password));
            } catch (Throwable t) {
                emitter.onError(t);
            }
        });
    }

    private User loginInternal(String username, String password) throws D2Error {
        throwExceptionIfUsernameNull(username);
        throwExceptionIfPasswordNull(password);
        throwExceptionIfAlreadyAuthenticated();

        Call<User> authenticateCall =
                userService.authenticate(basic(username, password), UserFields.allFieldsWithoutOrgUnit);

        try {
            User authenticatedUser = apiCallExecutor.executeObjectCallWithErrorCatcher(authenticateCall,
                    new UserAuthenticateCallErrorCatcher());
            return loginOnline(authenticatedUser, username, password);
        } catch (D2Error d2Error) {
            if (
                    d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR ||
                            d2Error.errorCode() == D2ErrorCode.SOCKET_TIMEOUT ||
                            d2Error.errorCode() == D2ErrorCode.UNKNOWN_HOST) {
                return loginOffline(username, password);
            } else if (d2Error.errorCode() == D2ErrorCode.USER_ACCOUNT_DISABLED) {
                wipeModule.wipeEverything();
                throw d2Error;
            } else {
                throw d2Error;
            }
        }
    }

    private User loginOnline(User authenticatedUser, String username, String password) throws D2Error {
        if (wasLoggedAndUserIsNew(authenticatedUser) || wasLoggedAndServerIsNew()) {
            wipeModule.wipeEverything();
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            AuthenticatedUser authenticatedUserToStore = buildAuthenticatedUser(authenticatedUser.uid(),
                    username, password);
            authenticatedUserStore.updateOrInsertWhere(authenticatedUserToStore);

            systemInfoRepository.download().blockingAwait();

            handleUser(authenticatedUser);
            transaction.setSuccessful();
            return authenticatedUser;
        } finally {
            transaction.end();
        }
    }

    private User loginOffline(String username, String password) throws D2Error {
        if (wasLoggedAndServerIsNew()) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.DIFFERENT_SERVER_OFFLINE)
                    .errorDescription("Cannot switch servers offline.")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }

        AuthenticatedUser existingUser = authenticatedUserStore.selectFirst();

        if (existingUser == null) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE)
                    .errorDescription("No user has been previously authenticated. Cannot login offline.")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }

        if (!md5(username, password).equals(existingUser.hash())) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.DIFFERENT_AUTHENTICATED_USER_OFFLINE)
                    .errorDescription("Credentials do not match authenticated user. Cannot switch users offline.")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            AuthenticatedUser authenticatedUser = buildAuthenticatedUser(existingUser.user(),
                    username, password);
            authenticatedUserStore.updateOrInsertWhere(authenticatedUser);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }

        return userStore.selectByUid(existingUser.user());
    }

    private void throwExceptionIfUsernameNull(String username) throws D2Error {
        if (username == null) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.LOGIN_USERNAME_NULL)
                    .errorDescription("Username is null")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }
    }

    private void throwExceptionIfPasswordNull(String password) throws D2Error {
        if (password == null) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.LOGIN_PASSWORD_NULL)
                    .errorDescription("Password is null")
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }
    }

    private void throwExceptionIfAlreadyAuthenticated() throws D2Error {
        AuthenticatedUser authenticatedUser = authenticatedUserStore.selectFirst();
        if (authenticatedUser != null && authenticatedUser.credentials() != null) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                    .errorDescription("A user is already authenticated: " + authenticatedUser.user())
                    .errorComponent(D2ErrorComponent.SDK)
                    .build();
        }
    }

    private boolean wasLoggedAndUserIsNew(User newUser) {
        User lastUser = userStore.selectFirst();
        return lastUser != null && !lastUser.uid().equals(newUser.uid());
    }

    private boolean wasLoggedAndServerIsNew() {
        SystemInfo lastSystemInfo = systemInfoRepository.get();
        return lastSystemInfo != null && !(lastSystemInfo.contextPath() + "/api/").equals(apiUrlProvider.getAPIUrl());
    }

    private void handleUser(User user) {
        userHandler.handle(user);
        resourceHandler.handleResource(Resource.Type.USER);
        resourceHandler.handleResource(Resource.Type.USER_CREDENTIALS);
        resourceHandler.handleResource(Resource.Type.AUTHENTICATED_USER);
    }

    private AuthenticatedUser buildAuthenticatedUser(String uid, String username, String password) {
        return AuthenticatedUser.builder()
                .user(uid)
                .credentials(base64(username, password))
                .hash(md5(username, password))
                .build();
    }
}