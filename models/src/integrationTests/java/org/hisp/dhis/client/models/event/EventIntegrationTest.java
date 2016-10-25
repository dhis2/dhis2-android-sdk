package org.hisp.dhis.client.models.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class EventIntegrationTest {
    DateFormat dateFormat;
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(dateFormat);
    }

    @Test
    public void event_shouldMapFromJsonString() throws IOException, ParseException {
        Event event = objectMapper.readValue("{\n" +
                "\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/events/wYdGdw16H8F\",\n" +
                "    \"event\": \"wYdGdw16H8F\",\n" +
                "    \"status\": \"COMPLETED\",\n" +
                "    \"program\": \"eBAyeGv0exc\",\n" +
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
                .isEqualTo(dateFormat.parse("2016-10-25T09:21:33.884"));
        assertThat(event.created())
                .isEqualTo(dateFormat.parse("2013-04-18T17:15:08.401"));
        assertThat(event.eventDate())
                .isEqualTo(dateFormat.parse("2015-01-27T00:00:00"));
        assertThat(event.completedDate())
                .isEqualTo(dateFormat.parse("2016-04-21T00:00:00"));
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
