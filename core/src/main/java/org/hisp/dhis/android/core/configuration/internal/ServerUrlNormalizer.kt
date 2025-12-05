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
package org.hisp.dhis.android.core.configuration.internal

/**
 * Normalizes server URLs for consistent comparison and hash generation.
 *
 * Examples:
 * - "HTTPS://SERVER.COM/Path" -> "https://server.com/Path"
 * - "https://server.com/path/" -> "https://server.com/path"
 * - "https://server.com/path/api" -> "https://server.com/path"
 *
 * Note: http://server.com and https://server.com are NOT equivalent.
 */
internal object ServerUrlNormalizer {

    /**
     * Normalizes a server URL by:
     * 1. Lowercasing the protocol and domain (path remains case-sensitive)
     * 2. Converting backslashes to forward slashes
     * 3. Removing trailing slash
     * 4. Removing /api suffix
     */
    fun normalize(url: String): String {
        val normalized = url.replace('\\', '/')
        val (protocol, rest) = extractProtocol(normalized)
        val normalizedRest = lowercaseDomain(rest)
            .trimEnd('/')
            .removeSuffix("/api")

        return protocol + normalizedRest
    }

    /**
     * Extracts and lowercases the protocol from the URL.
     * Returns a pair of (protocol, restOfUrl).
     */
    private fun extractProtocol(url: String): Pair<String, String> {
        return when {
            url.lowercase().startsWith("https://") -> "https://" to url.substring(8)
            url.lowercase().startsWith("http://") -> "http://" to url.substring(7)
            else -> "" to url
        }
    }

    /**
     * Lowercases only the domain part of the URL, preserving the path case.
     * Domain is everything before the first '/'.
     */
    private fun lowercaseDomain(urlWithoutProtocol: String): String {
        val slashIndex = urlWithoutProtocol.indexOf('/')
        return if (slashIndex == -1) {
            urlWithoutProtocol.lowercase()
        } else {
            val domain = urlWithoutProtocol.take(slashIndex).lowercase()
            val path = urlWithoutProtocol.substring(slashIndex)
            domain + path
        }
    }

    /**
     * Checks if two server URLs are equivalent after normalization.
     */
    fun areEquivalent(url1: String, url2: String): Boolean {
        return normalize(url1) == normalize(url2)
    }
}
