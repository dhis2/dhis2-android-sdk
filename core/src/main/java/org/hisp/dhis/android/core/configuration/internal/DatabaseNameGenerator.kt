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

import org.koin.core.annotation.Singleton
import java.security.MessageDigest

@Singleton
internal class DatabaseNameGenerator {
    /**
     * Generates a unique database name based on serverUrl, username, and encryption.
     *
     * Format: <readable_part>_<username>_<hash>_<encrypted|unencrypted>.db
     *
     * Example:
     * - Input: "https://play.dhis2.org/android-current", "admin", true
     * - Output: "play-dhis2-org-android-current_admin_a3f5b821_encrypted.db"
     */
    fun getDatabaseName(serverUrl: String, username: String, encrypt: Boolean): String {
        val encryptedStr = if (encrypt) "encrypted" else "unencrypted"
        val processedUrl = processServerUrl(serverUrl)
        val hash = generateHash(serverUrl, username)
        return "${processedUrl}_${username}_${hash}_${encryptedStr}${DB_SUFFIX}"
    }

    /**
     * Generates database name using the OLD format (without hash).
     * Used only for migration purposes to identify existing databases.
     *
     * @deprecated Only for migration. Use getDatabaseName() for new databases.
     */
    @Deprecated("Only for migration compatibility")
    fun getOldDatabaseName(serverUrl: String, username: String, encrypt: Boolean): String {
        val encryptedStr = if (encrypt) "encrypted" else "unencrypted"
        return processServerUrl(serverUrl) + "_" + username + "_" + encryptedStr + DB_SUFFIX
    }

    /**
     * Generates an 8-character hex hash from the normalized serverUrl + username combination.
     */
    private fun generateHash(serverUrl: String, username: String): String {
        val normalizedUrl = normalizeForHash(serverUrl)
        val input = "$normalizedUrl|$username"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        // First 4 bytes = 8 hex characters
        return digest.take(HASH_BYTE_COUNT).joinToString("") {
            it.toUByte().toString(HEX_RADIX).padStart(HEX_STRING_WIDTH, '0')
        }
    }

    private fun normalizeForHash(serverUrl: String): String {
        return serverUrl
            .removePrefix("https://")
            .removePrefix("http://")
            .trimEnd('/')
            .removeSuffix("/api")
    }

    private fun processServerUrl(serverUrl: String): String {
        return serverUrl
            .removePrefix("https://")
            .removePrefix("http://")
            .removeSuffix("/")
            .removeSuffix("/api")
            .replace("[^a-zA-Z0-9]".toRegex(), "-")
            .replace("-+".toRegex(), "-")
            .removePrefix("-")
            .removeSuffix("-")
    }

    companion object {
        const val DB_SUFFIX = ".db"
        private const val HASH_BYTE_COUNT = 4
        private const val HEX_STRING_WIDTH = 2
        private const val HEX_RADIX = 16
    }
}
