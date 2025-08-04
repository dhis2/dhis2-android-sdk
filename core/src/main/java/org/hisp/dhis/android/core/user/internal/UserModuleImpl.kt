/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.user.internal

import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.user.*
import org.hisp.dhis.android.core.user.loginconfig.LoginConfigCall
import org.hisp.dhis.android.core.user.loginconfig.LoginConfigObjectRepository
import org.hisp.dhis.android.core.user.openid.OpenIDConnectHandler
import org.hisp.dhis.android.core.user.openid.OpenIDConnectHandlerImpl
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions", "LongParameterList")
internal class UserModuleImpl(
    private val isUserLoggedInCallFactory: IsUserLoggedInCallableFactory,
    private val logoutCallCallFactory: LogOutCall,
    private val logInCall: LogInCall,
    private val authenticatedUser: AuthenticatedUserObjectRepository,
    private val userRoles: UserRoleCollectionRepository,
    private val userGroups: UserGroupCollectionRepository,
    private val authorities: AuthorityCollectionRepository,
    private val userCredentials: UserCredentialsObjectRepository,
    private val user: UserObjectRepository,
    private val accountManager: AccountManagerImpl,
    private val openIDConnectHandler: OpenIDConnectHandlerImpl,
    private val loginConfigCall: LoginConfigCall,
    private val twoFactorAuthManager: TwoFactorAuthManagerImpl
) : UserModule {

    override fun authenticatedUser(): AuthenticatedUserObjectRepository {
        return authenticatedUser
    }

    override fun userRoles(): UserRoleCollectionRepository {
        return userRoles
    }

    override fun userGroups(): UserGroupCollectionRepository {
        return userGroups
    }

    override fun authorities(): AuthorityCollectionRepository {
        return authorities
    }

    @Deprecated("Use user() instead.")
    override fun userCredentials(): UserCredentialsObjectRepository {
        return userCredentials
    }

    override fun user(): UserObjectRepository {
        return user
    }

    override fun logIn(username: String, password: String, serverUrl: String): Single<User> {
        return rxSingle { logInCall.logIn(username, password, serverUrl) }
    }

    override fun blockingLogIn(username: String, password: String, serverUrl: String): User {
        return runBlocking { logInCall.logIn(username, password, serverUrl) }
    }

    override fun logOut(): Completable {
        return logoutCallCallFactory.logOut()
    }

    override fun blockingLogOut() {
        logOut().blockingAwait()
    }

    override fun isLogged(): Single<Boolean> {
        return isUserLoggedInCallFactory.isLogged
    }

    override fun blockingIsLogged(): Boolean {
        return isLogged().blockingGet()
    }

    override fun accountManager(): AccountManager {
        return accountManager
    }

    override fun openIdHandler(): OpenIDConnectHandler {
        return openIDConnectHandler
    }

    override fun loginConfig(serverUrl: String): LoginConfigObjectRepository {
        return LoginConfigObjectRepository(loginConfigCall, serverUrl)
    }

    override fun twoFactorAuthManager(): TwoFactorAuthManager {
        return twoFactorAuthManager
    }
}
