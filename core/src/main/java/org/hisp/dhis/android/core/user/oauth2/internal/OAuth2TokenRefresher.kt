/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.user.oauth2.internal

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.JWTHelper
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.koin.core.annotation.Singleton
import java.security.PrivateKey

private const val BAD_REQUEST = 400
private const val UNAUTHORIZED = 401

@Singleton
internal class OAuth2TokenRefresher(
    private val oauth2NetworkHandler: OAuth2NetworkHandler,
    private val keyStoreManager: KeyStoreManager,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val oauth2StateSecureStore: OAuth2StateSecureStore,
    private val logInExceptions: LogInExceptions,
) {
    private val mutex = Mutex()

    @Suppress("ReturnCount", "TooGenericExceptionCaught")
    suspend fun rotate(consumedRefreshToken: String?): RefreshResult {
        mutex.withLock {
            val credentials = credentialsSecureStore.get()
            val state = credentials?.oauth2State
                ?: return RefreshResult.Invalid(
                    logInExceptions.oauth2NoValidTokenError("There is no OAuth2 session"),
                )

            val currentRefreshToken = state.refreshToken
                ?: return RefreshResult.Invalid(
                    logInExceptions.oauth2NoValidTokenError("There is no refresh token"),
                )

            if (currentRefreshToken != consumedRefreshToken) {
                return RefreshResult.Success(state)
            }

            val privateKey = keyStoreManager.getPrivateKey(state.keyId)
                ?: return discardTokens(
                    credentials,
                    state,
                    logInExceptions.incompleteOAuth2RegistrationError("Private key"),
                )

            return try {
                when (val result = callTokenEndpoint(state, currentRefreshToken, privateKey)) {
                    is Result.Success -> {
                        persist(credentials, result.value)
                        RefreshResult.Success(result.value)
                    }

                    is Result.Failure -> classifyFailure(credentials, state, result.failure)
                }
            } catch (_: Exception) {
                RefreshResult.Retryable(null)
            }
        }
    }

    private suspend fun callTokenEndpoint(
        state: OAuth2State,
        refreshToken: String,
        privateKey: PrivateKey,
    ): Result<OAuth2State, D2Error> {
        val clientAssertion = JWTHelper.createClientAssertion(
            clientId = state.clientId,
            tokenEndpoint = state.tokenEndpoint,
            privateKey = privateKey,
            keyId = state.keyId,
        )

        return oauth2NetworkHandler.refreshToken(
            endpoint = state.tokenEndpoint,
            refreshToken = refreshToken,
            clientId = state.clientId,
            keyId = state.keyId,
            clientAssertion = clientAssertion,
        )
    }

    /**
     * Only a token the server explicitly rejects is unrecoverable. Anything else — offline, server
     * error — is transient and must leave the stored tokens alone so a later call can retry.
     */
    private fun classifyFailure(
        credentials: Credentials,
        state: OAuth2State,
        error: D2Error,
    ): RefreshResult {
        val httpCode = error.httpErrorCode()
        val isRejected = httpCode == BAD_REQUEST || httpCode == UNAUTHORIZED

        return if (!error.isOffline && isRejected) {
            discardTokens(
                credentials,
                state,
                logInExceptions.oauth2NoValidTokenError("The refresh token was rejected by the server"),
            )
        } else {
            RefreshResult.Retryable(error)
        }
    }

    private fun discardTokens(
        credentials: Credentials,
        state: OAuth2State,
        error: D2Error,
    ): RefreshResult.Invalid {
        persist(credentials, state.copy(accessToken = null, refreshToken = null))
        return RefreshResult.Invalid(error)
    }

    private fun persist(credentials: Credentials, state: OAuth2State) {
        credentialsSecureStore.set(credentials.copy(oauth2State = state))
        oauth2StateSecureStore.set(credentials.serverUrl, credentials.username, state)
    }
}
