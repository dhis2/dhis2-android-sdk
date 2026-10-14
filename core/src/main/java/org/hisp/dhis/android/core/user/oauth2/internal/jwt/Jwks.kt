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

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * The two RSA public key members a JWK needs, as big-endian byte arrays.
 *
 * Deliberately not a `java.security` key: the platform key type is an implementation detail of
 * whoever owns the key material (on Android, the hardware-backed key store), and it must not leak
 * into the code that builds the wire payload. Leading zero octets do not need to be trimmed by the
 * caller, [Base64Url.encodeUnsigned] takes care of that.
 */
internal class RsaPublicKeyMaterial(
    val modulus: ByteArray,
    val publicExponent: ByteArray,
)

/**
 * Builds the JSON Web Key documents (RFC 7517) sent to the server during Dynamic Client
 * Registration.
 *
 * This is deliberately kept apart from the key store so that it can be exercised with a plain JVM
 * key pair: the members of the emitted JWK are part of the wire contract with the server and must
 * not drift.
 */
internal object Jwks {

    private const val KEY_TYPE_RSA = "RSA"

    /**
     * Public RSA JWK. The member set is intentionally limited to `kty`, `e`, `kid` and `n`: adding
     * optional members such as `use` or `alg` would change the registration payload sent to the
     * server.
     */
    fun rsaPublicJwk(publicKey: RsaPublicKeyMaterial, keyId: String): JsonObject = buildJsonObject {
        put("kty", KEY_TYPE_RSA)
        put("e", Base64Url.encodeUnsigned(publicKey.publicExponent))
        put("kid", keyId)
        put("n", Base64Url.encodeUnsigned(publicKey.modulus))
    }

    /** JWK Set holding a single public RSA key. */
    fun rsaJwkSet(publicKey: RsaPublicKeyMaterial, keyId: String): JsonObject = buildJsonObject {
        put(
            "keys",
            buildJsonArray { add(rsaPublicJwk(publicKey, keyId)) },
        )
    }
}
