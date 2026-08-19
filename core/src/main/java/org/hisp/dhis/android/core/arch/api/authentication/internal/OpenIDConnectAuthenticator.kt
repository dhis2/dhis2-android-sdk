/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.core.arch.api.authentication.internal

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.Send.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.openid.OpenIDConnectStateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher
import org.hisp.dhis.android.core.user.openid.OpenIdRefreshResult
import org.koin.core.annotation.Singleton

private const val UNAUTHORIZED = 401

@Singleton
internal class OpenIDConnectAuthenticator(
    private val credentialsSecureStore: CredentialsSecureStore,
    private val tokenRefresher: OpenIDConnectTokenRefresher,
    private val userIdHelper: UserIdAuthenticatorHelper,
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore,
    private val logInExceptions: LogInExceptions,
) {

    suspend fun handleTokenCall(
        sender: Sender,
        requestBuilder: HttpRequestBuilder,
        credentials: Credentials,
    ): HttpClientCall {
        userIdHelper.builderWithUserId(requestBuilder)

        val state = credentials.openIDConnectState
            ?: throw logInExceptions.openIdConnectNoValidTokenError("There is no OpenID Connect session")
        addTokenHeader(requestBuilder, idTokenOf(state))

        val call = sender.proceed(requestBuilder)
        if (call.response.status.value != UNAUTHORIZED) {
            return call
        }

        return when (val refresh = tokenRefresher.refresh(state)) {
            is OpenIdRefreshResult.Success -> {
                persist(credentials, state)
                addTokenHeader(requestBuilder, refresh.idToken)
                sender.proceed(requestBuilder)
            }

            is OpenIdRefreshResult.Invalid -> throw discardState(credentials)

            // Offline or a provider error: the session stays open and the caller sees the 401.
            is OpenIdRefreshResult.Retryable -> call
        }
    }

    private fun idTokenOf(state: AuthState): String {
        return state.idToken
            ?: throw logInExceptions.openIdConnectNoValidTokenError("There is no idToken")
    }

    private fun persist(credentials: Credentials, state: AuthState) {
        credentialsSecureStore.set(credentials) // The auth state was updated in place.
        // Keep the per-account persisted state fresh so relogin uses the latest refresh token.
        openIDConnectStateSecureStore.set(credentials.serverUrl, credentials.username, state)
    }

    /**
     * The refresh token is dead, so the stored state can only lead to the same failure. Removing it
     * keeps the next login deterministic: it opens the account offline and reports that a new
     * authorization is required, instead of retrying a refresh that can never succeed.
     */
    private fun discardState(credentials: Credentials) = logInExceptions
        .openIdConnectNoValidTokenError("The refresh token was rejected by the provider")
        .also { openIDConnectStateSecureStore.remove(credentials.serverUrl, credentials.username) }

    private fun addTokenHeader(requestBuilder: HttpRequestBuilder, token: String) {
        requestBuilder.apply {
            headers.remove(UserIdAuthenticatorHelper.AUTHORIZATION_KEY)
            header(UserIdAuthenticatorHelper.AUTHORIZATION_KEY, "Bearer $token")
        }
    }
}
