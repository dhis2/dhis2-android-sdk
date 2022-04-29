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
import okhttp3.HttpUrl
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser.parse
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser.removeTrailingApi
import org.hisp.dhis.android.core.configuration.internal.ServerUrlParser.trimAndRemoveTrailingSlash
import org.hisp.dhis.android.core.maintenance.D2Error
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ServerUrlParserShould {
    private val expected = HttpUrl.parse("http://dhis2.org/api/")

    @Test(expected = D2Error::class)
    fun return_error_empty_string() {
        parse("")
    }

    @Test(expected = D2Error::class)
    fun return_error_null_string() {
        parse(null)
    }

    @Test(expected = D2Error::class)
    fun return_error_malformed_url() {
        parse("malformed url")
    }

    @Test
    fun parse_url_with_no_api_no_slash() {
        assertThat(parse("http://dhis2.org")).isEqualTo(expected)
    }

    @Test
    fun parse_url_slash() {
        assertThat(parse("http://dhis2.org/")).isEqualTo(expected)
    }

    @Test
    fun parse_url_slash_api() {
        assertThat(parse("http://dhis2.org/api")).isEqualTo(expected)
    }

    @Test
    fun parse_url_slash_api_slash() {
        assertThat(parse("http://dhis2.org/api/")).isEqualTo(expected)
    }

    @Test
    fun remove_trailing_slash() {
        val expectedUrl = "http://dhis2.org/demo"
        assertThat(trimAndRemoveTrailingSlash("http://dhis2.org/demo/")).isEqualTo(expectedUrl)
        assertThat(trimAndRemoveTrailingSlash("http://dhis2.org/demo//")).isEqualTo(expectedUrl)
    }

    @Test
    fun remove_trailing_api() {
        val expectedUrl = "http://dhis2.org/demo"
        assertThat(removeTrailingApi("http://dhis2.org/demo/api")).isEqualTo(expectedUrl)
        assertThat(removeTrailingApi("http://dhis2.org/demo/api/")).isEqualTo(expectedUrl)
    }
}
