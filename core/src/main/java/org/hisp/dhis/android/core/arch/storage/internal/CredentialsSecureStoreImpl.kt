/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.storage.internal

class CredentialsSecureStoreImpl(private val secureStore: SecureStore) : ObjectKeyValueStore<Credentials> {
    private var credentials: Credentials? = null

    override fun set(credentials: Credentials) {
        this.credentials = credentials
        secureStore.setData(USERNAME_KEY, credentials.username)
        secureStore.setData(PASSWORD_KEY, credentials.password)
        secureStore.setData(TOKEN_KEY, credentials.token)
    }

    override fun get(): Credentials? {
        if (credentials == null) {
            val username = secureStore.getData(USERNAME_KEY)
            val password = secureStore.getData(PASSWORD_KEY)
            val token = secureStore.getData(TOKEN_KEY)

            if (username != null) {
                credentials = Credentials(username, password, token)
            }
        }
        return credentials
    }

    override fun remove() {
        credentials = null
        secureStore.removeData(USERNAME_KEY)
        secureStore.removeData(PASSWORD_KEY)
        secureStore.removeData(TOKEN_KEY)
    }

    companion object {
        private const val USERNAME_KEY = "username"
        private const val PASSWORD_KEY = "password"
        private const val TOKEN_KEY = "token"
    }
}
