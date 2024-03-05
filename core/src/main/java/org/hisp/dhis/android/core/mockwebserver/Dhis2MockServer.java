/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.mockwebserver;

import static okhttp3.internal.Util.UTC;

import android.util.Log;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.file.IFileReader;
import org.hisp.dhis.android.core.arch.file.ResourcesFileReader;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SuppressWarnings("PMD")
public class Dhis2MockServer {
    private static final int OK_CODE = 200;

    private static final String AUTHORITIES_JSON = "user/authorities.json";
    private static final String SYSTEM_INFO_JSON = "systeminfo/system_info.json";
    private static final String SYSTEM_SETTINGS_JSON = "settings/system_settings.json";
    private static final String STOCK_USE_CASES_JSON = "usecase.stock/stock_use_cases.json";
    private static final String ANDROID_SETTINGS_INFO_JSON = "settings/app_info.json";
    private static final String GENERAL_SETTINGS_V1_JSON = "settings/general_settings_v1.json";
    private static final String GENERAL_SETTINGS_V2_JSON = "settings/general_settings_v2.json";
    private static final String DATASET_SETTINGS_JSON = "settings/dataset_settings.json";
    private static final String PROGRAM_SETTINGS_JSON = "settings/program_settings.json";
    private static final String SYNCHRONIZATION_SETTTINGS_JSON = "settings/synchronization_settings.json";
    private static final String APPEARANCE_SETTINGS_JSON = "settings/appearance_settings_v2.json";
    private static final String ANALYTICS_SETTINGS_JSON = "settings/analytics_settings_v3.json";
    private static final String USER_SETTINGS_JSON = "settings/user_settings.json";
    private static final String LATEST_APP_VERSION_JSON = "settings/latest_app_version.json";
    private static final String PROGRAMS_JSON = "program/programs.json";
    private static final String PROGRAMS_INDICATORS_JSON = "program/program_indicators.json";
    private static final String PROGRAM_STAGES_JSON = "program/program_stages.json";
    private static final String PROGRAM_RULES_JSON = "program/program_rules.json";
    private static final String TRACKED_ENTITY_INSTANCE_FILTERS_JSON =
            "trackedentity/tracked_entity_instance_filters.json";
    private static final String EVENT_FILTERS_JSON = "event/event_filters.json";

    private static final String PROGRAM_STAGE_WORKING_LISTS =
            "programstageworkinglist/program_stage_working_lists.json";
    private static final String TRACKED_ENTITY_TYPES_JSON = "trackedentity/tracked_entity_types.json";
    private static final String TRACKED_ENTITY_ATTRIBUTES_JSON = "trackedentity/tracked_entity_attributes.json";
    private static final String RELATIONSHIP_TYPES_JSON = "relationship/relationship_types.json";
    private static final String OPTION_SETS_JSON = "option/option_sets.json";
    private static final String OPTIONS_JSON = "option/options.json";
    private static final String OPTION_GROUPS_JSON = "option/option_groups.json";
    private static final String VALIDATION_RULE_UIDS_JSON = "validation/validation_rule_uids.json";
    private static final String VALIDATION_RULES_JSON = "validation/validation_rules.json";
    private static final String DATA_SETS_JSON = "dataset/data_sets.json";
    private static final String DATA_ELEMENTS_JSON = "dataelement/data_elements.json";
    private static final String ATTRIBUTES_JSON = "attribute/attributes.json";
    private static final String INDICATORS_JSON = "indicators/indicators.json";
    private static final String INDICATOR_TYPES_JSON = "indicators/indicator_types.json";
    private static final String CATEGORY_COMBOS_JSON = "category/category_combos.json";
    private static final String CATEGORIES_JSON = "category/categories.json";
    private static final String CATEGORY_OPTIONS_JSON = "category/category_options.json";
    private static final String CATEGORY_OPTION_ORGUNITS_JSON = "category/category_option_orgunits.json";
    private static final String VISUALIZATIONS_1_JSON = "visualization/visualizations_1.json";
    private static final String VISUALIZATIONS_2_JSON = "visualization/visualizations_2.json";
    private static final String TRACKER_VISUALIZATIONS_1_JSON = "visualization/tracker_visualizations_1.json";
    private static final String ORGANISATION_UNIT_LEVELS_JSON = "organisationunit/organisation_unit_levels.json";
    private static final String CONSTANTS_JSON = "constant/constants.json";
    private static final String USER_JSON = "user/user38.json";
    private static final String EVENTS_JSON = "event/events.json";
    private static final String NEW_EVENTS_JSON = "event/new_tracker_importer_events.json";
    private static final String LEGEND_SETS_JSON = "legendset/legend_sets.json";
    private static final String EXPRESSION_DIMENSION_ITEMS = "expressiondimensionitem/expression_dimension_items.json";
    private static final String CUSTOM_ICONS_JSON = "icon/custom_icons.json";
    private static final String TRACKED_ENTITY_INSTANCES_JSON = "trackedentity/tracked_entity_instances.json";
    private static final String NEW_TRACKED_ENTITY_INSTANCES_JSON =
            "trackedentity/new_tracker_importer_tracked_entities.json";
    private static final String DATA_VALUES_JSON = "datavalue/data_values.json";
    private static final String TRACKED_ENTITY_IMAGE = "trackedentity/tracked_entity_attribute_value_image.png";
    private static final String FILE_RESOURCE = "trackedentity/tracked_entity_attribute_value_image_resource.json";
    private static final String DATA_STORE_NAMESPACES = "datastore/namespaces.json";
    private static final String DATA_STORE_NAMESPACE_CAPTURE = "datastore/namespace_capture.json";
    private static final String DATA_STORE_NAMESPACE_SCORECARD = "datastore/namespace_scorecard.json";
    private static final String DATA_SET_COMPLETE_REGISTRATIONS_JSON = "dataset/data_set_complete_registrations.json";
    private static final String DATA_APPROVALS_MULTIPLE_JSON = "dataapproval/data_approvals_multiple.json";
    private static final String ORGANISATION_UNITS_JSON = "organisationunit/organisation_units.json";
    private static final String RESERVE_VALUES_JSON = "trackedentity/tracked_entity_attribute_reserved_values.json";
    private static final String SMS_METADATA = "sms/metadata_ids.json";
    private static final String MOCKWEBSERVER = "Dhis2MockWebServer";

