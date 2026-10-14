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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64
import java.util.UUID

@RunWith(JUnit4::class)
class JWTHelperShould {

    private val jdkDecoder: Base64.Decoder = Base64.getUrlDecoder()
    private val json = KotlinxJsonParser.instance

    private lateinit var keyPair: KeyPair

    @Before
    fun setUp() {
        keyPair = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(RSA_KEY_SIZE) }
            .generateKeyPair()
    }

    // region createClientAssertion

    @Test
    fun createClientAssertion_returns_three_base64url_segments() {
        val parts = createAssertion().split(".")

        assertThat(parts).hasSize(3)
        parts.forEach { assertThat(it).matches("[A-Za-z0-9_-]+") }
    }

    @Test
    fun createClientAssertion_is_signed_with_the_matching_private_key() {
        val assertion = createAssertion()
        val parts = assertion.split(".")
        val signingInput = "${parts[0]}.${parts[1]}"

        val verified = Signature.getInstance(JCA_SIGNATURE_ALGORITHM).run {
            initVerify(keyPair.public)
            update(signingInput.toByteArray(Charsets.US_ASCII))
            verify(jdkDecoder.decode(parts[2]))
        }

        assertThat(verified).isTrue()
    }

    @Test
    fun createClientAssertion_signature_does_not_verify_for_a_tampered_payload() {
        val parts = createAssertion().split(".")
        val tamperedPayload = parts[1].dropLast(1) + if (parts[1].last() == 'A') 'B' else 'A'
        val signingInput = "${parts[0]}.$tamperedPayload"

        val verified = Signature.getInstance(JCA_SIGNATURE_ALGORITHM).run {
            initVerify(keyPair.public)
            update(signingInput.toByteArray(Charsets.US_ASCII))
            verify(jdkDecoder.decode(parts[2]))
        }

        assertThat(verified).isFalse()
    }

    /**
     * The header must stay limited to `alg` and `kid`. `typ` is allowed by RFC 7515 but it was not
     * part of what the SDK used to send, so adding it would change the bytes reaching the server.
     */
    @Test
    fun createClientAssertion_header_holds_exactly_the_algorithm_and_the_key_id() {
        val header = headerOf(createAssertion())

        assertThat(header.keys).containsExactly("alg", "kid")
        assertThat(header["alg"]!!.jsonPrimitive.content).isEqualTo("RS256")
        assertThat(header["kid"]!!.jsonPrimitive.content).isEqualTo(KEY_ID)
    }

    @Test
    fun createClientAssertion_claims_hold_exactly_the_rfc7523_set() {
        val claims = claimsOf(createAssertion())

        assertThat(claims.keys).containsExactly("iss", "sub", "aud", "iat", "exp", "jti")
        assertThat(claims["iss"]!!.jsonPrimitive.content).isEqualTo(CLIENT_ID)
        assertThat(claims["sub"]!!.jsonPrimitive.content).isEqualTo(CLIENT_ID)
    }

    @Test
    fun createClientAssertion_expires_one_minute_after_issuance_by_default() {
        val claims = claimsOf(createAssertion())

        val issuedAt = claims["iat"]!!.jsonPrimitive.long
        val expiration = claims["exp"]!!.jsonPrimitive.long

        assertThat(expiration - issuedAt).isEqualTo(DEFAULT_EXPIRES_IN_SECONDS)
    }

    @Test
    fun createClientAssertion_expires_the_requested_number_of_seconds_after_issuance() {
        val claims = claimsOf(createAssertion(expiresInSeconds = EXPIRES_IN_SECONDS))

        val issuedAt = claims["iat"]!!.jsonPrimitive.long
        val expiration = claims["exp"]!!.jsonPrimitive.long

        assertThat(expiration - issuedAt).isEqualTo(EXPIRES_IN_SECONDS)
    }

    /**
     * A single-valued audience has to be a plain JSON string. Emitting a one-element array instead
     * is the most likely way to silently break the server side RFC 7523 validation.
     */
    @Test
    fun createClientAssertion_audience_is_a_json_string_and_not_an_array() {
        val audience = claimsOf(createAssertion())["aud"]

        assertThat(audience).isInstanceOf(JsonPrimitive::class.java)
        assertThat((audience as JsonPrimitive).isString).isTrue()
    }

    @Test
    fun createClientAssertion_derives_the_audience_from_the_context_path() {
        val expectedAudiences = mapOf(
            "https://play.dhis2.org/dev/oauth2/token" to "https://play.dhis2.org/dev/",
            // A DHIS2 instance deployed at the root has no context path, so the first path segment
            // is picked up instead. Locked in here because it is the current behaviour.
            "https://server.com/oauth2/token" to "https://server.com/oauth2/",
            "http://localhost:8080/api/oauth2/token" to "http://localhost:8080/api/",
            "https://server.com" to "https://server.com/",
            "not-a-url" to "not-a-url",
        )

        expectedAudiences.forEach { (tokenEndpoint, expectedAudience) ->
            val claims = claimsOf(createAssertion(tokenEndpoint = tokenEndpoint))

            assertThat(claims["aud"]!!.jsonPrimitive.content).isEqualTo(expectedAudience)
        }
    }

    @Test
    fun createClientAssertion_uses_a_fresh_uuid_identifier_on_every_call() {
        val first = claimsOf(createAssertion())["jti"]!!.jsonPrimitive.content
        val second = claimsOf(createAssertion())["jti"]!!.jsonPrimitive.content

        assertThat(UUID.fromString(first)).isNotNull()
        assertThat(first).isNotEqualTo(second)
    }

    // endregion

    // region isUnexpired

    @Test
    fun isUnexpired_accepts_a_token_expiring_in_the_future() {
        val jwt = TestJwtFactory.iatJwt(keyPair.private, TestJwtFactory.nowSeconds() + TTL_SECONDS)

        assertThat(JWTHelper.isUnexpired(jwt)).isTrue()
    }

    @Test
    fun isUnexpired_rejects_a_token_that_already_expired() {
        val jwt = TestJwtFactory.iatJwt(keyPair.private, TestJwtFactory.nowSeconds() - TTL_SECONDS)

        assertThat(JWTHelper.isUnexpired(jwt)).isFalse()
    }

    @Test
    fun isUnexpired_accepts_a_token_without_an_expiration_claim() {
        val jwt = TestJwtFactory.iatJwtWithoutExpiration(keyPair.private)

        assertThat(JWTHelper.isUnexpired(jwt)).isTrue()
    }

    @Test
    fun isUnexpired_rejects_a_malformed_token() {
        assertThat(JWTHelper.isUnexpired("")).isFalse()
        assertThat(JWTHelper.isUnexpired("abc")).isFalse()
        assertThat(JWTHelper.isUnexpired("header.payload")).isFalse()
        assertThat(JWTHelper.isUnexpired("a.b.c.d")).isFalse()
    }

    @Test
    fun isUnexpired_rejects_a_token_whose_segments_are_not_valid_base64url_json() {
        assertThat(JWTHelper.isUnexpired("a.b.c")).isFalse()
        assertThat(JWTHelper.isUnexpired("!!!.!!!.!!!")).isFalse()
    }

    @Test
    fun isUnexpired_rejects_a_token_whose_payload_is_not_a_json_object() {
        val jwt = TestJwtFactory.signedJwt(
            keyPair.private,
            """{"alg":"RS256","kid":"test-kid"}""",
            """[1,2,3]""",
        )

        assertThat(JWTHelper.isUnexpired(jwt)).isFalse()
    }

    // endregion

    // region PKCE and state

    /** Test vector from RFC 7636 appendix B. */
    @Test
    fun generateCodeChallenge_matches_the_rfc7636_test_vector() {
        val challenge = JWTHelper.generateCodeChallenge("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")

        assertThat(challenge).isEqualTo("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")
    }

    @Test
    fun generateCodeVerifier_returns_a_unique_unpadded_url_safe_string() {
        val first = JWTHelper.generateCodeVerifier()
        val second = JWTHelper.generateCodeVerifier()

        // 48 random bytes encoded without padding.
        assertThat(first).matches("[A-Za-z0-9_-]{64}")
        assertThat(first).isNotEqualTo(second)
    }

    @Test
    fun generateState_returns_a_unique_uuid() {
        val first = JWTHelper.generateState()
        val second = JWTHelper.generateState()

        assertThat(UUID.fromString(first)).isNotNull()
        assertThat(first).isNotEqualTo(second)
    }

    // endregion

    /** Uses the default expiration of [JWTHelper.createClientAssertion]. */
    private fun createAssertion(tokenEndpoint: String = TOKEN_ENDPOINT): String =
        JWTHelper.createClientAssertion(
            clientId = CLIENT_ID,
            tokenEndpoint = tokenEndpoint,
            privateKey = keyPair.private,
            keyId = KEY_ID,
        )

    private fun createAssertion(expiresInSeconds: Long): String =
        JWTHelper.createClientAssertion(
            clientId = CLIENT_ID,
            tokenEndpoint = TOKEN_ENDPOINT,
            privateKey = keyPair.private,
            keyId = KEY_ID,
            expiresInSeconds = expiresInSeconds,
        )

    private fun headerOf(assertion: String): JsonObject = decodeSegment(assertion, 0)

    private fun claimsOf(assertion: String): JsonObject = decodeSegment(assertion, 1)

    private fun decodeSegment(assertion: String, index: Int): JsonObject {
        val decoded = String(jdkDecoder.decode(assertion.split(".")[index]), Charsets.UTF_8)
        return json.parseToJsonElement(decoded).jsonObject
    }

    companion object {
        private const val CLIENT_ID = "client-1"
        private const val KEY_ID = "key-1"
        private const val TOKEN_ENDPOINT = "https://play.dhis2.org/dev/oauth2/token"
        private const val JCA_SIGNATURE_ALGORITHM = "SHA256withRSA"
        private const val RSA_KEY_SIZE = 2048
        private const val DEFAULT_EXPIRES_IN_SECONDS = 60L
        private const val EXPIRES_IN_SECONDS = 120L
        private const val TTL_SECONDS = 300L
    }
}
