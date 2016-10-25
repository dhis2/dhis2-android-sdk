package org.hisp.dhis.client.models.trackedentity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackedEntityIntegrationTest {

    @Test
    public void trackedEntity_shouldMapFromJsonString() throws IOException, ParseException {
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
                "        \"update\": true,\n" +
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
