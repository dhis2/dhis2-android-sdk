/*
 *  Copyright (c) 2004-2024, University of Oslo
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

import com.google.common.truth.Truth
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.UserIdInMemoryStore
import org.hisp.dhis.android.core.user.openid.OpenIDConnectLogoutHandler
import org.hisp.dhis.android.core.user.openid.OpenIDConnectTokenRefresher
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ParentAuthenticatorPluginShould {
    @Mock
    private lateinit var credentialsSecureStore: CredentialsSecureStore

    @Mock
    private lateinit var userIdStore: UserIdInMemoryStore

    @Mock
    private lateinit var tokenRefresher: OpenIDConnectTokenRefresher

    @Mock
    private lateinit var logoutHandler: OpenIDConnectLogoutHandler

    private lateinit var mockEngine: MockEngine
    private lateinit var authenticator: ParentAuthenticatorPlugin
    private lateinit var ktorClient: HttpClient

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        mockEngine = MockEngine { request ->
            respond(
                content = "OK",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }

        val userIdHelper = UserIdAuthenticatorHelper(userIdStore)
        val cookieHelper = CookieAuthenticatorHelper()

        authenticator = ParentAuthenticatorPlugin(
            credentialsSecureStore,
            PasswordAndCookieAuthenticator(userIdHelper, cookieHelper),
            OpenIDConnectAuthenticator(
                credentialsSecureStore,
                tokenRefresher,
                userIdHelper,
                logoutHandler,
            ),
            cookieHelper,
        )

        ktorClient = HttpClient(mockEngine) {
            install(authenticator.instance)
        }
    }

    @Test
    fun return_test_and_user_when_server_take_request() = runTest {
        val credentials = Credentials("test_user", "test_server", "test_password", null)
        Mockito.`when`(credentialsSecureStore.get()).thenReturn(credentials)
        Mockito.`when`(userIdStore.get()).thenReturn("user-id")

        val response: HttpResponse = ktorClient.get("https://test.com")
        Truth.assertThat(response.bodyAsText()).isEqualTo("OK")
        val authorizationHeader = response.request.headers[HttpHeaders.Authorization]
        Truth.assertThat(authorizationHeader).isEqualTo("Basic dGVzdF91c2VyOnRlc3RfcGFzc3dvcmQ=")
    }

    @Test
    fun return_null_when_server_take_request_with_authenticate_with_empty_list() = runTest {
        Mockito.`when`(credentialsSecureStore.get()).thenReturn(null)

        val response: HttpResponse = ktorClient.get("https://test.com")

        Truth.assertThat(response.bodyAsText()).isEqualTo("OK")
        val authorizationHeader = response.request.headers[HttpHeaders.Authorization]
        Truth.assertThat(authorizationHeader).isNull()
    }

    @After
    fun tearDown() {
        ktorClient.close()
    }
}
