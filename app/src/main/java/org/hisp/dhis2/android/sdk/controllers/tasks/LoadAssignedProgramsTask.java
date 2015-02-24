package org.hisp.dhis2.android.sdk.controllers.tasks;

import android.util.Log;

import org.hisp.dhis2.android.sdk.network.http.ApiRequest;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Header;
import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.RestMethod;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;


public class LoadAssignedProgramsTask implements INetworkTask {
    private final ApiRequest.Builder<List<OrganisationUnit>> requestBuilder;

    public LoadAssignedProgramsTask(NetworkManager networkManager,
                                    ApiRequestCallback<List<OrganisationUnit>> callback) {

        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Accept", "application/json"));

        String url = networkManager.getServerUrl() + "/api/me/programs/";
        Request request = new Request(RestMethod.GET, url, headers, null);

        requestBuilder = new ApiRequest.Builder<>();
        requestBuilder.setRequest(request);
        requestBuilder.setNetworkManager(networkManager.getHttpManager());
        requestBuilder.setRequestCallback(callback);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                requestBuilder.build().request();
            }
        }.start();
    }
}
