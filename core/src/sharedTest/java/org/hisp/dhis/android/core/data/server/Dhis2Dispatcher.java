package org.hisp.dhis.android.core.data.server;

import android.util.Log;

import org.hisp.dhis.android.core.data.file.IFileReader;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class Dhis2Dispatcher extends Dispatcher {

    private static final String JSON = ".json";
    private static final String DISPATCHER = "Dispatcher";

    private IFileReader fileReader;
    private ResponseController responseController;

    public Dhis2Dispatcher(IFileReader fileReader, ResponseController responseController){
        this.fileReader = fileReader;
        this.responseController = responseController;
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String method = request.getMethod().toUpperCase();
        String path = request.getPath();

        String fileName = responseController.getBody(method, path);
        int httpCode = responseController.getCode(fileName);

        try {
            String body = fileReader.getStringFromFile(fileName+JSON);
            Log.i(DISPATCHER, String.format(method, path, body));
            return new MockResponse().setBody(body).setResponseCode(httpCode);
        } catch (IOException e) {
            return new MockResponse().setResponseCode(500).setBody("Error reading JSON file for MockServer");
        }
    }

    public void addResponse(String method, String path, String responseName, int responseCode) {
        responseController.addResponse(method, path, responseName, responseCode);
    }
}
