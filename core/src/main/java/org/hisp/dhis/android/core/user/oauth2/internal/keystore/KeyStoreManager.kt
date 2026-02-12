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
package org.hisp.dhis.android.core.user.oauth2.internal.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import org.koin.core.annotation.Singleton
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

@Singleton
internal class KeyStoreManager {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    fun generateKeyPair(): String {
        val keyId = UUID.randomUUID().toString()

        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            ANDROID_KEYSTORE,
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            getKeyAlias(keyId),
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY,
        )
            .setKeySize(KEY_SIZE)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .build()

        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()

        return keyId
    }

    fun getPrivateKey(keyId: String): PrivateKey? {
        return keyStore.getKey(getKeyAlias(keyId), null) as? PrivateKey
    }

    fun getPublicKey(keyId: String): PublicKey? {
        return keyStore.getCertificate(getKeyAlias(keyId))?.publicKey
    }

    fun createJWKS(keyId: String): String {
        val publicKey = getPublicKey(keyId) as? RSAPublicKey
            ?: throw IllegalStateException("Public key not found for keyId: $keyId")

        val rsaKey = RSAKey.Builder(publicKey)
            .keyID(keyId)
            .build()

        val jwkSet = JWKSet(rsaKey)
        return jwkSet.toString()
    }

    fun hasKey(keyId: String): Boolean {
        return keyStore.containsAlias(getKeyAlias(keyId))
    }

    fun deleteKey(keyId: String) {
        if (hasKey(keyId)) {
            keyStore.deleteEntry(getKeyAlias(keyId))
        }
    }

    private fun getKeyAlias(keyId: String): String = "$KEY_ALIAS_PREFIX$keyId"

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS_PREFIX = "dhis2_oauth_key_"
        private const val KEY_SIZE = 2048
    }
}
