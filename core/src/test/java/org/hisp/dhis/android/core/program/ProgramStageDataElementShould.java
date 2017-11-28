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

public class ProgramStageDataElementShould {

    @Test
    public void have_the_equals_method_conform_to_contract() {
        EqualsVerifier.forClass(ProgramStageDataElementModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramStageDataElement programStageDataElement = objectMapper.readValue("{\n" +
                "\n" +
                "    \"created\": \"2015-03-27T16:27:19.000\",\n" +
                "    \"lastUpdated\": \"2015-08-06T20:16:48.444\",\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/programStageDataElements/LfgZNmadu4W\",\n" +
                "    \"id\": \"LfgZNmadu4W\",\n" +
                "    \"displayInReports\": false,\n" +
                "    \"externalAccess\": false,\n" +
                "    \"compulsory\": false,\n" +
                "    \"allowProvidedElsewhere\": false,\n" +
                "    \"sortOrder\": 11,\n" +
                "    \"allowFutureDate\": false,\n" +
                "    \"programStage\": {\n" +
                "        \"id\": \"ZzYYXq4fJie\"\n" +
                "    },\n" +
                "    \"access\": {\n" +
                "        \"read\": true,\n" +
                "        \"updateWithSection\": true,\n" +
                "        \"externalize\": false,\n" +
                "        \"delete\": true,\n" +
                "        \"write\": true,\n" +
                "        \"manage\": false\n" +
                "    },\n" +
                "    \"dataElement\": {\n" +
                "        \"id\": \"aei1xRjSU2l\"\n" +
                "    },\n" +
                "    \"userGroupAccesses\": [ ],\n" +
                "    \"attributeValues\": [ ],\n" +
                "    \"translations\": [ ]\n" +
                "\n" +
                "}", ProgramStageDataElement.class);

        assertThat(programStageDataElement.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-06T20:16:48.444"));
        assertThat(programStageDataElement.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-27T16:27:19.000"));
        assertThat(programStageDataElement.uid()).isEqualTo("LfgZNmadu4W");
        assertThat(programStageDataElement.dataElement().uid()).isEqualTo("aei1xRjSU2l");
        assertThat(programStageDataElement.allowFutureDate()).isFalse();
        assertThat(programStageDataElement.compulsory()).isFalse();
        assertThat(programStageDataElement.sortOrder()).isEqualTo(11);
        assertThat(programStageDataElement.allowProvidedElsewhere()).isFalse();
        assertThat(programStageDataElement.displayInReports()).isFalse();
    }

}
