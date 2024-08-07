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
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import java.io.IOException
import java.math.BigInteger
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.UnrecoverableEntryException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal

@Suppress("TooGenericExceptionThrown", "TooGenericExceptionCaught")
class AndroidSecureStore(context: Context) : SecureStore {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    init {
        try {
            val ks = getKeyStore()
            val privateKey = ks.getKey(ALIAS, null) as PrivateKey?

            if (privateKey == null || ks.getCertificate(ALIAS)?.publicKey == null) {
                generateKeys(ks, context)
            }
        } catch (ex: KeyStoreException) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        } catch (ex: CertificateException) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        } catch (ex: IOException) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        } catch (ex: NoSuchAlgorithmException) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        } catch (ex: UnrecoverableKeyException) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE)
        }
    }

    @Suppress("MagicNumber", "ThrowsCount")
    private fun generateKeys(ks: KeyStore, context: Context) {
        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        val start: Calendar = GregorianCalendar()
        val end: Calendar = GregorianCalendar()
        end.add(Calendar.YEAR, 10)
        val spec = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            KeyPairGeneratorSpec.Builder(context)
                .setAlias(ALIAS)
                .setSubject(X500Principal("CN=$ALIAS"))
                .setSerialNumber(BigInteger.valueOf(1337))
                .setStartDate(start.time).setEndDate(end.time)
                .build()
        } else {
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
        }

        try {
            val kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            kpGenerator.initialize(spec)
            kpGenerator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            deleteKeyStoreEntry(ks, ALIAS)
            throw keyStoreError(e, D2ErrorCode.CANT_INSTANTIATE_KEYSTORE)
        } catch (e: InvalidAlgorithmParameterException) {
            deleteKeyStoreEntry(ks, ALIAS)
            throw keyStoreError(e, D2ErrorCode.CANT_INSTANTIATE_KEYSTORE)
        } catch (e: NoSuchProviderException) {
            deleteKeyStoreEntry(ks, ALIAS)
            throw keyStoreError(e, D2ErrorCode.CANT_INSTANTIATE_KEYSTORE)
        }
    }

    override fun setData(key: String, data: String?) {
        if (data == null) {
            removeData(key)
        } else {
            try {
                val ks = getKeyStore()

                if (ks.getCertificate(ALIAS) == null) {
                    throw RuntimeException("Couldn't find certificate for key: $key")
                }

                val publicKey = ks.getCertificate(ALIAS).publicKey
                    ?: throw RuntimeException("Couldn't find publicKey for key: $key")

                val value = encrypt(publicKey, data.toByteArray(CHARSET))

                val editor = preferences.edit()
                editor.putString(key, value)
                editor.apply()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: InvalidKeyException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: NoSuchPaddingException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: IllegalBlockSizeException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: BadPaddingException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: KeyStoreException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: CertificateException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            } catch (e: IOException) {
                throw RuntimeException("Couldn't store value in AndroidSecureStore for key: $key", e)
            }
        }
    }

    override fun getData(key: String): String? {
        var value: String? = null
        try {
            val ks = getKeyStore()
            val privateKey = ks.getKey(ALIAS, null) as PrivateKey
            value = preferences.getString(key, null)

            return value?.let {
                String(decrypt(privateKey, value), CHARSET)
            }
        } catch (e: KeyStoreException) {
            throw getDataError(key, value, e)
        } catch (e: NoSuchAlgorithmException) {
            throw getDataError(key, value, e)
        } catch (e: CertificateException) {
            throw getDataError(key, value, e)
        } catch (e: IOException) {
            throw getDataError(key, value, e)
        } catch (e: UnrecoverableEntryException) {
            throw getDataError(key, value, e)
        } catch (e: InvalidKeyException) {
            throw getDataError(key, value, e)
        } catch (e: NoSuchPaddingException) {
            throw getDataError(key, value, e)
        } catch (e: IllegalBlockSizeException) {
            throw getDataError(key, value, e)
        } catch (e: BadPaddingException) {
            throw getDataError(key, value, e)
        }
    }

    override fun removeData(key: String) {
        val editor = preferences.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun getAllKeys(): Set<String> {
        return preferences.all.keys
    }

    private fun deleteKeyStoreEntry(ks: KeyStore?, entry: String) {
        try {
            ks?.deleteEntry(entry)
        } catch (e1: Exception) {
            Log.w("SECURE_STORE", "Cannot deleted entry $entry")
        }
    }

    private fun getDataError(key: String, value: Any?, e: Throwable): RuntimeException {
        val valueToDisplay = value ?: "null"
        val errorMessage = String.format(
            "Couldn't get value from AndroidSecureStore for key: %s and value: %s",
            key,
            valueToDisplay,
        )
        return RuntimeException(errorMessage, e)
    }

    private fun keyStoreError(ex: Exception, d2ErrorCode: D2ErrorCode): D2Error {
        return D2Error.builder()
            .errorComponent(D2ErrorComponent.SDK)
            .errorCode(d2ErrorCode)
            .errorDescription(ex.message)
            .originalException(ex)
            .created(Date())
            .build()
    }

    private fun getKeyStore(): KeyStore {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
        ks.load(null)
        return ks
    }

    companion object {
        private const val KEY_CIPHER_JELLYBEAN_PROVIDER = "AndroidOpenSSL"
        private const val KEY_CIPHER_MARSHMALLOW_PROVIDER = "AndroidKeyStoreBCWorkaround"

        private const val KEY_ALGORITHM_RSA = "RSA"

        private const val KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"
        private val CHARSET: Charset = StandardCharsets.UTF_8

        private const val PREFERENCES_FILE = "preferences"
        private const val ALIAS = "dhis_sdk_key"

        private val cipherInstance: Cipher
            get() {
                try {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Cipher.getInstance(RSA_ECB_PKCS1_PADDING, KEY_CIPHER_MARSHMALLOW_PROVIDER)
                    } else {
                        Cipher.getInstance(RSA_ECB_PKCS1_PADDING, KEY_CIPHER_JELLYBEAN_PROVIDER)
                    }
                } catch (exception: Exception) {
                    throw RuntimeException("getCipher: Failed to get an instance of Cipher", exception)
                }
            }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            IllegalBlockSizeException::class,
            BadPaddingException::class,
        )
        private fun encrypt(encryptionKey: PublicKey, data: ByteArray): String {
            val cipher = cipherInstance
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
            val encrypted = cipher.doFinal(data)
            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            IllegalBlockSizeException::class,
            BadPaddingException::class,
        )
        private fun decrypt(decryptionKey: PrivateKey, encryptedData: String): ByteArray {
            val encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT)
            val cipher = cipherInstance
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey)
            return cipher.doFinal(encryptedBuffer)
        }
    }
}
