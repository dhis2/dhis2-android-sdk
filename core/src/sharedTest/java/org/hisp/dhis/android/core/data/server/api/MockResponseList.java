package org.hisp.dhis.android.core.data.server.api;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.file.IFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;

public class MockResponseList {
    private static final int OK_CODE = 200;

    protected List<MockResponse> mockResponseList;

    private IFileReader fileReader;

    public MockResponseList(IFileReader fileReader) {
        mockResponseList = new ArrayList<>();
        this.fileReader = fileReader;
    }

    public void addFileResponse(String fileName) throws IOException {
        mockResponseList.add(createMockResponse(fileName, OK_CODE));
    }

    public List<MockResponse> toList() {
        return new ArrayList<>(mockResponseList);
    }

    @NonNull
    private MockResponse createMockResponse(String fileName, int code) throws IOException {
        String body = fileReader.getStringFromFile(fileName);
        MockResponse response = new MockResponse();
        response.setResponseCode(code);
        response.setBody(body);
        return response;
    }
}
