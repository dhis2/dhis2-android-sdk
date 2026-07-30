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
package org.hisp.dhis.android.core.user.openid

import android.content.Context
import com.google.common.truth.Truth.assertThat
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.internal.AuthenticatedUserStore
import org.hisp.dhis.android.core.user.internal.LogInCall
import org.hisp.dhis.android.core.user.internal.LogInExceptions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class OpenIDConnectHandlerImplShould {

    private val context: Context = mock()
    private val logInCall: LogInCall = mock()
    private val logoutHandler: OpenIDConnectLogoutHandler = mock()
    private val openIDConnectStateSecureStore: OpenIDConnectStateSecureStore = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val authenticatedUserStore: AuthenticatedUserStore = mock()
    private val logInExceptions: LogInExceptions = mock()

    private val authState: AuthState = mock()

    private lateinit var handler: OpenIDConnectHandlerImpl

    @Before
    fun setUp() {
        whenever(logInExceptions.noActiveSessionError()).thenReturn(sdkError("no session"))
        whenever(logInExceptions.pinRequiresTokenBasedAccountError()).thenReturn(sdkError("not openid"))
        whenever(logInExceptions.noAuthenticatedUserPersistedError()).thenReturn(sdkError("no user"))
        whenever(logInExceptions.incorrectPinError()).thenReturn(sdkError("bad pin"))
        handler = OpenIDConnectHandlerImpl(
            context,
            logInCall,
            logoutHandler,
            openIDConnectStateSecureStore,
            credentialsSecureStore,
            authenticatedUserStore,
            logInExceptions,
        )
    }

    @Test
    fun setPin_persists_pin_as_credentials_pin_and_updates_authenticated_user_hash() {
        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOpenId(authState))
        val existing = AuthenticatedUser.builder().user("uid").hash(null).build()
        authenticatedUserStore.stub {
            onBlocking { selectFirst() }.doReturn(existing)
        }

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val credentialsCaptor = argumentCaptor<Credentials>()
        verify(credentialsSecureStore).set(credentialsCaptor.capture())
        assertThat(credentialsCaptor.firstValue.pin).isEqualTo(PIN)
        assertThat(credentialsCaptor.firstValue.openIDConnectState).isNotNull()
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
    fun setPin_fails_when_account_is_not_openid() {
        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOpenId(state = null))

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
        verify(credentialsSecureStore, never()).set(any())
    }

    @Test
    fun setPin_fails_when_no_authenticated_user_persisted() {
        whenever(credentialsSecureStore.get()).thenReturn(credentialsWithOpenId(authState))
        authenticatedUserStore.stub {
            onBlocking { selectFirst() }.doReturn(null)
        }

        val result = handler.blockingSetPin(PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
    }

    @Test
    fun changePin_replaces_pin_when_current_matches() {
        val current = credentialsWithOpenId(authState).copy(pin = PIN)
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
        val current = credentialsWithOpenId(authState).copy(pin = PIN)
        whenever(credentialsSecureStore.get()).thenReturn(current)

        val result = handler.blockingChangePin("wrong", NEW_PIN)

        assertThat(result).isInstanceOf(Result.Failure::class.java)
        verify(credentialsSecureStore, never()).set(any())
    }

    private fun credentialsWithOpenId(state: AuthState?): Credentials =
        Credentials(
            username = USERNAME,
            serverUrl = SERVER_URL,
            password = null,
            pin = null,
            openIDConnectState = state,
            oauth2State = null,
        )

    private fun sdkError(description: String): D2Error =
        D2Error.builder()
            .errorCode(D2ErrorCode.UNEXPECTED)
            .errorDescription(description)
            .errorComponent(D2ErrorComponent.SDK)
            .build()

    companion object {
        private const val USERNAME = "user-1"
        private const val SERVER_URL = "https://server.com"
        private const val PIN = "1234"
        private const val NEW_PIN = "5678"
    }
}
