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

package org.hisp.dhis.android.core.organisationunit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class OrganisationShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        OrganisationUnit organisationUnit = objectMapper.readValue("{\n" +
                        "\n" +
                        "    \"code\": \"OU_1126\",\n" +
                        "    \"lastUpdated\": \"2014-11-25T09:37:54.154\",\n" +
                        "    \"id\": \"FLjwMPWLrL2\",\n" +
                        "    \"level\": 4,\n" +
                        "    \"created\": \"2012-02-17T15:54:39.987\",\n" +
                        "    \"name\": \"Baomahun CHC\",\n" +
                        "    \"shortName\": \"Baomahun CHC\",\n" +
                        "    \"displayName\": \"Baomahun CHC\",\n" +
                        "    \"displayShortName\": \"Baomahun CHC\",\n" +
                        "    \"externalAccess\": false,\n" +
                        "    \"deleted\": false,\n" +
                        "    \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/npWGUj37qDe/FLjwMPWLrL2\",\n" +
                        "    \"featureType\": \"POINT\",\n" +
                        "    \"openingDate\": \"1970-01-01T00:00:00.000\",\n" +
                        "    \"closedDate\": \"1971-01-01T00:00:00.000\",\n" +
                        "    \"dimensionItem\": \"FLjwMPWLrL2\",\n" +
                        "    \"coordinates\": \"[-11.6677,8.4165]\",\n" +
                        "    \"dimensionItemType\": \"ORGANISATION_UNIT\",\n" +
                        "    \"parent\": {\n" +
                        "        \"id\": \"npWGUj37qDe\"\n" +
                        "    },\n" +
                        "    \"access\": {\n" +
                        "        \"read\": true,\n" +
                        "        \"updateWithSection\": true,\n" +
                        "        \"externalize\": false,\n" +
                        "        \"delete\": true,\n" +
                        "        \"write\": true,\n" +
                        "        \"manage\": false\n" +
                        "    },\n" +
                        "    \"children\": [ ],\n" +
                        "    \"ancestors\": [\n" +
                        "        {\n" +
                        "            \"id\": \"ImspTQPwCqd\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": \"O6uvpzGd5pu\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": \"npWGUj37qDe\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"organisationUnitGroups\": [\n" +
                        "        {\n" +
                        "            \"id\": \"GGghZsfu7qV\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": \"CXw2yu5fodb\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": \"oRVt7g429ZO\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"userGroupAccesses\": [ ],\n" +
                        "    \"attributeValues\": [ ],\n" +
                        "    \"users\": [ ],\n" +
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
                        "        }\n" +
                        "    ]\n" +
                        "\n" +
                        "}",
                OrganisationUnit.class);

        assertThat(organisationUnit.uid()).isEqualTo("FLjwMPWLrL2");

        assertThat(organisationUnit.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-11-25T09:37:54.154"));
        assertThat(organisationUnit.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-02-17T15:54:39.987"));
        assertThat(organisationUnit.openingDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("1970-01-01T00:00:00.000"));
        assertThat(organisationUnit.closedDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("1971-01-01T00:00:00.000"));

        assertThat(organisationUnit.code()).isEqualTo("OU_1126");

        // names
        assertThat(organisationUnit.level()).isEqualTo(4);
        assertThat(organisationUnit.name()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.shortName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.displayName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.displayShortName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.parent().uid()).isEqualTo("npWGUj37qDe");

        assertThat(organisationUnit.deleted()).isFalse();

        // checking programs
        assertThat(organisationUnit.programs().get(0).uid()).isEqualTo("uy2gU8kT1jF");
        assertThat(organisationUnit.programs().get(1).uid()).isEqualTo("q04UBOqq3rp");
        assertThat(organisationUnit.programs().get(2).uid()).isEqualTo("VBqh0ynB2wv");
        assertThat(organisationUnit.programs().get(3).uid()).isEqualTo("eBAyeGv0exc");
    }
}
