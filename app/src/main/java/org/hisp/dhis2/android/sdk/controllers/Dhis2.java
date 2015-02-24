package org.hisp.dhis2.android.sdk.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.Base64Manager;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.User;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.hisp.dhis2.android.sdk.utils.CustomDialogFragment;

import java.io.IOException;

public final class Dhis2 {

    public final static String QUEUED = "queued";
    public final static String PREFS_NAME = "DHIS2";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String SERVER = "server";
    private static Dhis2 dhis2;
    private MetaDataController metaDataController;
    private DataValueController dataValueController;
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

    public ObjectMapper getObjectMapper() {
        if(objectMapper==null) objectMapper = new ObjectMapper();
        return objectMapper;
    }

    public MetaDataController getMetaDataController() {
        if(metaDataController == null) metaDataController = new MetaDataController();
        return metaDataController;
    }

    public DataValueController getDataValueController() {
        if(dataValueController == null) dataValueController = new DataValueController();
        return dataValueController;
    }

    /**
     *
     * @param serverUrl
     */
    public void setServer(String serverUrl) {
        NetworkManager.getInstance().setServerUrl(serverUrl);
    }

    public void saveCredentials(Context context, String serverUrl, String username, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putString(SERVER, serverUrl);
        editor.commit();
    }

    public String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(USERNAME, null);
        return username;
    }

    public String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String password = prefs.getString(PASSWORD, null);
        return password;
    }

    public String getServer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String server = prefs.getString(SERVER, null);
        return server;
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

                try {
                    User user = objectMapper.readValue(response.getBody(), User.class);
                    holder.setItem(user);
                } catch (IOException e) {
                    e.printStackTrace();
                    holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                }
                ResponseEvent<User> event = new ResponseEvent<User>(ResponseEvent.EventType.onLogin);
                event.setResponseHolder(holder);
                Dhis2Application.bus.post(event);
            }

            @Override
            public void onFailure(APIException exception) {
                holder.setApiException(exception);
                ResponseEvent event = new ResponseEvent(ResponseEvent.EventType.onLogin);
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

    public void showErrorDialog(final Activity activity, final String title, final String message) {
        if(activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CustomDialogFragment( title, message,
                        "OK", null ).show(activity.getFragmentManager(), title);
            }
        });
    }

    public void showConfirmDialog(final Activity activity, final String title, final String message,
                                  final String confirmOption, final String cancelOption,
                                  DialogInterface.OnClickListener onClickListener) {
        new CustomDialogFragment( title, message, confirmOption, cancelOption, onClickListener ).
                show(activity.getFragmentManager(), title);
    }
}
