/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.data.server;

import org.hisp.dhis.android.core.data.file.IFileReader;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static okhttp3.internal.Util.UTC;

public class Dhis2MockServer {
    private static final int OK_CODE = 200;

    private static final String AUTHORITIES_JSON = "user/authorities.json";
    private static final String SYSTEM_INFO_JSON = "systeminfo/system_info.json";
    private static final String SYSTEM_SETTINGS_JSON = "settings/system_settings.json";
    private static final String PROGRAMS_JSON = "program/programs.json";
    private static final String PROGRAM_STAGES_JSON = "program/program_stages.json";
    private static final String PROGRAM_RULES_JSON = "program/program_rules.json";
    private static final String TRACKED_ENTITY_TYPES_JSON = "trackedentity/tracked_entity_types.json";
    private static final String RELATIONSHIP_TYPES_JSON = "relationship/relationship_types.json";
    private static final String OPTION_SETS_JSON = "option/option_sets.json";
    private static final String OPTION_GROUPS_JSON = "option/option_groups.json";
    private static final String DATA_SETS_JSON = "dataset/data_sets.json";
    private static final String DATA_ELEMENTS_JSON = "dataelement/data_elements.json";
    private static final String INDICATORS_JSON = "indicators/indicators.json";
    private static final String INDICATOR_TYPES_JSON = "indicators/indicator_types.json";
    private static final String CATEGORY_COMBOS_JSON = "category/category_combos.json";
    private static final String CATEGORIES_JSON = "category/categories.json";
    private static final String ORGANISATION_UNIT_LEVELS_JSON = "organisationunit/organisation_unit_levels.json";
    private static final String CONSTANTS_JSON = "constant/constants.json";
    private static final String USER_JSON = "user/user.json";
    private static final String EVENTS_JSON = "event/events.json";
    private static final String TRACKED_ENTITY_INSTANCES_JSON = "trackedentity/tracked_entity_instances.json";
    private static final String DATA_VALUES_JSON = "datavalue/data_values.json";
    private static final String DATA_SET_COMPLETE_REGISTRATIONS_JSON = "dataset/data_set_complete_registrations.json";
    private static final String ORGANISATION_UNITS_JSON = "organisationunit/organisation_units.json";

    private MockWebServer server;
    private IFileReader fileReader;

    private Dhis2MockServer(IFileReader fileReader) throws IOException {
        this.fileReader = fileReader;
        this.server = new MockWebServer();
        this.server.start();
    }

    public Dhis2MockServer() throws IOException {
        this(new ResourcesFileReader());
    }

    public void shutdown() throws IOException {
        server.shutdown();
    }

    public void enqueueMockResponse() {
        enqueueMockResponse(OK_CODE);
    }

    public void enqueueMockResponse(int code) {
        enqueueMockResponse(code, "{}");
    }

