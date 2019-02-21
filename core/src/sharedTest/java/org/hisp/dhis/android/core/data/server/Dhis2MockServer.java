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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.file.IFileReader;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static okhttp3.internal.Util.UTC;

public class Dhis2MockServer {
    private static final int OK_CODE = 200;

    private MockWebServer server;
    private IFileReader fileReader;

    public Dhis2MockServer(IFileReader fileReader) throws IOException {
        this.fileReader = fileReader;
        this.server = new MockWebServer();
        this.server.start();
    }

    public void shutdown() throws IOException {
        server.shutdown();
    }

    public void enqueueMockResponse() throws IOException {
        enqueueMockResponse(OK_CODE);
    }

    public void enqueueMockResponse(int code) throws IOException {
        enqueueMockResponse(code, "{}");
    }

    public void enqueueMockResponse(int code, String response) throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        mockResponse.setBody(response);
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponse(String fileName) throws IOException {
        MockResponse response = createMockResponse(fileName);
        server.enqueue(response);
    }

    public void enqueueLoginResponses() throws IOException {
        enqueueMockResponse("user/login.json");
        enqueueMockResponse("systeminfo/system_info.json");
    }

    public void enqueueMetadataResponses() throws IOException {
        enqueueMetadataResponsesWithUserAndOrgUnits(
                "user/user.json",
                "organisationunit/organisation_units.json");
    }

    public void enqueueAggregatedDataResponses() throws IOException {
        enqueueMockResponse("systeminfo/system_info.json");
        enqueueMockResponse("datavalue/data_values.json");
        enqueueMockResponse("dataset/data_set_complete_registrations.json");
    }

    public void enqueueEventResponses() throws IOException {
        enqueueMockResponse("systeminfo/system_info.json");
        enqueueMockResponse("event/events.json");
    }

    public void enqueueTrackedEntityInstanceResponses() throws IOException {
        enqueueMockResponse("systeminfo/system_info.json");
        enqueueMockResponse("trackedentity/tracked_entity_instances.json");
    }

    public void enqueueMetadataWithDescendentsResponses() throws IOException {
        enqueueMetadataResponsesWithUserAndOrgUnits(
                "user/admin_user.json",
                "organisationunits/admin_organisation_units.json");
    }

    private void enqueueMetadataResponsesWithUserAndOrgUnits(String userPath, String orgUnitPath)
            throws IOException {
        enqueueMockResponse("systeminfo/system_info.json");
        enqueueMockResponse("settings/system_settings.json");
        enqueueMockResponse(userPath);
        enqueueMockResponse("user/authorities.json");
        enqueueMockResponse("program/programs.json");
        enqueueMockResponse("program/program_stages.json");
        enqueueMockResponse("program/program_rules.json");
        enqueueMockResponse("trackedentity/tracked_entity_types.json");
        enqueueMockResponse("relationship/relationship_types.json");
        enqueueMockResponse("option/option_sets.json");
        enqueueMockResponse("option/option_groups.json");
        enqueueMockResponse("dataset/data_sets.json");
        enqueueMockResponse("dataelement/data_elements.json");
        enqueueMockResponse("indicators/indicators.json");
        enqueueMockResponse("indicators/indicator_types.json");
        enqueueMockResponse("category/category_combos.json");
        enqueueMockResponse("category/categories.json");
        enqueueMockResponse(orgUnitPath);
        enqueueMockResponse("organisationunit/organisation_unit_levels.json");
    }

    @NonNull
    private MockResponse createMockResponse(String fileName) throws IOException {
        String body = fileReader.getStringFromFile(fileName);
        MockResponse response = new MockResponse();
        response.setResponseCode(OK_CODE);
        response.setBody(body);
        return response;
    }

    public void enqueueMockResponse(String fileName, Date dateHeader)
            throws IOException {
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
