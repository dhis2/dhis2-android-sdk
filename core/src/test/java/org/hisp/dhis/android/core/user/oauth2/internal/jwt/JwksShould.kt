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
package org.hisp.dhis.android.core.user.oauth2.internal.jwt

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

@RunWith(JUnit4::class)
class JwksShould {

    private val jdkDecoder: Base64.Decoder = Base64.getUrlDecoder()

    private lateinit var publicKey: RSAPublicKey

    @Before
    fun setUp() {
        publicKey = generateRsaPublicKey()
    }

    @Test
    fun rsaJwkSet_holds_a_single_keys_member_with_one_key() {
        val jwkSet = Jwks.rsaJwkSet(publicKey.toKeyMaterial(), KEY_ID)

        assertThat(jwkSet.keys).containsExactly("keys")
        assertThat(jwkSet["keys"]!!.jsonArray).hasSize(1)
    }

    /**
     * The member set is part of the wire contract of the Dynamic Client Registration payload. It
     * must stay exactly as it is: optional members such as `use` or `alg` are not expected by the
     * server and adding them would silently change the request.
     */
    @Test
    fun rsaPublicJwk_holds_exactly_kty_e_kid_and_n() {
        val jwk = Jwks.rsaPublicJwk(publicKey.toKeyMaterial(), KEY_ID)

        assertThat(jwk.keys).containsExactly("kty", "e", "kid", "n")
    }

    @Test
    fun rsaPublicJwk_reports_the_key_type_the_key_id_and_the_exponent() {
        val jwk = Jwks.rsaPublicJwk(publicKey.toKeyMaterial(), KEY_ID)

        assertThat(jwk["kty"]!!.jsonPrimitive.content).isEqualTo("RSA")
        assertThat(jwk["kid"]!!.jsonPrimitive.content).isEqualTo(KEY_ID)
        assertThat(jwk["e"]!!.jsonPrimitive.content).isEqualTo("AQAB")
    }

    @Test
    fun rsaPublicJwk_round_trips_to_the_original_public_key() {
        repeat(KEY_PAIRS) {
            val key = if (it == 0) publicKey else generateRsaPublicKey()
            val jwk = Jwks.rsaPublicJwk(key.toKeyMaterial(), KEY_ID)

            val rebuilt = rebuildPublicKey(jwk)

            assertThat(rebuilt.modulus).isEqualTo(key.modulus)
            assertThat(rebuilt.publicExponent).isEqualTo(key.publicExponent)
            assertThat(rebuilt.encoded).isEqualTo(key.encoded)
        }
    }

    @Test
    fun rsa_modulus_encodes_to_exactly_256_bytes_without_a_leading_zero() {
        repeat(KEY_PAIRS) {
            val key = if (it == 0) publicKey else generateRsaPublicKey()
            val jwk = Jwks.rsaPublicJwk(key.toKeyMaterial(), KEY_ID)

            val modulusBytes = jdkDecoder.decode(jwk["n"]!!.jsonPrimitive.content)

            assertThat(modulusBytes).hasLength(RSA_KEY_SIZE / 8)
            assertThat(modulusBytes[0].toInt()).isNotEqualTo(0)
        }
    }

    /** This is what `DCRNetworkHandlerImpl` does with the string returned by `createJWKS`. */
    @Test
    fun jwk_set_serializes_to_json_that_parses_back_to_the_same_structure() {
        val jwkSet = Jwks.rsaJwkSet(publicKey.toKeyMaterial(), KEY_ID)

        val reparsed = KotlinxJsonParser.instance.parseToJsonElement(jwkSet.toString())

        assertThat(reparsed).isEqualTo(jwkSet)
    }

    /** Mirrors what `KeyStoreManager` does with the key it reads from the Android key store. */
    private fun RSAPublicKey.toKeyMaterial(): RsaPublicKeyMaterial = RsaPublicKeyMaterial(
        modulus = modulus.toByteArray(),
        publicExponent = publicExponent.toByteArray(),
    )

    private fun rebuildPublicKey(jwk: JsonObject): RSAPublicKey {
        val modulus = BigInteger(1, jdkDecoder.decode(jwk["n"]!!.jsonPrimitive.content))
        val exponent = BigInteger(1, jdkDecoder.decode(jwk["e"]!!.jsonPrimitive.content))

        return KeyFactory.getInstance("RSA")
            .generatePublic(RSAPublicKeySpec(modulus, exponent)) as RSAPublicKey
    }

    private fun generateRsaPublicKey(): RSAPublicKey =
        KeyPairGenerator.getInstance("RSA")
            .apply { initialize(RSA_KEY_SIZE) }
            .generateKeyPair()
            .public as RSAPublicKey

    companion object {
        private const val KEY_ID = "key-1"
        private const val RSA_KEY_SIZE = 2048
        private const val KEY_PAIRS = 3
    }
}