    public void enqueueMockResponse(int code, String response) {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        mockResponse.setBody(response);
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponse(String fileName) {
        MockResponse response = createMockResponse(fileName);
        server.enqueue(response);
    }

    public void setRequestDispatcher() {
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {

                String path = request.getPath();
                if (path.startsWith("/me?")) {
                    return createMockResponse(USER_JSON);
                } else if (path.equals("/me/authorization")) {
                    return createMockResponse(AUTHORITIES_JSON);
                } else if (path.startsWith("/system/info?")) {
                    return createMockResponse(SYSTEM_INFO_JSON);
                } else if (path.startsWith("/systemSettings?")) {
                    return createMockResponse(SYSTEM_SETTINGS_JSON);
                } else if (path.startsWith("/programs?")) {
                    return createMockResponse(PROGRAMS_JSON);
                } else if (path.startsWith("/programStages?")) {
                    return createMockResponse(PROGRAM_STAGES_JSON);
                } else if (path.startsWith("/programRules?")) {
                    return createMockResponse(PROGRAM_RULES_JSON);
                } else if (path.startsWith("/trackedEntityTypes?")) {
                    return createMockResponse(TRACKED_ENTITY_TYPES_JSON);
                } else if (path.startsWith("/relationshipTypes?")) {
                    return createMockResponse(RELATIONSHIP_TYPES_JSON);
                } else if (path.startsWith("/optionSets?")) {
                    return createMockResponse(OPTION_SETS_JSON);
                } else if (path.startsWith("/optionGroups?")) {
                    return createMockResponse(OPTION_GROUPS_JSON);
                } else if (path.startsWith("/dataSets?")) {
                    return createMockResponse(DATA_SETS_JSON);
                } else if (path.startsWith("/dataElements?")) {
                    return createMockResponse(DATA_ELEMENTS_JSON);
                } else if (path.startsWith("/indicators?")) {
                    return createMockResponse(INDICATORS_JSON);
                } else if (path.startsWith("/indicatorTypes?")) {
                    return createMockResponse(INDICATOR_TYPES_JSON);
                } else if (path.startsWith("/categoryCombos?")) {
                    return createMockResponse(CATEGORY_COMBOS_JSON);
                } else if (path.startsWith("/categories?")) {
                    return createMockResponse(CATEGORIES_JSON);
                } else if (path.startsWith("/organisationUnits/")) {
                    return createMockResponse(ORGANISATION_UNITS_JSON);
                } else if (path.startsWith("/organisationUnitLevels?")) {
                    return createMockResponse(ORGANISATION_UNIT_LEVELS_JSON);
                } else if (path.startsWith("/constants?")) {
                    return createMockResponse(CONSTANTS_JSON);
                } else if (path.startsWith("/trackedEntityInstances?")) {
                    return createMockResponse(TRACKED_ENTITY_INSTANCES_JSON);
                } else if (path.startsWith("/events?")) {
                    return createMockResponse(EVENTS_JSON);
                } else if (path.startsWith("/dataValueSets?")) {
                    return createMockResponse(DATA_VALUES_JSON);
                } else if (path.startsWith("/completeDataSetRegistrations?")) {
                    return createMockResponse(DATA_SET_COMPLETE_REGISTRATIONS_JSON);
                } else {
                    return new MockResponse().setResponseCode(404).setBody("Path not present in Dhis2MockServer dispatcher");
                }
            }
        };
        server.setDispatcher(dispatcher);
    }

    public void enqueueMetadataResponses() {
        enqueueMockResponse(SYSTEM_INFO_JSON);
        enqueueMockResponse(SYSTEM_SETTINGS_JSON);
        enqueueMockResponse(USER_JSON);
        enqueueMockResponse(AUTHORITIES_JSON);
        enqueueMockResponse(PROGRAMS_JSON);
        enqueueMockResponse(PROGRAM_STAGES_JSON);
        enqueueMockResponse(PROGRAM_RULES_JSON);
        enqueueMockResponse(TRACKED_ENTITY_TYPES_JSON);
        enqueueMockResponse(RELATIONSHIP_TYPES_JSON);
        enqueueMockResponse(OPTION_SETS_JSON);
        enqueueMockResponse(OPTION_GROUPS_JSON);
        enqueueMockResponse(DATA_SETS_JSON);
        enqueueMockResponse(DATA_ELEMENTS_JSON);
        enqueueMockResponse(INDICATORS_JSON);
        enqueueMockResponse(INDICATOR_TYPES_JSON);
        enqueueMockResponse(CATEGORY_COMBOS_JSON);
        enqueueMockResponse(CATEGORIES_JSON);
        enqueueMockResponse(ORGANISATION_UNITS_JSON);
        enqueueMockResponse(ORGANISATION_UNIT_LEVELS_JSON);
        enqueueMockResponse(CONSTANTS_JSON);
    }

    @NonNull
    private MockResponse createMockResponse(String fileName) {
        try {
            String body = fileReader.getStringFromFile(fileName);
            MockResponse response = new MockResponse();
            response.setResponseCode(OK_CODE);
            response.setBody(body);
            return response;
        } catch (IOException e) {
            return new MockResponse().setResponseCode(500).setBody("Error reading JSON file for MockServer");
        }
    }

    public void enqueueMockResponse(String fileName, Date dateHeader) {
        MockResponse response = createMockResponse(fileName);

        DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        rfc1123.setLenient(false);
        rfc1123.setTimeZone(UTC);
        String dateHeaderValue = rfc1123.format(dateHeader);

        response.setHeader(HeaderUtils.DATE, dateHeaderValue);

        server.enqueue(response);
    }

    public String getBaseEndpoint() {
        return server.url("/").toString();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }
}
