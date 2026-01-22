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

import android.util.Base64
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.util.Date
import java.util.UUID

internal object JWTHelper {

    fun createClientAssertion(
        clientId: String,
        tokenEndpoint: String,
        privateKey: PrivateKey,
        keyId: String,
        expiresInSeconds: Long = 60,
    ): String {
        val now = Date()
        val expirationTime = Date(now.time + expiresInSeconds * 1000)

        val base = try {
            val url = java.net.URL(tokenEndpoint)
            val pathParts = url.path.split("/").filter { it.isNotBlank() }
            val contextPath = if (pathParts.isNotEmpty()) "/${pathParts[0]}" else ""
            url.protocol + "://" + url.host + (if (url.port != -1) ":${url.port}" else "") + contextPath + "/"
        } catch (e: Exception) {
            tokenEndpoint
        }

        val claimsSet = JWTClaimsSet.Builder()
            .issuer(clientId)
            .subject(clientId)
            .audience(base)
            .issueTime(now)
            .expirationTime(expirationTime)
            .jwtID(UUID.randomUUID().toString())
            .build()

        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(keyId)
            .build()

        val signedJWT = SignedJWT(header, claimsSet)
        val signer = RSASSASigner(privateKey)
        signedJWT.sign(signer)

        return signedJWT.serialize()
    }

    fun generateState(): String {
        return UUID.randomUUID().toString()
    }

    fun validateJWT(jwtString: String): JWTClaimsSet? {
        return try {
            val signedJWT = SignedJWT.parse(jwtString)
            val claims = signedJWT.jwtClaimsSet

            val now = Date()
            if (claims.expirationTime != null && claims.expirationTime.before(now)) {
                null
            } else {
                claims
            }
        } catch (e: Exception) {
            null
        }
    }

    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifierBytes = ByteArray(48)
        secureRandom.nextBytes(codeVerifierBytes)
        return Base64.encodeToString(
            codeVerifierBytes,
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
        )
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return Base64.encodeToString(
            digest,
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
        )
    }
}
