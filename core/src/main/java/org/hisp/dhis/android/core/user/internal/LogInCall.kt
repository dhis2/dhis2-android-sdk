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

import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoCall
import org.hisp.dhis.android.core.user.AccountDeletionReason
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2StateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectStateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList", "TooManyFunctions")
internal class LogInCall(
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val networkHandler: UserNetworkHandler,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val userIdStore: UserIdInMemoryStore,
    private val userHandler: UserHandler,
    private val authenticatedUserStore: AuthenticatedUserStore,
    private val systemInfoCall: SystemInfoCall,
    private val userStore: UserStore,
    private val loginDatabaseManager: LogInDatabaseManager,
    private val exceptions: LogInExceptions,
    private val accountManager: AccountManagerImpl,
    private val apiCallErrorCatcher: UserAuthenticateCallErrorCatcher,
    private val oauth2StateSecureStore: OAuth2StateSecureStore,
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore,
    private val openIDConnectTokenRefresher: Lazy<OpenIDConnectTokenRefresher>,
) {
    @Throws(D2Error::class)
    suspend fun logIn(username: String?, password: String?, serverUrl: String?): User {
        exceptions.throwExceptionIfUsernameNull(username)
        exceptions.throwExceptionIfAlreadyAuthenticated()

        val trimmedServerUrl = ServerUrlParser.trimAndRemoveTrailingSlash(serverUrl)
        val parsedServerUrl = ServerUrlParser.parse(trimmedServerUrl)
        ServerURLWrapper.setServerUrl(parsedServerUrl.toString())

        val oauth2State = oauth2StateSecureStore.get(trimmedServerUrl!!, username!!)
        val openIdState = openIDConnectStateSecureStore.get(trimmedServerUrl, username)
        return when {
            oauth2State != null ->
                logInWithOAuth2State(trimmedServerUrl, username, password, oauth2State)
            openIdState != null ->
                logInWithOpenIdState(trimmedServerUrl, username, password, openIdState)
            else -> {
                exceptions.throwExceptionIfPasswordNull(password)
                logInWithPassword(trimmedServerUrl, username, password)
            }
        }
    }

    private suspend fun logInWithOAuth2State(
        serverUrl: String,
        username: String,
        pin: String?,
        state: OAuth2State,
    ): User {
        val credentials = Credentials(username, serverUrl, pin, null, state)
        return try {
            val user = coroutineAPICallExecutor.wrap(errorCatcher = apiCallErrorCatcher) {
                networkHandler.authenticate("Bearer ${state.accessToken!!}")
            }.getOrThrow()
            loginOnline(user, credentials)
        } catch (d2Error: D2Error) {
            if (d2Error.isOffline) {
                tryLoginOffline(credentials, d2Error)
            } else {
                throw handleOnlineException(d2Error, credentials)
            }
        }
    }

    private suspend fun logInWithOpenIdState(
        serverUrl: String,
        username: String,
        pin: String?,
        state: AuthState,
    ): User {
        val credentials = Credentials(username, serverUrl, pin, state, null)
        // Try to refresh the idToken first; if it fails (offline or invalid refresh token) fall
        // back to the stored idToken and let the network call decide between online and offline.
        val token = openIDConnectTokenRefresher.value.blockingGetFreshTokenOrNull(state) ?: state.idToken!!
        return try {
            val user = coroutineAPICallExecutor.wrap(errorCatcher = apiCallErrorCatcher) {
                networkHandler.authenticate("Bearer $token")
            }.getOrThrow()
            openIDConnectStateSecureStore.set(serverUrl, username, state)
            loginOnline(user, credentials)
        } catch (d2Error: D2Error) {
            if (d2Error.isOffline) {
                tryLoginOffline(credentials, d2Error)
            } else {
                throw handleOnlineException(d2Error, credentials)
            }
        }
    }

    private suspend fun logInWithPassword(
        serverUrl: String,
        username: String,
        password: String?,
    ): User {
        val credentials = Credentials(username, serverUrl, password, null)
        return try {
            if (loginDatabaseManager.isPendingToImportDB(serverUrl, username)) {
                importDB(serverUrl, credentials)
            } else {
                val user = coroutineAPICallExecutor.wrap(errorCatcher = apiCallErrorCatcher) {
                    networkHandler.authenticate(
                        okhttp3.Credentials.basic(username, password!!),
                    )
                }.getOrThrow()
                loginOnline(user, credentials)
            }
        } catch (d2Error: D2Error) {
            if (d2Error.isOffline) {
                tryLoginOffline(credentials, d2Error)
            } else {
                throw handleOnlineException(d2Error, credentials)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleOnlineException(d2Error: D2Error, credentials: Credentials?): D2Error {
        return if (d2Error.errorCode() == D2ErrorCode.USER_ACCOUNT_DISABLED) {
            try {
                if (credentials != null) {
                    accountManager.deleteAccountAndEmit(credentials, AccountDeletionReason.ACCOUNT_DISABLED)
                }
                d2Error
            } catch (e: Exception) {
                d2Error
            }
        } else if (d2Error.errorCode() == D2ErrorCode.UNEXPECTED ||
            d2Error.errorCode() == D2ErrorCode.API_RESPONSE_PROCESS_ERROR
        ) {
            exceptions.noDHIS2Server()
        } else {
            d2Error
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun loginOnline(user: User, credentials: Credentials): User {
        credentialsSecureStore.set(credentials)
        userIdStore.set(user.uid())

        loginDatabaseManager.loadDatabaseOnline(
            credentials.serverUrl,
            credentials.username,
            credentials.authorizationType,
        )

        return coroutineAPICallExecutor.wrapTransactionallyRoom {
            try {
                if (credentials.oauth2State != null || credentials.openIDConnectState != null) {
                    verifyPinAgainstStoredHash(credentials)
                }
                val authenticatedUser = AuthenticatedUser.builder()
                    .user(user.uid())
                    .hash(credentials.getHash())
                    .build()

                authenticatedUserStore.updateOrInsertWhere(authenticatedUser)
                systemInfoCall.download(storeError = true)
                userHandler.handle(user)
                user
            } catch (e: Exception) {
                // Credentials are stored and then removed in case of error (required to download system info)
                credentialsSecureStore.remove()
                userIdStore.remove()
                throw e
            }
        }
    }

    private suspend fun verifyPinAgainstStoredHash(credentials: Credentials) {
        val existing = authenticatedUserStore.selectFirst() ?: return
        if (existing.hash() != credentials.getHash()) {
            throw exceptions.badCredentialsError()
        }
    }

    @Throws(D2Error::class)
    @Suppress("ThrowsCount")
    private suspend fun tryLoginOffline(credentials: Credentials, originalError: D2Error): User {
        val existingDatabase =
            loginDatabaseManager.loadExistingKeepingEncryption(credentials.serverUrl, credentials.username)
        if (!existingDatabase) {
            throw originalError
        }
        val existingUser = authenticatedUserStore.selectFirst() ?: throw exceptions.noUserOfflineError()

        if (credentials.getHash() != existingUser.hash()) {
            throw exceptions.badCredentialsError()
        }
        credentialsSecureStore.set(credentials)
        userIdStore.set(existingUser.user()!!)
        return userStore.selectByUid(existingUser.user()!!)!!
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun importDB(serverUrl: String, credentials: Credentials): User {
        try {
            loginDatabaseManager.importDB(serverUrl, credentials)
            credentialsSecureStore.set(credentials)
            val existingUser = authenticatedUserStore.selectFirst() ?: throw exceptions.noUserOfflineError()
            userIdStore.set(existingUser.user()!!)
            return userStore.selectByUid(existingUser.user()!!)!!
        } catch (e: Exception) {
            throw exceptions.badCredentialsError()
        }
    }

    @Throws(D2Error::class)
    suspend fun blockingLogInOpenIDConnect(serverUrl: String, openIDConnectState: AuthState): User {
        return logInWithToken(
            serverUrl = serverUrl,
            token = openIDConnectState.idToken!!,
            openIDConnectState = openIDConnectState,
            oauth2State = null,
        )
    }

    @Throws(D2Error::class)
    suspend fun logInOAuth2(serverUrl: String, oauth2State: OAuth2State): User {
        return logInWithToken(
            serverUrl = serverUrl,
            token = oauth2State.accessToken!!,
            openIDConnectState = null,
            oauth2State = oauth2State,
        )
    }

    @Throws(D2Error::class)
    private suspend fun logInWithToken(
        serverUrl: String,
        token: String,
        openIDConnectState: AuthState?,
        oauth2State: OAuth2State?,
    ): User {
        val trimmedServerUrl = ServerUrlParser.trimAndRemoveTrailingSlash(serverUrl)

        val parsedServerUrl = ServerUrlParser.parse(trimmedServerUrl)
        ServerURLWrapper.setServerUrl(parsedServerUrl.toString())

        var credentials: Credentials? = null
        return try {
            val user = coroutineAPICallExecutor.wrap(errorCatcher = apiCallErrorCatcher) {
                networkHandler.authenticate("Bearer $token")
            }.getOrThrow()
            credentials = Credentials(
                username = user.username()!!,
                serverUrl = trimmedServerUrl!!,
                password = null,
                openIDConnectState = openIDConnectState,
                oauth2State = oauth2State,
            )
            loginOnline(user, credentials)
        } catch (d2Error: D2Error) {
            throw handleOnlineException(d2Error, credentials)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return credentialsSecureStore.get() != null
    }
}
