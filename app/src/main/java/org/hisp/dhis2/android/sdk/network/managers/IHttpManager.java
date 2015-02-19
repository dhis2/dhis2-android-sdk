package org.hisp.dhis2.android.sdk.network.managers;

import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.Response;

import java.io.IOException;

public interface IHttpManager {
    public Response request(Request request) throws IOException;
}
