/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.arch.storage.internal

import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.toByteString
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Derives and verifies the password hash stored in the AuthenticatedUser table.
 *
 * Hashes are stored in a self-describing, PHC-like format so that the algorithm and its parameters
 * travel with the value: `$pbkdf2-sha256$i=210000$<salt-base64>$<hash-base64>`.
 *
 * Values that do not start with the separator are legacy MD5 digests of `"username:secret"` written
 * by previous SDK versions. They can still be verified, but they are never produced again: a
 * successful legacy verification reports [HashVerification.Match.needsUpgrade], so the caller
 * rewrites the row with a current hash. The same mechanism allows raising the iteration count later
 * without invalidating anything already stored.
 */
internal object PasswordHasher {

    private const val PHC_SEPARATOR = '$'
    private const val ITERATIONS_PREFIX = "i="
    private const val PHC_SEGMENTS = 5
    private const val ALGORITHM_SEGMENT = 1
    private const val ITERATIONS_SEGMENT = 2
    private const val SALT_SEGMENT = 3
    private const val HASH_SEGMENT = 4
    private const val SALT_LENGTH_BYTES = 16
    private const val KEY_LENGTH_BITS = 256

    /**
     * Cost factor applied to newly derived hashes. It is embedded in every stored value, so it can
     * be raised in a future release without invalidating the hashes already written: they will
     * simply report [HashVerification.Match.needsUpgrade] the next time they are verified.
     */
    private const val ITERATIONS = 210_000

    /**
     * Supported PBKDF2 variants, declared from strongest to weakest: the order defines which stored
     * hashes are considered outdated. PBKDF2WithHmacSHA256 is only available from API 26, so devices
     * below that fall back to SHA-1, available since API 10.
     */
    private enum class Pbkdf2Algorithm(
        val id: String,
        val jcaName: String,
        val iterations: Int,
    ) {
        SHA256("pbkdf2-sha256", "PBKDF2WithHmacSHA256", ITERATIONS),
        SHA1("pbkdf2-sha1", "PBKDF2WithHmacSHA1", ITERATIONS),
        ;

        fun isWeakerThan(other: Pbkdf2Algorithm) = ordinal > other.ordinal

        companion object {
            fun byId(id: String) = entries.firstOrNull { it.id == id }
        }
    }

    private val currentAlgorithm: Pbkdf2Algorithm by lazy {
        Pbkdf2Algorithm.entries.firstOrNull { it.isAvailable() }
            ?: throw AssertionError("No PBKDF2 implementation available")
    }

    /**
     * Derives a new hash for the given secret using the current algorithm and parameters. Every call
     * generates a fresh random salt, so the result is never the same twice: it is only valid for the
     * write path, never for comparing.
     */
    fun hash(secret: String): String {
        val algorithm = currentAlgorithm
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val derived = deriveKey(algorithm, secret, salt, algorithm.iterations)
        return listOf(
            "",
            algorithm.id,
            ITERATIONS_PREFIX + algorithm.iterations,
            salt.base64(),
            derived.base64(),
        ).joinToString(PHC_SEPARATOR.toString())
    }

    /**
     * Verifies [secret] against [storedHash]. [username] is only used by the legacy MD5 path, which
     * hashes `"username:secret"` instead of the secret alone.
     */
    fun verify(username: String, secret: String, storedHash: String): HashVerification {
        return if (storedHash.startsWith(PHC_SEPARATOR)) {
            verifyPbkdf2(secret, storedHash)
        } else {
            verifyLegacyMd5(username, secret, storedHash)
        }
    }

    @Suppress("ReturnCount")
    private fun verifyPbkdf2(secret: String, storedHash: String): HashVerification {
        val parsed = parse(storedHash) ?: return HashVerification.Mismatch
        val candidate = try {
            deriveKey(parsed.algorithm, secret, parsed.salt, parsed.iterations)
        } catch (_: NoSuchAlgorithmException) {
            // The hash was written on a device running a newer Android version. It cannot be
            // verified here, so the account has to be authenticated online again.
            return HashVerification.Mismatch
        }

        return if (MessageDigest.isEqual(candidate, parsed.hash)) {
            HashVerification.Match(
                needsUpgrade = parsed.algorithm.isWeakerThan(currentAlgorithm) ||
                    parsed.iterations < parsed.algorithm.iterations,
            )
        } else {
            HashVerification.Mismatch
        }
    }

    @Suppress("DEPRECATION")
    private fun verifyLegacyMd5(username: String, secret: String, storedHash: String): HashVerification {
        val candidate = UserHelper.md5(username, secret)
        return if (MessageDigest.isEqual(candidate.encodeToByteArray(), storedHash.encodeToByteArray())) {
            HashVerification.Match(needsUpgrade = true)
        } else {
            HashVerification.Mismatch
        }
    }

    @Suppress("ReturnCount")
    private fun parse(storedHash: String): ParsedHash? {
        val segments = storedHash.split(PHC_SEPARATOR)
        if (segments.size != PHC_SEGMENTS) return null

        val algorithm = Pbkdf2Algorithm.byId(segments[ALGORITHM_SEGMENT]) ?: return null
        val iterationsSegment = segments[ITERATIONS_SEGMENT]
        if (!iterationsSegment.startsWith(ITERATIONS_PREFIX)) return null
        val iterations = iterationsSegment.removePrefix(ITERATIONS_PREFIX)
            .toIntOrNull()?.takeIf { it > 0 } ?: return null
        val salt = segments[SALT_SEGMENT].decodeBase64()?.toByteArray() ?: return null
        val hash = segments[HASH_SEGMENT].decodeBase64()?.toByteArray() ?: return null

        return ParsedHash(algorithm, iterations, salt, hash)
    }

    private fun deriveKey(
        algorithm: Pbkdf2Algorithm,
        secret: String,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray {
        val spec = PBEKeySpec(secret.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(algorithm.jcaName).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun Pbkdf2Algorithm.isAvailable(): Boolean {
        return try {
            SecretKeyFactory.getInstance(jcaName)
            true
        } catch (_: NoSuchAlgorithmException) {
            false
        }
    }

    private fun ByteArray.base64(): String = toByteString().base64()

    private class ParsedHash(
        val algorithm: Pbkdf2Algorithm,
        val iterations: Int,
        val salt: ByteArray,
        val hash: ByteArray,
    )
}
