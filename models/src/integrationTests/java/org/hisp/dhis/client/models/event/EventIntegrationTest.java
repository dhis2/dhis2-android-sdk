/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.models.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class EventIntegrationTest {

    @Test
    public void event_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Event event = objectMapper.readValue("{\n" +
                "\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/events/wYdGdw16H8F\",\n" +
                "    \"event\": \"wYdGdw16H8F\",\n" +
                "    \"status\": \"COMPLETED\",\n" +
                "    \"program\": \"eBAyeGv0excﬁ\",\n" +
                "    \"programStage\": \"Zj7UnCAulEk\",\n" +
                "    \"enrollment\": \"RiLEKhWHlxZ\",\n" +
                "    \"enrollmentStatus\": \"ACTIVE\",\n" +
                "    \"organisationUnit\": \"Rp268JB6Ne4\",\n" +
                "    \"eventDate\": \"2015-01-27T00:00:00\",\n" +
                "    \"trackedEntityDataValues\": [\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"142\",\n" +
                "            \"dataElement\": \"GieVkTxp4HH\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"76\",\n" +
                "            \"dataElement\": \"vV9UWAZohSf\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"44\",\n" +
                "            \"dataElement\": \"qrur9Dvnyt5\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"Male\",\n" +
                "            \"dataElement\": \"oZg33kd9taw\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"2014-01-13\",\n" +
                "            \"dataElement\": \"eMyVanycQSC\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"2014-01-27\",\n" +
                "            \"dataElement\": \"msodh3rEMJa\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"MODABSC\",\n" +
                "            \"dataElement\": \"fWIAEtYVEGk\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"created\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"lastUpdated\": \"2015-04-21T14:19:09.186+0000\",\n" +
                "            \"value\": \"V595\",\n" +
                "            \"dataElement\": \"K6uUAvq500H\",\n" +
                "            \"providedElsewhere\": false,\n" +
                "            \"storedBy\": \"[Unknown]\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"followup\": false,\n" +
                "    \"created\": \"2016-04-21T14:19:09\",\n" +
                "    \"lastUpdated\": \"2016-04-21T14:19:09\",\n" +
                "    \"completedDate\": \"2016-04-21T00:00:00\"\n" +
                "\n" +
                "}", Event.class);

        assertThat(event.lastUpdated())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-25T09:21:33.884"));
        assertThat(event.created())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-18T17:15:08.401"));
        assertThat(event.eventDate())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2015-01-27T00:00:00"));
        assertThat(event.completedDate())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-21T00:00:00"));
        assertThat(event.uid()).isEqualTo("ZyjSDLHGPv4");
        assertThat(event.status().toString()).isEqualTo("ACTIVE");
        assertThat(event.organisationUnit()).isEqualTo("Rp268JB6Ne4");
        assertThat(event.program()).isEqualTo("eBAyeGv0exc");
        assertThat(event.programStage()).isEqualTo("Zj7UnCAulEk");
        assertThat(event.enrollmentUid()).isEqualTo("RiLEKhWHlxZ");

        assertThat(event.trackedEntityDataValues().get(0).dataElement()).isEqualTo("GieVkTxp4HH");
        assertThat(event.trackedEntityDataValues().get(1).dataElement()).isEqualTo("vV9UWAZohSf");
        assertThat(event.trackedEntityDataValues().get(2).dataElement()).isEqualTo("vV9UWAZohSf");
        assertThat(event.trackedEntityDataValues().get(3).dataElement()).isEqualTo("qrur9Dvnyt5");
        assertThat(event.trackedEntityDataValues().get(4).dataElement()).isEqualTo("oZg33kd9taw");
        assertThat(event.trackedEntityDataValues().get(5).dataElement()).isEqualTo("eMyVanycQSC");
        assertThat(event.trackedEntityDataValues().get(6).dataElement()).isEqualTo("msodh3rEMJa");
        assertThat(event.trackedEntityDataValues().get(7).dataElement()).isEqualTo("fWIAEtYVEGk");
        assertThat(event.trackedEntityDataValues().get(8).dataElement()).isEqualTo("K6uUAvq500H");




    }
}
