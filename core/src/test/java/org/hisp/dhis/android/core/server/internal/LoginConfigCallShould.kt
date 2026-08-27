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
package org.hisp.dhis.android.core.server.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.server.LoginConfig
import org.hisp.dhis.android.core.server.OauthConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.hisp.dhis.android.core.systeminfo.internal.PingNetworkHandler
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.hisp.dhis.android.core.user.oauth2.internal.OAuth2SecureStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class LoginConfigCallShould {

    private val pingNetworkHandler: PingNetworkHandler = mock()
    private val loginExceptions: LogInExceptions = mock()
    private val networkHandler: LoginConfigNetworkHandler = mock()
    private val oauth2SecureStore = OAuth2SecureStore(InMemorySecureStore())
    private val dhisVersionManager: DHISVersionManagerImpl = mock()
    private val executor = CoroutineAPICallExecutorMock()

    private lateinit var call: LoginConfigCall

    @Before
    fun setUp() = runTest {
        whenever(dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_43)).doReturn(true)

        call = LoginConfigCall(
            pingNetworkHandler,
            loginExceptions,
            executor,
            networkHandler,
            oauth2SecureStore,
            dhisVersionManager,
        )
    }

    @Test
    fun marks_oauth_enabled_and_persists_endpoints_when_oauth_config_is_available() = runTest {
        networkHandler.stub {
            onBlocking { loginConfigFor(NORMALIZED_URL) } doReturn loginConfigSample()
            onBlocking { oauthConfigFor(any(), any()) } doReturn oauthConfigSample()
        }

        val result = call.checkServerUrl(NORMALIZED_URL)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val loginConfig = (result as Result.Success<LoginConfig, D2Error>).value
        assertThat(loginConfig.isOauthEnabled).isTrue()
        assertThat(oauth2SecureStore.authorizationEndpoint).isEqualTo(AUTHORIZATION_ENDPOINT)
        assertThat(oauth2SecureStore.jwksUri).isEqualTo(JWKS_URI)
    }

    @Test
    fun marks_oauth_disabled_and_clears_endpoints_when_oauth_config_fetch_fails() = runTest {
        oauth2SecureStore.authorizationEndpoint = "stale-auth"
        oauth2SecureStore.jwksUri = "stale-jwks"

        networkHandler.stub {
            onBlocking { loginConfigFor(NORMALIZED_URL) } doReturn loginConfigSample()
            onBlocking { oauthConfigFor(any(), any()) } doAnswer { throw serverError() }
        }

        val result = call.checkServerUrl(NORMALIZED_URL)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val loginConfig = (result as Result.Success<LoginConfig, D2Error>).value
        assertThat(loginConfig.isOauthEnabled).isFalse()
        assertThat(oauth2SecureStore.authorizationEndpoint).isNull()
        assertThat(oauth2SecureStore.jwksUri).isNull()
    }

    @Test
    fun marks_oauth_disabled_and_clears_endpoints_when_oauth_config_does_not_support_required_functions() = runTest {
        oauth2SecureStore.authorizationEndpoint = "stale-auth"
        oauth2SecureStore.jwksUri = "stale-jwks"

        networkHandler.stub {
            onBlocking { loginConfigFor(NORMALIZED_URL) } doReturn loginConfigSample()
            onBlocking { oauthConfigFor(any(), any()) } doReturn unsupportedOauthConfigSample()
        }

        val result = call.checkServerUrl(NORMALIZED_URL)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val loginConfig = (result as Result.Success<LoginConfig, D2Error>).value
        assertThat(loginConfig.isOauthEnabled).isFalse()
        assertThat(oauth2SecureStore.authorizationEndpoint).isNull()
        assertThat(oauth2SecureStore.jwksUri).isNull()
    }

    @Test
    fun falls_back_to_ping_without_touching_oauth_store_when_login_config_fails() = runTest {
        oauth2SecureStore.authorizationEndpoint = "kept-auth"
        oauth2SecureStore.jwksUri = "kept-jwks"

        networkHandler.stub {
            onBlocking { loginConfigFor(NORMALIZED_URL) } doAnswer { throw serverError() }
        }
        pingNetworkHandler.stub {
            onBlocking { getPingFor(NORMALIZED_URL) } doReturn mock()
        }

        val result = call.checkServerUrl(NORMALIZED_URL)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        verifyBlocking(networkHandler, never()) { oauthConfigFor(any(), any()) }
        // Pre-existing endpoint state is preserved: the OAuth store is only mutated
        // along the loginConfig-success branch.
        assertThat(oauth2SecureStore.authorizationEndpoint).isEqualTo("kept-auth")
        assertThat(oauth2SecureStore.jwksUri).isEqualTo("kept-jwks")
    }

    @Test
    fun marks_oauth_disabled_without_fetching_config_when_server_version_is_lower_than_2_43() = runTest {
        oauth2SecureStore.authorizationEndpoint = "stale-auth"
        oauth2SecureStore.jwksUri = "stale-jwks"

        whenever(dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_43)).doReturn(false)
        networkHandler.stub {
            onBlocking { loginConfigFor(NORMALIZED_URL) } doReturn loginConfigSample()
            onBlocking { oauthConfigFor(any(), any()) } doReturn oauthConfigSample()
        }

        val result = call.checkServerUrl(NORMALIZED_URL)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val loginConfig = (result as Result.Success<LoginConfig, D2Error>).value
        assertThat(loginConfig.isOauthEnabled).isFalse()
        verifyBlocking(networkHandler, never()) { oauthConfigFor(any(), any()) }
        assertThat(oauth2SecureStore.authorizationEndpoint).isNull()
        assertThat(oauth2SecureStore.jwksUri).isNull()
    }

    private fun loginConfigSample(): LoginConfig =
        LoginConfig(applicationTitle = "Demo")

    private fun oauthConfigSample(): OauthConfig =
        OauthConfig(
            authorizationEndpoint = AUTHORIZATION_ENDPOINT,
            jwksUri = JWKS_URI,
            codeChallengeMethodsSupported = listOf("S256"),
            grantTypesSupported = listOf("authorization_code", "refresh_token"),
            responseTypesSupported = listOf("code"),
        )

    private fun unsupportedOauthConfigSample(): OauthConfig =
        OauthConfig(
            authorizationEndpoint = AUTHORIZATION_ENDPOINT,
            jwksUri = JWKS_URI,
            codeChallengeMethodsSupported = listOf("plain"),
            grantTypesSupported = listOf("authorization_code"),
            responseTypesSupported = listOf("code"),
        )

    private fun serverError(): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription("test failure")
            .errorComponent(D2ErrorComponent.Server)
            .build()

    companion object {
        private const val NORMALIZED_URL = "https://server.com"
        private const val AUTHORIZATION_ENDPOINT = "https://server.com/oauth2/authorize"
        private const val JWKS_URI = "https://server.com/.well-known/jwks.json"
    }
}
