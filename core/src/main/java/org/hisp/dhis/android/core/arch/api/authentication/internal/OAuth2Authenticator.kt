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
package org.hisp.dhis.android.core.arch.api.authentication.internal

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.Send.Sender
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2LogoutHandler
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2TokenRefresher
import org.koin.core.annotation.Singleton

private const val UNAUTHORIZED = 401

@Singleton
internal class OAuth2Authenticator(
    private val credentialsSecureStore: CredentialsSecureStore,
    private val tokenRefresher: Lazy<OAuth2TokenRefresher>,
    private val userIdHelper: UserIdAuthenticatorHelper,
    private val logoutHandler: OAuth2LogoutHandler,
) {

    suspend fun handleTokenCall(
        sender: Sender,
        requestBuilder: HttpRequestBuilder,
        credentials: Credentials,
    ): HttpClientCall {
        userIdHelper.builderWithUserId(requestBuilder)
        addTokenHeader(requestBuilder, getUpdatedToken(credentials))

        val call = sender.proceed(requestBuilder)

        if (call.response.status.value == UNAUTHORIZED) {
            val state = credentials.oauth2State
            if (state != null) {
                val refreshedState = tokenRefresher.value.refreshToken(state)
                if (refreshedState != null) {
                    val updatedCredentials = credentials.copy(oauth2State = refreshedState)
                    credentialsSecureStore.set(updatedCredentials)
                    addTokenHeader(requestBuilder, refreshedState.accessToken!!)
                    return sender.proceed(requestBuilder)
                }
            }
            logoutHandler.logOut()
        }
        return call
    }

    private suspend fun getUpdatedToken(credentials: Credentials): String {
        val state = credentials.oauth2State!!
        return if (state.needsTokenRefresh()) {
            val newState = tokenRefresher.value.refreshToken(state)
            if (newState != null) {
                credentialsSecureStore.set(credentials.copy(oauth2State = newState))
                newState.accessToken!!
            } else {
                state.accessToken!!
            }
        } else {
            state.accessToken!!
        }
    }

    private fun addTokenHeader(requestBuilder: HttpRequestBuilder, token: String) {
        requestBuilder.apply {
            headers.remove(UserIdAuthenticatorHelper.AUTHORIZATION_KEY)
            header(UserIdAuthenticatorHelper.AUTHORIZATION_KEY, "Bearer $token")
        }
    }
}
