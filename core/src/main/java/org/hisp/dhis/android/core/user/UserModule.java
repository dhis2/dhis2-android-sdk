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

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Completable;
import io.reactivex.Single;

@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
@Reusable
public final class UserModule {

    private final IsUserLoggedInCallableFactory isUserLoggedInCallFactory;
    private final LogOutCallFactory logoutCallCallFactory;
    private final UserAuthenticateCallFactory loginCallFactory;

    public final AuthenticatedUserObjectRepository authenticatedUser;
    public final UserRoleCollectionRepository userRoles;
    public final AuthorityCollectionRepository authorities;
    public final UserCredentialsObjectRepository userCredentials;
    public final UserObjectRepository user;

    @Inject
    UserModule(IsUserLoggedInCallableFactory isUserLoggedInCallFactory,
               LogOutCallFactory logoutCallCallFactory,
               UserAuthenticateCallFactory loginCallFactory,
               AuthenticatedUserObjectRepository authenticatedUser,
               UserRoleCollectionRepository userRoles,
               AuthorityCollectionRepository authorities,
               UserCredentialsObjectRepository userCredentials,
               UserObjectRepository user) {
        this.isUserLoggedInCallFactory = isUserLoggedInCallFactory;
        this.logoutCallCallFactory = logoutCallCallFactory;
        this.loginCallFactory = loginCallFactory;
        this.authenticatedUser = authenticatedUser;
        this.userRoles = userRoles;
        this.authorities = authorities;
        this.userCredentials = userCredentials;
        this.user = user;
    }

    @NonNull
    public Single<User> logIn(String username, String password) {
        return loginCallFactory.logIn(username, password);
    }

    @NonNull
    public Completable logOut() {
        return logoutCallCallFactory.logOut();
    }

    @NonNull
    public Single<Boolean> isLogged() {
        return isUserLoggedInCallFactory.isLogged();
    }
}