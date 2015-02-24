package org.hisp.dhis2.android.sdk.controllers.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.network.http.ApiRequest;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Header;
import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.RestMethod;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Program;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class RegisterEventTask implements INetworkTask {
    private final ApiRequest.Builder<Object> requestBuilder;

    /**
     *
     * @param networkManager
     * @param callback
     * @param event
     * @param dataValues can be null if no values are entered
     */
    public RegisterEventTask(NetworkManager networkManager,
                           ApiRequestCallback<Object> callback, Event event,
                           List<DataValue> dataValues) {

        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(event, "Event must not be null");

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Accept", "application/json"));
        headers.add(new Header("Content-Type", "application/json"));

        event.dataValues = null;
        byte[] body = null;
        try {
            body = Dhis2.getInstance().getObjectMapper().writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Log.e("ddd", new String(body));

        String url = networkManager.getServerUrl() + "/api/events";
        Request request = new Request(RestMethod.POST, url, headers, null);

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