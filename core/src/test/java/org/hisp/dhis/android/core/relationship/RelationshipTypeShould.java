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

package org.hisp.dhis.android.core.relationship;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RelationshipTypeShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        RelationshipType relationshipType = objectMapper.readValue("{\n" +
                        "\n" +
                        "    \"created\": \"2013-09-19T15:17:41.000\",\n" +
                        "    \"lastUpdated\": \"2014-04-14T13:53:20.166\",\n" +
                        "    \"name\": \"Mother-Child\",\n" +
                        "    \"id\": \"V2kkHafqs8G\",\n" +
                        "    \"displayName\": \"Mother-Child\",\n" +
                        "    \"bIsToA\": \"Child\",\n" +
                        "    \"externalAccess\": false,\n" +
                        "    \"aIsToB\": \"Mother\",\n" +
                        "    \"access\": {\n" +
                        "        \"read\": true,\n" +
                        "        \"updateWithSection\": true,\n" +
                        "        \"externalize\": false,\n" +
                        "        \"delete\": true,\n" +
                        "        \"write\": true,\n" +
                        "        \"manage\": false\n" +
                        "    },\n" +
                        "    \"userGroupAccesses\": [ ],\n" +
                        "    \"attributeValues\": [ ],\n" +
                        "    \"translations\": [ ]\n" +
                        "\n" +
                        "}",
                RelationshipType.class);

        assertThat(relationshipType.uid()).isEqualTo("V2kkHafqs8G");
        assertThat(relationshipType.name()).isEqualTo("Mother-Child");
        assertThat(relationshipType.displayName()).isEqualTo("Mother-Child");
        assertThat(relationshipType.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-09-19T15:17:41.000"));
        assertThat(relationshipType.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-14T13:53:20.166"));
        assertThat(relationshipType.aIsToB()).isEqualTo("Mother");
        assertThat(relationshipType.bIsToA()).isEqualTo("Child");
    }
}
