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

import kotlin.io.encoding.Base64

/**
 * "base64url" codec (RFC 4648 section 5) without padding, as required by RFC 7515 (JWS) and
 * RFC 7517 (JWK).
 *
 * Backed by the Kotlin standard library instead of [android.util.Base64] so that it behaves
 * identically on Android (any API level) and on the JVM, which keeps the JOSE code unit testable.
 * Only multiplatform APIs are used here on purpose: no `java.*` type appears in the signatures.
 */
internal object Base64Url {

    private const val ZERO_BYTE: Byte = 0

    private val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

    /**
     * The JWT compact serialization never carries padding, but a tolerant decoder costs nothing
     * and avoids failing on a lenient peer.
     */
    private val decoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    fun encode(bytes: ByteArray): String = encoder.encode(bytes)

    fun encode(text: String): String = encode(text.toByteArray(Charsets.UTF_8))

    fun decode(value: String): ByteArray = decoder.decode(value)

    fun decodeToString(value: String): String = decode(value).toString(Charsets.UTF_8)

    /**
     * Base64url of the unsigned big-endian magnitude held in [bigEndianBytes], as mandated by
     * RFC 7518 section 6.3.1 for the RSA `n` and `e` JWK parameters.
     *
     * Callers typically obtain the bytes from a two's-complement representation (for instance
     * `BigInteger.toByteArray()`), which prepends a 0x00 sign byte whenever the most significant
     * bit is set — always the case for an RSA modulus. Leading zero octets are not part of the
     * magnitude and are dropped here. An all-zero input encodes as a single zero octet.
     */
    fun encodeUnsigned(bigEndianBytes: ByteArray): String = encode(bigEndianBytes.dropLeadingZeroBytes())

    private fun ByteArray.dropLeadingZeroBytes(): ByteArray {
        return when (val firstSignificant = indexOfFirst { it != ZERO_BYTE }) {
            -1 -> byteArrayOf(ZERO_BYTE)
            0 -> this
            else -> copyOfRange(firstSignificant, size)
        }
    }
}
