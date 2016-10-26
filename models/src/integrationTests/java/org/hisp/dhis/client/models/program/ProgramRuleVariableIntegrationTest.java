package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramRuleVariableIntegrationTest {
    @Test
    public void programRuleVariable_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramRuleVariable programRuleVariable = objectMapper.readValue("{\n" +
                "\"created\": \"2015-08-07T18:38:12.931\",\n" +
                "\"lastUpdated\": \"2015-08-07T18:38:12.932\",\n" +
                "\"name\": \"age\",\n" +
                "\"href\": \"https://play.dhis2.org/dev/api/programRuleVariables/RycV5uDi66i\",\n" +
                "\"id\": \"RycV5uDi66i\",\n" +
                "\"displayName\": \"age\",\n" +
                "\"programRuleVariableSourceType\": \"DATAELEMENT_NEWEST_EVENT_PROGRAM\",\n" +
                "\"externalAccess\": false,\n" +
                "\"access\": {\n" +
                "\"read\": true,\n" +
                "\"update\": true,\n" +
                "\"externalize\": false,\n" +
                "\"delete\": true,\n" +
                "\"write\": true,\n" +
                "\"manage\": false\n" +
                "},\n" +
                "\"program\": {\n" +
                "\"id\": \"eBAyeGv0exc\"\n" +
                "},\n" +
                "\"dataElement\": {\n" +
                "\"id\": \"qrur9Dvnyt5\"\n" +
                "},\n" +
                "\"userGroupAccesses\": [],\n" +
                "\"attributeValues\": [],\n" +
                "\"translations\": []\n" +
                "}", ProgramRuleVariable.class);

        assertThat(programRuleVariable.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-07T18:38:12.931"));
        assertThat(programRuleVariable.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-07T18:38:12.932"));
        assertThat(programRuleVariable.uid()).isEqualTo("RycV5uDi66i");

        assertThat(programRuleVariable.name()).isEqualTo("age");
        assertThat(programRuleVariable.displayName()).isEqualTo("age");

        assertThat(programRuleVariable.programStage()).isNull();
        assertThat(programRuleVariable.programRuleVariableSourceType()).isEqualTo(ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM);
        assertThat(programRuleVariable.useCodeForOptionSet()).isNull();
        assertThat(programRuleVariable.program().uid()).isEqualTo("eBAyeGv0exc");
        assertThat(programRuleVariable.dataElement().uid()).isEqualTo("qrur9Dvnyt5");
        assertThat(programRuleVariable.trackedEntityAttribute()).isNull();
    }
}
