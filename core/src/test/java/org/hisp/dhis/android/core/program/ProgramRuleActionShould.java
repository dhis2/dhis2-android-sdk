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

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramRuleActionShould {
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramRuleAction programRuleAction = objectMapper.readValue("{\n" +
                "\"lastUpdated\": \"2015-09-14T22:22:15.458\",\n" +
                "\"href\": \"https://play.dhis2.org/dev/api/programRuleActions/v434s5YPDcP\",\n" +
                "\"id\": \"v434s5YPDcP\",\n" +
                "\"created\": \"2015-09-14T21:17:41.033\",\n" +
                "\"content\": \"It is suggested that an explanation is provided when the Apgar score is below 4\",\n" +
                "\"externalAccess\": false,\n" +
                "\"programRuleActionType\": \"SHOWWARNING\",\n" +
                "\"access\": {\n" +
                "\"read\": true,\n" +
                "\"updateWithSection\": true,\n" +
                "\"externalize\": false,\n" +
                "\"delete\": true,\n" +
                "\"write\": true,\n" +
                "\"manage\": false\n" +
                "},\n" +
                "\"programRule\": {\n" +
                "\"id\": \"NAgjOfWMXg6\"\n" +
                "},\n" +
                "\"dataElement\": {\n" +
                "\"id\": \"H6uSAMO5WLD\"\n" +
                "},\n" +
                "\"translations\": [],\n" +
                "\"userGroupAccesses\": [],\n" +
                "\"attributeValues\": []\n" +
                "}", ProgramRuleAction.class);

        assertThat(programRuleAction.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-14T22:22:15.458"));
        assertThat(programRuleAction.uid()).isEqualTo("v434s5YPDcP");
        assertThat(programRuleAction.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-14T21:17:41.033"));
        assertThat(programRuleAction.content()).isEqualTo("It is suggested that an explanation is provided when the Apgar score is below 4");
        assertThat(programRuleAction.data()).isNull();
        assertThat(programRuleAction.location()).isNull();
        assertThat(programRuleAction.trackedEntityAttribute()).isNull();
        assertThat(programRuleAction.programIndicator()).isNull();
        assertThat(programRuleAction.programStageSection()).isNull();
        assertThat(programRuleAction.programRuleActionType()).isEqualTo(ProgramRuleActionType.SHOWWARNING);
        assertThat(programRuleAction.programStage()).isNull();
        assertThat(programRuleAction.dataElement().uid()).isEqualTo("H6uSAMO5WLD");
        assertThat(programRuleAction.programRule().uid()).isEqualTo("NAgjOfWMXg6");
    }
}
