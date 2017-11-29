/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UserCredentialShould {

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(UserCredentialsModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        UserCredentials userCredentials = objectMapper.readValue("{\n" +
                "\n" +
                "    \"code\": \"admin\",\n" +
                "    \"created\": \"2013-04-18T17:15:08.401\",\n" +
                "    \"lastUpdated\": \"2016-10-25T09:21:33.884\",\n" +
                "    \"id\": \"ZyjSDLHGPv4\",\n" +
                "    \"lastLogin\": \"2016-10-25T09:21:33.881\",\n" +
                "    \"passwordLastUpdated\": \"2014-12-18T20:56:05.264\",\n" +
                "    \"invitation\": false,\n" +
                "    \"externalAuth\": false,\n" +
                "    \"selfRegistered\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"username\": \"admin\",\n" +
                "    \"userInfo\": {\n" +
                "        \"id\": \"xE7jOejl9FI\"\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"id\": \"xE7jOejl9FI\"\n" +
                "    },\n" +
                "    \"cogsDimensionConstraints\": [ ],\n" +
                "    \"catDimensionConstraints\": [ ],\n" +
                "    \"userRoles\": [\n" +
                "        {\n" +
                "            \"id\": \"Ufph3mGRmMo\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"UYXOT4A7JMI\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"aNk5AyC7ydy\"\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "},", UserCredentials.class);


        assertThat(userCredentials.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-25T09:21:33.884"));
        assertThat(userCredentials.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-18T17:15:08.401"));
        assertThat(userCredentials.uid()).isEqualTo("ZyjSDLHGPv4");
        assertThat(userCredentials.username()).isEqualTo("admin");
        assertThat(userCredentials.code()).isEqualTo("admin");

        assertThat(userCredentials.userRoles().get(0).uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(userCredentials.userRoles().get(1).uid()).isEqualTo("UYXOT4A7JMI");
        assertThat(userCredentials.userRoles().get(2).uid()).isEqualTo("aNk5AyC7ydy");
    }
}
