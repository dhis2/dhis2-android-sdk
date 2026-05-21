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
package org.hisp.dhis.android.core.arch.storage.internal

import com.google.common.truth.Truth.assertThat
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CredentialsSecureStoreImplShould {

    private val secureStore: ChunkedSecureStore = mock()
    private val openIDConnectState: AuthState = mock()
    private val oauth2State = OAuth2State(
        clientId = "client",
        keyId = "key",
        accessToken = "token",
        refreshToken = null,
        expiresAt = Long.MAX_VALUE,
        scope = null,
    )

    private lateinit var store: CredentialsSecureStoreImpl

    @Before
    fun setUp() {
        whenever(openIDConnectState.jsonSerializeString()).thenReturn("{'key:value'}")
        store = CredentialsSecureStoreImpl(secureStore)
    }

    @Test
    fun return_basic_when_no_credentials_set() {
        assertThat(store.getAuthorizationType()).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun return_basic_when_credentials_have_no_sso_state() {
        store.set(Credentials("user", "https://dhis2.org", "password", null, null))
        assertThat(store.getAuthorizationType()).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun return_open_id_connect_when_credentials_have_open_id_connect_state() {
        store.set(Credentials("user", "https://dhis2.org", null, openIDConnectState, null))
        assertThat(store.getAuthorizationType()).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }

    @Test
    fun return_oauth2_when_credentials_have_oauth2_state() {
        store.set(Credentials("user", "https://dhis2.org", null, null, oauth2State))
        assertThat(store.getAuthorizationType()).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun return_open_id_connect_when_credentials_have_both_sso_states() {
        store.set(Credentials("user", "https://dhis2.org", null, openIDConnectState, oauth2State))
        assertThat(store.getAuthorizationType()).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }
}
