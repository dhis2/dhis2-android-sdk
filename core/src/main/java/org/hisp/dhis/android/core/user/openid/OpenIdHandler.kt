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

package org.hisp.dhis.android.core.user.openid

import android.content.Context
import android.content.Intent
import io.reactivex.Single
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

private const val RC_AUTH = 2021

class OpenIdHandler(private val context: Context) {

    fun logIn(config: OpenIDConnectConfig): Single<IntentWithRequestCode> {
        return OpenIdRequestHelper(config).prepareAuthRequest().map {
            val authService = AuthorizationService(context)
            val intent = authService.getAuthorizationRequestIntent(it)
            authService.dispose()
            IntentWithRequestCode(intent, RC_AUTH)
        }
    }

    fun blockingLogIn(config: OpenIDConnectConfig): IntentWithRequestCode {
        return logIn(config).blockingGet()
    }

    fun handleLoginResponse(
        intent: Intent?,
        requestCode: Int
    ): Single<String> {
        return if (requestCode == RC_AUTH && intent != null) {
            val response = AuthorizationResponse.fromIntent(intent)!!
            val ex = AuthorizationException.fromIntent(intent)
            if (ex != null) {
                Single.just(response.authorizationCode!!)
            } else {
                refreshToken(response.createTokenExchangeRequest())
            }
        } else {
            Single.error<String>(RuntimeException("Unexpected intent or request code"))
        }
    }

    fun blockingHandleLogInResponse(
        intent: Intent?,
        requestCode: Int
    ): String {
        return handleLoginResponse(intent, requestCode).blockingGet()
    }

    private fun refreshToken(
        tokenRequest: TokenRequest
    ): Single<String> {
        return Single.create { emitter ->
            val authService = AuthorizationService(context)
            authService.performTokenRequest(
                tokenRequest
            ) { tokenResponse, tokenEx ->
                authService.dispose()
                if (tokenResponse?.idToken != null) {
                    emitter.onSuccess(tokenResponse.idToken!!)
                } else {
                    emitter.onError(RuntimeException(tokenEx))
                }
            }
        }
    }
}