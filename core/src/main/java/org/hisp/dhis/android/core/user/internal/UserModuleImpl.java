/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.user.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.user.AccountManager;
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository;
import org.hisp.dhis.android.core.user.AuthorityCollectionRepository;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentialsObjectRepository;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.android.core.user.UserObjectRepository;
import org.hisp.dhis.android.core.user.UserRoleCollectionRepository;
import org.hisp.dhis.android.core.user.openid.OpenIDConnectHandler;
import org.hisp.dhis.android.core.user.openid.OpenIDConnectHandlerImpl;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Single;

@Reusable
public final class UserModuleImpl implements UserModule {

    private final IsUserLoggedInCallableFactory isUserLoggedInCallFactory;
    private final LogOutCall logoutCallCallFactory;
    private final LogInCall logInCall;

    private final AuthenticatedUserObjectRepository authenticatedUser;
    private final UserRoleCollectionRepository userRoles;
    private final AuthorityCollectionRepository authorities;
    private final UserCredentialsObjectRepository userCredentials;
    private final UserObjectRepository user;

    private final AccountManager accountManager;

    private final OpenIDConnectHandler openIDConnectHandler;

    @Inject
    UserModuleImpl(IsUserLoggedInCallableFactory isUserLoggedInCallFactory,
                   LogOutCall logoutCallCallFactory,
                   LogInCall logInCall,
                   AuthenticatedUserObjectRepository authenticatedUser,
                   UserRoleCollectionRepository userRoles,
                   AuthorityCollectionRepository authorities,
                   UserCredentialsObjectRepository userCredentials,
                   UserObjectRepository user,
                   AccountManagerImpl accountManager,
                   OpenIDConnectHandlerImpl openIdHandlerImpl) {
        this.isUserLoggedInCallFactory = isUserLoggedInCallFactory;
        this.logoutCallCallFactory = logoutCallCallFactory;
        this.logInCall = logInCall;
        this.authenticatedUser = authenticatedUser;
        this.userRoles = userRoles;
        this.authorities = authorities;
        this.userCredentials = userCredentials;
        this.user = user;
        this.accountManager = accountManager;
        this.openIDConnectHandler = openIdHandlerImpl;
    }

    @Override
    public AuthenticatedUserObjectRepository authenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public UserRoleCollectionRepository userRoles() {
        return userRoles;
    }

    @Override
    public AuthorityCollectionRepository authorities() {
        return authorities;
    }

    @Override
    public UserCredentialsObjectRepository userCredentials() {
        return userCredentials;
    }

    @Override
    public UserObjectRepository user() {
        return user;
    }

    @Override
    @NonNull
    public Single<User> logIn(String username, String password, String serverUrl) {
        return logInCall.logIn(username, password, serverUrl);
    }

    @Override
    @NonNull
    public User blockingLogIn(String username, String password, String serverUrl) {
        return logIn(username, password, serverUrl).blockingGet();
    }

    @Override
    @NonNull
    public Completable logOut() {
        return logoutCallCallFactory.logOut();
    }

    @Override
    @NonNull
    public void blockingLogOut() {
        logOut().blockingAwait();
    }

    @Override
    @NonNull
    public Single<Boolean> isLogged() {
        return isUserLoggedInCallFactory.isLogged();
    }

    @Override
    @NonNull
    public boolean blockingIsLogged() {
        return isLogged().blockingGet();
    }

    @Override
    public AccountManager accountManager() {
        return accountManager;
    }

    @Override
    @NonNull
    public OpenIDConnectHandler openIdHandler() {
        return openIDConnectHandler;
    }
}