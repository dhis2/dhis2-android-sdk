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
package org.hisp.dhis.android.core.arch.helpers

import okio.ByteString
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Suppress("MagicNumber")
object UserHelper {
    private const val SALT_SIZE = 16
    private const val HASH_SIZE = 32
    private const val ITERATIONS = 10000
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    /**
     * Encode the given username and password to a base 64 [String].
     *
     * @param username The username of the user account.
     * @param password The password of the user account.
     * @return An encoded base 64 [String].
     */
    fun base64(username: String, password: String): String {
        return base64(usernameAndPassword(username, password))
    }

    /**
     * Encode the given string to a base 64 [String].
     *
     * @param value The value to encode
     * @return An encoded base 64 [String].
     */
    @Suppress("SpreadOperator")
    fun base64(value: String): String {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        return ByteString.of(*bytes).base64()
    }

    /**
     * Encode the given username and password to a MD5 [String].
     *
     * @param username The username of the user account.
     * @param password The password of the user account.
     * @return An encoded MD5 [String].
     */
    @Deprecated("Use createPasswordHash instead")
    fun md5(username: String, password: String): String {
        return try {
            val credentials = usernameAndPassword(username, password)
            val md = MessageDigest.getInstance("MD5")
            md.reset()
            md.update(credentials.toByteArray(StandardCharsets.UTF_8))
            bytesToHex(md.digest()).lowercase()
        } catch (noSuchAlgorithmException: NoSuchAlgorithmException) {
            // noop. Every implementation of Java is required to support MD5
            throw AssertionError(noSuchAlgorithmException)
        }
    }

    @Suppress("MagicNumber")
    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)

        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun usernameAndPassword(username: String, password: String): String {
        return "$username:$password"
    }

    /**
     * Generates a random salt of length [SALT_SIZE].
     */
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_SIZE)
        random.nextBytes(salt)
        return salt
    }

    /**
     * Applies PBKDF2 with [ITERATIONS] iterations to obtain a hash
     * of length [HASH_SIZE] bytes.
     */
    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_SIZE * 8)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }

    /**
     * Converts a byte array to a hexadecimal String.
     */
    private fun toHex(array: ByteArray): String {
        val sb = StringBuilder(array.size * 2)
        for (b in array) {
            val i = b.toInt() and 0xFF
            val hex = i.toString(16)
            if (hex.length < 2) sb.append('0')
            sb.append(hex)
        }
        return sb.toString()
    }

    /**
     * Converts a hexadecimal String to a byte array.
     */
    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return bytes
    }

    /**
     * Creates a hash from the password. Returns "salt:hash" in hexadecimal.
     */
    fun createPasswordHash(password: String): String {
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        return "${toHex(salt)}:${toHex(hash)}"
    }

    /**
     * Verifies if the given password matches the stored hash.
     * The expected format of [stored] is "salt:hash" in hexadecimal.
     */
    fun verifyPassword(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 2) return false

        val salt = fromHex(parts[0])
        val storedHash = fromHex(parts[1])
        val candidateHash = hashPassword(password, salt)

        // Compare byte by byte to prevent timing attacks
        if (candidateHash.size != storedHash.size) return false
        for (i in candidateHash.indices) {
            if (candidateHash[i] != storedHash[i]) {
                return false
            }
        }
        return true
    }
}
