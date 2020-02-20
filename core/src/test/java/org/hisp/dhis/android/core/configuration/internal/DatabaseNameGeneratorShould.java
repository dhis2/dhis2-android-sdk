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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class DatabaseNameGeneratorShould {

    private final String URL = "https://play.dhis2.org/android-current";
    private final String URL_HTTP = "http://play.dhis2.org/android-current";
    private final String USERNAME = "user23";
    private final DatabaseNameGenerator generator = new DatabaseNameGenerator();

    private final String EXPECTED_ENCR = "play-dhis2-org-android-current_user23_encrypted.db";
    private final String EXPECTED_UNEN = "play-dhis2-org-android-current_user23_unencrypted.db";

    @Test
    public void return_name_with_server_and_username_with_valid_characters_for_encrypted_db() {
        String name = generator.getDatabaseName(URL, USERNAME, true);
        assertThat(name).isEqualTo(EXPECTED_ENCR);
    }

    @Test
    public void return_name_with_server_and_username_with_valid_characters_for_unencrypted_db() {
        String name = generator.getDatabaseName(URL, USERNAME, false);
        assertThat(name).isEqualTo(EXPECTED_UNEN);
    }

    @Test
    public void return_name_with_server_and_username_for_http() {
        String name = generator.getDatabaseName(URL_HTTP, USERNAME, true);
        assertThat(name).isEqualTo(EXPECTED_ENCR);
    }

    @Test
    public void return_name_with_server_and_username_with_trailing_slash() {
        String name = generator.getDatabaseName(URL + "/", USERNAME, true);
        assertThat(name).isEqualTo(EXPECTED_ENCR);
    }

    @Test
    public void return_name_with_server_and_username_with_api() {
        String name = generator.getDatabaseName(URL + "/api", USERNAME, true);
        assertThat(name).isEqualTo(EXPECTED_ENCR);
    }

    @Test
    public void return_name_with_server_and_username_with_api_and_trailing_slash() {
        String name = generator.getDatabaseName(URL + "/api/", USERNAME, true);
        assertThat(name).isEqualTo(EXPECTED_ENCR);
    }

}