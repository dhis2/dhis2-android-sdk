package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class WebResponseShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        WebResponse webResponse = objectMapper.readValue("{\n" +
                "  \"httpStatus\": \"OK\",\n" +
                "  \"httpStatusCode\": 200,\n" +
                "  \"status\": \"OK\",\n" +
                "  \"message\": \"Import was successful.\",\n" +
                "  \"response\": {\n" +
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
                "  }\n" +
                "}", WebResponse.class);

        assertThat(webResponse.message()).isEqualTo("Import was successful.");
        assertThat(webResponse.importSummaries()).isNotNull();

    }

    @Test
    public void map_from_json_string_with_import_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        objectMapper.readValue("{\n" +
                "  \"httpStatus\": \"OK\",\n" +
                "  \"httpStatusCode\": 200,\n" +
                "  \"status\": \"OK\",\n" +
                "  \"message\": \"Import was successful.\",\n" +
                "  \"response\": {\n" +
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
                "              \"reference\": \"q6voWUYNtFz\",\n" +
                "              \"events\": {\n" +
                "                \"responseType\": \"ImportSummaries\",\n" +
                "                \"status\": \"SUCCESS\",\n" +
                "                \"imported\": 0,\n" +
                "                \"updated\": 0,\n" +
                "                \"deleted\": 0,\n" +
                "                \"ignored\": 0,\n" +
                "                \"importSummaries\": [\n" +
                "                  {\n" +
                "                    \"responseType\": \"ImportSummary\",\n" +
                "                    \"status\": \"SUCCESS\",\n" +
                "                    \"importCount\": {\n" +
                "                      \"imported\": 0,\n" +
                "                      \"updated\": 0,\n" +
                "                      \"ignored\": 0,\n" +
                "                      \"deleted\": 0\n" +
                "                    }\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
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
                "              \"reference\": \"IalhfSlbLVD\",\n" +
                "              \"events\": {\n" +
                "                \"responseType\": \"ImportSummaries\",\n" +
                "                \"status\": \"SUCCESS\",\n" +
                "                \"imported\": 0,\n" +
                "                \"updated\": 0,\n" +
                "                \"deleted\": 0,\n" +
                "                \"ignored\": 29,\n" +
                "                \"importSummaries\": [\n" +
                "                  {\n" +
                "                    \"responseType\": \"ImportSummary\",\n" +
                "                    \"status\": \"SUCCESS\",\n" +
                "                    \"importCount\": {\n" +
                "                      \"imported\": 0,\n" +
                "                      \"updated\": 0,\n" +
                "                      \"ignored\": 29,\n" +
                "                      \"deleted\": 0\n" +
                "                    },\n" +
                "                    \"conflicts\": [\n" +
                "                      {\n" +
                "                        \"object\": \"f2MduVqwPXO\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"IgkWhfPryqt\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"ihgI4dDwUQT\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"KRJeVOPBy6t\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"lsPerCow7QG\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"Ef3ueStqxJj\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"ogBU5CfbAUZ\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"fQMBEt42CSl\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"msiehvzdkh0\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"z8Ay2GaaqpC\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"CtpjtJOlix1\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"VDxTE7l7sc7\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"dGdEEpNb7GW\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"Y35b9mKPwgz\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"qv3Ivgr7qA8\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"ZpvFqxRhFuP\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"GXNUsigphqK\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"zNXca47AaTh\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"uYrt6Rjh0q2\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"BLNHqFdGFRv\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"suMqFd2seuA\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"OKN6ZG1z7pq\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"yqiAt2vL2Oe\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"ZhGKg3ssbmX\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"kSDiqFhUP8P\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"Lh9x3J6EF0g\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"Q5725FPinzH\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"HpC2iqyoMR8\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"object\": \"ROEGWNaasDP\",\n" +
                "                        \"value\": \"value_not_true_only\"\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}", WebResponse.class);

    }
}
