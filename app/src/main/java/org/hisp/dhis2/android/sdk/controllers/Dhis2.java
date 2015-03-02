/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis2.android.sdk.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.tasks.AuthUserTask;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.Base64Manager;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.User;
import org.hisp.dhis2.android.sdk.services.PeriodicSynchronizer;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.hisp.dhis2.android.sdk.utils.CustomDialogFragment;

import java.io.IOException;

public final class Dhis2 {

    public static final String LOAD_TRACKER = "load_tracker";

    public static final String UPDATE_FREQUENCY = "update_frequency";
    public final static String QUEUED = "queued";
    public final static String PREFS_NAME = "DHIS2";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String SERVER = "server";
    private final static String CREDENTIALS = "credentials";
    private static Dhis2 dhis2;
    private MetaDataController metaDataController;
    private DataValueController dataValueController;
    private ObjectMapper objectMapper;
    public boolean toggle = false;  //used during development stage to avoid triggering sendData and syncMeta at the same time (in periodicSyncronizer)

    private Dhis2() {
        objectMapper = new ObjectMapper();
    }

    public static Dhis2 getInstance() {
        if (dhis2 == null) {
            dhis2 = new Dhis2();
        }

        return dhis2;
    }

    /**
     * Logs out the current user
     */
    public static void logout(Context context) {
        Dhis2.getInstance().setServer(null);
        Dhis2.getInstance().saveCredentials(context, null, null, null);
        NetworkManager.getInstance().setCredentials(null);
    }

    /**
     * Returns the currently set Update Frequency
     * @param context
     * @return
     */
    public static int getUpdateFrequency(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int updateFrequency = sharedPreferences.getInt(UPDATE_FREQUENCY, 0);
        return updateFrequency;
    }

    /**
     * Sets the update frequency by an integer referencing the indexes in the update_frequencies string-array
     * @param context
     * @param frequency index of update frequencies. See update_frequencies string-array
     */
    public static void setUpdateFrequency(Context context, int frequency) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UPDATE_FREQUENCY, frequency);
        editor.commit();
        PeriodicSynchronizer.reActivate(context);

    }

    /**
     * Enables loading of different data
     * @param flag
     */
    public void enableLoading(Context context, String flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(flag, true);
    }

    public boolean isLoadTrackerDataEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOAD_TRACKER, false);
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
        editor.putString(CREDENTIALS, NetworkManager.getInstance().getBase64Manager()
                .toBase64(username, password));
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

    public String getCredentials(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String credentials = prefs.getString(CREDENTIALS, null);
        return credentials;
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
        new AuthUserTask(NetworkManager.getInstance(), new ApiRequestCallback<User>() {
            @Override
            public void onSuccess(Response response) {
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
        }, username, password).execute();

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
