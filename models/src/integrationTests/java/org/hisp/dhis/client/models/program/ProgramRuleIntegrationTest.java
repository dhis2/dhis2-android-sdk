package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramRuleIntegrationTest {

    @Test
    public void programRule_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramRule programRule = objectMapper.readValue("{\n" +
                "\"created\": \"2015-09-14T21:17:40.841\",\n" +
                "\"lastUpdated\": \"2015-09-14T22:22:15.383\",\n" +
                "\"name\": \"Ask for comment for low apgar\",\n" +
                "\"href\": \"https://play.dhis2.org/dev/api/programRules/NAgjOfWMXg6\",\n" +
                "\"id\": \"NAgjOfWMXg6\",\n" +
                "\"displayName\": \"Ask for comment for low apgar\",\n" +
                "\"description\": \"Show warrning if Apgar is between 0 and 4 and there is no comment provided.\",\n" +
                "\"externalAccess\": false,\n" +
                "\"condition\": \"#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''\",\n" +
                "\"access\": {\n" +
                "\"read\": true,\n" +
                "\"update\": true,\n" +
                "\"externalize\": false,\n" +
                "\"delete\": true,\n" +
                "\"write\": true,\n" +
                "\"manage\": false\n" +
                "},\n" +
                "\"program\": {\n" +
                "\"id\": \"IpHINAT79UW\"\n" +
                "},\n" +
                "\"userGroupAccesses\": [],\n" +
                "\"attributeValues\": [],\n" +
                "\"programRuleActions\": [\n" +
                "{\n" +
                "\"id\": \"v434s5YPDcP\"\n" +
                "}\n" +
                "],\n" +
                "\"translations\": []\n" +
                "},", ProgramRule.class);

        assertThat(programRule.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-14T21:17:40.841"));
        assertThat(programRule.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-14T22:22:15.383"));
        assertThat(programRule.uid()).isEqualTo("NAgjOfWMXg6");

        assertThat(programRule.name()).isEqualTo("Ask for comment for low apgar");
        assertThat(programRule.displayName()).isEqualTo("Ask for comment for low apgar");

        assertThat(programRule.programStage()).isNull();
        assertThat(programRule.program().uid()).isEqualTo("IpHINAT79UW");
        assertThat(programRule.priority()).isNull();
        assertThat(programRule.condition()).isEqualTo("#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''");
        assertThat(programRule.programRuleActions()).isNotNull();
        assertThat(programRule.programRuleActions()).isNotEmpty();
        assertThat(programRule.programRuleActions().get(0).uid()).isEqualTo("v434s5YPDcP");
    }
}
