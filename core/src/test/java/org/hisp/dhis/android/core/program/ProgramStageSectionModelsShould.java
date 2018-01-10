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

package org.hisp.dhis.android.core.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramStageSectionModelsShould {

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(ProgramStageSectionModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        //Json (modified) from: https://play.dhis2.org/dev/api/programStageSections/bbjzL5gp0NZ.json
        ProgramStageSection programStageSection = objectMapper.readValue("{\n" +
                "    \"created\": \"2015-01-26T13:14:09.957\",\n" +
                "    \"lastUpdated\": \"2015-10-14T13:37:29.904\",\n" +
                "    \"name\": \"Care at Birth\",\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/programStageSections/bbjzL5gp0NZ\",\n" +
                "    \"id\": \"bbjzL5gp0NZ\",\n" +
                "    \"displayName\": \"Care at Birth\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"sortOrder\": 0,\n" +
                "    \"programStage\": {\n" +
                "        \"id\": \"PFDfvmGpsR3\"\n" +
                "    },\n" +
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
                "    \"programIndicators\": [ ],\n" +
                "    \"dataElements\": [\n" +
                "        {\n" + "\"id\": \"Itl05OEupgQ\"\n" + "},\n" +
                "        {\n" + "\"id\": \"Mfq2Y9N21KZ\"\n" + "},\n" +
                "        {\n" + "\"id\": \"mGHBXrtqSut\"\n" + "}\n" +
                "    ]," +
                "    \"translations\": [ ]\n" +
                "}", ProgramStageSection.class);

        assertThat(programStageSection.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-10-14T13:37:29.904"));
        assertThat(programStageSection.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-01-26T13:14:09.957"));
        assertThat(programStageSection.uid()).isEqualTo("bbjzL5gp0NZ");
        assertThat(programStageSection.name()).isEqualTo("Care at Birth");
        assertThat(programStageSection.displayName()).isEqualTo("Care at Birth");
        assertThat(programStageSection.sortOrder()).isEqualTo(0);
        assertThat(programStageSection.programIndicators()).isEmpty();

        assertThat(programStageSection.dataElements().get(0).uid()).isEqualTo("Itl05OEupgQ");
        assertThat(programStageSection.dataElements().get(1).uid()).isEqualTo("Mfq2Y9N21KZ");
        assertThat(programStageSection.dataElements().get(2).uid()).isEqualTo("mGHBXrtqSut");
    }
}
