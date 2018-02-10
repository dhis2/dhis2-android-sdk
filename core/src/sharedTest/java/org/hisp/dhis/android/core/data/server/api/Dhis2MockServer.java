/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.data.server.api;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.file.IFileReader;
import org.hisp.dhis.android.core.data.http.HttpHeaderDate;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class Dhis2MockServer {
    private static final int OK_CODE = 200;

    private MockWebServer server;
    IFileReader fileReader;

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

    public void enqueueStringMockResponse(String response, Date dateHeader) throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(OK_CODE);
        mockResponse.setBody(response);

        mockResponse.setHeader(HeaderUtils.DATE, new HttpHeaderDate(dateHeader).toString());

        server.enqueue(mockResponse);
    }

    public void enqueueMockResponse(String fileName) throws IOException {
        MockResponse response = createMockResponse(fileName, OK_CODE);
        server.enqueue(response);
    }

    public void enqueueMockResponse(String fileName, int code) throws IOException {
        MockResponse response = createMockResponse(fileName, code);
        server.enqueue(response);
    }

    @NonNull
    private MockResponse createMockResponse(String fileName, int code) throws IOException {
        String body = fileReader.getStringFromFile(fileName);
        MockResponse response = new MockResponse();
        response.setResponseCode(code);
        response.setBody(body);
        return response;
    }

    public void enqueueMockResponse(String fileName, Date dateHeader)
            throws IOException {
        MockResponse response = createMockResponse(fileName, OK_CODE);

        response.setHeader(HeaderUtils.DATE, new HttpHeaderDate(dateHeader).toString());

        server.enqueue(response);
    }

    public String getBaseEndpoint() {
        return server.url("/").toString();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }
}
