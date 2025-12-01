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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ServerUrlNormalizerShould {

    @Test
    fun normalize_removes_https_protocol() {
        val result = ServerUrlNormalizer.normalize("https://play.dhis2.org/demo")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun normalize_removes_http_protocol() {
        val result = ServerUrlNormalizer.normalize("http://play.dhis2.org/demo")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun normalize_converts_to_lowercase() {
        val result = ServerUrlNormalizer.normalize("HTTPS://PLAY.DHIS2.ORG/DEMO")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun normalize_removes_trailing_slash() {
        val result = ServerUrlNormalizer.normalize("https://play.dhis2.org/demo/")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun normalize_removes_api_suffix() {
        val result = ServerUrlNormalizer.normalize("https://play.dhis2.org/demo/api")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun normalize_removes_api_suffix_with_trailing_slash() {
        val result = ServerUrlNormalizer.normalize("https://play.dhis2.org/demo/api/")
        assertThat(result).isEqualTo("play.dhis2.org/demo")
    }

    @Test
    fun areEquivalent_returns_true_for_http_and_https() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "https://play.dhis2.org/demo",
                "http://play.dhis2.org/demo",
            ),
        ).isTrue()
    }

    @Test
    fun areEquivalent_returns_true_for_different_case() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "HTTPS://PLAY.DHIS2.ORG/DEMO",
                "https://play.dhis2.org/demo",
            ),
        ).isTrue()
    }

    @Test
    fun areEquivalent_returns_true_with_and_without_trailing_slash() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "https://play.dhis2.org/demo/",
                "https://play.dhis2.org/demo",
            ),
        ).isTrue()
    }

    @Test
    fun areEquivalent_returns_true_with_and_without_api_suffix() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "https://play.dhis2.org/demo/api",
                "https://play.dhis2.org/demo",
            ),
        ).isTrue()
    }

    @Test
    fun areEquivalent_returns_false_for_different_servers() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "https://play.dhis2.org/demo",
                "https://play.dhis2.org/dev",
            ),
        ).isFalse()
    }

    @Test
    fun areEquivalent_returns_true_for_complex_mixed_case() {
        assertThat(
            ServerUrlNormalizer.areEquivalent(
                "HTTP://Play.Dhis2.Org/Demo/API/",
                "https://play.dhis2.org/demo",
            ),
        ).isTrue()
    }
}
