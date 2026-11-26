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

package org.hisp.dhis.android.core.user.openid

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.AuthenticatedUserStore
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.koin.core.annotation.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val RC_AUTH = 2021

@Suppress("LongParameterList")
@Singleton
internal class OpenIDConnectHandlerImpl(
    private val context: Context,
    private val logInCall: LogInCall,
    private val logoutHandler: OpenIDConnectLogoutHandler,
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val authenticatedUserStore: AuthenticatedUserStore,
    private val logInExceptions: LogInExceptions,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OpenIDConnectHandler {

    override fun logIn(config: OpenIDConnectConfig): Single<IntentWithRequestCode> {
        return rxSingle { suspendLogIn(config) }
    }

    override fun blockingLogIn(config: OpenIDConnectConfig): IntentWithRequestCode {
        return runBlocking { suspendLogIn(config) }
    }

    private suspend fun suspendLogIn(config: OpenIDConnectConfig): IntentWithRequestCode {
        val authRequest = OpenIDConnectRequestHelper(config).prepareAuthRequest()
        val authService = AuthorizationService(context)
        val intent = authService.getAuthorizationRequestIntent(authRequest)
        authService.dispose()
        return IntentWithRequestCode(intent, RC_AUTH)
    }

    override fun handleLogInResponse(
        serverUrl: String,
        intent: Intent?,
        requestCode: Int,
    ): Single<User> {
        return rxSingle { suspendHandleLogInResponse(serverUrl, intent, requestCode) }
    }

    override fun blockingHandleLogInResponse(
        serverUrl: String,
        intent: Intent?,
        requestCode: Int,
    ): User {
        return runBlocking { suspendHandleLogInResponse(serverUrl, intent, requestCode) }
    }

    @Suppress("TooGenericExceptionThrown")
    private suspend fun suspendHandleLogInResponse(
        serverUrl: String,
        intent: Intent?,
        requestCode: Int,
    ): User {
        if (requestCode != RC_AUTH || intent == null) {
            throw RuntimeException("Unexpected intent or request code")
        }

        AuthorizationException.fromIntent(intent)?.let { throw it }

        val response = AuthorizationResponse.fromIntent(intent)!!
        val authState = downloadToken(response.createTokenExchangeRequest())

        return withContext(dispatcher) {
            val user = logInCall.blockingLogInOpenIDConnect(serverUrl, authState)
            openIDConnectStateSecureStore.set(serverUrl, user.username()!!, authState)
            user
        }
    }

    override fun logOutObservable(): Observable<Unit> {
        return logoutHandler.logOutObservable()
    }

    override suspend fun suspendSetPin(pin: String): Result<Unit, D2Error> {
        val credentials = credentialsSecureStore.get()
        return when {
            credentials == null -> Result.Failure(logInExceptions.noActiveSessionError())
            credentials.openIDConnectState == null ->
                Result.Failure(logInExceptions.pinRequiresTokenBasedAccountError())
            else -> {
                val updated = credentials.copy(pin = pin)
                credentialsSecureStore.set(updated)
                val existing = authenticatedUserStore.selectFirst()
                if (existing == null) {
                    Result.Failure(logInExceptions.noAuthenticatedUserPersistedError())
                } else {
                    authenticatedUserStore.updateOrInsertWhere(
                        existing.toBuilder().hash(updated.getHash()).build(),
                    )
                    Result.Success(Unit)
                }
            }
        }
    }

    override suspend fun suspendChangePin(currentPin: String, newPin: String): Result<Unit, D2Error> {
        val credentials = credentialsSecureStore.get()
        return when {
            credentials == null -> Result.Failure(logInExceptions.noActiveSessionError())
            credentials.pin != currentPin -> Result.Failure(logInExceptions.incorrectPinError())
            else -> suspendSetPin(newPin)
        }
    }

    private suspend fun downloadToken(tokenRequest: TokenRequest): AuthState {
        return suspendCancellableCoroutine { continuation ->
            val authService = AuthorizationService(context)
            authService.performTokenRequest(
                tokenRequest,
            ) { tokenResponse, tokenEx ->
                authService.dispose()
                val authState = AuthState()
                authState.update(tokenResponse, tokenEx)
                if (tokenResponse?.idToken != null) {
                    continuation.resume(authState)
                } else {
                    continuation.resumeWithException(RuntimeException(tokenEx))
                }
            }
        }
    }
}
