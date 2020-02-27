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

package org.hisp.dhis.android.core.user.internal;

import android.content.Context;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithDownloadObjectRepository;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectSecureStore;
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationHelper;
import org.hisp.dhis.android.core.configuration.internal.DatabaseUserConfiguration;
import org.hisp.dhis.android.core.configuration.internal.DatabasesConfiguration;
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.wipe.internal.WipeModule;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import retrofit2.Call;

import static okhttp3.Credentials.basic;
import static org.hisp.dhis.android.core.arch.helpers.UserHelper.md5;

@Reusable
@SuppressWarnings("PMD.ExcessiveImports")
public final class UserAuthenticateCallFactory {

    private final DatabaseAdapter databaseAdapter;
    private final APICallExecutor apiCallExecutor;

    private final UserService userService;

    private final ObjectSecureStore<Credentials> credentialsSecureStore;

    private final Handler<User> userHandler;
    private final ResourceHandler resourceHandler;
    private final ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore;
    private final ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository;
    private final IdentifiableObjectStore<User> userStore;
    private final WipeModule wipeModule;
    private final ObjectSecureStore<DatabasesConfiguration> configurationSecureStore;
    private final DatabaseConfigurationHelper configurationHelper;

    private final Context context;

    @Inject
    UserAuthenticateCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull UserService userService,
            @NonNull ObjectSecureStore<Credentials> credentialsSecureStore,
            @NonNull Handler<User> userHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull ObjectWithoutUidStore<AuthenticatedUser> authenticatedUserStore,
            @NonNull ReadOnlyWithDownloadObjectRepository<SystemInfo> systemInfoRepository,
            @NonNull IdentifiableObjectStore<User> userStore,
            @NonNull WipeModule wipeModule,
            @NonNull ObjectSecureStore<DatabasesConfiguration> configurationSecureStore,
            @NonNull DatabaseConfigurationHelper configurationHelper,
            @NonNull Context context) {
        this.databaseAdapter = databaseAdapter;
        this.apiCallExecutor = apiCallExecutor;

        this.userService = userService;

        this.credentialsSecureStore = credentialsSecureStore;

        this.userHandler = userHandler;
        this.resourceHandler = resourceHandler;
        this.authenticatedUserStore = authenticatedUserStore;
        this.systemInfoRepository = systemInfoRepository;
        this.userStore = userStore;
        this.wipeModule = wipeModule;
        this.configurationSecureStore = configurationSecureStore;
        this.configurationHelper = configurationHelper;
        this.context = context;
    }

    public Single<User> logIn(final String username, final String password, final String serverUrl) {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(loginInternal(username, password, serverUrl));
            } catch (Throwable t) {
                emitter.onError(t);
            }
        });
    }

    private User loginInternal(String username, String password, String serverUrl) throws D2Error {
        throwExceptionIfUsernameNull(username);
        throwExceptionIfPasswordNull(password);

        Call<User> authenticateCall =
                userService.authenticate(basic(username, password), UserFields.allFieldsWithoutOrgUnit);

        try {
            HttpUrl httpServerUrl = ServerUrlParser.parse(serverUrl);
            ServerURLWrapper.setServerUrl(httpServerUrl.toString());
            DatabasesConfiguration updatedConfiguration = configurationHelper.addConfiguration(
                    configurationSecureStore.get(), httpServerUrl.toString(), username,
                    DatabaseAdapterFactory.getExperimentalEncryption());
            configurationSecureStore.set(updatedConfiguration);

            User authenticatedUser = apiCallExecutor.executeObjectCallWithErrorCatcher(authenticateCall,
                    new UserAuthenticateCallErrorCatcher());

            DatabaseUserConfiguration userConfiguration = configurationHelper.getLoggedUserConfiguration(
                    updatedConfiguration, username);
            DatabaseAdapterFactory.createOrOpenDatabase(databaseAdapter, userConfiguration.databaseName(),
                    context, userConfiguration.encrypted());

            throwExceptionIfAlreadyAuthenticatedAndDbNotEmpty();

            return loginOnline(authenticatedUser, username, password);
        } catch (D2Error d2Error) {
            if (
                    d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR ||
                            d2Error.errorCode() == D2ErrorCode.SOCKET_TIMEOUT ||
                            d2Error.errorCode() == D2ErrorCode.UNKNOWN_HOST) {
                return loginOffline(username, password, serverUrl);
            } else if (d2Error.errorCode() == D2ErrorCode.USER_ACCOUNT_DISABLED) {
                wipeModule.wipeEverything();
                throw d2Error;
            } else {
                throw d2Error;
            }
        }
    }

    private User loginOnline(User authenticatedUser, String username, String password) {

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            AuthenticatedUser authenticatedUserToStore = buildAuthenticatedUser(authenticatedUser.uid(),
                    username, password);
            authenticatedUserStore.updateOrInsertWhere(authenticatedUserToStore);
            credentialsSecureStore.set(Credentials.create(username, password));

            systemInfoRepository.download().blockingAwait();

            handleUser(authenticatedUser);
            transaction.setSuccessful();
            return authenticatedUser;
        } finally {
            transaction.end();
        }
    }

    private User loginOffline(String username, String password, String serverUrl) throws D2Error {
        if (wasLoggedAndServerIsNew(serverUrl)) {
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
            credentialsSecureStore.set(Credentials.create(username, password));
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

    private void throwExceptionIfAlreadyAuthenticatedAndDbNotEmpty() throws D2Error {
        Credentials credentials = credentialsSecureStore.get();
        AuthenticatedUser existingUser = authenticatedUserStore.selectFirst();
        if (credentials != null) {
            if (existingUser == null) {
                credentialsSecureStore.remove();
            } else {
                throw D2Error.builder()
                        .errorCode(D2ErrorCode.ALREADY_AUTHENTICATED)
                        .errorDescription("A user is already authenticated: " + credentials.username())
                        .errorComponent(D2ErrorComponent.SDK)
                        .build();
            }
        }
    }

    private boolean wasLoggedAndServerIsNew(String serverUrl) {
        String baseServerUrl = ServerUrlParser.removeTrailingSlash(serverUrl);

        SystemInfo lastSystemInfo = systemInfoRepository.blockingGet();
        String existingServerUrl = lastSystemInfo == null ? null :
                ServerUrlParser.removeTrailingSlash(lastSystemInfo.contextPath());

        return !baseServerUrl.equals(existingServerUrl);
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
                .hash(md5(username, password))
                .build();
    }
}