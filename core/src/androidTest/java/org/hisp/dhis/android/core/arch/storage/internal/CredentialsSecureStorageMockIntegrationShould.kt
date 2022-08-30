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
package org.hisp.dhis.android.core.arch.storage.internal

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class CredentialsSecureStorageMockIntegrationShould {
    private fun instantiateStore(): CredentialsSecureStoreImpl = CredentialsSecureStoreImpl(
        ChunkedSecureStore(
            AndroidSecureStore(InstrumentationRegistry.getInstrumentation().context)
        )
    )

    @Test
    fun credentials_are_correctly_stored_for_regular_password() {
        setAndVerify(Credentials("username", "serverUrl", "password", null))
    }

    @Test
    fun credentials_are_correctly_stored_for_really_log_password() {
        val pw = (0 until 1000).joinToString { it.toString() }
        setAndVerify(Credentials("username", "serverUrl", pw, null))
    }

    @Test
    fun credentials_are_correctly_stored_for_open_id_connect_config() {
        val authState = AuthState()
        setAndVerify(Credentials("username", "serverUrl", null, authState))
    }

    private fun setAndVerify(credentials: Credentials) {
        val store1 = instantiateStore()
        store1.set(credentials)
        val extractedCredentials1 = store1.get()
        assertThat(extractedCredentials1).isEqualTo(credentials)

        // Instantiating a second store ensure credentials are not retrieved from the cache
        val store2 = instantiateStore()
        val extractedCredentials2 = store2.get()
        assertThat(extractedCredentials2).isEqualTo(credentials)
    }

    @Test
    fun credentials_are_correctly_removed() {
        val store1 = instantiateStore()
        val credentials = Credentials("username", "serverUrl", "password", null)
        store1.set(credentials)
        store1.remove()

        val retrievedCredentials1 = store1.get()
        assertThat(retrievedCredentials1).isNull()

        val store2 = instantiateStore()
        val retrievedCredentials2 = store2.get()
        assertThat(retrievedCredentials2).isNull()
    }
}
