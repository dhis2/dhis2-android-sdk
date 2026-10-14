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

import java.security.PrivateKey
import java.security.Signature
import java.util.Base64

/**
 * Builds the signed JWT fixtures used by [JWTHelperShould] and `OAuth2HandlerImplShould`.
 *
 * It deliberately does not reuse the production JOSE code, and relies on `java.util.Base64` plus
 * the JCA directly instead. Two reasons:
 *
 * - It keeps the assertions differential. Were the fixtures built with the same codec as
 *   [Base64Url], a mistake in that codec — standard alphabet instead of url-safe, stray padding —
 *   would leave fixture and parser consistently wrong and the tests would still pass.
 * - [iatJwt] stands in for the initial access token issued by the DHIS2 server, which does not
 *   encode it with the SDK's own codec. Building it with a foreign library is what makes the
 *   fixture resemble an external issuer.
 *
 * [Base64UrlShould] and [JwksShould] do not use this factory: they compare against the JDK codec
 * directly, which is the same idea applied to a narrower surface.
 *
 * `java.util.Base64` requires API 26 and therefore cannot be used in the main source set, but unit
 * tests run on the JDK, so the constraint that shapes the production code does not apply here.
 */
internal object TestJwtFactory {

    private val encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()

    fun signedJwt(privateKey: PrivateKey, headerJson: String, claimsJson: String): String {
        val signingInput = encoder.encodeToString(headerJson.toByteArray(Charsets.UTF_8)) + "." +
            encoder.encodeToString(claimsJson.toByteArray(Charsets.UTF_8))

        val signature = Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update(signingInput.toByteArray(Charsets.US_ASCII))
            sign()
        }

        return "$signingInput.${encoder.encodeToString(signature)}"
    }

    /** Initial access token as issued by the server on device enrollment. */
    fun iatJwt(privateKey: PrivateKey, expirationSeconds: Long): String = signedJwt(
        privateKey,
        """{"alg":"RS256","kid":"test-kid"}""",
        """{"iss":"test","sub":"test","iat":${nowSeconds()},"exp":$expirationSeconds}""",
    )

    /** Initial access token without an `exp` claim. */
    fun iatJwtWithoutExpiration(privateKey: PrivateKey): String = signedJwt(
        privateKey,
        """{"alg":"RS256","kid":"test-kid"}""",
        """{"iss":"test","sub":"test","iat":${nowSeconds()}}""",
    )

    fun nowSeconds(): Long = System.currentTimeMillis() / 1000
}
