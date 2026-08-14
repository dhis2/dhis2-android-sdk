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

import io.reactivex.Observable
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.configuration.internal.ServerUrlNormalizer
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.AuthenticatedUserStore
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.OAuth2Handler
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.JWTHelper
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.koin.core.annotation.Singleton

@Suppress("LongParameterList", "TooManyFunctions")
@Singleton
internal class OAuth2HandlerImpl(
    private val logInCall: LogInCall,
    private val logoutHandler: OAuth2LogoutHandler,
    private val dcrNetworkHandler: DCRNetworkHandler,
    private val oauth2NetworkHandler: OAuth2NetworkHandler,
    private val keyStoreManager: KeyStoreManager,
    private val oauth2SecureStore: OAuth2SecureStore,
    private val oauth2StateSecureStore: OAuth2StateSecureStore,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val authenticatedUserStore: AuthenticatedUserStore,
    private val logInExceptions: LogInExceptions,
) : OAuth2Handler {

    private suspend fun buildEnrollmentUrlInternal(serverUrl: String): String {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        val state = JWTHelper.generateState()
        oauth2SecureStore.tempState = state
        return dcrNetworkHandler.buildEnrollmentUrl(normalizedUrl, state)
    }

    override fun blockingBuildEnrollmentUrl(serverUrl: String): String {
        return runBlocking { buildEnrollmentUrlInternal(serverUrl) }
    }

    private suspend fun handleEnrollmentResponseInternal(serverUrl: String, iat: String, state: String) {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        verifyState(state)
        if (!JWTHelper.isUnexpired(iat)) {
            throw logInExceptions.invalidOAuth2IatError()
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

    override fun blockingHandleEnrollmentResponse(serverUrl: String, iat: String, state: String) {
        runBlocking { handleEnrollmentResponseInternal(serverUrl, iat, state) }
    }

    /**
     * Checks the `state` returned by the server against the one generated when the URL the user was
     * sent to was built. Without it the redirect could be forged by a third party.
     *
     * Fails with a [D2Error]
     */
    @Throws(D2Error::class)
    private fun verifyState(state: String) {
        val expectedState = oauth2SecureStore.tempState
        if (expectedState == null || expectedState != state) {
            oauth2SecureStore.clearTemporaryData()
            throw logInExceptions.invalidOAuth2StateError()
        }
        oauth2SecureStore.tempState = null
    }

    override fun blockingBuildLogoutUrl(config: OAuth2Config): String {
        return runBlocking { buildLogoutUrlInternal(config) }
    }

    private fun buildLogoutUrlInternal(config: OAuth2Config): String {
        val normalizedUrl = ServerUrlNormalizer.normalize(config.serverUrl)
        return oauth2NetworkHandler.buildLogoutUrl(config.copy(serverUrl = normalizedUrl))
    }

    private suspend fun logInInternal(config: OAuth2Config): String {
        if (!isDeviceRegistered()) {
            throw logInExceptions.oauth2DeviceNotRegisteredError()
        }

        val state = JWTHelper.generateState()
        val codeVerifier = JWTHelper.generateCodeVerifier()
        val codeChallenge = JWTHelper.generateCodeChallenge(codeVerifier)

        oauth2SecureStore.tempState = state
        oauth2SecureStore.tempCodeVerifier = codeVerifier

        val clientId = oauth2SecureStore.clientId
            ?: throw logInExceptions.incompleteOAuth2RegistrationError("Client ID")
        return oauth2NetworkHandler.buildAuthorizationUrl(
            serverUrl = config.serverUrl,
            clientId = clientId,
            state = state,
            codeChallenge = codeChallenge,
            scope = config.scope,
        )
    }

    override fun blockingLogIn(config: OAuth2Config): String {
        return runBlocking { logInInternal(config) }
    }

    @Suppress("ThrowsCount")
    private suspend fun handleLogInResponseInternal(
        serverUrl: String,
        authorizationCode: String,
        state: String,
    ): User {
        val normalizedUrl = ServerUrlNormalizer.normalize(serverUrl)
        verifyState(state)

        val codeVerifier = oauth2SecureStore.tempCodeVerifier
            ?: throw logInExceptions.incompleteOAuth2RegistrationError("Code verifier")

        val clientId = oauth2SecureStore.clientId
            ?: throw logInExceptions.incompleteOAuth2RegistrationError("Client ID")

        val keyId = oauth2SecureStore.keyId
            ?: throw logInExceptions.incompleteOAuth2RegistrationError("Key ID")

        val privateKey = keyStoreManager.getPrivateKey(keyId)
            ?: throw logInExceptions.incompleteOAuth2RegistrationError("Private key")

        // Resolved before signing so that the assertion audience matches the endpoint the token
        // request is posted to.
        val tokenEndpoint = oauth2NetworkHandler.getTokenEndpoint(normalizedUrl)

        val clientAssertion = JWTHelper.createClientAssertion(
            clientId = clientId,
            tokenEndpoint = tokenEndpoint,
            privateKey = privateKey,
            keyId = keyId,
        )

        val result = oauth2NetworkHandler.exchangeCodeForToken(
            tokenEndpoint = tokenEndpoint,
            code = authorizationCode,
            redirectUri = OAuth2Config.DEFAULT_REDIRECT_URI,
            clientId = clientId,
            codeVerifier = codeVerifier,
            clientAssertion = clientAssertion,
        )

        return when (result) {
            is Result.Success -> {
                val oauth2State = result.value.copy(keyId = keyId)
                val user = logInCall.logInOAuth2(normalizedUrl, oauth2State)
                val username = user.username()
                    ?: throw logInExceptions.oauth2ResponseWithoutUsernameError()
                oauth2StateSecureStore.set(normalizedUrl, username, oauth2State)
                oauth2SecureStore.clearAll()
                user
            }
            is Result.Failure -> {
                oauth2SecureStore.clearTemporaryData()
                throw result.failure
            }
        }
    }

    override fun blockingHandleLogInResponse(serverUrl: String, authorizationCode: String, state: String): User {
        return runBlocking { handleLogInResponseInternal(serverUrl, authorizationCode, state) }
    }

    override fun isDeviceRegistered(): Boolean {
        return oauth2SecureStore.isRegistered &&
            oauth2SecureStore.clientId != null &&
            oauth2SecureStore.keyId != null
    }

    override fun isLoggedIn(): Boolean {
        return logInCall.isUserLoggedIn() && credentialsSecureStore.get()?.oauth2State != null
    }

    override fun getClientId(): String? {
        return credentialsSecureStore.get()?.oauth2State?.clientId
    }

    override suspend fun suspendSetPin(pin: String): Result<Unit, D2Error> {
        val credentials = credentialsSecureStore.get()
        return when {
            credentials == null -> Result.Failure(logInExceptions.noActiveSessionError())
            credentials.oauth2State == null -> Result.Failure(logInExceptions.pinRequiresTokenBasedAccountError())
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

    override fun blockingLogOut() {
        logoutHandler.logOut()
    }

    override suspend fun suspendLogOut() {
        logoutHandler.logOut()
    }

    @Deprecated(message = "Use rxLogOutObservable instead", ReplaceWith("rxLogOutObservable()"))
    override fun logOutObservable(): Observable<Unit> {
        return logoutHandler.logOutObservable()
    }

    override fun rxLogOutObservable(): Observable<Unit> {
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