    private final MockWebServer server;
    private final IFileReader fileReader;
    private final Dhis2Dispatcher dhis2Dispatcher;

    public Dhis2MockServer(IFileReader fileReader, int port) {
        this.fileReader = fileReader;
        this.server = new MockWebServer();
        dhis2Dispatcher = new Dhis2Dispatcher(fileReader, new ResponseController());
        start(port);
    }

    public Dhis2MockServer(int port) {
        this(new ResourcesFileReader(), port);
        dhis2Dispatcher.configInternalResponseController();
    }

    private void start(int port) {
        try {
            this.server.start(port);
        } catch (IOException e) {
            Log.e(MOCKWEBSERVER, "Could not start server");
        }
    }

    public void shutdown() {
        try {
            this.server.shutdown();
        } catch (IOException e) {
            Log.e(MOCKWEBSERVER, "Could not shutdown server");
        }
    }

    public void enqueueMockResponse() {
        enqueueMockResponse(OK_CODE);
    }

    public void enqueueMockResponse(int code) {
        enqueueMockResponseText(code, "{}");
    }

    public void enqueueMockResponseWithEmptyBody(int code) {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponseText(int code, String response) {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        mockResponse.setBody(response);
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponse(int code, String fileName) {
        MockResponse response = createMockResponse(fileName, code);
        server.enqueue(response);
    }

    public void enqueueMockResponse(String fileName) {
        MockResponse response = createMockResponse(fileName);
        server.enqueue(response);
    }

    public void setDhis2Dispatcher() {
        server.setDispatcher(dhis2Dispatcher);
    }

    public void setRequestDispatcher() {
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {

                String path = request.getPath();
                if (path.startsWith("/api/me?")) {
                    return createMockResponse(USER_JSON);
                } else if ("/api/me/authorization".equals(path)) {
                    return createMockResponse(AUTHORITIES_JSON);
                } else if (path.startsWith("/api/system/info?")) {
                    return createMockResponse(SYSTEM_INFO_JSON);
                } else if (path.startsWith("/api/systemSettings?")) {
                    return createMockResponse(SYSTEM_SETTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/USE_CASES/stockUseCases")) {
                    return createMockResponse(STOCK_USE_CASES_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/info")) {
                    return createMockResponse(ANDROID_SETTINGS_INFO_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTING_APP/general_settings")) {
                    return createMockResponse(GENERAL_SETTINGS_V1_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTING_APP/dataSet_settings")) {
                    return createMockResponse(DATASET_SETTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTING_APP/program_settings")) {
                    return createMockResponse(PROGRAM_SETTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/generalSettings")) {
                    return createMockResponse(GENERAL_SETTINGS_V2_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/synchronization")) {
                    return createMockResponse(SYNCHRONIZATION_SETTTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/appearance")) {
                    return createMockResponse(APPEARANCE_SETTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/ANDROID_SETTINGS_APP/analytics")) {
                    return createMockResponse(ANALYTICS_SETTINGS_JSON);
                } else if (path.startsWith("/api/userSettings?")) {
                    return createMockResponse(USER_SETTINGS_JSON);
                } else if (path.startsWith("/api/dataStore/APK_DISTRIBUTION/latestVersion")) {
                    return createMockResponse(LATEST_APP_VERSION_JSON);
                } else if (path.startsWith("/api/programs?")) {
                    return createMockResponse(PROGRAMS_JSON);
                } else if (path.startsWith("/api/programIndicators?")) {
                    return createMockResponse(PROGRAMS_INDICATORS_JSON);
                } else if (path.startsWith("/api/programStages?")) {
                    return createMockResponse(PROGRAM_STAGES_JSON);
                } else if (path.startsWith("/api/trackedEntityTypes?")) {
                    return createMockResponse(TRACKED_ENTITY_TYPES_JSON);
                } else if (path.startsWith("/api/trackedEntityAttributes?")) {
                    return createMockResponse(TRACKED_ENTITY_ATTRIBUTES_JSON);
                } else if (path.startsWith("/api/programRules?")) {
                    return createMockResponse(PROGRAM_RULES_JSON);
                } else if (path.startsWith("/api/trackedEntityInstanceFilters?")) {
                    return createMockResponse(TRACKED_ENTITY_INSTANCE_FILTERS_JSON);
                } else if (path.startsWith("/api/eventFilters?")) {
                    return createMockResponse(EVENT_FILTERS_JSON);
                } else if (path.startsWith("/api/programStageWorkingLists?")) {
                    return createMockResponse(PROGRAM_STAGE_WORKING_LISTS);
                } else if (path.startsWith("/api/relationshipTypes?")) {
                    return createMockResponse(RELATIONSHIP_TYPES_JSON);
                } else if (path.startsWith("/api/optionSets?")) {
                    return createMockResponse(OPTION_SETS_JSON);
                } else if (path.startsWith("/api/options?")) {
                    return createMockResponse(OPTIONS_JSON);
                } else if (path.startsWith("/api/optionGroups?")) {
                    return createMockResponse(OPTION_GROUPS_JSON);
                } else if (path.startsWith("/api/validationRules?dataSet")) {
                    return createMockResponse(VALIDATION_RULE_UIDS_JSON);
                } else if (path.startsWith("/api/validationRules?")) {
                    return createMockResponse(VALIDATION_RULES_JSON);
                } else if (path.startsWith("/api/dataSets?")) {
                    return createMockResponse(DATA_SETS_JSON);
                } else if (path.startsWith("/api/dataElements?")) {
                    return createMockResponse(DATA_ELEMENTS_JSON);
                } else if (path.startsWith("/api/attributes?")) {
                    return createMockResponse(ATTRIBUTES_JSON);
                } else if (path.startsWith("/api/indicators?")) {
                    return createMockResponse(INDICATORS_JSON);
                } else if (path.startsWith("/api/indicatorTypes?")) {
                    return createMockResponse(INDICATOR_TYPES_JSON);
                } else if (path.startsWith("/api/categoryCombos?")) {
                    return createMockResponse(CATEGORY_COMBOS_JSON);
                } else if (path.startsWith("/api/categories?")) {
                    return createMockResponse(CATEGORIES_JSON);
                } else if (path.startsWith("/api/categoryOptions?")) {
                    return createMockResponse(CATEGORY_OPTIONS_JSON);
                } else if (path.startsWith("/api/categoryOptions/orgUnits?")) {
                    return createMockResponse(CATEGORY_OPTION_ORGUNITS_JSON);
                } else if (path.startsWith("/api/visualizations/PYBH8ZaAQnC?")) {
                    return createMockResponse(VISUALIZATIONS_1_JSON);
                } else if (path.startsWith("/api/visualizations/FAFa11yFeFe?")) {
                    return createMockResponse(VISUALIZATIONS_2_JSON);
                } else if (path.startsWith("/api/eventVisualizations/s85urBIkN0z?")) {
                    return createMockResponse(TRACKER_VISUALIZATIONS_1_JSON);
                } else if (path.startsWith("/api/organisationUnits?")) {
                    return createMockResponse(ORGANISATION_UNITS_JSON);
                } else if (path.startsWith("/api/organisationUnitLevels?")) {
                    return createMockResponse(ORGANISATION_UNIT_LEVELS_JSON);
                } else if (path.startsWith("/api/constants?")) {
                    return createMockResponse(CONSTANTS_JSON);
                } else if (path.startsWith("/api/trackedEntityInstances?")) {
                    return createMockResponse(TRACKED_ENTITY_INSTANCES_JSON);
                } else if (path.startsWith("/api/tracker/trackedEntities?")) {
                    return createMockResponse(NEW_TRACKED_ENTITY_INSTANCES_JSON);
                } else if (path.startsWith("/api/events?")) {
                    return createMockResponse(EVENTS_JSON);
                } else if (path.startsWith("/api/tracker/events?")) {
                    return createMockResponse(NEW_EVENTS_JSON);
                } else if (path.startsWith("/api/dataValueSets?")) {
                    return createMockResponse(DATA_VALUES_JSON);
                } else if (path.startsWith("/api/completeDataSetRegistrations?")) {
                    return createMockResponse(DATA_SET_COMPLETE_REGISTRATIONS_JSON);
                } else if (path.startsWith("/api/dataApprovals/multiple?")) {
                    return createMockResponse(DATA_APPROVALS_MULTIPLE_JSON);
                } else if (path.startsWith("/api/legendSets?")) {
                    return createMockResponse(LEGEND_SETS_JSON);
                } else if (path.startsWith("/api/expressionDimensionItems?")) {
                    return createMockResponse(EXPRESSION_DIMENSION_ITEMS);
                } else if (path.startsWith("/api/icons?")) {
                    return createMockResponse(CUSTOM_ICONS_JSON);
                } else if (path.startsWith("/api/trackedEntityAttributes/aejWyOfXge6/generateAndReserve")) {
                    return createMockResponse(RESERVE_VALUES_JSON);
                } else if (path.startsWith("/api/metadata")) {
                    return createMockResponse(SMS_METADATA);
                } else if (path.startsWith("/api/fileResources")) {
                    return createMockResponse(FILE_RESOURCE);
                } else if (path.startsWith("/api/trackedEntityInstances/nWrB0TfWlvh/aejWyOfXge6/image")) {
                    return createMockResponse(TRACKED_ENTITY_IMAGE);
                } else if (path.equals("/api/dataStore")) {
                    return createMockResponse(DATA_STORE_NAMESPACES);
                } else if (path.startsWith("/api/dataStore/capture")) {
                    return createMockResponse(DATA_STORE_NAMESPACE_CAPTURE);
                } else if (path.startsWith("/api/dataStore/scorecard")) {
                    return createMockResponse(DATA_STORE_NAMESPACE_SCORECARD);
                } else {
                    return new MockResponse()
                            .setResponseCode(404)
                            .setBody("Path not present in Dhis2MockServer dispatcher: " + path);
                }
            }
        };
        server.setDispatcher(dispatcher);
    }

    public void enqueueLoginResponses() {
        enqueueMockResponse(USER_JSON);
        enqueueMockResponse(ANDROID_SETTINGS_INFO_JSON);
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON);
        enqueueMockResponse(SYSTEM_INFO_JSON);
    }

    public void enqueueSystemInfoResponse() {
        enqueueMockResponse(SYSTEM_INFO_JSON);
    }

    public void enqueueMetadataResponses() {
        enqueueMockResponse(ANDROID_SETTINGS_INFO_JSON);
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON);
        enqueueMockResponse(SYSTEM_INFO_JSON);
        enqueueMockResponse(GENERAL_SETTINGS_V2_JSON);
        enqueueMockResponse(SYNCHRONIZATION_SETTTINGS_JSON);
        enqueueMockResponse(APPEARANCE_SETTINGS_JSON);
        enqueueMockResponse(ANALYTICS_SETTINGS_JSON);
        enqueueMockResponse(USER_SETTINGS_JSON);
        enqueueMockResponse(SYSTEM_SETTINGS_JSON);
        enqueueMockResponse(LATEST_APP_VERSION_JSON);
        enqueueMockResponse(STOCK_USE_CASES_JSON);
        enqueueMockResponse(CONSTANTS_JSON);
        enqueueMockResponse(USER_JSON);
        enqueueMockResponse(AUTHORITIES_JSON);
        enqueueMockResponse(ORGANISATION_UNIT_LEVELS_JSON);
        enqueueMockResponse(ORGANISATION_UNITS_JSON);
        enqueueMockResponse(PROGRAMS_JSON);
        enqueueMockResponse(PROGRAM_STAGES_JSON);
        enqueueMockResponse(TRACKED_ENTITY_TYPES_JSON);
        enqueueMockResponse(TRACKED_ENTITY_ATTRIBUTES_JSON);
        enqueueMockResponse(PROGRAM_RULES_JSON);
        enqueueMockResponse(RELATIONSHIP_TYPES_JSON);
        enqueueMockResponse(OPTION_SETS_JSON);
        enqueueMockResponse(OPTIONS_JSON);
        enqueueMockResponse(OPTION_GROUPS_JSON);
        enqueueMockResponse(TRACKED_ENTITY_INSTANCE_FILTERS_JSON);
        enqueueMockResponse(EVENT_FILTERS_JSON);
        enqueueMockResponse(PROGRAM_STAGE_WORKING_LISTS);
        enqueueMockResponse(DATA_SETS_JSON);
        enqueueMockResponse(DATA_ELEMENTS_JSON);
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON);
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON);
        enqueueMockResponse(VALIDATION_RULE_UIDS_JSON);
        enqueueMockResponse(VALIDATION_RULES_JSON);
        enqueueMockResponse(CATEGORY_COMBOS_JSON);
        enqueueMockResponse(CATEGORIES_JSON);
        enqueueMockResponse(CATEGORY_OPTIONS_JSON);
        enqueueMockResponse(CATEGORY_OPTION_ORGUNITS_JSON);
        enqueueMockResponse(VISUALIZATIONS_1_JSON);
        enqueueMockResponse(VISUALIZATIONS_2_JSON);
        enqueueMockResponse(404);
        enqueueMockResponse(TRACKER_VISUALIZATIONS_1_JSON);
        enqueueMockResponse(PROGRAMS_INDICATORS_JSON);
        enqueueMockResponse(PROGRAMS_INDICATORS_JSON);
        enqueueMockResponse(INDICATORS_JSON);
        enqueueMockResponse(INDICATOR_TYPES_JSON);
        enqueueMockResponse(LEGEND_SETS_JSON);
        enqueueMockResponse(ATTRIBUTES_JSON);
        enqueueMockResponse(EXPRESSION_DIMENSION_ITEMS);
        enqueueMockResponse(CUSTOM_ICONS_JSON);
    }

    private MockResponse createMockResponse(String fileName) {
        return createMockResponse(fileName, OK_CODE);
    }

    @NonNull
    private MockResponse createMockResponse(String fileName, int code) {
        try {
            String body = fileReader.getStringFromFile(fileName);
            MockResponse response = new MockResponse();
            response.setResponseCode(code);
            response.setBody(body);
            return response;
        } catch (IOException e) {
            return new MockResponse().setResponseCode(500).setBody("Error reading JSON file for MockServer");
        }
    }

    private MockResponse getErrorResponse() {
        return new MockResponse().setResponseCode(500).setBody("Error");
    }

    private MockResponse getErrorNotFoundResponse() {
        return new MockResponse().setResponseCode(404).setBody("Not found");
    }

    public void enqueueMockResponse(String fileName, Date dateHeader) {
        MockResponse response = createMockResponse(fileName);

        DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        rfc1123.setLenient(false);
        rfc1123.setTimeZone(UTC);
        String dateHeaderValue = rfc1123.format(dateHeader);

        response.setHeader("Date", dateHeaderValue);

        server.enqueue(response);
    }

    public String getBaseEndpoint() {
        return server.url("/").toString();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    public void addResponse(String method, String path, String responseName, int responseCode) {
        dhis2Dispatcher.addResponse(method, path, responseName, responseCode);
    }
}
