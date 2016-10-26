package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.common.ValueType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramTrackedEntityAttributeIntegrationTest {

    @Test
    public void programIndicator_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramTrackedEntityAttribute programTrackedEntityAttribute = objectMapper.readValue("{\n" +
                "\"lastUpdated\": \"2016-10-11T10:41:40.401\",\n" +
                "\"id\": \"YhqgQ6Iy4c4\",\n" +
                "\"href\": \"https://play.dhis2.org/dev/api/programTrackedEntityAttributes/YhqgQ6Iy4c4\",\n" +
                "\"created\": \"2016-10-11T10:41:40.401\",\n" +
                "\"name\": \"Child Programme Gender\",\n" +
                "\"shortName\": \"Child Programme Gender\",\n" +
                "\"displayName\": \"Child Programme Gender\",\n" +
                "\"mandatory\": false,\n" +
                "\"displayShortName\": \"Child Programme Gender\",\n" +
                "\"externalAccess\": false,\n" +
                "\"valueType\": \"TEXT\",\n" +
                "\"allowFutureDate\": false,\n" +
                "\"dimensionItem\": \"IpHINAT79UW.cejWyOfXge6\",\n" +
                "\"displayInList\": false,\n" +
                "\"dimensionItemType\": \"PROGRAM_ATTRIBUTE\",\n" +
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
                "\"trackedEntityAttribute\": {\n" +
                "\"id\": \"cejWyOfXge6\"\n" +
                "},\n" +
                "\"translations\": [],\n" +
                "\"userGroupAccesses\": [],\n" +
                "\"attributeValues\": []\n" +
                "}", ProgramTrackedEntityAttribute.class);

        assertThat(programTrackedEntityAttribute.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-11T10:41:40.401"));
        assertThat(programTrackedEntityAttribute.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-11T10:41:40.401"));
        assertThat(programTrackedEntityAttribute.uid()).isEqualTo("YhqgQ6Iy4c4");

        assertThat(programTrackedEntityAttribute.name()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.displayName()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.shortName()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.displayShortName()).isEqualTo("Child Programme Gender");

        assertThat(programTrackedEntityAttribute.mandatory()).isEqualTo(false);
        assertThat(programTrackedEntityAttribute.trackedEntityAttribute().uid()).isEqualTo("cejWyOfXge6");
        assertThat(programTrackedEntityAttribute.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(programTrackedEntityAttribute.allowFutureDate()).isEqualTo(false);
        assertThat(programTrackedEntityAttribute.displayInList()).isEqualTo(false);
    }
}