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
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2TokenRefresher
import org.hisp.dhis.android.core.user.oauth2.internal.RefreshResult
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class OAuth2AuthenticatorShould {

    private val tokenRefresher: OAuth2TokenRefresher = mock()
    private val userIdStore: UserIdInMemoryStore = mock<UserIdInMemoryStore>().apply {
        stub { on { get() } doReturn "user-uid" }
    }

    private val authenticator = OAuth2Authenticator(
        tokenRefresher = lazy { tokenRefresher },
        userIdHelper = UserIdAuthenticatorHelper(userIdStore),
        logInExceptions = LogInExceptions(mock()),
    )

    @Test
    fun `send the stored access token without rotating anything`() = runTest {
        val client = clientRespondingWith(firstStatus = HttpStatusCode.OK)

        val response: HttpResponse = client.get("https://test.com")

        assertThat(response.request.headers[HttpHeaders.Authorization]).isEqualTo("Bearer access-1")
        verifyNoInteractions(tokenRefresher)
    }

    @Test
    fun `rotate and retry once when the server rejects the access token`() = runTest {
        givenRotation(RefreshResult.Success(state("access-2")))
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val response: HttpResponse = client.get("https://test.com")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(response.request.headers[HttpHeaders.Authorization]).isEqualTo("Bearer access-2")
        verify(tokenRefresher, times(1)).rotate(anyOrNull())
    }

    @Test
    fun `return the unauthorized response when the rotation cannot be completed`() = runTest {
        givenRotation(RefreshResult.Retryable(null))
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val response: HttpResponse = client.get("https://test.com")

        // Offline or a server error: the caller sees the 401 and the session stays open.
        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `throw the error when the session cannot be renewed`() = runTest {
        givenRotation(RefreshResult.Invalid(noValidTokenError()))
        val client = clientRespondingWith(firstStatus = HttpStatusCode.Unauthorized)

        val thrown = runCatching { client.get("https://test.com").status }.exceptionOrNull()

        // Asserted on the top-level exception on purpose: CoroutineAPICallExecutor only lets a
        // D2Error through untouched if it is not wrapped, and a wrapped one would lose its code.
        assertThat(thrown).isInstanceOf(D2Error::class.java)
        assertThat((thrown as D2Error).errorCode()).isEqualTo(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
    }

    @Test
    fun `abort before reaching the server once the tokens have been discarded`() = runTest {
        val engine = MockEngine { respondOk("OK") }
        val plugin = createClientPlugin(name = "DiscardedTokensTestPlugin") {
            on(Send) { request ->
                authenticator.handleTokenCall(this, request, credentials(accessToken = null))
            }
        }
        val client = HttpClient(engine) { install(plugin) }

        val thrown = runCatching { client.get("https://test.com").status }.exceptionOrNull()

        assertThat((thrown as D2Error).errorCode()).isEqualTo(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
        assertThat(engine.requestHistory).isEmpty()
        verifyNoInteractions(tokenRefresher)
    }

    private fun givenRotation(result: RefreshResult) {
        tokenRefresher.stub {
            onBlocking { rotate(anyOrNull()) } doReturn result
        }
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
        val plugin = createClientPlugin(name = "OAuth2AuthenticatorTestPlugin") {
            on(Send) { request ->
                authenticator.handleTokenCall(this, request, credentials())
            }
        }
        return HttpClient(engine) { install(plugin) }
    }

    private fun credentials(accessToken: String? = "access-1") = Credentials(
        username = "test_username",
        serverUrl = "https://dhis-instance.org",
        password = null,
        pin = null,
        openIDConnectState = null,
        oauth2State = state(accessToken),
    )

    private fun state(accessToken: String?) = OAuth2State(
        clientId = "client-1",
        keyId = "key-1",
        accessToken = accessToken,
        refreshToken = "refresh-1",
        expiresAt = 1_700_000_000L,
        scope = "openid",
        tokenEndpoint = "https://dhis-instance.org/oauth/token",
    )

    private fun noValidTokenError() = D2Error.builder()
        .errorCode(D2ErrorCode.OAUTH2_NO_VALID_TOKEN)
        .errorDescription("no valid token")
        .errorComponent(D2ErrorComponent.SDK)
        .build()
}
