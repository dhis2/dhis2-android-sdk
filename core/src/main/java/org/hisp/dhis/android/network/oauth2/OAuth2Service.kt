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
package org.hisp.dhis.android.network.oauth2

import org.hisp.dhis.android.core.arch.api.HttpServiceClient

internal class OAuth2Service(private val client: HttpServiceClient) {

    @Suppress("LongParameterList")
    suspend fun exchangeCodeForToken(
        url: String,
        grantType: String,
        code: String,
        redirectUri: String,
        clientId: String,
        codeVerifier: String,
        clientAssertion: String,
    ): TokenResponseDTO {
        return client.post {
            absoluteUrl(url + "/oauth2/token")
            contentType("x-www-form-urlencoded")
            body(
                buildFormUrlEncoded(
                    mapOf(
                        "grant_type" to grantType,
                        "code" to code,
                        "redirect_uri" to redirectUri,
                        "client_id" to clientId,
                        "code_verifier" to codeVerifier,
                        "client_assertion_type" to "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                        "client_assertion" to clientAssertion,
                    ),
                ),
            )
        }
    }

    suspend fun refreshToken(
        url: String,
        grantType: String,
        refreshToken: String,
        clientId: String,
        clientAssertion: String,
    ): TokenResponseDTO {
        return client.post {
            absoluteUrl(url + "/oauth2/token")
            contentType("x-www-form-urlencoded")
            body(
                buildFormUrlEncoded(
                    mapOf(
                        "grant_type" to grantType,
                        "refresh_token" to refreshToken,
                        "client_id" to clientId,
                        "client_assertion_type" to "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                        "client_assertion" to clientAssertion,
                    ),
                ),
            )
        }
    }

    private fun buildFormUrlEncoded(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (key, value) ->
            "$key=${java.net.URLEncoder.encode(value, "UTF-8")}"
        }
    }
}
