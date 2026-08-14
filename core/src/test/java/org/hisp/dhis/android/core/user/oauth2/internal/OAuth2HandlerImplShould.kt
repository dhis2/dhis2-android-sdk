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
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.AuthenticatedUserStore
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2Config
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.jwt.TestJwtFactory
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

@RunWith(JUnit4::class)
class OAuth2HandlerImplShould {

    private val logInCall: LogInCall = mock()
    private val logoutHandler: OAuth2LogoutHandler = mock()
    private val dcrNetworkHandler: DCRNetworkHandler = mock()
    private val oauth2NetworkHandler: OAuth2NetworkHandler = mock()
    private val keyStoreManager: KeyStoreManager = mock()
    private val oauth2SecureStore = OAuth2SecureStore(InMemorySecureStore())
    private val oauth2StateSecureStore: OAuth2StateSecureStore = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()
    private val logInExceptions: LogInExceptions = mock()

    private lateinit var keyPair: KeyPair
    private lateinit var handler: OAuth2HandlerImpl

    @Before
    fun setUp() {
        keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(KEY_SIZE) }.generateKeyPair()
        whenever(logInExceptions.noActiveSessionError()).thenReturn(sdkError("no session"))
        whenever(logInExceptions.pinRequiresTokenBasedAccountError()).thenReturn(sdkError("not oauth2"))
        whenever(logInExceptions.noAuthenticatedUserPersistedError()).thenReturn(sdkError("no user"))
        whenever(logInExceptions.incorrectPinError()).thenReturn(sdkError("bad pin"))
        whenever(logInExceptions.invalidOAuth2StateError()).thenReturn(sdkError("invalid state"))
        whenever(logInExceptions.invalidOAuth2IatError()).thenReturn(sdkError("invalid iat"))
        whenever(logInExceptions.oauth2DeviceNotRegisteredError()).thenReturn(sdkError("not registered"))
        whenever(logInExceptions.incompleteOAuth2RegistrationError(any()))
            .thenReturn(sdkError("incomplete registration"))
        whenever(logInExceptions.oauth2ResponseWithoutUsernameError()).thenReturn(sdkError("no username"))
        handler = OAuth2HandlerImpl(
            logInCall,
            logoutHandler,
            dcrNetworkHandler,
            oauth2NetworkHandler,
            keyStoreManager,
            oauth2SecureStore,
            oauth2StateSecureStore,
            credentialsSecureStore,
            authenticatedUserStore,
            logInExceptions,
        )
    }

    // region blockingBuildEnrollmentUrl

    @Test
    fun blockingBuildEnrollmentUrl_persists_temp_state_and_returns_url() {
        dcrNetworkHandler.stub {
            onBlocking { buildEnrollmentUrl(any(), any()) }.doReturn(ENROLL_URL)
        }

        val result = handler.blockingBuildEnrollmentUrl("HTTPS://Server.com/")

        assertThat(result).isEqualTo(ENROLL_URL)
        assertThat(oauth2SecureStore.tempState).isNotNull()
        verify(dcrNetworkHandler).buildEnrollmentUrl(eq(NORMALIZED_URL), eq(oauth2SecureStore.tempState!!))
    }

    // endregion

    // region blockingHandleEnrollmentResponse

    @Test
    fun blockingHandleEnrollmentResponse_persists_registration_and_clears_temp_on_success() {
        seedRegistrationFlowMocks()
        dcrNetworkHandler.stub {
            onBlocking { registerClient(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Success(CLIENT_ID))
        }
        oauth2SecureStore.tempState = STATE

        handler.blockingHandleEnrollmentResponse("HTTPS://Server.com/", validJwt(), STATE)

        assertThat(oauth2SecureStore.clientId).isEqualTo(CLIENT_ID)
        assertThat(oauth2SecureStore.keyId).isEqualTo(KEY_ID)
        assertThat(oauth2SecureStore.serverUrl).isEqualTo(NORMALIZED_URL)
        assertThat(oauth2SecureStore.isRegistered).isTrue()
        assertThat(oauth2SecureStore.registrationDate).isGreaterThan(0L)
        assertThat(oauth2SecureStore.tempState).isNull()
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
    }

    @Test
    fun blockingHandleEnrollmentResponse_deletes_keypair_and_throws_on_failure() {
        seedRegistrationFlowMocks()
        dcrNetworkHandler.stub {
            onBlocking { registerClient(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Failure(serverError()))
        }
        oauth2SecureStore.tempState = STATE

        runCatching { handler.blockingHandleEnrollmentResponse("https://server.com", validJwt(), STATE) }
            .also { assertThat(it.isFailure).isTrue() }

        verify(keyStoreManager).deleteKey(KEY_ID)
        assertThat(oauth2SecureStore.clientId).isNull()
        assertThat(oauth2SecureStore.isRegistered).isFalse()
    }

    @Test(expected = D2Error::class)
    fun blockingHandleEnrollmentResponse_throws_on_expired_iat() {
        oauth2SecureStore.tempState = STATE

        handler.blockingHandleEnrollmentResponse("https://server.com", expiredJwt(), STATE)
    }

    @Test(expected = D2Error::class)
    fun blockingHandleEnrollmentResponse_throws_when_state_does_not_match() {
        oauth2SecureStore.tempState = STATE

        handler.blockingHandleEnrollmentResponse("https://server.com", validJwt(), "forged-state")
    }

    @Test(expected = D2Error::class)
    fun blockingHandleEnrollmentResponse_throws_when_no_state_was_generated() {
        handler.blockingHandleEnrollmentResponse("https://server.com", validJwt(), STATE)
    }

    // endregion

    // region blockingBuildLogoutUrl

    @Test
    fun blockingBuildLogoutUrl_delegates_config_to_network_handler_and_returns_url() {
        val config = OAuth2Config(serverUrl = NORMALIZED_URL)
        whenever(oauth2NetworkHandler.buildLogoutUrl(config)).thenReturn(LOGOUT_URL)

        val result = handler.blockingBuildLogoutUrl(config)

        assertThat(result).isEqualTo(LOGOUT_URL)
        verify(oauth2NetworkHandler).buildLogoutUrl(config)
    }

    @Test
    fun blockingBuildLogoutUrl_normalizes_the_server_url_before_delegating() {
        val config = OAuth2Config(serverUrl = "HTTPS://Server.com/")
        val normalizedConfig = config.copy(serverUrl = NORMALIZED_URL)
        whenever(oauth2NetworkHandler.buildLogoutUrl(normalizedConfig)).thenReturn(LOGOUT_URL)

        val result = handler.blockingBuildLogoutUrl(config)

        assertThat(result).isEqualTo(LOGOUT_URL)
        verify(oauth2NetworkHandler).buildLogoutUrl(normalizedConfig)
    }

    // endregion

    // region blockingHandleLogInResponse

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_state_does_not_match() {
        seedSuccessfulLogInPrerequisites()

        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, "forged-state")
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_no_state_was_generated() {
        oauth2SecureStore.tempCodeVerifier = CODE_VERIFIER
        oauth2SecureStore.clientId = CLIENT_ID
        oauth2SecureStore.keyId = KEY_ID

        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_temp_code_verifier_missing() {
        oauth2SecureStore.tempState = STATE

        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_client_id_missing() {
        oauth2SecureStore.tempState = STATE
        oauth2SecureStore.tempCodeVerifier = "verifier"
        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_key_id_missing() {
        oauth2SecureStore.tempState = STATE
        oauth2SecureStore.tempCodeVerifier = "verifier"
        oauth2SecureStore.clientId = CLIENT_ID
        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_private_key_missing() {
        seedSuccessfulLogInPrerequisites()
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(null)
        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    @Test
    fun blockingHandleLogInResponse_clears_temp_data_and_throws_on_exchange_failure() {
        seedSuccessfulLogInPrerequisites()
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(keyPair.private)
        oauth2NetworkHandler.stub {
            onBlocking { exchangeCodeForToken(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Failure(serverError()))
        }

        runCatching { handler.blockingHandleLogInResponse("HTTPS://Server.com/", AUTH_CODE, STATE) }
            .also { assertThat(it.isFailure).isTrue() }

        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
        assertThat(oauth2SecureStore.tempState).isNull()
    }

    /**
     * The assertion audience is derived from the token endpoint advertised by the server, so it
     * cannot diverge from the endpoint the request is actually posted to.
     */
    @Test
    fun blockingHandleLogInResponse_signs_the_assertion_for_the_discovered_token_endpoint() {
        seedSuccessfulLogInPrerequisites()
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(keyPair.private)
        oauth2NetworkHandler.stub {
            onBlocking { getTokenEndpoint(any(), any()) }.doReturn(DISCOVERED_TOKEN_ENDPOINT)
            onBlocking { exchangeCodeForToken(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Failure(serverError()))
        }

        runCatching { handler.blockingHandleLogInResponse("HTTPS://Server.com/", AUTH_CODE, STATE) }

        val endpointCaptor = argumentCaptor<String>()
        val assertionCaptor = argumentCaptor<String>()
        verifyBlocking(oauth2NetworkHandler) {
            exchangeCodeForToken(
                endpointCaptor.capture(),
                any(),
                any(),
                any(),
                any(),
                assertionCaptor.capture(),
            )
        }

        assertThat(endpointCaptor.firstValue).isEqualTo(DISCOVERED_TOKEN_ENDPOINT)
        assertThat(audienceOf(assertionCaptor.firstValue)).isEqualTo("https://auth.server.com/oidc/")
    }

    @Test
    fun blockingHandleLogInResponse_persists_state_and_delegates_to_logInCall_on_success() {
        seedSuccessfulLogInPrerequisites()
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(keyPair.private)
        val exchangedState = exchangedState()
        oauth2NetworkHandler.stub {
            onBlocking { exchangeCodeForToken(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Success(exchangedState))
        }
        val expectedUser: User = mock()
        whenever(expectedUser.username()).thenReturn(USERNAME)
        logInCall.stub {
            onBlocking { logInOAuth2(any(), any()) }.doReturn(expectedUser)
        }

        val user = handler.blockingHandleLogInResponse("HTTPS://Server.com/", AUTH_CODE, STATE)

        assertThat(user).isSameInstanceAs(expectedUser)
        // logInOAuth2 called with normalized server url and the state with our keyId
        val urlCaptor = argumentCaptor<String>()
        val stateCaptor = argumentCaptor<OAuth2State>()
        verifyBlocking(logInCall) { logInOAuth2(urlCaptor.capture(), stateCaptor.capture()) }
        assertThat(urlCaptor.firstValue).isEqualTo(NORMALIZED_URL)
        assertThat(stateCaptor.firstValue.keyId).isEqualTo(KEY_ID)
        assertThat(stateCaptor.firstValue.accessToken).isEqualTo(exchangedState.accessToken)
        // Temp data cleared after success.
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
        assertThat(oauth2SecureStore.tempState).isNull()
    }

    @Test(expected = D2Error::class)
    fun blockingHandleLogInResponse_throws_when_the_authenticated_user_has_no_username() {
        seedSuccessfulLogInPrerequisites()
        whenever(keyStoreManager.getPrivateKey(KEY_ID)).thenReturn(keyPair.private)
        oauth2NetworkHandler.stub {
            onBlocking { exchangeCodeForToken(any(), any(), any(), any(), any(), any()) }
                .doReturn(Result.Success(exchangedState()))
        }
        val userWithoutUsername: User = mock()
        whenever(userWithoutUsername.username()).thenReturn(null)
        logInCall.stub {
            onBlocking { logInOAuth2(any(), any()) }.doReturn(userWithoutUsername)
        }

        handler.blockingHandleLogInResponse("https://server.com", AUTH_CODE, STATE)
    }

    // endregion

    // region blockingLogIn

    @Test(expected = D2Error::class)
    fun blockingLogIn_throws_when_the_device_is_not_registered() {
        handler.blockingLogIn(OAuth2Config(serverUrl = NORMALIZED_URL))
    }

    // endregion

    // region predicates

    @Test
    fun isDeviceRegistered_true_only_when_all_fields_present() {
        assertThat(handler.isDeviceRegistered()).isFalse()

        oauth2SecureStore.clientId = CLIENT_ID
        oauth2SecureStore.keyId = KEY_ID
        assertThat(handler.isDeviceRegistered()).isFalse() // isRegistered still false

        oauth2SecureStore.isRegistered = true
        assertThat(handler.isDeviceRegistered()).isTrue()

        oauth2SecureStore.clientId = null
        assertThat(handler.isDeviceRegistered()).isFalse()
    }

    @Test
    fun isLoggedIn_requires_both_user_logged_in_and_oauth2_state_present() {
        whenever(logInCall.isUserLoggedIn()).thenReturn(false)
        assertThat(handler.isLoggedIn()).isFalse()

        whenever(logInCall.isUserLoggedIn()).thenReturn(true)
        whenever(credentialsSecureStore.get()).thenReturn(null)
        assertThat(handler.isLoggedIn()).isFalse()

        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(state = null))
        assertThat(handler.isLoggedIn()).isFalse()

        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(state = sampleOAuth2State()))
        assertThat(handler.isLoggedIn()).isTrue()
    }

    @Test
    fun getClientId_reflects_oauth2_state_in_credentials() {
        whenever(credentialsSecureStore.get()).thenReturn(null)
        assertThat(handler.getClientId()).isNull()

        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(state = null))
        assertThat(handler.getClientId()).isNull()

        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(state = sampleOAuth2State()))
        assertThat(handler.getClientId()).isEqualTo(CLIENT_ID)
    }

    // endregion

    // region logout / observables

    @Test
    fun blockingLogOut_delegates_to_logout_handler() {
        handler.blockingLogOut()
        verify(logoutHandler).logOut()
    }

    @Test
    fun suspendLogOut_delegates_to_logout_handler() = kotlinx.coroutines.test.runTest {
        handler.suspendLogOut()
        verify(logoutHandler).logOut()
    }

    @Test
    fun rxLogOutObservable_delegates_to_logout_handler() {
        handler.rxLogOutObservable()
        verify(logoutHandler).logOutObservable()
    }

    @Test
    @Suppress("DEPRECATION")
    fun logOutObservable_delegates_to_logout_handler() {
        handler.logOutObservable()
        verify(logoutHandler).logOutObservable()
    }

    // endregion

    // region setPin / changePin

    @Test
    fun setPin_persists_pin_as_credentials_pin_and_updates_authenticated_user_hash() {
        val credentials = credentialsWithOAuth2(sampleOAuth2State())
        whenever(credentialsSecureStore.get()).thenReturn(credentials)
        val existing = AuthenticatedUser.builder().user("uid").hash(null).build()
        authenticatedUserStore.stub {
            onBlocking { selectFirst() }.doReturn(existing)
        }

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val credentialsCaptor = argumentCaptor<Credentials>()
        verify(credentialsSecureStore).set(credentialsCaptor.capture())
        assertThat(credentialsCaptor.firstValue.pin).isEqualTo(PIN)
        assertThat(credentialsCaptor.firstValue.oauth2State).isNotNull()
        val userCaptor = argumentCaptor<AuthenticatedUser>()
        verifyBlocking(authenticatedUserStore) { updateOrInsertWhere(userCaptor.capture()) }
        assertThat(userCaptor.firstValue.hash()).isEqualTo(UserHelper.md5(USERNAME, PIN))
    }

    @Test
    fun setPin_fails_when_not_logged_in() {
        whenever(credentialsSecureStore.get()).thenReturn(null)

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun setPin_fails_when_account_is_not_oauth2() {
        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(state = null))

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun setPin_fails_when_no_authenticated_user_persisted() {
        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOAuth2(sampleOAuth2State()))
        authenticatedUserStore.stub {
            onBlocking { selectFirst() }.doReturn(null)
        }

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
    }

    @Test
    fun changePin_replaces_pin_when_current_matches() {
        val current = credentialsWithOAuth2(sampleOAuth2State()).copy(pin = PIN)
        whenever(credentialsSecureStore.get()).thenReturn(current)
        val existing = AuthenticatedUser.builder().user("uid").hash(current.getHash()).build()
        authenticatedUserStore.stub {
            onBlocking { selectFirst() }.doReturn(existing)
        }

        val result = handler.blockingChangePin(PIN, NEW_PIN)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val credentialsCaptor = argumentCaptor<Credentials>()
        verify(credentialsSecureStore).set(credentialsCaptor.capture())
        assertThat(credentialsCaptor.firstValue.pin).isEqualTo(NEW_PIN)
    }

    @Test
    fun changePin_fails_when_current_pin_does_not_match() {
        val current = credentialsWithOAuth2(sampleOAuth2State()).copy(pin = PIN)
        whenever(credentialsSecureStore.get()).thenReturn(current)

        val result = handler.blockingChangePin("wrong", NEW_PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
        verify(credentialsSecureStore, never()).set(any())
    }

    // endregion

    // region resetRegistration

    @Test
    fun resetRegistration_deletes_keypair_and_clears_store() {
        oauth2SecureStore.clientId = CLIENT_ID
        oauth2SecureStore.keyId = KEY_ID
        oauth2SecureStore.serverUrl = NORMALIZED_URL
        oauth2SecureStore.isRegistered = true
        oauth2SecureStore.registrationDate = 1_700_000_000L
        oauth2SecureStore.tempState = "s"
        oauth2SecureStore.tempCodeVerifier = "v"

        handler.resetRegistration()

        verify(keyStoreManager).deleteKey(KEY_ID)
        assertThat(oauth2SecureStore.clientId).isNull()
        assertThat(oauth2SecureStore.keyId).isNull()
        assertThat(oauth2SecureStore.serverUrl).isNull()
        assertThat(oauth2SecureStore.isRegistered).isFalse()
        assertThat(oauth2SecureStore.registrationDate).isEqualTo(0L)
        assertThat(oauth2SecureStore.tempState).isNull()
        assertThat(oauth2SecureStore.tempCodeVerifier).isNull()
    }

    @Test
    fun resetRegistration_skips_keypair_deletion_when_no_key_id() {
        handler.resetRegistration()

        verify(keyStoreManager, never()).deleteKey(any())
    }

    // endregion

    // region helpers

    private fun seedRegistrationFlowMocks() {
        whenever(keyStoreManager.generateKeyPair()).thenReturn(KEY_ID)
        whenever(keyStoreManager.createJWKS(KEY_ID)).thenReturn(JWKS)
        dcrNetworkHandler.stub {
            onBlocking { getDeviceId() }.doReturn(DEVICE_ID)
        }
    }

    private fun seedSuccessfulLogInPrerequisites() {
        oauth2SecureStore.tempCodeVerifier = CODE_VERIFIER
        oauth2SecureStore.tempState = STATE
        oauth2SecureStore.clientId = CLIENT_ID
        oauth2SecureStore.keyId = KEY_ID
        oauth2NetworkHandler.stub {
            onBlocking { getTokenEndpoint(any(), any()) }.doReturn(TOKEN_ENDPOINT)
        }
    }

    private fun exchangedState(): OAuth2State =
        OAuth2State(
            clientId = CLIENT_ID,
            keyId = "", // placeholder; will be overwritten by handler.copy(keyId = KEY_ID)
            accessToken = "access",
            refreshToken = "refresh",
            expiresAt = 1_700_000_000L,
            scope = "openid",
            tokenEndpoint = "https://server.com/oauth2/token",
        )

    private fun sampleOAuth2State(): OAuth2State =
        OAuth2State(
            clientId = CLIENT_ID,
            keyId = KEY_ID,
            accessToken = "access",
            refreshToken = "refresh",
            expiresAt = 1_700_000_000L,
            scope = null,
            tokenEndpoint = "https://server.com/oauth2/token",
        )

    private fun credentialsWithOAuth2(state: OAuth2State?): Credentials =
        Credentials(
            username = USERNAME,
            serverUrl = NORMALIZED_URL,
            password = null,
            pin = null,
            openIDConnectState = null,
            oauth2State = state,
        )

    /** Reads the `aud` claim out of a compact-serialized JWT. */
    private fun audienceOf(jwt: String): String {
        val payload = String(Base64.getUrlDecoder().decode(jwt.split(".")[1]), Charsets.UTF_8)
        return KotlinxJsonParser.instance.parseToJsonElement(payload)
            .jsonObject["aud"]!!
            .jsonPrimitive
            .content
    }

    private fun validJwt(): String =
        TestJwtFactory.iatJwt(keyPair.private, TestJwtFactory.nowSeconds() + JWT_TTL_SECONDS)

    private fun expiredJwt(): String =
        TestJwtFactory.iatJwt(keyPair.private, TestJwtFactory.nowSeconds() - JWT_TTL_SECONDS)

    private fun serverError(): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription("test failure")
            .errorComponent(D2ErrorComponent.Server)
            .build()

    private fun sdkError(description: String): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription(description)
            .errorComponent(D2ErrorComponent.SDK)
            .build()

    // endregion

    companion object {
        private const val ENROLL_URL = "https://server.com/oauth2/dcr/enroll"
        private const val LOGOUT_URL = "https://server.com/dhis-web-commons-security/logout.action"
        private const val NORMALIZED_URL = "https://server.com"
        private const val CLIENT_ID = "client-1"
        private const val KEY_ID = "key-1"
        private const val DEVICE_ID = "device-1"
        private const val JWKS = "{\"keys\":[]}"
        private const val CODE_VERIFIER = "verifier"
        private const val AUTH_CODE = "auth-code"
        private const val USERNAME = "user-1"
        private const val PIN = "1234"
        private const val NEW_PIN = "5678"
        private const val KEY_SIZE = 2048
        private const val JWT_TTL_SECONDS = 5L * 60L
        private const val STATE = "state-1"
        private const val TOKEN_ENDPOINT = "https://server.com/oauth2/token"
        private const val DISCOVERED_TOKEN_ENDPOINT = "https://auth.server.com/oidc/oauth2/token"
    }
}
