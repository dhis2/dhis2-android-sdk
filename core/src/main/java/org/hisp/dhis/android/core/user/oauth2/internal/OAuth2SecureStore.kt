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

import org.hisp.dhis.android.core.arch.storage.internal.SecureStore
import org.koin.core.annotation.Singleton

@Singleton
internal class OAuth2SecureStore(
    private val secureStore: SecureStore,
) {
    var clientId: String?
        get() = secureStore.getData(KEY_CLIENT_ID)
        set(value) = secureStore.setData(KEY_CLIENT_ID, value)

    var keyId: String?
        get() = secureStore.getData(KEY_KEY_ID)
        set(value) = secureStore.setData(KEY_KEY_ID, value)

    var serverUrl: String?
        get() = secureStore.getData(KEY_SERVER_URL)
        set(value) = secureStore.setData(KEY_SERVER_URL, value)

    var isRegistered: Boolean
        get() = secureStore.getData(KEY_IS_REGISTERED)?.toBoolean() ?: false
        set(value) = secureStore.setData(KEY_IS_REGISTERED, value.toString())

    var registrationDate: Long
        get() = secureStore.getData(KEY_REGISTRATION_DATE)?.toLongOrNull() ?: 0L
        set(value) = secureStore.setData(KEY_REGISTRATION_DATE, value.toString())

    var tempState: String?
        get() = secureStore.getData(KEY_TEMP_STATE)
        set(value) = secureStore.setData(KEY_TEMP_STATE, value)

    var tempCodeVerifier: String?
        get() = secureStore.getData(KEY_TEMP_CODE_VERIFIER)
        set(value) = secureStore.setData(KEY_TEMP_CODE_VERIFIER, value)

    fun clearRegistration() {
        clientId = null
        keyId = null
        serverUrl = null
        isRegistered = false
        registrationDate = 0L
    }

    fun clearTemporaryData() {
        tempState = null
        tempCodeVerifier = null
    }

    companion object {
        private const val KEY_CLIENT_ID = "oauth2_client_id"
        private const val KEY_KEY_ID = "oauth2_key_id"
        private const val KEY_SERVER_URL = "oauth2_server_url"
        private const val KEY_IS_REGISTERED = "oauth2_is_registered"
        private const val KEY_REGISTRATION_DATE = "oauth2_registration_date"
        private const val KEY_TEMP_STATE = "oauth2_temp_state"
        private const val KEY_TEMP_CODE_VERIFIER = "oauth2_temp_code_verifier"
    }
}
