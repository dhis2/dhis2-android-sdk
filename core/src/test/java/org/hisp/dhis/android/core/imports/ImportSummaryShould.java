package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportSummaryShould {

    @Test
    public void map_from_json_string_with_tei_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        ImportSummary importSummary = objectMapper.readValue("{\n" +
                "        \"responseType\": \"ImportSummary\",\n" +
                "        \"status\": \"ERROR\",\n" +
                "        \"importOptions\": {\n" +
                "          \"idSchemes\": {},\n" +
                "          \"dryRun\": false,\n" +
                "          \"async\": false,\n" +
                "          \"importStrategy\": \"CREATE_AND_UPDATE\",\n" +
                "          \"mergeMode\": \"REPLACE\",\n" +
                "          \"skipExistingCheck\": false,\n" +
                "          \"sharing\": false,\n" +
                "          \"skipNotifications\": false,\n" +
                "          \"datasetAllowsPeriods\": false,\n" +
                "          \"strictPeriods\": false,\n" +
                "          \"strictCategoryOptionCombos\": false,\n" +
                "          \"strictAttributeOptionCombos\": false,\n" +
                "          \"strictOrganisationUnits\": false,\n" +
                "          \"requireCategoryOptionCombo\": false,\n" +
                "          \"requireAttributeOptionCombo\": false\n" +
                "        },\n" +
                "        \"importCount\": {\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 0,\n" +
                "          \"ignored\": 1,\n" +
                "          \"deleted\": 0\n" +
                "        },\n" +
                "        \"conflicts\": [\n" +
                "          {\n" +
                "            \"object\": \"Attribute.value\",\n" +
                "            \"value\": \"Value '201921212' is not a valid date for attribute iESIqZ0R0R0\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"reference\": \"Rmp5T1vmZ74\"\n" +
                "      }", ImportSummary.class);

        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.ERROR);
        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importCount()).isNotNull();
        assertThat(importSummary.importCount().imported()).isEqualTo(0);
        assertThat(importSummary.importCount().updated()).isEqualTo(0);
        assertThat(importSummary.importCount().ignored()).isEqualTo(1);
        assertThat(importSummary.importCount().deleted()).isEqualTo(0);

        assertThat(importSummary.importConflicts()).isNotNull();
        assertThat(importSummary.importConflicts().size()).isEqualTo(1);

        ImportConflict importConflict = importSummary.importConflicts().get(0);

        assertThat(importConflict).isNotNull();
        assertThat(importConflict.value()).isEqualTo("Value '201921212' is not a valid date for attribute iESIqZ0R0R0");
        assertThat(importConflict.object()).isEqualTo("Attribute.value");
    }

    @Test
    public void importSummary_shouldParseImportSummaryWithEnrollmentConflictsFromJson() throws Exception {
        //TODO Test
    }

    @Test
    public void map_from_json_string_with_event_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        ImportSummary importSummary = objectMapper.readValue("{\n" +
                "        \"responseType\": \"ImportSummary\",\n" +
                "        \"status\": \"SUCCESS\",\n" +
                "        \"importOptions\": {\n" +
                "          \"idSchemes\": {},\n" +
                "          \"dryRun\": false,\n" +
                "          \"async\": false,\n" +
                "          \"importStrategy\": \"CREATE_AND_UPDATE\",\n" +
                "          \"mergeMode\": \"REPLACE\",\n" +
                "          \"skipExistingCheck\": false,\n" +
                "          \"sharing\": false,\n" +
                "          \"skipNotifications\": false,\n" +
                "          \"datasetAllowsPeriods\": false,\n" +
                "          \"strictPeriods\": false,\n" +
                "          \"strictCategoryOptionCombos\": false,\n" +
                "          \"strictAttributeOptionCombos\": false,\n" +
                "          \"strictOrganisationUnits\": false,\n" +
                "          \"requireCategoryOptionCombo\": false,\n" +
                "          \"requireAttributeOptionCombo\": false\n" +
                "        },\n" +
                "        \"importCount\": {\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 1,\n" +
                "          \"ignored\": 0,\n" +
                "          \"deleted\": 0\n" +
                "        },\n" +
                "        \"reference\": \"Rmp5T1vmZ74\",\n" +
                "        \"href\": \"https://play.dhis2.org/dev/api/trackedEntityInstances/Rmp5T1vmZ74\",\n" +
                "        \"enrollments\": {\n" +
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
                "        }\n" +
                "      }", ImportSummary.class);

        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importSummary.importCount()).isNotNull();

        assertThat(importSummary.importCount().imported()).isEqualTo(0);
        assertThat(importSummary.importCount().updated()).isEqualTo(1);
        assertThat(importSummary.importCount().ignored()).isEqualTo(0);
        assertThat(importSummary.importCount().deleted()).isEqualTo(0);

        assertThat(importSummary.reference()).isEqualTo("Rmp5T1vmZ74");

        assertThat(importSummary.importEvent()).isNull();
        assertThat(importSummary.importEnrollment()).isNotNull();


    }
}
