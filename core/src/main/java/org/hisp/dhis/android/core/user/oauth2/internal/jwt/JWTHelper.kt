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

import android.util.Log
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import java.net.MalformedURLException
import java.net.URL
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Signature
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Minimal JOSE support for the OAuth2 flow: it only implements what the DHIS2 OAuth2 client needs,
 * namely signing the RFC 7523 `private_key_jwt` client assertion, reading the expiration of a
 * server-issued JWT and generating the PKCE parameters.
 */
@OptIn(ExperimentalUuidApi::class)
internal object JWTHelper {
    private const val TAG = "JWTHelper"

    private const val BYTEARRAYSIZE = 48
    private const val EXPIRESINSECONDS = 60L
    private const val MILLIS_PER_SECOND = 1000L

    private const val JWS_ALGORITHM = "RS256"
    private const val JCA_SIGNATURE_ALGORITHM = "SHA256withRSA"
    private const val DIGEST_ALGORITHM = "SHA-256"
    private const val JWT_PART_COUNT = 3

    private val json = KotlinxJsonParser.instance

    /**
     * Builds the RFC 7523 `private_key_jwt` client assertion, signed with the RSA private key held
     * in the Android KeyStore.
     */
    fun createClientAssertion(
        clientId: String,
        tokenEndpoint: String,
        privateKey: PrivateKey,
        keyId: String,
        expiresInSeconds: Long = EXPIRESINSECONDS,
    ): String {
        val issuedAt = System.currentTimeMillis() / MILLIS_PER_SECOND

        // No `typ` member: the assertion is unambiguously a JWT in this context and adding it would
        // change the bytes sent to the server.
        val header = buildJsonObject {
            put("alg", JWS_ALGORITHM)
            put("kid", keyId)
        }

        val claims = buildJsonObject {
            put("iss", clientId)
            put("sub", clientId)
            // Single-valued audience: a plain JSON string, not an array.
            put("aud", audienceOf(tokenEndpoint))
            put("iat", issuedAt)
            put("exp", issuedAt + expiresInSeconds)
            put("jti", Uuid.random().toString())
        }

        val signingInput = "${encodeSegment(header)}.${encodeSegment(claims)}"

        return "$signingInput.${Base64Url.encode(sign(signingInput, privateKey))}"
    }

    fun generateState(): String {
        return Uuid.random().toString()
    }

    /**
     * Returns whether [jwt] parses as a JWT whose `exp` claim is not in the past. A JWT without an
     * `exp` claim is considered unexpired.
     *
     * The signature is deliberately not verified: this is only used for the server-issued initial
     * access token, whose signature the SDK has no key to check.
     */
    @Suppress("TooGenericExceptionCaught")
    fun isUnexpired(jwt: String): Boolean {
        return try {
            val expirationSeconds = expirationSecondsOf(jwt)
            expirationSeconds == null || expirationSeconds * MILLIS_PER_SECOND >= System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e(TAG, "Invalid JWT: $jwt", e)
            false
        }
    }

    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifierBytes = ByteArray(BYTEARRAYSIZE)
        secureRandom.nextBytes(codeVerifierBytes)
        return Base64Url.encode(codeVerifierBytes)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        // RFC 7636 mandates the ASCII bytes of the verifier. The verifier alphabet is a subset of
        // ASCII, so UTF-8 yields the very same bytes and is available on every platform.
        val bytes = codeVerifier.toByteArray(Charsets.UTF_8)
        val messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM)
        val digest = messageDigest.digest(bytes)
        return Base64Url.encode(digest)
    }

    private fun encodeSegment(element: JsonObject): String {
        return Base64Url.encode(json.encodeToString(JsonObject.serializer(), element))
    }

    private fun sign(signingInput: String, privateKey: PrivateKey): ByteArray {
        return Signature.getInstance(JCA_SIGNATURE_ALGORITHM).run {
            initSign(privateKey)
            // The signing input is `base64url.base64url`, so UTF-8 and ASCII are byte-identical.
            update(signingInput.toByteArray(Charsets.UTF_8))
            sign()
        }
    }

    /**
     * Derives the `aud` value from the token endpoint: scheme, host, optional port and the first
     * path segment, which is the DHIS2 context path, followed by a trailing slash.
     */
    private fun audienceOf(tokenEndpoint: String): String {
        return try {
            val url = URL(tokenEndpoint)
            val pathParts = url.path.split("/").filter { it.isNotBlank() }
            val contextPath = if (pathParts.isNotEmpty()) "/${pathParts[0]}" else ""
            url.protocol + "://" + url.host + (if (url.port != -1) ":${url.port}" else "") + contextPath + "/"
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Invalid token endpoint URL: $tokenEndpoint", e)
            tokenEndpoint
        }
    }

    private fun expirationSecondsOf(jwt: String): Long? {
        val parts = jwt.split(".")
        require(parts.size == JWT_PART_COUNT) { "Malformed JWT: expected $JWT_PART_COUNT parts" }

        // The header is parsed as well so that a structurally invalid token is rejected rather than
        // silently treated as a token without an expiration.
        json.parseToJsonElement(Base64Url.decodeToString(parts[0])).jsonObject
        val payload = json.parseToJsonElement(Base64Url.decodeToString(parts[1])).jsonObject

        return payload["exp"]?.jsonPrimitive?.long
    }
}
