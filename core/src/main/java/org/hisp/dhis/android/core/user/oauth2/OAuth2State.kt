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
package org.hisp.dhis.android.core.user.oauth2

import org.json.JSONObject

data class OAuth2State(
    val clientId: String,
    val keyId: String,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresAt: Long,
    val scope: String?,
) {
    @Suppress("MagicNumber")
    fun needsTokenRefresh(): Boolean {
        val currentTime = System.currentTimeMillis().div(1000)
        return (currentTime + BUFFER) >= expiresAt
    }

    fun jsonSerializeString(): String {
        return JSONObject().apply {
            put(KEY_CLIENT_ID, clientId)
            put(KEY_KEY_ID, keyId)
            put(KEY_ACCESS_TOKEN, accessToken)
            put(KEY_REFRESH_TOKEN, refreshToken)
            put(KEY_EXPIRES_AT, expiresAt)
            put(KEY_SCOPE, scope)
        }.toString()
    }

    companion object {
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_KEY_ID = "key_id"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_SCOPE = "scope"
        private const val BUFFER = 60


    }
}
