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
import org.hisp.dhis.android.core.arch.helpers.UserHelper
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
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null, null)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.BASIC)
    }

    @Test
    fun return_open_id_connect_when_open_id_connect_state() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, openIDConnectState, null)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }

    @Test
    fun return_oauth2_when_oauth2_state() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, null, oauth2State)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OAUTH2)
    }

    @Test
    fun return_open_id_connect_when_both_sso_states() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, openIDConnectState, oauth2State)
        assertThat(credentials.authorizationType).isEqualTo(AuthorizationType.OPEN_ID_CONNECT)
    }

    @Test
    fun return_password_as_password_or_pin_when_password_is_set() {
        val credentials = Credentials("user", "https://dhis2.org", "password", PIN, null, null)
        assertThat(credentials.passwordOrPin).isEqualTo("password")
    }

    @Test
    fun return_pin_as_password_or_pin_when_there_is_no_password() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        assertThat(credentials.passwordOrPin).isEqualTo(PIN)
    }

    @Test
    fun return_null_password_or_pin_when_neither_password_nor_pin() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, null, oauth2State)
        assertThat(credentials.passwordOrPin).isNull()
    }

    @Test
    fun build_hash_from_pin_when_there_is_no_password() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        assertThat(credentials.matches(credentials.newPasswordHash()))
            .isEqualTo(HashVerification.Match(needsUpgrade = false))
    }

    @Test
    fun build_hash_from_password_when_password_is_set() {
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null, null)
        assertThat(credentials.matches(credentials.newPasswordHash()))
            .isEqualTo(HashVerification.Match(needsUpgrade = false))
    }

    @Test
    fun build_a_different_hash_on_every_call() {
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null, null)
        assertThat(credentials.newPasswordHash()).isNotEqualTo(credentials.newPasswordHash())
    }

    @Test
    fun return_null_hash_when_neither_password_nor_pin() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, null, oauth2State)
        assertThat(credentials.newPasswordHash()).isNull()
    }

    @Test
    fun match_a_legacy_md5_hash_and_ask_for_an_upgrade() {
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null, null)

        @Suppress("DEPRECATION")
        val legacyHash = UserHelper.md5("user", "password")

        assertThat(credentials.matches(legacyHash)).isEqualTo(HashVerification.Match(needsUpgrade = true))
    }

    @Test
    fun not_match_a_hash_that_belongs_to_a_different_secret() {
        val credentials = Credentials("user", "https://dhis2.org", "password", null, null, null)
        val other = Credentials("user", "https://dhis2.org", "another-password", null, null, null)

        assertThat(credentials.matches(other.newPasswordHash())).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun match_when_neither_the_credentials_nor_the_stored_hash_have_a_secret() {
        val credentials = Credentials("user", "https://dhis2.org", null, null, null, oauth2State)

        assertThat(credentials.matches(null)).isEqualTo(HashVerification.Match(needsUpgrade = false))
    }

    @Test
    fun not_match_when_a_secret_is_given_but_nothing_is_stored() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)

        assertThat(credentials.matches(null)).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun not_match_when_a_hash_is_stored_but_no_secret_is_given() {
        val withPin = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        val withoutPin = Credentials("user", "https://dhis2.org", null, null, null, oauth2State)

        assertThat(withoutPin.matches(withPin.newPasswordHash())).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun be_equal_when_all_fields_including_pin_are_equal() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        val other = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        assertThat(credentials).isEqualTo(other)
        assertThat(credentials.hashCode()).isEqualTo(other.hashCode())
    }

    @Test
    fun not_be_equal_when_pin_differs() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        val other = credentials.copy(pin = "5678")
        assertThat(credentials).isNotEqualTo(other)
        assertThat(credentials.hashCode()).isNotEqualTo(other.hashCode())
    }

    @Test
    fun not_be_equal_when_pin_is_removed() {
        val credentials = Credentials("user", "https://dhis2.org", null, PIN, null, oauth2State)
        val other = credentials.copy(pin = null)
        assertThat(credentials).isNotEqualTo(other)
        assertThat(credentials.hashCode()).isNotEqualTo(other.hashCode())
    }

    companion object {
        private const val PIN = "1234"
    }
}
