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

import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton
import net.openid.appauth.AuthState

@Singleton
internal class CredentialsSecureStoreImpl @Inject constructor(private val secureStore: ChunkedSecureStore) :
    CredentialsSecureStore {

    private var credentials: Credentials? = null

    override fun set(credentials: Credentials) {
        this.credentials = credentials
        secureStore.setData(USERNAME_KEY, credentials.username)
        secureStore.setData(SERVER_URL_KEY, credentials.serverUrl)
        secureStore.setData(PASSWORD_KEY, credentials.password)
        secureStore.setData(OPEN_ID_CONNECT_STATE_KEY, credentials.openIDConnectState?.jsonSerializeString())
    }

    override fun setServerUrl(serverUrl: String) {
        secureStore.setData(SERVER_URL_KEY, serverUrl)
    }

    override fun get(): Credentials? {
        if (credentials == null) {
            credentials = tryGet()
        }
        return credentials
    }

    @Suppress("TooGenericExceptionCaught")
    private fun tryGet(): Credentials? {
        try {
            val username = secureStore.getData(USERNAME_KEY)
            val serverUrl = secureStore.getData(SERVER_URL_KEY)

            if (username != null && serverUrl != null) {
                val password = secureStore.getData(PASSWORD_KEY)
                val openIDConnectStateStr = secureStore.getData(OPEN_ID_CONNECT_STATE_KEY)
                val openIDConnectState = openIDConnectStateStr?.let { AuthState.jsonDeserialize(it) }
                return Credentials(username, serverUrl, password, openIDConnectState)
            }
        } catch (e: RuntimeException) {
            remove()
        }
        return null
    }

    override fun remove() {
        credentials = null
        secureStore.removeData(USERNAME_KEY)
        secureStore.removeData(SERVER_URL_KEY)
        secureStore.removeData(PASSWORD_KEY)
        secureStore.removeData(OPEN_ID_CONNECT_STATE_KEY)
    }

    companion object {
        private const val USERNAME_KEY = "username"
        internal const val SERVER_URL_KEY = "serverUrl"
        private const val PASSWORD_KEY = "password"
        private const val OPEN_ID_CONNECT_STATE_KEY = "oicState"
    }
}
