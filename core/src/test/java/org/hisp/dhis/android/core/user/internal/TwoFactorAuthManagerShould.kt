/*
 *  Copyright (c) 2004-2022, University of Oslo
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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.network.twofactorauth.TwoFactorAuthNetworkHandlerImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TwoFactorAuthManagerShould {
    private val networkHandler: TwoFactorAuthNetworkHandlerImpl = mock()
    private val userStore: UserStore = mock()
    private lateinit var authManager: TwoFactorAuthManagerImpl

    @Before
    fun setUp() {
        authManager = TwoFactorAuthManagerImpl(networkHandler, userStore)
    }

    @Test
    fun can_totp2fa_be_enabled_when_network_allows() = runTest {
        whenever(networkHandler.canTotp2faBeEnabled()).thenReturn(Result.Success(true))

        val result = authManager.canTotp2faBeEnabled()

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun is2faEnabled_returns_true_and_persists_when_enabled() = runTest {
        whenever(networkHandler.is2faEnabled()).thenReturn(Result.Success(true))

        val enabled = authManager.is2faEnabled()

        assertThat(enabled).isTrue()
        verify(userStore, times(1)).updateIs2faEnabled(true)
    }

    @Test
    fun is2faEnabled_returns_false_and_persists_when_disabled() = runTest {
        whenever(networkHandler.is2faEnabled()).thenReturn(Result.Success(false))

        val enabled = authManager.is2faEnabled()

        assertThat(enabled).isFalse()
        verify(userStore, times(1)).updateIs2faEnabled(false)
    }

    @Test
    fun is2faEnabled_returns_stored_value_on_network_failure() = runTest {
        val error: D2Error = mock()
        val user: User = mock()
        whenever(networkHandler.is2faEnabled()).thenReturn(Result.Failure(error))
        whenever(user.twoFactorAuthEnabled()).thenReturn(true)
        whenever(userStore.selectFirst()).thenReturn(user)

        val enabled = authManager.is2faEnabled()

        assertThat(enabled).isTrue()
        verify(userStore, times(0)).updateIs2faEnabled(true)
    }

    @Test
    fun getTotpSecret_returns_secret_when_available() = runTest {
        whenever(networkHandler.getTotpSecret()).thenReturn(Result.Success("SECRET"))

        val secret = authManager.getTotpSecret()

        assertThat(secret).isEqualTo("SECRET")
    }

    @Test
    fun getTotpSecret_enrolls_and_retries_on_not_in_enrollment_mode_error() = runTest {
        val enrollmentError: D2Error = mock()
        whenever(enrollmentError.errorCode()).thenReturn(D2ErrorCode.NOT_IN_TOTP_2FA_ENROLLMENT_MODE)
        whenever(networkHandler.getTotpSecret())
            .thenReturn(Result.Failure(enrollmentError))
            .thenReturn(Result.Success("NEW_SECRET"))
        whenever(networkHandler.enrollTOTP2FA()).thenReturn(
            Result.Success(
                HttpMessageResponse.builder()
                    .httpStatus(HttpStatusCode.OK.description)
                    .httpStatusCode(HttpStatusCode.OK.value)
                    .status(HttpStatusCode.OK.description)
                    .message("Two factor authentication was enabled successfully")
                    .build(),
            ),
        )

        val secret = authManager.getTotpSecret()

        verify(networkHandler, times(1)).enrollTOTP2FA()
        assertThat(secret).isEqualTo("NEW_SECRET")
    }

    @Test
    fun getTotpSecret_throws_error_when_unrelated_d2error() = runTest {
        val otherError: D2Error = mock()
        whenever(otherError.errorCode()).thenReturn(D2ErrorCode.UNEXPECTED)
        whenever(networkHandler.getTotpSecret()).thenReturn(Result.Failure(otherError))

        try {
            authManager.getTotpSecret()
            throw AssertionError("Expected D2Error to be thrown")
        } catch (e: D2Error) {
            assertThat(e).isEqualTo(otherError)
        }
    }

    @Test
    fun enable2fa_delegates_to_network_handler_and_persists_on_success() = runTest {
        val code = "123456"
        val response: HttpMessageResponse = mock()
        whenever(networkHandler.enable2fa(code)).thenReturn(Result.Success(response))

        val result = authManager.enable2fa(code)

        verify(networkHandler, times(1)).enable2fa(code)
        verify(userStore, times(1)).updateIs2faEnabled(true)
        assertThat(result.getOrNull()).isEqualTo(response)
    }

    @Test
    fun disable2fa_delegates_to_network_handler_and_persists_on_success() = runTest {
        val code = "098765"
        val response: HttpMessageResponse = mock()
        whenever(networkHandler.disable2fa(code)).thenReturn(Result.Success(response))

        val result = authManager.disable2fa(code)

        verify(networkHandler, times(1)).disable2fa(code)
        verify(userStore, times(1)).updateIs2faEnabled(false)
        assertThat(result.getOrNull()).isEqualTo(response)
    }
}
