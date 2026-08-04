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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.RSAPublicKey
import java.util.Base64

@RunWith(JUnit4::class)
class Base64UrlShould {

    private val jdkEncoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
    private val jdkDecoder: Base64.Decoder = Base64.getUrlDecoder()

    @Test
    fun encode_matches_the_jdk_url_encoder_without_padding() {
        val random = SecureRandom()
        for (size in 0..64) {
            val bytes = ByteArray(size).also { random.nextBytes(it) }
            assertThat(Base64Url.encode(bytes)).isEqualTo(jdkEncoder.encodeToString(bytes))
        }
    }

    @Test
    fun encode_produces_only_the_url_safe_alphabet_without_padding_or_line_breaks() {
        val random = SecureRandom()
        val urlSafeAlphabet = Regex("^[A-Za-z0-9_-]*$")

        repeat(REPETITIONS) {
            val bytes = ByteArray(RANDOM_BYTES).also { random.nextBytes(it) }
            val encoded = Base64Url.encode(bytes)

            assertThat(encoded).matches(urlSafeAlphabet.pattern)
            assertThat(encoded).doesNotContain("=")
            assertThat(encoded).doesNotContain("+")
            assertThat(encoded).doesNotContain("/")
            assertThat(encoded).doesNotContain("\n")
        }
    }

    @Test
    fun decode_round_trips_encode() {
        val random = SecureRandom()
        for (size in 0..64) {
            val bytes = ByteArray(size).also { random.nextBytes(it) }
            assertThat(Base64Url.decode(Base64Url.encode(bytes))).isEqualTo(bytes)
        }
    }

    @Test
    fun decode_accepts_both_padded_and_unpadded_input() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val unpadded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        val padded = Base64.getUrlEncoder().encodeToString(bytes)

        assertThat(Base64Url.decode(unpadded)).isEqualTo(bytes)
        assertThat(Base64Url.decode(padded)).isEqualTo(bytes)
    }

    @Test
    fun decodeToString_reads_back_an_encoded_string() {
        val text = """{"alg":"RS256","kid":"key-1"}"""
        assertThat(Base64Url.decodeToString(Base64Url.encode(text))).isEqualTo(text)
    }

    @Test
    fun encodeUnsigned_of_the_default_rsa_exponent_is_AQAB() {
        val exponent = BigInteger.valueOf(RSA_DEFAULT_EXPONENT)
        assertThat(Base64Url.encodeUnsigned(exponent.toByteArray())).isEqualTo("AQAB")
    }

    @Test
    fun encodeUnsigned_drops_every_leading_zero_octet() {
        assertThat(decodeUnsigned(byteArrayOf(0, 0, 0, 1, 2))).isEqualTo(byteArrayOf(1, 2))
        assertThat(decodeUnsigned(byteArrayOf(0, 0x80.toByte()))).isEqualTo(byteArrayOf(0x80.toByte()))
        assertThat(decodeUnsigned(byteArrayOf(1, 0, 2))).isEqualTo(byteArrayOf(1, 0, 2))
    }

    @Test
    fun encodeUnsigned_of_an_all_zero_magnitude_is_a_single_zero_octet() {
        assertThat(decodeUnsigned(byteArrayOf(0))).isEqualTo(byteArrayOf(0))
        assertThat(decodeUnsigned(byteArrayOf(0, 0, 0))).isEqualTo(byteArrayOf(0))
        assertThat(decodeUnsigned(byteArrayOf())).isEqualTo(byteArrayOf(0))
    }

    @Test
    fun encodeUnsigned_strips_the_two_complement_sign_byte() {
        // 255 and 128 have the most significant bit set, so BigInteger.toByteArray prepends 0x00.
        assertThat(decodeUnsigned(BigInteger.valueOf(255))).isEqualTo(byteArrayOf(0xFF.toByte()))
        assertThat(decodeUnsigned(BigInteger.valueOf(128))).isEqualTo(byteArrayOf(0x80.toByte()))
        assertThat(decodeUnsigned(BigInteger("FF00", 16))).isEqualTo(byteArrayOf(0xFF.toByte(), 0x00))

        // 127 and 1 do not, so nothing must be stripped.
        assertThat(decodeUnsigned(BigInteger.valueOf(127))).isEqualTo(byteArrayOf(0x7F))
        assertThat(decodeUnsigned(BigInteger.ONE)).isEqualTo(byteArrayOf(0x01))
    }

    @Test
    fun encodeUnsigned_preserves_the_value() {
        val values = listOf(
            BigInteger.ONE,
            BigInteger.valueOf(RSA_DEFAULT_EXPONENT),
            BigInteger.valueOf(255),
            BigInteger("DEADBEEF", 16),
            BigInteger(RSA_KEY_SIZE, SecureRandom()),
        )

        values.forEach { value ->
            assertThat(BigInteger(1, decodeUnsigned(value))).isEqualTo(value)
        }
    }

    @Test
    fun encodeUnsigned_of_an_rsa2048_modulus_is_256_bytes_without_a_leading_zero() {
        val keyPair = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(RSA_KEY_SIZE) }
            .generateKeyPair()
        val modulus = (keyPair.public as RSAPublicKey).modulus

        val decoded = decodeUnsigned(modulus)

        assertThat(decoded).hasLength(RSA_KEY_SIZE / 8)
        assertThat(decoded[0].toInt()).isNotEqualTo(0)
        assertThat(BigInteger(1, decoded)).isEqualTo(modulus)
    }

    private fun decodeUnsigned(magnitude: ByteArray): ByteArray =
        jdkDecoder.decode(Base64Url.encodeUnsigned(magnitude))

    private fun decodeUnsigned(value: BigInteger): ByteArray = decodeUnsigned(value.toByteArray())

    companion object {
        private const val RSA_KEY_SIZE = 2048
        private const val RSA_DEFAULT_EXPONENT = 65537L
        private const val RANDOM_BYTES = 48
        private const val REPETITIONS = 20
    }
}
