package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramIndicatorIntegrationTest {
    @Test
    public void programIndicator_shouldMapFromJsonString() throws IOException, ParseException {
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
                "\"update\": true,\n" +
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