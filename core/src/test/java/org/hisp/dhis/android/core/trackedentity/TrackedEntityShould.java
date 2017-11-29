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

package org.hisp.dhis.android.core.trackedentity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TrackedEntityShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        TrackedEntity trackedEntity = objectMapper.readValue("{\n" +
                "\n" +
                "    \"created\": \"2014-08-20T12:28:56.409\",\n" +
                "    \"lastUpdated\": \"2015-10-14T13:36:53.063\",\n" +
                "    \"name\": \"Person\",\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/trackedEntities/nEenWmSyUEp\",\n" +
                "    \"id\": \"nEenWmSyUEp\",\n" +
                "    \"displayDescription\": \"Person\",\n" +
                "    \"displayName\": \"Person\",\n" +
                "    \"description\": \"Person\",\n" +
                "    \"externalAccess\": false,\n" +
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
                "}",TrackedEntity.class);

        assertThat(trackedEntity.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-10-14T13:36:53.063"));
        assertThat(trackedEntity.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-20T12:28:56.409"));
        assertThat(trackedEntity.uid()).isEqualTo("nEenWmSyUEp");
        assertThat(trackedEntity.displayName()).isEqualTo("Person");
        assertThat(trackedEntity.name()).isEqualTo("Person");
        assertThat(trackedEntity.description()).isEqualTo("Person");
        assertThat(trackedEntity.displayDescription()).isEqualTo("Person");
    }
}
