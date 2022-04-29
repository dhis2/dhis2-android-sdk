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
    private val username = "user23"
    private val generator = DatabaseNameGenerator()
    private val expectedEncr = "play-dhis2-org-android-current_user23_encrypted.db"
    private val expectedUnen = "play-dhis2-org-android-current_user23_unencrypted.db"

    @Test
    fun return_name_with_server_and_username_with_valid_characters_for_encrypted_db() {
        val name = generator.getDatabaseName(url, username, true)
        assertThat(name).isEqualTo(expectedEncr)
    }

    @Test
    fun return_name_with_server_and_username_with_valid_characters_for_unencrypted_db() {
        val name = generator.getDatabaseName(url, username, false)
        assertThat(name).isEqualTo(expectedUnen)
    }

    @Test
    fun return_name_with_server_and_username_for_http() {
        val name = generator.getDatabaseName(urlHttp, username, true)
        assertThat(name).isEqualTo(expectedEncr)
    }

    @Test
    fun return_name_with_server_and_username_with_trailing_slash() {
        val name = generator.getDatabaseName("$url/", username, true)
        assertThat(name).isEqualTo(expectedEncr)
    }

    @Test
    fun return_name_with_server_and_username_with_api() {
        val name = generator.getDatabaseName("$url/api", username, true)
        assertThat(name).isEqualTo(expectedEncr)
    }

    @Test
    fun return_name_with_server_and_username_with_api_and_trailing_slash() {
        val name = generator.getDatabaseName("$url/api/", username, true)
        assertThat(name).isEqualTo(expectedEncr)
    }
}
