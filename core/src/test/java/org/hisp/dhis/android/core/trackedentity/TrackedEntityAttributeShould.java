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
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TrackedEntityAttributeShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        TrackedEntityAttribute trackedEntityAttribute = objectMapper.readValue("{\n" +
                "\n" +
                "    \"lastUpdated\": \"2016-08-04T11:48:56.928\",\n" +
                "    \"id\": \"ruQQnf6rswq\",\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/trackedEntityAttributes/ruQQnf6rswq\",\n" +
                "    \"created\": \"2014-01-09T19:12:46.551\",\n" +
                "    \"name\": \"TB number\",\n" +
                "    \"shortName\": \"TB number\",\n" +
                "    \"programScope\": false,\n" +
                "    \"displayInListNoProgram\": false,\n" +
                "    \"displayName\": \"TB number\",\n" +
                "    \"pattern\": \"\",\n" +
                "    \"description\": \"TB number\",\n" +
                "    \"displayShortName\": \"TB number\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"sortOrderInListNoProgram\": 0,\n" +
                "    \"generated\": false,\n" +
                "    \"displayOnVisitSchedule\": false,\n" +
                "    \"valueType\": \"TEXT\",\n" +
                "    \"sortOrderInVisitSchedule\": 0,\n" +
                "    \"orgunitScope\": false,\n" +
                "    \"confidential\": false,\n" +
                "    \"displayDescription\": \"TB number\",\n" +
                "    \"dimensionItem\": \"ruQQnf6rswq\",\n" +
                "    \"searchScope\": \"SEARCH_ORG_UNITS\",\n" +
                "    \"unique\": false,\n" +
                "    \"inherit\": false,\n" +
                "    \"optionSetValue\": false,\n" +
                "    \"dimensionItemType\": \"PROGRAM_ATTRIBUTE\",\n" +
                "    \"access\": {\n" +
                "        \"read\": true,\n" +
                "        \"updateWithSection\": false,\n" +
                "        \"externalize\": false,\n" +
                "        \"delete\": false,\n" +
                "        \"write\": false,\n" +
                "        \"manage\": false\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"id\": \"GOLswS44mh8\"\n" +
                "    },\n" +
                "    \"translations\": [ ],\n" +
                "    \"userGroupAccesses\": [ ],\n" +
                "    \"attributeValues\": [ ],\n" +
                "    \"optionSet\": {\n" +
                "        \"id\": \"xjA5E9MimMU\"\n" +
                "     }\n" +
                "}", TrackedEntityAttribute.class);

        assertThat(trackedEntityAttribute.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-08-04T11:48:56.928"));
        assertThat(trackedEntityAttribute.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-01-09T19:12:46.551"));
        assertThat(trackedEntityAttribute.uid()).isEqualTo("ruQQnf6rswq");
        assertThat(trackedEntityAttribute.name()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.shortName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayShortName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.description()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayDescription()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayInListNoProgram()).isFalse();
        assertThat(trackedEntityAttribute.displayOnVisitSchedule()).isFalse();
        assertThat(trackedEntityAttribute.generated()).isFalse();
        assertThat(trackedEntityAttribute.inherit()).isFalse();
        assertThat(trackedEntityAttribute.optionSet().uid()).isEqualTo("xjA5E9MimMU");
        assertThat(trackedEntityAttribute.orgUnitScope()).isFalse();
        assertThat(trackedEntityAttribute.searchScope()).isEqualTo(TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS);
        assertThat(trackedEntityAttribute.programScope()).isFalse();
        assertThat(trackedEntityAttribute.unique()).isFalse();
        assertThat(trackedEntityAttribute.valueType()).isEqualTo(ValueType.TEXT);
    }
}