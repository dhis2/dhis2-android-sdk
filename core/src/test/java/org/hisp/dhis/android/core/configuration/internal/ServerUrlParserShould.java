/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.configuration.internal;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.HttpUrl;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(JUnit4.class)
public class ServerUrlParserShould {

    private final HttpUrl EXPECTED = HttpUrl.parse("http://dhis2.org/api/");

    @Test(expected = D2Error.class)
    public void return_error_empty_string() throws D2Error {
        ServerUrlParser.parse("");
    }

    @Test(expected = D2Error.class)
    public void return_error_null_string() throws D2Error {
        ServerUrlParser.parse(null);
    }

    @Test(expected = D2Error.class)
    public void return_error_malformed_url() throws D2Error {
        ServerUrlParser.parse("malformed url");
    }

    @Test
    public void parse_url_with_no_api_no_slash() throws D2Error {
        assertThat(ServerUrlParser.parse("http://dhis2.org")).isEqualTo(EXPECTED);
    }

    @Test
    public void parse_url_slash() throws D2Error {
        assertThat(ServerUrlParser.parse("http://dhis2.org/")).isEqualTo(EXPECTED);
    }

    @Test
    public void parse_url_slash_api() throws D2Error {
        assertThat(ServerUrlParser.parse("http://dhis2.org/api")).isEqualTo(EXPECTED);
    }

    @Test
    public void parse_url_slash_api_slash() throws D2Error {
        assertThat(ServerUrlParser.parse("http://dhis2.org/api/")).isEqualTo(EXPECTED);
    }

    @Test
    public void remove_trailing_slash() {
        String expected = "http://dhis2.org/demo";
        assertThat(ServerUrlParser.removeTrailingSlash("http://dhis2.org/demo/")).isEqualTo(expected);
        assertThat(ServerUrlParser.removeTrailingSlash("http://dhis2.org/demo//")).isEqualTo(expected);
    }
}