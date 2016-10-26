package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramStageDataElementIntegrationTest {

    @Test
    public void programStageDataElement_shouldMapFromJsonString() throws IOException, ParseException {
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
                "        \"update\": true,\n" +
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
