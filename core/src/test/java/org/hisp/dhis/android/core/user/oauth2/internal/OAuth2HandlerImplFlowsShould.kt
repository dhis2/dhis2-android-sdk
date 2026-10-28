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

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.AuthenticatedUserStore
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.TestJwtFactory
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature
import java.util.Base64

/**
 * Alternative branches of the enrollment and log in flows of [OAuth2HandlerImpl]: key rotation,
 * scope fallback, PKCE parameters and the contents of the requests sent to the server.
 *
 * It complements [OAuth2HandlerImplShould], kept apart so that neither class grows beyond the
 * detekt `LargeClass` threshold. Unlike that class it uses the real [LogInExceptions], so the
 * assertions can tell the failures apart by their [D2ErrorCode].
 */
@RunWith(JUnit4::class)
class OAuth2HandlerImplFlowsShould {

    private val logInCall: LogInCall = mock()
    private val logoutHandler: OAuth2LogoutHandler = mock()
    private val dcrNetworkHandler: DCRNetworkHandler = mock()
    private val oauth2NetworkHandler: OAuth2NetworkHandler = mock()
    private val keyStoreManager: KeyStoreManager = mock()
    private val oauth2SecureStore = OAuth2SecureStore(InMemorySecureStore())
    private val oauth2StateSecureStore: OAuth2StateSecureStore = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()

    private lateinit var keyPair: KeyPair
    private lateinit var handler: OAuth2HandlerImpl

