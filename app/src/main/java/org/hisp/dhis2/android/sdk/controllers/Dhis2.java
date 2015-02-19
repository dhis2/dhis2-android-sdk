package org.hisp.dhis2.android.sdk.controllers;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.Base64Manager;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.User;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.io.IOException;

public final class Dhis2 {
    private static Dhis2 dhis2;
    private MetaDataController metaDataController;
    private ObjectMapper objectMapper;

    private Dhis2() {
        objectMapper = new ObjectMapper();
    }

    public static Dhis2 getInstance() {
        if (dhis2 == null) {
            dhis2 = new Dhis2();
        }

        return dhis2;
    }

    public MetaDataController getMetaDataController() {
        if(metaDataController == null) metaDataController = new MetaDataController();
        return metaDataController;
    }

    /**
     *
     * @param serverUrl
     */
    public void setServer(String serverUrl) {
        NetworkManager.getInstance().setServerUrl(serverUrl);
    }

    /**
     * Tries to log in to the given DHIS 2 server
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        // TODO first check if we already have User through persistence layer
        // TODO if yes, return it, if not call network
        final ResponseHolder<User> holder = new ResponseHolder<>();
        NetworkManager.getInstance().authUser(new ApiRequestCallback<User>() {
            @Override
            public void onSuccess(Response response, User data) {
                holder.setItem(data);
                holder.setResponse(response);

                // TODO call to persistence layer to save
                try {
                    User user = objectMapper.readValue(response.getBody(), User.class);
                    holder.setItem(user);
                } catch (IOException e) {
                    e.printStackTrace();
                    holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                }
                ResponseEvent event = new ResponseEvent();
                event.setResponseHolder(holder);
                Dhis2Application.bus.post(event);
            }

            @Override
            public void onFailure(APIException exception) {
                holder.setApiException(exception);
                ResponseEvent event = new ResponseEvent();
                event.setResponseHolder(holder);
                Dhis2Application.bus.post(event);
            }
        }, username, password);

        /*if (holder.getApiException() != null) {
            throw holder.getApiException();
        } else {
            return holder.getItem();
        }*/
    }
}
