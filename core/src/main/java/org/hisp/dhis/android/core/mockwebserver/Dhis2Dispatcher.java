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
