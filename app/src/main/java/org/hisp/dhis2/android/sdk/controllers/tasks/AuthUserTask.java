package org.hisp.dhis2.android.sdk.controllers.tasks;

import org.hisp.dhis2.android.sdk.network.http.ApiRequest;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Header;
import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.RestMethod;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;


public class AuthUserTask implements INetworkTask {
    private final ApiRequest.Builder<User> requestBuilder;

    public AuthUserTask(NetworkManager networkManager,
                        ApiRequestCallback<User> callback,
                        String username, String password) {
        isNull(username, "Username must not be null");
        isNull(password, "Password must not be null");
        isNull(callback, "ApiRequestCallback must not be null");

        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getBase64Manager()
                .toBase64(username, password)));
        headers.add(new Header("Accept", "application/json"));

        String url = networkManager.getServerUrl() + "/api/me/";
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
