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

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UserRoleShould {
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        UserRole userRole = objectMapper.readValue("{\n" +
                "\n" +
                "    \"created\": \"2012-11-13T18:10:26.881\",\n" +
                "    \"lastUpdated\": \"2016-10-12T19:59:11.734\",\n" +
                "    \"name\": \"Superuser\",\n" +
                "    \"id\": \"Ufph3mGRmMo\",\n" +
                "    \"displayName\": \"Superuser\",\n" +
                "    \"description\": \"Superuser\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"user\": {\n" +
                "        \"id\": \"GOLswS44mh8\"\n" +
                "    },\n" +
                "    \"userGroupAccesses\": [ ],\n" +
                "    \"attributeValues\": [ ],\n" +
                "    \"users\": [\n" +
                "        {\n" +
                "            \"id\": \"cddnwKV2gm9\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"DXyJmlo9rge\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"oXD88WWSQpR\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"xE7jOejl9FI\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"awtnYWiVEd5\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"translations\": [ ]\n" +
                "\n" +
                "}", UserRole.class);


        assertThat(userRole.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-12T19:59:11.734"));
        assertThat(userRole.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-11-13T18:10:26.881"));
        assertThat(userRole.uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(userRole.displayName()).isEqualTo("Superuser");
        assertThat(userRole.name()).isEqualTo("Superuser");
    }
}
