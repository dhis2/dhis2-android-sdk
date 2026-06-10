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
import org.junit.Test
import org.mockito.kotlin.mock

class CredentialsShould {

    private val openIDConnectState: AuthState = mock()
    private val oauth2State = OAuth2State(
        clientId = "client",
        keyId = "key",
        accessToken = "token",
        refreshToken = null,
        expiresAt = Long.MAX_VALUE,
        scope = null,
        tokenEndpoint = "tokenEndpoint",
    )

    @Test
    fun return_basic_when_no_sso_state() {
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun return_open_id_connect_when_open_id_connect_state() {
        val credentials = Credentials("user", "https://dhis2.org", null, openIDConnectState, null)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }

    @Test
    fun return_oauth2_when_oauth2_state() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, oauth2State)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun return_open_id_connect_when_both_sso_states() {
        val credentials = Credentials("user", "https://dhis2.org", null, openIDConnectState, oauth2State)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }
}
