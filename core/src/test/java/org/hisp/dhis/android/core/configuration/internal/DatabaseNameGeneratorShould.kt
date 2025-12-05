/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DatabaseNameGeneratorShould {
    private val url = "https://play.dhis2.org/android-current"
    private val urlHttp = "http://play.dhis2.org/android-current"
    private val urlWithBackslashes = "https://play.dhis2.org\\android-current"
    private val username = "user23"
    private val generator = DatabaseNameGenerator()

    @Test
    fun return_name_with_server_username_hash_and_encryption_for_encrypted_db() {
        val name = generator.getDatabaseName(url, username, true)

        // Should have format: https-<readable>_<username>_<hash>_encrypted.db
        assertThat(name).matches("https-play-dhis2-org-android-current_user23_[0-9a-f]{8}_encrypted\\.db")
        assertThat(name).contains("_user23_")
        assertThat(name).endsWith("_encrypted.db")
    }

    @Test
    fun return_name_with_server_username_hash_and_encryption_for_unencrypted_db() {
        val name = generator.getDatabaseName(url, username, false)

        assertThat(name).matches("https-play-dhis2-org-android-current_user23_[0-9a-f]{8}_unencrypted\\.db")
        assertThat(name).endsWith("_unencrypted.db")
    }

    @Test
    fun return_different_name_for_http_and_https() {
        val nameHttps = generator.getDatabaseName(url, username, true)
        val nameHttp = generator.getDatabaseName(urlHttp, username, true)

        // HTTP and HTTPS are different servers, should have different database names
        assertThat(nameHttps).isNotEqualTo(nameHttp)
        assertThat(nameHttps).startsWith("https-")
        assertThat(nameHttp).startsWith("http-")
    }

    @Test
    fun return_same_name_with_trailing_slash() {
        val name1 = generator.getDatabaseName(url, username, true)
        val name2 = generator.getDatabaseName("$url/", username, true)

        assertThat(name1).isEqualTo(name2)
    }

    @Test
    fun return_same_name_with_api_suffix() {
        val name1 = generator.getDatabaseName(url, username, true)
        val name2 = generator.getDatabaseName("$url/api", username, true)

        assertThat(name1).isEqualTo(name2)
    }

    @Test
    fun return_same_name_with_api_and_trailing_slash() {
        val name1 = generator.getDatabaseName(url, username, true)
        val name2 = generator.getDatabaseName("$url/api/", username, true)

        assertThat(name1).isEqualTo(name2)
    }

    @Test
    fun return_different_names_for_collision_case_dash_vs_slash() {
        val url1 = "https://play.dhis2.org/android-current"
        val url2 = "https://play.dhis2.org/android/current"

        val name1 = generator.getDatabaseName(url1, username, true)
        val name2 = generator.getDatabaseName(url2, username, true)

        // These URLs should generate DIFFERENT database names (hash distinguishes them)
        assertThat(name1).isNotEqualTo(name2)

        val hash1 = name1.substringAfter("user23_").substringBefore("_encrypted")
        val hash2 = name2.substringAfter("user23_").substringBefore("_encrypted")
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun return_different_names_for_different_usernames() {
        val name1 = generator.getDatabaseName(url, "admin", true)
        val name2 = generator.getDatabaseName(url, "user", true)

        assertThat(name1).isNotEqualTo(name2)
    }

    @Test
    fun return_same_hash_for_identical_inputs() {
        val name1 = generator.getDatabaseName(url, username, true)
        val name2 = generator.getDatabaseName(url, username, true)

        assertThat(name1).isEqualTo(name2)
    }

    @Test
    fun return_same_name_for_different_domain_case() {
        val url1 = "https://PLAY.dhis2.org/android-current"
        val url2 = "https://play.dhis2.org/android-current"

        val name1 = generator.getDatabaseName(url1, username, true)
        val name2 = generator.getDatabaseName(url2, username, true)

        // Domain case is normalized, so these should be equal
        assertThat(name1).isEqualTo(name2)
    }

    @Test
    fun return_different_name_for_different_path_case() {
        val url1 = "https://play.dhis2.org/Android-Current"
        val url2 = "https://play.dhis2.org/android-current"

        val name1 = generator.getDatabaseName(url1, username, true)
        val name2 = generator.getDatabaseName(url2, username, true)

        // Path case is preserved, so these should be different
        assertThat(name1).isNotEqualTo(name2)
    }

    @Test
    fun hash_should_be_8_hex_characters() {
        val name = generator.getDatabaseName(url, username, true)

        val hash = name.substringAfter("${username}_").substringBefore("_encrypted")

        assertThat(hash).hasLength(8)
        assertThat(hash).matches("[0-9a-f]{8}")
    }

    @Test
    fun old_database_name_should_not_have_hash() {
        @Suppress("DEPRECATION")
        val oldName = generator.getOldDatabaseName(url, username, true)

        // Old format includes protocol prefix now but no hash
        assertThat(oldName).isEqualTo("https-play-dhis2-org-android-current_user23_encrypted.db")
        assertThat(oldName).doesNotMatch(".*_[0-9a-f]{8}_.*")
    }

    @Test
    fun return_same_name_for_url_with_backslashes_and_forward_slashes() {
        val nameWithSlashes = generator.getDatabaseName(url, username, true)
        val nameWithBackslashes = generator.getDatabaseName(urlWithBackslashes, username, true)

        assertThat(nameWithSlashes).isEqualTo(nameWithBackslashes)
        assertThat(nameWithSlashes).matches("https-play-dhis2-org-android-current_user23_[0-9a-f]{8}_encrypted\\.db")
    }
}