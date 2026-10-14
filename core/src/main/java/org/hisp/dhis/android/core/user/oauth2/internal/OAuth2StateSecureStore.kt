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

import org.hisp.dhis.android.core.arch.storage.internal.ChunkedSecureStore
import org.hisp.dhis.android.core.configuration.internal.ServerUrlNormalizer
import org.hisp.dhis.android.core.user.oauth2.OAuth2State
import org.koin.core.annotation.Singleton
import java.security.MessageDigest

@Singleton
internal class OAuth2StateSecureStore(
    private val secureStore: ChunkedSecureStore,
) {
    fun set(serverUrl: String, username: String, state: OAuth2State) {
        secureStore.setData(buildKey(serverUrl, username), state.jsonSerializeString())
    }

    fun get(serverUrl: String, username: String): OAuth2State? {
        return secureStore.getData(buildKey(serverUrl, username))
            ?.let { OAuth2State.jsonDeserialize(it) }
    }

    fun remove(serverUrl: String, username: String) {
        secureStore.removeData(buildKey(serverUrl, username))
    }

    private fun buildKey(serverUrl: String, username: String): String {
        val normalized = ServerUrlNormalizer.normalize(serverUrl)
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            .digest("$normalized|$username".toByteArray())
        val hash = digest.take(HASH_BYTE_COUNT).joinToString("") {
            it.toUByte().toString(HEX_RADIX).padStart(HEX_STRING_WIDTH, '0')
        }
        return "$KEY_PREFIX$hash"
    }

    companion object {
        private const val KEY_PREFIX = "oauth2_state_"
        private const val HASH_ALGORITHM = "SHA-256"
        private const val HASH_BYTE_COUNT = 8
        private const val HEX_STRING_WIDTH = 2
        private const val HEX_RADIX = 16
    }
}
