/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import android.util.Log;

import org.hisp.dhis.android.core.arch.file.IFileReader;

import java.io.IOException;
import java.util.Locale;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class Dhis2Dispatcher extends Dispatcher {

    private static final String DISPATCHER = "Dispatcher";

    private final IFileReader fileReader;
    private final ResponseController responseController;

    Dhis2Dispatcher(IFileReader fileReader, ResponseController responseController){
        this.fileReader = fileReader;
        this.responseController = responseController;
    }

    void configInternalResponseController(){
        responseController.populateInternalResponses();
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String method = request.getMethod().toUpperCase(Locale.getDefault());
        String path = request.getPath();

        String fileName = responseController.getBody(method, path);
        int httpCode = responseController.getCode(fileName);

        try {
            String body = fileReader.getStringFromFile(fileName);
            Log.i(DISPATCHER, String.format(method, path, body));
            return new MockResponse().setBody(body).setResponseCode(httpCode);
        } catch (IOException e) {
            return new MockResponse().setResponseCode(500).setBody("Error reading JSON file for MockServer");
        }
    }

    void addResponse(String method, String path, String responseName, int responseCode) {
        responseController.addResponse(method, path, responseName, responseCode);
    }
}
