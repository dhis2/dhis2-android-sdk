package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportEnrollmentShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        ImportEnrollment importEnrollment = objectMapper.readValue("{\n" +
                "          \"responseType\": \"ImportSummaries\",\n" +
                "          \"status\": \"SUCCESS\",\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 1,\n" +
                "          \"deleted\": 0,\n" +
                "          \"ignored\": 0,\n" +
                "          \"importSummaries\": [\n" +
                "            {\n" +
                "              \"responseType\": \"ImportSummary\",\n" +
                "              \"status\": \"SUCCESS\",\n" +
                "              \"importCount\": {\n" +
                "                \"imported\": 0,\n" +
                "                \"updated\": 1,\n" +
                "                \"ignored\": 0,\n" +
                "                \"deleted\": 0\n" +
                "              },\n" +
                "              \"reference\": \"XaBZwKbHVxS\",\n" +
                "              \"events\": {\n" +
                "                \"responseType\": \"ImportSummaries\",\n" +
                "                \"status\": \"SUCCESS\",\n" +
                "                \"imported\": 0,\n" +
                "                \"updated\": 0,\n" +
                "                \"deleted\": 0,\n" +
                "                \"ignored\": 1,\n" +
                "                \"importSummaries\": [\n" +
                "                  {\n" +
                "                    \"responseType\": \"ImportSummary\",\n" +
                "                    \"status\": \"SUCCESS\",\n" +
                "                    \"importCount\": {\n" +
                "                      \"imported\": 0,\n" +
                "                      \"updated\": 0,\n" +
                "                      \"ignored\": 0,\n" +
                "                      \"deleted\": 0\n" +
                "                    },\n" +
                "                    \"reference\": \"xqpUvfxT4PZ\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"responseType\": \"ImportSummary\",\n" +
                "                    \"status\": \"SUCCESS\",\n" +
                "                    \"importCount\": {\n" +
                "                      \"imported\": 0,\n" +
                "                      \"updated\": 0,\n" +
                "                      \"ignored\": 1,\n" +
                "                      \"deleted\": 0\n" +
                "                    },\n" +
                "                    \"conflicts\": [\n" +
                "                      {\n" +
                "                        \"object\": \"bx6fsa0t90x\",\n" +
                "                        \"value\": \"value_not_bool\"\n" +
                "                      }\n" +
                "                    ],\n" +
                "                    \"reference\": \"DB2DLIEi2sX\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }", ImportEnrollment.class);

        assertThat(importEnrollment.imported()).isEqualTo(0);
        assertThat(importEnrollment.updated()).isEqualTo(1);
        assertThat(importEnrollment.ignored()).isEqualTo(0);
        assertThat(importEnrollment.deleted()).isEqualTo(0);

        assertThat(importEnrollment.responseType()).isEqualTo("ImportSummaries");
        assertThat(importEnrollment.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importEnrollment.importSummaries()).isNotNull();

        assertThat(importEnrollment.importSummaries()).size().isEqualTo(1);

        ImportSummary importSummary = importEnrollment.importSummaries().get(0);

        assertThat(importSummary).isNotNull();
        assertThat(importSummary.importEvent()).isNotNull();
        assertThat(importSummary.reference()).isEqualTo("XaBZwKbHVxS");
    }
}
