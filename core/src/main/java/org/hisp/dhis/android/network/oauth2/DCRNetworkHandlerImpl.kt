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

import android.os.Build
import kotlinx.serialization.json.Json
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.internal.DCRNetworkHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class DCRNetworkHandlerImpl(
    httpClient: HttpServiceClient,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) : DCRNetworkHandler {

    private val service = DCRService(httpClient)
    private val json = Json { ignoreUnknownKeys = true }

    override fun buildEnrollmentUrl(serverUrl: String, state: String): String {
        val deviceVersion = Build.VERSION.RELEASE
        val deviceType = "android"
        val deviceAttestation = "android_sdk_${Build.VERSION.SDK_INT}"

        return "$serverUrl/api/auth/enrollDevice" +
                "?deviceVersion=$deviceVersion" +
                "&deviceType=$deviceType" +
                "&deviceAttestation=$deviceAttestation" +
                "&redirectUri=${OAuth2Config.DEFAULT_REDIRECT_URI}" +
                "&state=$state"
    }

    override fun getDeviceId(): String {
        return Build.MANUFACTURER + "_" + Build.MODEL + "_" + Build.DEVICE
    }

    override suspend fun registerClient(
        url: String,
        iat: String,
        clientName: String,
        redirectUri: String,
        scope: String,
        jwks: String,
    ): Result<String, D2Error> {
        return coroutineAPICallExecutor.wrap(storeError = false) {
            val request = ClientRegistrationRequestDTO(
                clientName = clientName,
                redirectUris = listOf(redirectUri),
                grantTypes = listOf("authorization_code", "refresh_token"),
                responseTypes = listOf("code"),
                tokenEndpointAuthMethod = "private_key_jwt",
                tokenEndpointAuthSigningAlg = "RS256",
                scope = scope,
                jwksUri = "https://dhis2.org/jwks.json",
                jwks = json.parseToJsonElement(jwks),
            )

            val response = service.registerClient(url, iat, request)
            response.clientId
        }
    }
}
