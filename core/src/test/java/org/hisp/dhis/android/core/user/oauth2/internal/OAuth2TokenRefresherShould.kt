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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.storage.internal.ChunkedSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStoreImpl
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.keystore.KeyStoreManager
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.security.KeyPairGenerator

private const val SERVER_URL = "https://dhis-instance.org"
private const val USERNAME = "test_username"
private const val TOKEN_ENDPOINT = "https://dhis-instance.org/oauth/token"
private const val KEY_ID = "key-1"
private const val RSA_KEY_SIZE = 2048

class OAuth2TokenRefresherShould {

    private val networkHandler: OAuth2NetworkHandler = mock()
    private val keyStoreManager: KeyStoreManager = mock()

    private lateinit var credentialsSecureStore: CredentialsSecureStore
    private lateinit var oauth2StateSecureStore: OAuth2StateSecureStore
    private lateinit var refresher: OAuth2TokenRefresher

    @Before
    fun setUp() {
        credentialsSecureStore = CredentialsSecureStoreImpl(ChunkedSecureStore(InMemorySecureStore()))
        oauth2StateSecureStore = OAuth2StateSecureStore(ChunkedSecureStore(InMemorySecureStore()))

        val keyPair = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(RSA_KEY_SIZE) }
            .generateKeyPair()
        keyStoreManager.stub { on { getPrivateKey(KEY_ID) } doReturn keyPair.private }

        refresher = OAuth2TokenRefresher(
            oauth2NetworkHandler = networkHandler,
            keyStoreManager = keyStoreManager,
            credentialsSecureStore = credentialsSecureStore,
            oauth2StateSecureStore = oauth2StateSecureStore,
            logInExceptions = LogInExceptions(credentialsSecureStore),
        )
    }

    @Test
    fun `persist the rotated refresh token in both stores`() = runTest {
        givenActiveSession()
        givenServerReturns(refreshToken = "refresh-2")

        val result = refresher.rotate("refresh-1")

        assertThat(result).isInstanceOf(RefreshResult.Success::class.java)
        // The long-lived store is the one the relogin reads: if it keeps the consumed token, the
        // account becomes unusable after a logout.
        assertThat(oauth2StateSecureStore.get(SERVER_URL, USERNAME)?.refreshToken).isEqualTo("refresh-2")
        assertThat(credentialsSecureStore.get()?.oauth2State?.refreshToken).isEqualTo("refresh-2")
    }

    @Test
    fun `keep the previous refresh token when the server does not return a new one`() = runTest {
        givenActiveSession()
        givenServerReturns(refreshToken = "refresh-1")

        refresher.rotate("refresh-1")

        assertThat(credentialsSecureStore.get()?.oauth2State?.refreshToken).isEqualTo("refresh-1")
    }

    @Test
    fun `not send a refresh token that has already been replaced`() = runTest {
        // Two requests got a 401 at once; the first rotated already. Replaying refresh-1 would trip
        // the server's reuse detection and kill the whole token family.
        givenActiveSession(refreshToken = "refresh-2")

        val result = refresher.rotate("refresh-1")

        assertThat((result as RefreshResult.Success).state.refreshToken).isEqualTo("refresh-2")
        verifyNoInteractions(networkHandler)
    }

    @Test
    fun `send the token the caller used`() = runTest {
        givenActiveSession()
        givenServerReturns(refreshToken = "refresh-2")

        refresher.rotate("refresh-1")

        verify(networkHandler).refreshToken(
            endpoint = eq(TOKEN_ENDPOINT),
            refreshToken = eq("refresh-1"),
            clientId = any(),
            keyId = any(),
            clientAssertion = any(),
        )
    }

    @Test
    fun `keep the tokens when the rotation fails because the device is offline`() = runTest {
        givenActiveSession()
        givenServerFails(D2ErrorCode.SERVER_CONNECTION_ERROR, httpErrorCode = null)

        val result = refresher.rotate("refresh-1")

        assertThat(result).isInstanceOf(RefreshResult.Retryable::class.java)
        assertThat(credentialsSecureStore.get()?.oauth2State?.refreshToken).isEqualTo("refresh-1")
    }

    @Test
    fun `keep the tokens when the token endpoint returns a server error`() = runTest {
        givenActiveSession()
        givenServerFails(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE, httpErrorCode = 503)

        val result = refresher.rotate("refresh-1")

        assertThat(result).isInstanceOf(RefreshResult.Retryable::class.java)
        assertThat(credentialsSecureStore.get()?.oauth2State?.refreshToken).isEqualTo("refresh-1")
    }

    @Test
    fun `discard the tokens but keep the session when the server rejects the refresh token`() = runTest {
        givenActiveSession()
        givenServerFails(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE, httpErrorCode = 400)

        val result = refresher.rotate("refresh-1")

        assertThat((result as RefreshResult.Invalid).error.errorCode())
            .isEqualTo(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
        // The session survives so that the user can keep working offline or authorize again...
        val credentials = credentialsSecureStore.get()
        assertThat(credentials).isNotNull()
        assertThat(credentials?.authorizationType).isEqualTo(AuthorizationType.OAUTH2)
        // ...but the dead tokens are gone from both stores, so nothing can send them anywhere.
        assertThat(credentials?.oauth2State?.accessToken).isNull()
        assertThat(credentials?.oauth2State?.refreshToken).isNull()
        assertThat(oauth2StateSecureStore.get(SERVER_URL, USERNAME)?.refreshToken).isNull()
    }

    @Test
    fun `report no valid token when the session has no refresh token`() = runTest {
        givenActiveSession(refreshToken = null)

        val result = refresher.rotate(null)

        assertThat((result as RefreshResult.Invalid).error.errorCode())
            .isEqualTo(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
        verifyNoInteractions(networkHandler)
    }

    @Test
    fun `report no valid token when there is no session`() = runTest {
        val result = refresher.rotate("refresh-1")

        assertThat((result as RefreshResult.Invalid).error.errorCode())
            .isEqualTo(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
        verifyNoInteractions(networkHandler)
    }

    private fun givenActiveSession(refreshToken: String? = "refresh-1") {
        val state = state(accessToken = "access-1", refreshToken = refreshToken)
        credentialsSecureStore.set(
            Credentials(
                username = USERNAME,
                serverUrl = SERVER_URL,
                password = null,
                pin = null,
                openIDConnectState = null,
                oauth2State = state,
            ),
        )
        oauth2StateSecureStore.set(SERVER_URL, USERNAME, state)
    }

    private suspend fun givenServerReturns(refreshToken: String) {
        val response = state(accessToken = "access-2", refreshToken = refreshToken)
        networkHandler.stub {
            onBlocking { refreshToken(any(), any(), any(), any(), any()) } doReturn Result.Success(response)
        }
    }

    private suspend fun givenServerFails(errorCode: D2ErrorCode, httpErrorCode: Int?) {
        val error = D2Error.builder()
            .errorCode(errorCode)
            .errorDescription("failed")
            .errorComponent(D2ErrorComponent.Server)
            .httpErrorCode(httpErrorCode)
            .build()
        networkHandler.stub {
            onBlocking { refreshToken(any(), any(), any(), any(), any()) } doReturn Result.Failure(error)
        }
    }

    private fun state(accessToken: String, refreshToken: String?) = OAuth2State(
        clientId = "client-1",
        keyId = KEY_ID,
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresAt = 1_700_000_000L,
        scope = "openid",
        tokenEndpoint = TOKEN_ENDPOINT,
    )
}
