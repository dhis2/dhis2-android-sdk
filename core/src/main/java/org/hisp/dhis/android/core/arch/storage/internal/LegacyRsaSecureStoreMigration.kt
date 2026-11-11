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

import android.content.Context
import android.util.Base64
import org.hisp.dhis.android.core.arch.helpers.internal.D2Logger
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.PrivateKey
import java.security.ProviderException
import java.security.interfaces.RSAKey
import javax.crypto.Cipher

/**
 * Moves the values encrypted with the RSA key used before AES-GCM, which shared their preferences file with
 * [AndroidInsecureStore], to [AndroidSecureStore]. The RSA key is deleted once every value is moved, so the
 * migration only runs again if a value could not be moved.
 */
internal class LegacyRsaSecureStoreMigration(context: Context, private val keyStore: KeyStore) {
    private val preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun migrate(store: (Map<String, String>) -> Boolean) {
        val privateKey = keyStore.getKey(ALIAS, null) as? PrivateKey? ?: return
        val blockSize = ((privateKey as RSAKey).modulus.bitLength() + Byte.SIZE_BITS - 1) / Byte.SIZE_BITS

        val candidates = preferences.all.mapNotNull { (key, value) ->
            (value as? String)?.let { decodeOrNull(it) }?.takeIf { it.size == blockSize }?.let { key to it }
        }
        val migrated = candidates.mapNotNull { (key, value) ->
            decryptOrNull(privateKey, value)?.let { key to it }
        }.toMap()

        if (store(migrated)) {
            val editor = preferences.edit()
            migrated.keys.forEach { editor.remove(it) }
            if (editor.commit() && migrated.size == candidates.size) {
                keyStore.deleteEntry(ALIAS)
            } else {
                D2Logger.w(TAG,"${candidates.size - migrated.size} secure store values could not be migrated")
            }
        }
    }

    private fun decodeOrNull(value: String): ByteArray? {
        return try {
            Base64.decode(value, Base64.DEFAULT)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun decryptOrNull(privateKey: PrivateKey, value: ByteArray): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(value).decodeToString()
        } catch (_: GeneralSecurityException) {
            null
        } catch (_: ProviderException) {
            null
        }
    }

    private companion object {
        const val TAG = "SecureStore"
        const val PREFERENCES_FILE = "preferences"
        const val ALIAS = "dhis_sdk_key"
        const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        const val PROVIDER = "AndroidKeyStoreBCWorkaround"
    }
}
