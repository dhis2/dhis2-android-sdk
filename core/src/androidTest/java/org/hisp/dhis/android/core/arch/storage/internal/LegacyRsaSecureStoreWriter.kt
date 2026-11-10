package org.hisp.dhis.android.core.arch.storage.internal

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

/**
 * Copy of the writing side of the RSA secure store used before the move to AES-GCM, including the chunking of
 * ChunkedSecureStore, to create the state that an existing installation has before the upgrade.
 */
internal class LegacyRsaSecureStoreWriter(context: Context) {
    private val preferences = context.getSharedPreferences(LEGACY_PREFERENCES_FILE, Context.MODE_PRIVATE)

    init {
        if (!hasLegacyKey()) {
            val spec = KeyGenParameterSpec.Builder(
                LEGACY_ALIAS,
                KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT,
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE).run {
                initialize(spec)
                generateKeyPair()
            }
        }
    }

    fun setData(key: String, data: String) {
        val publicKey = keyStore().getCertificate(LEGACY_ALIAS).publicKey
        val cipher = Cipher.getInstance(TRANSFORMATION, BC_WORKAROUND_PROVIDER)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encrypted = Base64.encodeToString(cipher.doFinal(data.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        preferences.edit().putString(key, encrypted).commit()
    }

    fun setChunkedData(key: String, data: String) {
        val chunks = data.chunked(CHUNK_SIZE)
        setData("${key}_[LEN]_", chunks.size.toString())
        chunks.forEachIndexed { i, chunk -> setData("${key}_[$i]_", chunk) }
    }

    companion object {
        const val LEGACY_PREFERENCES_FILE = "preferences"
        private const val LEGACY_ALIAS = "dhis_sdk_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val BC_WORKAROUND_PROVIDER = "AndroidKeyStoreBCWorkaround"
        private const val CHUNK_SIZE = 200

        private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        fun hasLegacyKey(): Boolean = keyStore().containsAlias(LEGACY_ALIAS)

        fun deleteLegacyKey() {
            keyStore().deleteEntry(LEGACY_ALIAS)
        }
    }
}
