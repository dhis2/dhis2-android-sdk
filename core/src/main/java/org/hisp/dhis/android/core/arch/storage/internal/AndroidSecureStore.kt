/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64
import androidx.core.content.edit

/**
 * Values are encrypted with AES-256-GCM using a key of the Android Keystore. The name of each entry is bound
 * to its value as additional authenticated data, so a value moved under another key fails to decrypt.
 */
@Suppress("TooGenericExceptionThrown")
class AndroidSecureStore(context: Context) : SecureStore {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    private val secretKey: SecretKey

    init {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            secretKey = keyStore.getKey(ALIAS, null) as SecretKey? ?: generateKey()
            LegacyRsaSecureStoreMigration(context, keyStore).migrate(::storeMigratedEntries)
        } catch (e: GeneralSecurityException) {
            throw keyStoreError(e, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        } catch (e: IOException) {
            throw keyStoreError(e, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        }
    }

    private fun generateKey(): SecretKey {
        val spec = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        return try {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).run {
                init(spec)
                generateKey()
            }
        } catch (e: GeneralSecurityException) {
            throw keyStoreError(e, D2ErrorCode.CANT_INSTANTIATE_KEYSTORE)
        }
    }

    override fun setData(key: String, data: String?) {
        if (data == null) {
            removeData(key)
        } else {
            preferences.edit { putString(key, encrypt(key, data)) }
        }
    }

    override fun getData(key: String): String? {
        return preferences.getString(key, null)?.let { decrypt(key, it) }
    }

    override fun removeData(key: String) {
        preferences.edit { remove(key) }
    }

    override fun getAllKeys(): Set<String> {
        return preferences.all.keys
    }

    private fun storeMigratedEntries(entries: Map<String, String>): Boolean {
        val editor = preferences.edit()
        entries.filterKeys { !preferences.contains(it) }.forEach { (key, value) ->
            editor.putString(key, encrypt(key, value))
        }
        return editor.commit()
    }

    private fun encrypt(key: String, data: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher.updateAAD(key.encodeToByteArray())
            return Base64.encode(cipher.iv + cipher.doFinal(data.encodeToByteArray()))
        } catch (e: GeneralSecurityException) {
            throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
        }
    }

    private fun decrypt(key: String, value: String): String {
        try {
            val bytes = Base64.decode(value)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_SIZE_BITS, bytes, 0, IV_SIZE))
            cipher.updateAAD(key.encodeToByteArray())
            return cipher.doFinal(bytes, IV_SIZE, bytes.size - IV_SIZE).decodeToString()
        } catch (e: GeneralSecurityException) {
            throw RuntimeException("Couldn't get value from AndroidSecureStore for key: $key", e)
        }
    }

    private fun keyStoreError(ex: Exception, d2ErrorCode: D2ErrorCode): D2Error {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.SDK)
            .errorCode(d2ErrorCode)
            .errorDescription(ex.message ?: "KeyStore error")
            .originalException(ex)
            .created(Date())
            .build()
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALIAS = "dhis_sdk_aes_key"
        private const val PREFERENCES_FILE = "dhis2_secure_store"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 12
        private const val TAG_SIZE_BITS = 128
    }
}
