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

import android.content.Intent
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.configuration.internal.ServerUrlNormalizer
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.OAuth2Handler
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.JWTHelper
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.hisp.dhis.android.core.user.openid.IntentWithRequestCode
import org.koin.core.annotation.Singleton

private const val RC_AUTH = 2022

@Singleton
internal class OAuth2HandlerImpl(
    private val logInCall: LogInCall,
    private val logoutHandler: OAuth2LogoutHandler,
    private val dcrNetworkHandler: DCRNetworkHandler,
    private val oauth2NetworkHandler: OAuth2NetworkHandler,
    private val keyStoreManager: KeyStoreManager,
    private val oauth2SecureStore: OAuth2SecureStore,
) : OAuth2Handler {

    override fun buildEnrollmentUrl(serverUrl: String): Single<String> {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        return Single.fromCallable {
            val state = JWTHelper.generateState()
            oauth2SecureStore.tempState = state
            dcrNetworkHandler.buildEnrollmentUrl(normalizedUrl, state)
        }
    }

    override fun blockingBuildEnrollmentUrl(serverUrl: String): String {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        return buildEnrollmentUrl(normalizedUrl).blockingGet()
    }

    override fun handleEnrollmentResponse(serverUrl: String, iat: String): Single<Unit> {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        return Single.fromCallable {
            runBlocking {
                if (JWTHelper.validateJWT(iat) == null) {
                    throw IllegalArgumentException("Invalid or expired IAT")
                }

                val keyId = keyStoreManager.generateKeyPair()

                val jwks = keyStoreManager.createJWKS(keyId)

                val deviceId = dcrNetworkHandler.getDeviceId()
                val clientName = "DHIS2 Android - $deviceId"

                val result = dcrNetworkHandler.registerClient(
                    url = normalizedUrl,
                    iat = iat,
                    clientName = clientName,
                    redirectUri = OAuth2Config.DEFAULT_REDIRECT_URI,
                    scope = OAuth2Config.DEFAULT_SCOPE,
                    jwks = jwks,
                )

                when (result) {
                    is Result.Success -> {
                        oauth2SecureStore.clientId = result.value
                        oauth2SecureStore.keyId = keyId
                        oauth2SecureStore.serverUrl = normalizedUrl
                        oauth2SecureStore.isRegistered = true
                        oauth2SecureStore.registrationDate = System.currentTimeMillis()
                        oauth2SecureStore.clearTemporaryData()
                    }
                    is Result.Failure -> {
                        keyStoreManager.deleteKey(keyId)
                        throw result.failure
                    }
                }
            }
        }
    }

    override fun blockingHandleEnrollmentResponse(serverUrl: String, iat: String) {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        handleEnrollmentResponse(normalizedUrl, iat).blockingGet()
    }

    override fun logIn(config: OAuth2Config): Single<IntentWithRequestCode> {
        return Single.fromCallable {
            if (!isDeviceRegistered()) {
                throw IllegalStateException("Device not registered. Call handleEnrollmentResponse first.")
            }

            val state = JWTHelper.generateState()
            val codeVerifier = JWTHelper.generateCodeVerifier()
            val codeChallenge = JWTHelper.generateCodeChallenge(codeVerifier)

            oauth2SecureStore.tempState = state
            oauth2SecureStore.tempCodeVerifier = codeVerifier

            val clientId = oauth2SecureStore.clientId!!
            val authUrl = oauth2NetworkHandler.buildAuthorizationUrl(
                serverUrl = config.serverUrl,
                clientId = clientId,
                state = state,
                codeChallenge = codeChallenge,
                scope = config.scope,
            )

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
            IntentWithRequestCode(intent, RC_AUTH)
        }
    }

    override fun blockingLogIn(config: OAuth2Config): IntentWithRequestCode {
        return logIn(config).blockingGet()
    }

    override fun handleLogInResponse(serverUrl: String, authorizationCode: String): Single<User> {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        return Single.fromCallable {
            runBlocking {
                val codeVerifier = oauth2SecureStore.tempCodeVerifier
                    ?: throw IllegalStateException("Code verifier not found")

                val clientId = oauth2SecureStore.clientId
                    ?: throw IllegalStateException("Client ID not found")

                val keyId = oauth2SecureStore.keyId
                    ?: throw IllegalStateException("Key ID not found")

                val privateKey = keyStoreManager.getPrivateKey(keyId)
                    ?: throw IllegalStateException("Private key not found")

                val clientAssertion = JWTHelper.createClientAssertion(
                    clientId = clientId,
                    tokenEndpoint = "$normalizedUrl/oauth2/token",
                    privateKey = privateKey,
                    keyId = keyId,
                )

                val result = oauth2NetworkHandler.exchangeCodeForToken(
                    url = normalizedUrl,
                    code = authorizationCode,
                    redirectUri = OAuth2Config.DEFAULT_REDIRECT_URI,
                    clientId = clientId,
                    codeVerifier = codeVerifier,
                    clientAssertion = clientAssertion,
                )

                when (result) {
                    is Result.Success -> {
                        val oauth2State = result.value.copy(keyId = keyId)
                        oauth2SecureStore.clearTemporaryData()
                        logInCall.logInOAuth2(normalizedUrl, oauth2State)
                    }
                    is Result.Failure -> {
                        oauth2SecureStore.clearTemporaryData()
                        throw result.failure
                    }
                }
            }
        }
    }

    override fun blockingHandleLogInResponse(serverUrl: String, authorizationCode: String): User {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        return handleLogInResponse(normalizedUrl, authorizationCode).blockingGet()
    }

    override fun isDeviceRegistered(): Boolean {
        return oauth2SecureStore.isRegistered &&
            oauth2SecureStore.clientId != null &&
            oauth2SecureStore.keyId != null
    }

    override fun isLoggedIn(): Boolean {
        return logInCall.isUserLoggedIn() && oauth2SecureStore.clientId != null
    }

    override fun getClientId(): String? {
        return oauth2SecureStore.clientId
    }

    override fun logOut(): Completable {
        return Completable.fromAction {
            logoutHandler.logOut()
        }
    }

    override fun blockingLogOut() {
        logOut().blockingAwait()
    }

    override fun logOutObservable(): Observable<Unit> {
        return logoutHandler.logOutObservable()
    }

    override fun resetRegistration() {
        val keyId = oauth2SecureStore.keyId
        if (keyId != null) {
            keyStoreManager.deleteKey(keyId)
        }
        oauth2SecureStore.clearRegistration()
        oauth2SecureStore.clearTemporaryData()
    }
}
