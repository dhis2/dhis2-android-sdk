package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProgramStageSectionIntegrationTest {

    @Test
    public void programStageSection_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        ProgramStageSection programStageSection = objectMapper.readValue("{\n" +
                "\n" +
                "    \"created\": \"2015-01-26T13:14:09.957\",\n" +
                "    \"lastUpdated\": \"2015-10-14T13:37:29.904\",\n" +
                "    \"name\": \"Care at Birth\",\n" +
                "    \"href\": \"https://play.dhis2.org/dev/api/programStageSections/bbjzL5gp0NZ\",\n" +
                "    \"id\": \"bbjzL5gp0NZ\",\n" +
                "    \"displayName\": \"Care at Birth\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"sortOrder\": 0,\n" +
                "    \"programStage\": {\n" +
                "        \"id\": \"PFDfvmGpsR3\"\n" +
                "    },\n" +
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
                "    \"programIndicators\": [ ],\n" +
                "    \"programStageDataElements\": [\n" +
                "        {\n" +
                "            \"id\": \"mNXtw47lMLW\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Va8xtfuPX1u\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"zjxvVN18Qus\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"IWrTFi5pWRy\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"jQCTAPgN8HX\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"De4ZlZy7TbF\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Te4Z5287Osc\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"PlnFiS1txNO\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"yXzfUiuZZXI\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"eqfd3ASkxGV\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"qVXGGtpjEgh\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"wRs9Fa01zGf\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"tChBdOVHz8D\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"BpUCdOZJLz1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"zOEy7uhyDGz\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"irTn0Agjzz2\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"vvYxuplCyfx\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pbQrNUaPT2Z\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pyD3sW0qBQl\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"rJYL0ogcG8H\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"qVo72Oe3Jb5\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ISufoESNnZ3\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"translations\": [ ]\n" +
                "\n" +
                "}", ProgramStageSection.class);

        assertThat(programStageSection.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-10-14T13:37:29.904"));
        assertThat(programStageSection.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-01-26T13:14:09.957"));
        assertThat(programStageSection.uid()).isEqualTo("bbjzL5gp0NZ");
        assertThat(programStageSection.name()).isEqualTo("Care at Birth");
        assertThat(programStageSection.displayName()).isEqualTo("Care at Birth");
        assertThat(programStageSection.sortOrder()).isEqualTo(0);
        assertThat(programStageSection.programIndicators()).isEmpty();

        assertThat(programStageSection.programStageDataElements().get(0).uid()).isEqualTo("mNXtw47lMLW");
        assertThat(programStageSection.programStageDataElements().get(1).uid()).isEqualTo("Va8xtfuPX1u");
        assertThat(programStageSection.programStageDataElements().get(2).uid()).isEqualTo("zjxvVN18Qus");
        assertThat(programStageSection.programStageDataElements().get(3).uid()).isEqualTo("IWrTFi5pWRy");
        assertThat(programStageSection.programStageDataElements().get(4).uid()).isEqualTo("jQCTAPgN8HX");
        assertThat(programStageSection.programStageDataElements().get(5).uid()).isEqualTo("De4ZlZy7TbF");
        assertThat(programStageSection.programStageDataElements().get(6).uid()).isEqualTo("Te4Z5287Osc");
        assertThat(programStageSection.programStageDataElements().get(7).uid()).isEqualTo("PlnFiS1txNO");
        assertThat(programStageSection.programStageDataElements().get(8).uid()).isEqualTo("yXzfUiuZZXI");
        assertThat(programStageSection.programStageDataElements().get(9).uid()).isEqualTo("eqfd3ASkxGV");
        assertThat(programStageSection.programStageDataElements().get(10).uid()).isEqualTo("qVXGGtpjEgh");
        assertThat(programStageSection.programStageDataElements().get(11).uid()).isEqualTo("wRs9Fa01zGf");
        assertThat(programStageSection.programStageDataElements().get(12).uid()).isEqualTo("tChBdOVHz8D");
        assertThat(programStageSection.programStageDataElements().get(13).uid()).isEqualTo("BpUCdOZJLz1");
        assertThat(programStageSection.programStageDataElements().get(14).uid()).isEqualTo("zOEy7uhyDGz");
        assertThat(programStageSection.programStageDataElements().get(15).uid()).isEqualTo("irTn0Agjzz2");
        assertThat(programStageSection.programStageDataElements().get(16).uid()).isEqualTo("vvYxuplCyfx");
        assertThat(programStageSection.programStageDataElements().get(17).uid()).isEqualTo("pbQrNUaPT2Z");
        assertThat(programStageSection.programStageDataElements().get(18).uid()).isEqualTo("pyD3sW0qBQl");
        assertThat(programStageSection.programStageDataElements().get(19).uid()).isEqualTo("rJYL0ogcG8H");
        assertThat(programStageSection.programStageDataElements().get(20).uid()).isEqualTo("qVo72Oe3Jb5");
        assertThat(programStageSection.programStageDataElements().get(21).uid()).isEqualTo("ISufoESNnZ3");

    }
}
