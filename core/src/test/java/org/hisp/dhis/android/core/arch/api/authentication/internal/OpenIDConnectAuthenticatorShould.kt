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
package org.hisp.dhis.android.core.arch.api.authentication.internal

import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.openid.OpenIDConnectStateSecureStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher
import org.hisp.dhis.android.core.user.openid.OpenIdRefreshResult
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class OpenIDConnectAuthenticatorShould {

    private val tokenRefresher: OpenIDConnectTokenRefresher = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val stateSecureStore: OpenIDConnectStateSecureStore = mock()
    private val userIdStore: UserIdInMemoryStore = mock<UserIdInMemoryStore>().apply {
        stub { on { get() } doReturn "user-uid" }
    }

    private val authenticator = OpenIDConnectAuthenticator(
        credentialsSecureStore = credentialsSecureStore,
        tokenRefresher = tokenRefresher,
        userIdHelper = UserIdAuthenticatorHelper(userIdStore),
        openIDConnectStateSecureStore = stateSecureStore,
        logInExceptions = LogInExceptions(mock()),
    )

    @Test
    fun `send the stored id token without refreshing anything`() = runTest {
        val client = clientRespondingWith(firstStatus = HttpStatusCode.OK)

        val response: HttpResponse = client.get("https://test.com")

        assertThat(response.request.headers[HttpHeaders.Authorization]).isEqualTo("Bearer id-1")
        verifyNoInteractions(tokenRefresher)
    }

    @Test
    fun `refresh and retry once when the server rejects the id token`() = runTest {
        givenRefresh(OpenIdRefreshResult.Success("id-2"))
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val response: HttpResponse = client.get("https://test.com")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(response.request.headers[HttpHeaders.Authorization]).isEqualTo("Bearer id-2")
        verify(tokenRefresher, times(1)).refresh(any())
    }

    @Test
    fun `persist the refreshed state so a later login uses the newest refresh token`() = runTest {
        givenRefresh(OpenIdRefreshResult.Success("id-2"))
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val response: HttpResponse = client.get("https://test.com")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        verify(credentialsSecureStore).set(any())
        verify(stateSecureStore).set(eq(SERVER_URL), eq(USERNAME), any())
    }

    @Test
    fun `keep the session open when the refresh cannot be completed`() = runTest {
        givenRefresh(OpenIdRefreshResult.Retryable)
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val response: HttpResponse = client.get("https://test.com")

        // Offline or a provider error: the caller sees the 401 and the session survives. Logging the
        // user out here would strand an account that only needs connectivity to recover.
        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        verify(credentialsSecureStore, never()).remove()
        verify(stateSecureStore, never()).remove(any(), any())
    }

    @Test
    fun `report a typed error and discard the state when the provider rejects the refresh token`() = runTest {
        givenRefresh(OpenIdRefreshResult.Invalid)
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val thrown = runCatching { client.get("https://test.com").status }.exceptionOrNull()

        assertThat(thrown).isInstanceOf(D2Error::class.java)
        assertThat((thrown as D2Error).errorCode()).isEqualTo(D2ErrorCode.OPEN_ID_CONNECT_NO_VALID_TOKEN)
        // The dead state is removed so the next login opens the account offline instead of looping
        // on a refresh that can never succeed.
        verify(stateSecureStore).remove(SERVER_URL, USERNAME)
        verify(credentialsSecureStore, never()).remove()
    }

    @Test
    fun `abort before reaching the server when the account has no state`() = runTest {
        val engine = MockEngine { respondOk("OK") }
        val plugin = createClientPlugin(name = "MissingStateTestPlugin") {
            on(Send) { request ->
                authenticator.handleTokenCall(this, request, credentialsWithoutState())
            }
        }
        val client = HttpClient(engine) { install(plugin) }

        val thrown = runCatching { client.get("https://test.com").status }.exceptionOrNull()

        // An imported database has no state. Before, this was a non-null assertion and crashed.
        assertThat(thrown).isInstanceOf(D2Error::class.java)
        assertThat((thrown as D2Error).errorCode()).isEqualTo(D2ErrorCode.OPEN_ID_CONNECT_NO_VALID_TOKEN)
        assertThat(engine.requestHistory).isEmpty()
        verifyNoInteractions(tokenRefresher)
    }

    private fun givenRefresh(result: OpenIdRefreshResult) {
        tokenRefresher.stub { on { refresh(any()) } doReturn result }
    }

    /**
     * The engine answers [firstStatus] once and OK from then on, so a retry can be told apart from
     * the original attempt by the status of the response that comes out.
     */
    private fun clientRespondingWith(firstStatus: HttpStatusCode): HttpClient {
        var calls = 0
        val engine = MockEngine {
            calls++
            if (calls == 1) respond(content = "", status = firstStatus) else respondOk("OK")
        }
        val plugin = createClientPlugin(name = "OpenIDConnectAuthenticatorTestPlugin") {
            on(Send) { request ->
                authenticator.handleTokenCall(this, request, credentials())
            }
        }
        return HttpClient(engine) { install(plugin) }
    }

    private fun credentials(): Credentials {
        val state: AuthState = mock()
        whenever(state.idToken).thenReturn("id-1")
        return Credentials(
            username = USERNAME,
            serverUrl = SERVER_URL,
            password = null,
            pin = "1234",
            openIDConnectState = state,
            oauth2State = null,
        )
    }

    private fun credentialsWithoutState() = Credentials(
        username = USERNAME,
        serverUrl = SERVER_URL,
        password = null,
        pin = "1234",
        openIDConnectState = null,
        oauth2State = null,
        authorizationType = AuthorizationType.OPEN_ID_CONNECT,
    )

    companion object {
        private const val USERNAME = "test_username"
        private const val SERVER_URL = "https://dhis-instance.org"
    }
}
