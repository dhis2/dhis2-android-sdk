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

import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.OAuth2State

internal interface OAuth2NetworkHandler {
    fun buildAuthorizationUrl(
        clientId: String,
        state: String,
        codeChallenge: String,
        scope: String,
    ): String

    /**
     * Reads the `token_endpoint` advertised by the server discovery document. It must be resolved
     * before the client assertion is signed, so that the assertion audience and the endpoint the
     * request is actually posted to cannot diverge.
     */
    suspend fun getTokenEndpoint(
        url: String,
        oauthConfigPath: String = DEFAULT_OAUTH_CONFIG_PATH,
    ): String

    @Suppress("LongParameterList")
    suspend fun exchangeCodeForToken(
        tokenEndpoint: String,
        code: String,
        redirectUri: String,
        clientId: String,
        codeVerifier: String,
        clientAssertion: String,
    ): Result<OAuth2State, D2Error>

    suspend fun refreshToken(
        endpoint: String,
        refreshToken: String,
        clientId: String,
        keyId: String,
        clientAssertion: String,
    ): Result<OAuth2State, D2Error>

    fun buildLogoutUrl(config: OAuth2Config): String

    companion object {
        const val DEFAULT_OAUTH_CONFIG_PATH = "/.well-known/oauth-authorization-server"
    }
}
