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

import com.google.common.truth.Truth.assertThat
import okio.ByteString.Companion.toByteString
import org.hisp.dhis.android.core.arch.helpers.UserHelper
import org.junit.Test
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Suppress("DEPRECATION")
class PasswordHasherShould {

    @Test
    fun produce_a_self_describing_hash() {
        val hash = PasswordHasher.hash(SECRET)

        val segments = hash.split("$")
        assertThat(segments).hasSize(5)
        assertThat(segments[0]).isEmpty()
        assertThat(segments[1]).isEqualTo("pbkdf2-sha256")
        assertThat(segments[2]).isEqualTo("i=210000")
    }

    @Test
    fun produce_a_different_hash_on_every_call() {
        assertThat(PasswordHasher.hash(SECRET)).isNotEqualTo(PasswordHasher.hash(SECRET))
    }

    @Test
    fun verify_a_hash_it_produced_without_requiring_an_upgrade() {
        val verification = PasswordHasher.verify(USERNAME, SECRET, PasswordHasher.hash(SECRET))

        assertThat(verification).isEqualTo(HashVerification.Match(needsUpgrade = false))
    }

    @Test
    fun reject_a_wrong_secret() {
        val verification = PasswordHasher.verify(USERNAME, "wrong", PasswordHasher.hash(SECRET))

        assertThat(verification).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun ignore_the_username_when_verifying_a_current_hash() {
        val verification = PasswordHasher.verify("someone-else", SECRET, PasswordHasher.hash(SECRET))

        assertThat(verification).isEqualTo(HashVerification.Match(needsUpgrade = false))
    }

    @Test
    fun verify_a_legacy_md5_hash_and_ask_for_an_upgrade() {
        val legacy = UserHelper.md5(USERNAME, SECRET)

        val verification = PasswordHasher.verify(USERNAME, SECRET, legacy)

        assertThat(verification).isEqualTo(HashVerification.Match(needsUpgrade = true))
    }

    @Test
    fun reject_a_legacy_md5_hash_that_belongs_to_a_different_secret() {
        val legacy = UserHelper.md5(USERNAME, "another-secret")

        val verification = PasswordHasher.verify(USERNAME, SECRET, legacy)

        assertThat(verification).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun reject_a_legacy_md5_hash_that_belongs_to_a_different_username() {
        val legacy = UserHelper.md5("another-user", SECRET)

        val verification = PasswordHasher.verify(USERNAME, SECRET, legacy)

        assertThat(verification).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun ask_for_an_upgrade_when_the_stored_iteration_count_is_outdated() {
        val outdated = pbkdf2Hash(SECRET, iterations = 10_000)

        val verification = PasswordHasher.verify(USERNAME, SECRET, outdated)

        assertThat(verification).isEqualTo(HashVerification.Match(needsUpgrade = true))
    }

    @Test
    fun reject_a_wrong_secret_against_an_outdated_hash() {
        val outdated = pbkdf2Hash(SECRET, iterations = 10_000)

        val verification = PasswordHasher.verify(USERNAME, "wrong", outdated)

        assertThat(verification).isEqualTo(HashVerification.Mismatch)
    }

    @Test
    fun reject_malformed_stored_values() {
        val malformed = listOf(
            "\$pbkdf2-sha256\$i=210000\$only-three-segments",
            "\$pbkdf2-sha256\$210000\$c2FsdA==\$aGFzaA==",
            "\$pbkdf2-sha256\$i=zero\$c2FsdA==\$aGFzaA==",
            "\$pbkdf2-sha256\$i=0\$c2FsdA==\$aGFzaA==",
            "\$unknown-algorithm\$i=210000\$c2FsdA==\$aGFzaA==",
            "\$pbkdf2-sha256\$i=210000\$not base64!\$aGFzaA==",
        )

        malformed.forEach {
            assertThat(PasswordHasher.verify(USERNAME, SECRET, it)).isEqualTo(HashVerification.Mismatch)
        }
    }

    private fun pbkdf2Hash(secret: String, iterations: Int): String {
        val salt = ByteArray(SALT_LENGTH) { it.toByte() }
        val spec = PBEKeySpec(secret.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        val derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return "\$pbkdf2-sha256\$i=$iterations\$${salt.toByteString().base64()}\$${derived.toByteString().base64()}"
    }

    companion object {
        private const val USERNAME = "username"
        private const val SECRET = "s3cr3t"
        private const val SALT_LENGTH = 16
        private const val KEY_LENGTH_BITS = 256
    }
}
