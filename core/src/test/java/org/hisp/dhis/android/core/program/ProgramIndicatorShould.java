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

public class ProgramIndicatorShould {
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramIndicator programIndicator = objectMapper.readValue("{\n" +
                "\"lastUpdated\": \"2015-09-21T23:47:57.820\",\n" +
                "\"id\": \"GSae40Fyppf\",\n" +
                "\"href\": \"https://play.dhis2.org/dev/api/programIndicators/GSae40Fyppf\",\n" +
                "\"created\": \"2015-09-21T23:35:50.945\",\n" +
                "\"name\": \"Age at visit\",\n" +
                "\"shortName\": \"Age\",\n" +
                "\"aggregationType\": \"AVERAGE\",\n" +
                "\"displayName\": \"Age at visit\",\n" +
                "\"displayInForm\": true,\n" +
                "\"publicAccess\": \"rw------\",\n" +
                "\"description\": \"Age at visit\",\n" +
                "\"displayShortName\": \"Age\",\n" +
                "\"externalAccess\": false,\n" +
                "\"displayDescription\": \"Age at visit\",\n" +
                "\"expression\": \"d2:yearsBetween(A{iESIqZ0R0R0},V{event_date})\",\n" +
                "\"dimensionItem\": \"GSae40Fyppf\",\n" +
                "\"dimensionItemType\": \"PROGRAM_INDICATOR\",\n" +
                "\"access\": {\n" +
                "\"read\": true,\n" +
                "\"updateWithSection\": true,\n" +
                "\"externalize\": false,\n" +
                "\"delete\": true,\n" +
                "\"write\": true,\n" +
                "\"manage\": true\n" +
                "},\n" +
                "\"program\": {\n" +
                "\"id\": \"uy2gU8kT1jF\"\n" +
                "},\n" +
                "\"user\": {\n" +
                "\"id\": \"xE7jOejl9FI\"\n" +
                "},\n" +
                "\"translations\": [],\n" +
                "\"programIndicatorGroups\": [],\n" +
                "\"userGroupAccesses\": [],\n" +
                "\"attributeValues\": []\n" +
                "}", ProgramIndicator.class);

        assertThat(programIndicator.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-21T23:35:50.945"));
        assertThat(programIndicator.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-21T23:47:57.820"));
        assertThat(programIndicator.uid()).isEqualTo("GSae40Fyppf");

        assertThat(programIndicator.name()).isEqualTo("Age at visit");
        assertThat(programIndicator.displayName()).isEqualTo("Age at visit");

        assertThat(programIndicator.displayInForm()).isEqualTo(true);
        assertThat(programIndicator.expression()).isEqualTo("d2:yearsBetween(A{iESIqZ0R0R0},V{event_date})");
        assertThat(programIndicator.dimensionItem()).isEqualTo("GSae40Fyppf");
        assertThat(programIndicator.filter()).isNull();
        assertThat(programIndicator.decimals()).isNull();
    }
}