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

package org.hisp.dhis.android.core.user.internal

import com.google.common.truth.Truth.assertThat
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.TwoFactorAuthManager
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TwoFactorAuthManagerMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    private lateinit var authManager: TwoFactorAuthManager

    @Before
    fun setUp() = runTest {
        authManager = d2.userModule().twoFactorAuthManager()
    }

    @After
    @Throws(D2Error::class)
    fun tearDown() {
        runBlocking { d2.wipeModule().wipeData() }
    }

    @Test
    fun can_totp2fa_be_enabled_success() {
        dhis2MockServer.enqueueMockResponse("user/two-factor-methods.json")

        val result = runBlocking { authManager.canTotp2faBeEnabled() }

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun is2faEnabled_stores_and_fallback_to_stored_on_failure() = runTest {
        dhis2MockServer.enqueueMockResponse("user/two-factor-enabled-true.json")

        val enabled = runBlocking { authManager.is2faEnabled() }
        assertThat(enabled).isTrue()

        dhis2MockServer.enqueueMockResponse(HttpStatusCode.InternalServerError.value, "user/server-error.json")
        val fallback = runBlocking { authManager.is2faEnabled() }
        assertThat(fallback).isTrue()
    }

    @Test
    fun is2faEnabled_stores_and_fallback_false_to_stored_on_failure() = runTest {
        dhis2MockServer.enqueueMockResponse("user/two-factor-enabled-false.json")

        val enabled = runBlocking { authManager.is2faEnabled() }
        assertThat(enabled).isFalse()

        dhis2MockServer.enqueueMockResponse(HttpStatusCode.InternalServerError.value, "user/server-error.json")
        val fallback = runBlocking { authManager.is2faEnabled() }
        assertThat(fallback).isFalse()
    }

    @Test
    fun getTotpSecret_returns_secret_when_already_enrolled() {
        dhis2MockServer.enqueueMockResponse("user/qr-code.json")

        val secret = runBlocking { authManager.getTotpSecret() }

        assertThat(secret).isEqualTo("HMFQS5AAEFRHQ5D6UNIQCGJUKB3ITNPD")
    }

    @Test
    fun getTotpSecret_enrolls_and_retries_when_not_in_enrollment_mode() {
        dhis2MockServer.enqueueMockResponse(HttpStatusCode.Conflict.value, "user/totp-secret-error.json")
        dhis2MockServer.enqueueMockResponse("user/enroll-totp-response.json")
        dhis2MockServer.enqueueMockResponse("user/qr-code.json")

        val secret = runBlocking { authManager.getTotpSecret() }

        assertThat(secret).isEqualTo("HMFQS5AAEFRHQ5D6UNIQCGJUKB3ITNPD")
    }

    @Test
    fun enable2fa_succeeds_and_updates_store_fallback() {
        dhis2MockServer.enqueueMockResponse("user/enable2fa-response.json")

        val response: HttpMessageResponse = runBlocking { authManager.enable2fa("123456").getOrThrow() }

        assertThat(response.message()).isEqualTo("2FA was enabled successfully")

        dhis2MockServer.enqueueMockResponse(HttpStatusCode.InternalServerError.value, "user/server-error.json")
        val postEnable = runBlocking { authManager.is2faEnabled() }
        assertThat(postEnable).isTrue()
    }

    @Test
    fun disable2fa_succeeds_and_updates_store_fallback() {
        dhis2MockServer.enqueueMockResponse("user/disable2fa-response.json")

        val response: HttpMessageResponse = runBlocking { authManager.disable2fa("098765").getOrThrow() }

        assertThat(response.message()).isEqualTo("2FA was disabled successfully")

        dhis2MockServer.enqueueMockResponse(HttpStatusCode.InternalServerError.value, "user/server-error.json")
        val postDisable = runBlocking { authManager.is2faEnabled() }
        assertThat(postDisable).isFalse()
    }
}