    @Before
    fun setUp() {
        keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(KEY_SIZE) }.generateKeyPair()
        handler = handlerWith(oauth2SecureStore)
    }

    // region blockingHandleEnrollmentResponse

    @Test
    fun blockingHandleEnrollmentResponse_registers_the_client_with_the_enrollment_details() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        val iat = validJwt()

        enroll(iat = iat)

        verify(keyStoreManager).createJWKS(KEY_ID)
        verifyBlocking(dcrNetworkHandler) {
            registerClient(
                url = NORMALIZED_URL,
                iat = iat,
                clientName = "DHIS2 Android - $DEVICE_ID",
                redirectUri = OAuth2Config.DEFAULT_REDIRECT_URI,
                scope = OAuth2Config.DEFAULT_SCOPE,
                jwks = JWKS,
            )
        }
    }

    @Test
    fun blockingHandleEnrollmentResponse_throws_the_retry_failure_when_the_retry_without_scope_fails_too() {
        seedEnrollment()
        val retryFailure = serverError()
        dcrNetworkHandler.stub {
            onBlocking { registerClient(any(), any(), any(), any(), eq(OAuth2Config.DEFAULT_SCOPE), any()) }
                .doReturn(Result.Failure(httpError(HTTP_BAD_REQUEST, INVALID_SCOPE_BODY)))
            onBlocking { registerClient(any(), any(), any(), any(), isNull(), any()) }
                .doReturn(Result.Failure(retryFailure))
        }

        val error = assertThrows(D2Error::class.java) { enroll() }

        assertThat(error).isSameInstanceAs(retryFailure)
        verifyBlocking(dcrNetworkHandler, times(2)) {
            registerClient(any(), any(), any(), any(), anyOrNull(), any())
        }
        verify(keyStoreManager).deleteKey(KEY_ID)
        assertThat(handler.isDeviceRegistered()).isFalse()
    }

    @Test
    fun blockingHandleEnrollmentResponse_does_not_retry_an_invalid_scope_error_without_bad_request_status() {
        seedEnrollment()
        stubRegistration(Result.Failure(httpError(HTTP_UNAUTHORIZED, INVALID_SCOPE_BODY)))

        assertThrows(D2Error::class.java) { enroll() }

        verifyBlocking(dcrNetworkHandler, times(1)) {
            registerClient(any(), any(), any(), any(), anyOrNull(), any())
        }
        verify(keyStoreManager).deleteKey(KEY_ID)
    }

    @Test
    fun blockingHandleEnrollmentResponse_keeps_the_key_when_the_keystore_reuses_the_previous_key_id() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        oauth2SecureStore.keyId = KEY_ID

        enroll()

        verify(keyStoreManager, never()).deleteKey(any())
        assertThat(oauth2SecureStore.keyId).isEqualTo(KEY_ID)
    }

    @Test
    fun blockingHandleEnrollmentResponse_keeps_the_previous_key_while_the_active_session_signs_with_it() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        oauth2SecureStore.keyId = PREVIOUS_KEY_ID
        activeSessionSigningWith(PREVIOUS_KEY_ID)

        enroll()

        // The refresh token of the current session is still bound to the previous key.
        verify(keyStoreManager, never()).deleteKey(any())
        assertThat(oauth2SecureStore.keyId).isEqualTo(KEY_ID)
    }

    @Test
    fun blockingHandleEnrollmentResponse_deletes_the_previous_key_when_the_active_session_uses_another_key() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        oauth2SecureStore.keyId = PREVIOUS_KEY_ID
        activeSessionSigningWith(OTHER_KEY_ID)

        enroll()

        verify(keyStoreManager).deleteKey(PREVIOUS_KEY_ID)
        verify(keyStoreManager, never()).deleteKey(OTHER_KEY_ID)
        verify(keyStoreManager, never()).deleteKey(KEY_ID)
    }

    @Test
    fun blockingHandleEnrollmentResponse_deletes_the_previous_key_when_the_active_session_is_not_oauth2() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        oauth2SecureStore.keyId = PREVIOUS_KEY_ID
        activeSessionSigningWith(keyId = null)

        enroll()

        verify(keyStoreManager).deleteKey(PREVIOUS_KEY_ID)
    }

    @Test
    fun blockingHandleEnrollmentResponse_keeps_the_previous_registration_when_the_new_one_fails() {
        seedEnrollment()
        stubRegistration(Result.Failure(serverError()))
        oauth2SecureStore.clientId = PREVIOUS_CLIENT_ID
        oauth2SecureStore.keyId = PREVIOUS_KEY_ID
        oauth2SecureStore.isRegistered = true

        assertThrows(D2Error::class.java) { enroll() }

        verify(keyStoreManager).deleteKey(KEY_ID)
        verify(keyStoreManager, never()).deleteKey(PREVIOUS_KEY_ID)
        assertThat(oauth2SecureStore.clientId).isEqualTo(PREVIOUS_CLIENT_ID)
        assertThat(oauth2SecureStore.keyId).isEqualTo(PREVIOUS_KEY_ID)
        assertThat(handler.isDeviceRegistered()).isTrue()
    }

    @Test
    fun blockingHandleEnrollmentResponse_rejects_a_replayed_state() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))
        enroll()

        val error = assertThrows(D2Error::class.java) { enroll() }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_INVALID_STATE)
        verifyBlocking(dcrNetworkHandler, times(1)) {
            registerClient(any(), any(), any(), any(), anyOrNull(), any())
        }
    }

    @Test
    fun blockingHandleEnrollmentResponse_clears_temp_data_and_generates_no_key_when_state_does_not_match() {
        seedEnrollment()
        oauth2SecureStore.tempCodeVerifier = CODE_VERIFIER

        val error = assertThrows(D2Error::class.java) { enroll(state = "forged-state") }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_INVALID_STATE)
        assertThat(oauth2SecureStore.tempState).isNull()
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
        verify(keyStoreManager, never()).generateKeyPair()
    }

    @Test
    fun blockingHandleEnrollmentResponse_consumes_the_state_and_generates_no_key_on_a_malformed_iat() {
        seedEnrollment()

        val error = assertThrows(D2Error::class.java) { enroll(iat = "not-a-jwt") }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_INVALID_IAT)
        assertThat(oauth2SecureStore.tempState).isNull()
        verify(keyStoreManager, never()).generateKeyPair()
    }

    @Test
    fun blockingHandleEnrollmentResponse_accepts_an_iat_without_expiration() {
        seedEnrollment()
        stubRegistration(Result.Success(CLIENT_ID))

        enroll(iat = TestJwtFactory.iatJwtWithoutExpiration(keyPair.private))

        assertThat(handler.isDeviceRegistered()).isTrue()
    }

    // endregion

    // region blockingLogIn

    @Test
    fun blockingLogIn_persists_the_pkce_parameters_and_builds_the_authorization_url() {
        registerDevice()
        whenever(oauth2NetworkHandler.buildAuthorizationUrl(any(), any(), any(), any())).thenReturn(AUTHORIZATION_URL)

        val url = handler.blockingLogIn(OAuth2Config(serverUrl = NORMALIZED_URL, scope = CUSTOM_SCOPE))

        val state = oauth2SecureStore.tempState
        val codeVerifier = oauth2SecureStore.tempCodeVerifier
        assertThat(url).isEqualTo(AUTHORIZATION_URL)
        assertThat(state).isNotNull()
        assertThat(codeVerifier).isNotNull()
        verify(oauth2NetworkHandler).buildAuthorizationUrl(CLIENT_ID, state!!, s256(codeVerifier!!), CUSTOM_SCOPE)
    }

    @Test
    fun blockingLogIn_generates_new_pkce_parameters_on_every_attempt() {
        registerDevice()
        whenever(oauth2NetworkHandler.buildAuthorizationUrl(any(), any(), any(), any())).thenReturn(AUTHORIZATION_URL)
        val config = OAuth2Config(serverUrl = NORMALIZED_URL)

        handler.blockingLogIn(config)
        val firstState = oauth2SecureStore.tempState
        val firstCodeVerifier = oauth2SecureStore.tempCodeVerifier
        handler.blockingLogIn(config)

        assertThat(oauth2SecureStore.tempState).isNotEqualTo(firstState)
        assertThat(oauth2SecureStore.tempCodeVerifier).isNotEqualTo(firstCodeVerifier)
    }

    @Test
    fun blockingLogIn_leaves_no_pending_authorization_when_the_key_id_is_missing() {
        oauth2SecureStore.isRegistered = true
        oauth2SecureStore.clientId = CLIENT_ID

        val error = assertThrows(D2Error::class.java) {
            handler.blockingLogIn(OAuth2Config(serverUrl = NORMALIZED_URL))
        }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_DEVICE_NOT_REGISTERED)
        assertThat(oauth2SecureStore.tempState).isNull()
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
        verify(oauth2NetworkHandler, never()).buildAuthorizationUrl(any(), any(), any(), any())
    }

    /** Defensive branch: only reachable when the registration is cleared right after it is checked. */
    @Test
    fun blockingLogIn_throws_when_the_client_id_vanishes_after_the_registration_check() {
        val racingStore: OAuth2SecureStore = mock()
        whenever(racingStore.isRegistered).thenReturn(true)
        whenever(racingStore.keyId).thenReturn(KEY_ID)
        whenever(racingStore.clientId).thenReturn(CLIENT_ID, null)

        val error = assertThrows(D2Error::class.java) {
            handlerWith(racingStore).blockingLogIn(OAuth2Config(serverUrl = NORMALIZED_URL))
        }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_INCOMPLETE_REGISTRATION)
        verify(oauth2NetworkHandler, never()).buildAuthorizationUrl(any(), any(), any(), any())
    }

    // endregion

    // region blockingHandleLogInResponse

    @Test
    fun blockingHandleLogInResponse_exchanges_the_code_with_the_registration_and_pkce_details() {
        seedLogInResponse()
        stubExchange(Result.Failure(serverError()))

        assertThrows(D2Error::class.java) { respond() }

        val assertionCaptor = argumentCaptor<String>()
        verifyBlocking(oauth2NetworkHandler) {
            getTokenEndpoint(NORMALIZED_URL, OAuth2NetworkHandler.DEFAULT_OAUTH_CONFIG_PATH)
        }
        verifyBlocking(oauth2NetworkHandler) {
            exchangeCodeForToken(
                eq(TOKEN_ENDPOINT),
                eq(AUTH_CODE),
                eq(OAuth2Config.DEFAULT_REDIRECT_URI),
                eq(CLIENT_ID),
                eq(CODE_VERIFIER),
                assertionCaptor.capture(),
            )
        }
        val (header, claims, signature) = assertionCaptor.firstValue.split(".")
        assertThat(jsonMember(header, "kid")).isEqualTo(KEY_ID)
        assertThat(jsonMember(claims, "iss")).isEqualTo(CLIENT_ID)
        assertThat(isSignedBy(keyPair.public, "$header.$claims", signature)).isTrue()
    }

    @Test
    fun blockingHandleLogInResponse_forwards_the_existing_username_and_stores_the_state_for_that_user() {
        seedLogInResponse()
        stubExchange(Result.Success(exchangedState()))
        stubLogIn(username = USERNAME)

        respond(existingUsername = USERNAME)

        verifyBlocking(logInCall) { logInOAuth2(eq(USERNAME), eq(NORMALIZED_URL), any()) }
        verify(oauth2StateSecureStore).set(NORMALIZED_URL, USERNAME, exchangedState().copy(keyId = KEY_ID))
    }

    @Test
    fun blockingHandleLogInResponse_rethrows_the_exchange_failure_without_logging_in() {
        seedLogInResponse()
        val failure = serverError()
        stubExchange(Result.Failure(failure))

        val error = assertThrows(D2Error::class.java) { respond() }

        assertThat(error).isSameInstanceAs(failure)
        verifyBlocking(logInCall, never()) { logInOAuth2(anyOrNull(), any(), any()) }
        verifyNoInteractions(oauth2StateSecureStore)
    }

    @Test
    fun blockingHandleLogInResponse_does_not_store_the_state_when_the_user_has_no_username() {
        seedLogInResponse()
        stubExchange(Result.Success(exchangedState()))
        stubLogIn(username = null)

        val error = assertThrows(D2Error::class.java) { respond() }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
        verifyNoInteractions(oauth2StateSecureStore)
    }

    @Test
    fun blockingHandleLogInResponse_propagates_log_in_errors_without_storing_the_state() {
        seedLogInResponse()
        stubExchange(Result.Success(exchangedState()))
        val logInError = serverError()
        logInCall.stub {
            onBlocking { logInOAuth2(anyOrNull(), any(), any()) }.doAnswer { throw logInError }
        }

        val error = assertThrows(D2Error::class.java) { respond() }

        assertThat(error).isSameInstanceAs(logInError)
        verifyNoInteractions(oauth2StateSecureStore)
    }

    @Test
    fun blockingHandleLogInResponse_clears_temp_data_and_skips_the_exchange_when_state_does_not_match() {
        seedLogInResponse()

        val error = assertThrows(D2Error::class.java) { respond(state = "forged-state") }

        assertThat(error.errorCode).isEqualTo(D2ErrorCode.OAUTH2_INVALID_STATE)
        assertThat(oauth2SecureStore.tempState).isNull()
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
        verifyBlocking(oauth2NetworkHandler, never()) {
            exchangeCodeForToken(any(), any(), any(), any(), any(), any())
        }
    }

    // endregion

    // region helpers

    private fun handlerWith(store: OAuth2SecureStore) = OAuth2HandlerImpl(
        logInCall,
        logoutHandler,
        dcrNetworkHandler,
        oauth2NetworkHandler,
        keyStoreManager,
        store,
        oauth2StateSecureStore,
        credentialsSecureStore,
        authenticatedUserStore,
        LogInExceptions(credentialsSecureStore),
    )

    private fun seedEnrollment() {
        oauth2SecureStore.tempState = STATE
        whenever(keyStoreManager.generateKeyPair()).thenReturn(KEY_ID)
        whenever(keyStoreManager.createJWKS(KEY_ID)).thenReturn(JWKS)
        whenever(dcrNetworkHandler.getDeviceId()).thenReturn(DEVICE_ID)
    }

    private fun stubRegistration(result: Result<String, D2Error>) {
        dcrNetworkHandler.stub {
            onBlocking { registerClient(any(), any(), any(), any(), anyOrNull(), any()) }.doReturn(result)
        }
    }

    private fun enroll(iat: String = validJwt(), state: String = STATE) {
        handler.blockingHandleEnrollmentResponse("HTTPS://Server.com/api/", iat, state)
    }

    private fun activeSessionSigningWith(keyId: String?) {
        val oauth2State = keyId?.let { exchangedState().copy(keyId = it) }
        whenever(credentialsSecureStore.get()).thenReturn(
            Credentials(
                username = USERNAME,
                serverUrl = NORMALIZED_URL,
                password = null,
                pin = null,
                openIDConnectState = null,
                oauth2State = oauth2State,
            ),
        )
    }

    private fun registerDevice() {
        oauth2SecureStore.isRegistered = true
        oauth2SecureStore.clientId = CLIENT_ID
        oauth2SecureStore.keyId = KEY_ID
    }

    private fun seedLogInResponse() {
        registerDevice()
        oauth2SecureStore.tempState = STATE
        oauth2SecureStore.tempCodeVerifier = CODE_VERIFIER
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(keyPair.private)
        oauth2NetworkHandler.stub {
            onBlocking { getTokenEndpoint(any(), any()) }.doReturn(TOKEN_ENDPOINT)
        }
    }

    private fun stubExchange(result: Result<OAuth2State, D2Error>) {
        oauth2NetworkHandler.stub {
            onBlocking { exchangeCodeForToken(any(), any(), any(), any(), any(), any()) }.doReturn(result)
        }
    }

    private fun stubLogIn(username: String?) {
        val user: User = mock()
        whenever(user.username()).thenReturn(username)
        logInCall.stub {
            onBlocking { logInOAuth2(anyOrNull(), any(), any()) }.doReturn(user)
        }
    }

    private fun respond(existingUsername: String? = null, state: String = STATE): User =
        handler.blockingHandleLogInResponse(existingUsername, "HTTPS://Server.com/", AUTH_CODE, state)

    private fun exchangedState(): OAuth2State =
        OAuth2State(
            clientId = CLIENT_ID,
            keyId = "",
            accessToken = "access",
            refreshToken = "refresh",
            expiresAt = EXPIRES_AT,
            scope = "openid",
            tokenEndpoint = TOKEN_ENDPOINT,
        )

    /** RFC 7636 `S256` code challenge, computed with the JDK rather than with the production codec. */
    private fun s256(codeVerifier: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(
            MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray(Charsets.US_ASCII)),
        )

    private fun jsonMember(segment: String, name: String): String {
        val json = String(Base64.getUrlDecoder().decode(segment), Charsets.UTF_8)
        return KotlinxJsonParser.instance.parseToJsonElement(json).jsonObject[name]!!.jsonPrimitive.content
    }

    private fun isSignedBy(publicKey: PublicKey, signingInput: String, signature: String): Boolean =
        Signature.getInstance("SHA256withRSA").run {
            initVerify(publicKey)
            update(signingInput.toByteArray(Charsets.US_ASCII))
            verify(Base64.getUrlDecoder().decode(signature))
        }

    private fun validJwt(): String =
        TestJwtFactory.iatJwt(keyPair.private, TestJwtFactory.nowSeconds() + JWT_TTL_SECONDS)

    private fun serverError(): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription("test failure")
            .errorComponent(D2ErrorComponent.Server)
            .build()

    private fun httpError(status: Int, body: String): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            .httpErrorCode(status)
            .errorDescription("API call failed, server message: $body")
            .errorComponent(D2ErrorComponent.Server)
            .build()

    // endregion

    companion object {
        private const val NORMALIZED_URL = "https://server.com"
        private const val AUTHORIZATION_URL = "https://server.com/oauth2/authorize"
        private const val TOKEN_ENDPOINT = "https://server.com/oauth2/token"
        private const val CLIENT_ID = "client-1"
        private const val PREVIOUS_CLIENT_ID = "client-0"
        private const val KEY_ID = "key-1"
        private const val PREVIOUS_KEY_ID = "key-0"
        private const val OTHER_KEY_ID = "key-other"
        private const val DEVICE_ID = "device-1"
        private const val JWKS = "{\"keys\":[]}"
        private const val STATE = "state-1"
        private const val CODE_VERIFIER = "verifier"
        private const val AUTH_CODE = "auth-code"
        private const val USERNAME = "user-1"
        private const val CUSTOM_SCOPE = "openid custom"
        private const val INVALID_SCOPE_BODY = "{\"error\":\"invalid_scope\"}"
        private const val HTTP_BAD_REQUEST = 400
        private const val HTTP_UNAUTHORIZED = 401
        private const val EXPIRES_AT = 1_700_000_000L
        private const val KEY_SIZE = 2048
        private const val JWT_TTL_SECONDS = 5L * 60L
    }
}
