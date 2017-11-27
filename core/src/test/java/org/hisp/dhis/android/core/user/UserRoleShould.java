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
                "    \"translations\": [ ],\n" +
                "    \"dataSets\": [\n" +
                "        {\n" +
                "            \"id\": \"EDzMBk0RRji\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"VTdjfLXXmoi\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"aLpVgfXiz0f\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"N4fIX1HL3TQ\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pBOMPrpg1QX\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"SF8FDSqw30D\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"EKWVBc5C0ms\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"BfMAe6Itzgt\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ULowA8V3ucd\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"QX4ZTUbOt3a\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"eZDhcZi6FLP\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"OsPTWNqq26W\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"TuL8IOPzpHh\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"PLq9sJluXvc\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"V8MHeZHIrcP\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Y8gAn9DfAGU\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ce7DSxx5H2I\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"YZhd4nu3mzY\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Rl58JxmKJo2\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"YFTk3VdO9av\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"lyLU2wR22tC\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"programs\": [\n" +
                "        {\n" +
                "            \"id\": \"uy2gU8kT1jF\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"q04UBOqq3rp\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"VBqh0ynB2wv\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"eBAyeGv0exc\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"kla3mAPgvCH\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"lxAQ7Zs9VYR\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"IpHINAT79UW\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"WSGAb5XwJ3Y\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ur1Edk5Oe2n\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"fDd25txQckK\"\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "}", UserRole.class);


        assertThat(userRole.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-12T19:59:11.734"));
        assertThat(userRole.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-11-13T18:10:26.881"));
        assertThat(userRole.uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(userRole.displayName()).isEqualTo("Superuser");
        assertThat(userRole.name()).isEqualTo("Superuser");

        assertThat(userRole.programs().get(0).uid()).isEqualTo("uy2gU8kT1jF");
        assertThat(userRole.programs().get(1).uid()).isEqualTo("q04UBOqq3rp");
        assertThat(userRole.programs().get(2).uid()).isEqualTo("VBqh0ynB2wv");
        assertThat(userRole.programs().get(3).uid()).isEqualTo("eBAyeGv0exc");
        assertThat(userRole.programs().get(4).uid()).isEqualTo("kla3mAPgvCH");
        assertThat(userRole.programs().get(5).uid()).isEqualTo("lxAQ7Zs9VYR");
        assertThat(userRole.programs().get(6).uid()).isEqualTo("IpHINAT79UW");
        assertThat(userRole.programs().get(7).uid()).isEqualTo("WSGAb5XwJ3Y");
        assertThat(userRole.programs().get(8).uid()).isEqualTo("ur1Edk5Oe2n");
        assertThat(userRole.programs().get(9).uid()).isEqualTo("fDd25txQckK");
    }
}
