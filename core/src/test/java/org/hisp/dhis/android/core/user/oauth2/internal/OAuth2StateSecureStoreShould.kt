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
package org.hisp.dhis.android.core.user.oauth2.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.storage.internal.ChunkedSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.junit.Before
import org.junit.Test

class OAuth2StateSecureStoreShould {

    private lateinit var store: OAuth2StateSecureStore

    private val stateA = OAuth2State(
        clientId = "client-A",
        keyId = "key-A",
        accessToken = "access-A",
        refreshToken = "refresh-A",
        expiresAt = 1_700_000_000L,
        scope = "openid",
        tokenEndpoint = "https://dhis-instance.org/oauth/token",
    )

    private val stateB = OAuth2State(
        clientId = "client-B",
        keyId = "key-B",
        accessToken = "access-B",
        refreshToken = "refresh-B",
        expiresAt = 1_800_000_000L,
        scope = null,
        tokenEndpoint = "https://dhis-instance.org/oauth/token",

    )

    @Before
    fun setUp() {
        store = OAuth2StateSecureStore(ChunkedSecureStore(InMemorySecureStore()))
    }

    @Test
    fun should_return_null_for_unknown_account() {
        assertThat(store.get("https://play.dhis2.org", "admin")).isNull()
    }

    @Test
    fun should_round_trip_state() {
        store.set("https://play.dhis2.org", "admin", stateA)

        assertThat(store.get("https://play.dhis2.org", "admin")).isEqualTo(stateA)
    }

    @Test
    fun should_isolate_entries_per_account() {
        store.set("https://play.dhis2.org", "admin", stateA)
        store.set("https://play.dhis2.org", "user", stateB)
        store.set("https://other.dhis2.org", "admin", stateB)

        assertThat(store.get("https://play.dhis2.org", "admin")).isEqualTo(stateA)
        assertThat(store.get("https://play.dhis2.org", "user")).isEqualTo(stateB)
        assertThat(store.get("https://other.dhis2.org", "admin")).isEqualTo(stateB)
    }

    @Test
    fun should_treat_normalized_server_url_variants_as_same_account() {
        store.set("https://play.dhis2.org", "admin", stateA)

        assertThat(store.get("HTTPS://PLAY.DHIS2.ORG/", "admin")).isEqualTo(stateA)
        assertThat(store.get("https://play.dhis2.org/api", "admin")).isEqualTo(stateA)
    }

    @Test
    fun should_remove_only_the_target_account() {
        store.set("https://play.dhis2.org", "admin", stateA)
        store.set("https://play.dhis2.org", "user", stateB)

        store.remove("https://play.dhis2.org", "admin")

        assertThat(store.get("https://play.dhis2.org", "admin")).isNull()
        assertThat(store.get("https://play.dhis2.org", "user")).isEqualTo(stateB)
    }

    @Test
    fun should_overwrite_existing_state_for_same_account() {
        store.set("https://play.dhis2.org", "admin", stateA)
        store.set("https://play.dhis2.org", "admin", stateB)

        assertThat(store.get("https://play.dhis2.org", "admin")).isEqualTo(stateB)
    }
}
