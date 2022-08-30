/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.user.openid

import android.content.Context
import android.content.Intent
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import net.openid.appauth.*
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.LogInCall

private const val RC_AUTH = 2021

@Reusable
internal class OpenIDConnectHandlerImpl @Inject constructor(
    private val context: Context,
    private val logInCall: LogInCall,
    private val logoutHandler: OpenIDConnectLogoutHandler
) : OpenIDConnectHandler {

    override fun logIn(config: OpenIDConnectConfig): Single<IntentWithRequestCode> {
        return OpenIDConnectRequestHelper(config).prepareAuthRequest().map {
            val authService = AuthorizationService(context)
            val intent = authService.getAuthorizationRequestIntent(it)
            authService.dispose()
            IntentWithRequestCode(intent, RC_AUTH)
        }
    }

    override fun blockingLogIn(config: OpenIDConnectConfig): IntentWithRequestCode {
        return logIn(config).blockingGet()
    }

    override fun handleLogInResponse(
        serverUrl: String,
        intent: Intent?,
        requestCode: Int
    ): Single<User> {
        return if (requestCode == RC_AUTH && intent != null) {
            val ex = AuthorizationException.fromIntent(intent)
            if (ex != null) {
                Single.error<User>(ex)
            } else {
                val response = AuthorizationResponse.fromIntent(intent)!!
                downloadToken(response.createTokenExchangeRequest())
                    .observeOn(Schedulers.io())
                    .map {
                        logInCall.blockingLogInOpenIDConnect(serverUrl, it)
                    }
            }
        } else {
            Single.error<User>(RuntimeException("Unexpected intent or request code"))
        }
    }

    override fun blockingHandleLogInResponse(
        serverUrl: String,
        intent: Intent?,
        requestCode: Int
    ): User {
        return handleLogInResponse(serverUrl, intent, requestCode).blockingGet()
    }

    override fun logOutObservable(): Observable<Unit> {
        return logoutHandler.logOutObservable()
    }

    private fun downloadToken(tokenRequest: TokenRequest): Single<AuthState> {
        return Single.create { emitter ->
            val authService = AuthorizationService(context)
            authService.performTokenRequest(
                tokenRequest
            ) { tokenResponse, tokenEx ->
                authService.dispose()
                val authState = AuthState()
                authState.update(tokenResponse, tokenEx)
                if (tokenResponse?.idToken != null) {
                    emitter.onSuccess(authState)
                } else {
                    emitter.onError(RuntimeException(tokenEx))
                }
            }
        }
    }
}
