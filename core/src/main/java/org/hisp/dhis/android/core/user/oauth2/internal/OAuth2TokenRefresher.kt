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

import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.JWTHelper
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.koin.core.annotation.Singleton

@Singleton
internal class OAuth2TokenRefresher(
    private val oauth2NetworkHandler: OAuth2NetworkHandler,
    private val keyStoreManager: KeyStoreManager,
    private val oauth2SecureStore: OAuth2SecureStore,
    private val logoutHandler: OAuth2LogoutHandler,
) {
    @Suppress("ReturnCount")
    suspend fun refreshToken(state: OAuth2State): OAuth2State? {
        return try {
            if (state.refreshToken == null) {
                logoutHandler.logOut()
                return null
            }

            val serverUrl = oauth2SecureStore.serverUrl ?: return null
            val privateKey = keyStoreManager.getPrivateKey(state.keyId) ?: return null

            val clientAssertion = JWTHelper.createClientAssertion(
                clientId = state.clientId,
                tokenEndpoint = "$serverUrl/oauth2/token",
                privateKey = privateKey,
                keyId = state.keyId,
            )

            val result = oauth2NetworkHandler.refreshToken(
                url = serverUrl,
                refreshToken = state.refreshToken,
                clientId = state.clientId,
                keyId = state.keyId,
                clientAssertion = clientAssertion,
            )

            when (result) {
                is org.hisp.dhis.android.core.arch.helpers.Result.Success -> result.value
                is org.hisp.dhis.android.core.arch.helpers.Result.Failure -> {
                    logoutHandler.logOut()
                    null
                }
            }
        } catch (_: Exception) {
            logoutHandler.logOut()
            null
        }
    }
}
