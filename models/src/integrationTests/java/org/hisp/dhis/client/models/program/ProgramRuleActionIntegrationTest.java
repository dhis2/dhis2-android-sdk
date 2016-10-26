package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramRuleActionIntegrationTest {
    @Test
    public void programRuleAction_shouldMapFromJsonString() throws IOException, ParseException {
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
                "\"update\": true,\n" +
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
    }
}
