package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ImportSummariesShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        objectMapper.readValue("{\n" +
                "    \"responseType\": \"ImportSummaries\",\n" +
                "    \"status\": \"SUCCESS\",\n" +
                "    \"imported\": 0,\n" +
                "    \"updated\": 2,\n" +
                "    \"deleted\": 0,\n" +
                "    \"ignored\": 0,\n" +
                "    \"importOptions\": {\n" +
                "      \"idSchemes\": {},\n" +
                "      \"dryRun\": false,\n" +
                "      \"async\": false,\n" +
                "      \"importStrategy\": \"CREATE_AND_UPDATE\",\n" +
                "      \"mergeMode\": \"REPLACE\",\n" +
                "      \"skipExistingCheck\": false,\n" +
                "      \"sharing\": false,\n" +
                "      \"skipNotifications\": false,\n" +
                "      \"datasetAllowsPeriods\": false,\n" +
                "      \"strictPeriods\": false,\n" +
                "      \"strictCategoryOptionCombos\": false,\n" +
                "      \"strictAttributeOptionCombos\": false,\n" +
                "      \"strictOrganisationUnits\": false,\n" +
                "      \"requireCategoryOptionCombo\": false,\n" +
                "      \"requireAttributeOptionCombo\": false\n" +
                "    },\n" +
                "    \"importSummaries\": [\n" +
                "      {\n" +
                "        \"responseType\": \"ImportSummary\",\n" +
                "        \"status\": \"SUCCESS\",\n" +
                "        \"importCount\": {\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 1,\n" +
                "          \"ignored\": 0,\n" +
                "          \"deleted\": 0\n" +
                "        },\n" +
                "        \"reference\": \"k68SkK5yDH9\",\n" +
                "        \"href\": \"https://play.dhis2.org/dev/api/trackedEntityInstances/k68SkK5yDH9\",\n" +
                "        \"enrollments\": {\n" +
                "          \"responseType\": \"ImportSummaries\",\n" +
                "          \"status\": \"SUCCESS\",\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 0,\n" +
                "          \"deleted\": 0,\n" +
                "          \"ignored\": 0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"responseType\": \"ImportSummary\",\n" +
                "        \"status\": \"SUCCESS\",\n" +
                "        \"importCount\": {\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 1,\n" +
                "          \"ignored\": 0,\n" +
                "          \"deleted\": 0\n" +
                "        },\n" +
                "        \"reference\": \"AHgGHO6ZH9b\",\n" +
                "        \"href\": \"https://play.dhis2.org/dev/api/trackedEntityInstances/AHgGHO6ZH9b\",\n" +
                "        \"enrollments\": {\n" +
                "          \"responseType\": \"ImportSummaries\",\n" +
                "          \"status\": \"SUCCESS\",\n" +
                "          \"imported\": 0,\n" +
                "          \"updated\": 0,\n" +
                "          \"deleted\": 0,\n" +
                "          \"ignored\": 0\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }", ImportSummaries.class);

    }
}
