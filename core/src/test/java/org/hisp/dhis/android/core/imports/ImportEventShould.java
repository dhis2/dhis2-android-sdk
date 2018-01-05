package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportEventShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        ImportEvent importEvent = objectMapper.readValue("{\n" +
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
                "              }", ImportEvent.class);

        assertThat(importEvent.imported()).isEqualTo(0);
        assertThat(importEvent.updated()).isEqualTo(0);
        assertThat(importEvent.ignored()).isEqualTo(1);
        assertThat(importEvent.deleted()).isEqualTo(0);

        assertThat(importEvent.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importEvent.responseType()).isEqualTo("ImportSummaries");
        assertThat(importEvent.importSummaries()).isNotNull();
        assertThat(importEvent.importSummaries().size()).isEqualTo(2);

        ImportSummary importSummary = importEvent.importSummaries().get(0);
        assertThat(importSummary).isNotNull();
        assertThat(importSummary.reference()).isEqualTo("xqpUvfxT4PZ");
        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importSummary.importCount()).isNotNull();

    }
}
