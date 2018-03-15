/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.events.LoadingMessageEvent;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.UiUtils;

/**
 * @author Simen Skogly Russnes on 25.08.15.
 */
public final class LoadingController {

    public static final String CLASS_TAG = LoadingController.class.getSimpleName();
    public final static String LOAD = "load";

    private LoadingController() {
    }

    public static void enableLoading(Context context, ResourceType resourceType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DhisController.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOAD + resourceType, true);
        editor.commit();
    }

    /**
     * clears all loading flags
     *
     * @param context
     */
    public static void clearLoadFlags(Context context) {
        for (ResourceType resourceType : ResourceType.values()) {
            clearLoadFlag(context, resourceType);
        }
    }

    public static void clearLoadFlag(Context context, ResourceType resourceType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DhisController.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOAD + resourceType, false);
        editor.commit();
    }

    /**
     * returns whether or not a load flag has been enabled.
     *
     * @param context
     * @param flag
     * @return
     */
    public static boolean isLoadFlagEnabled(Context context, ResourceType flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DhisController.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOAD + flag, false);
    }

    public static boolean isInitialDataLoaded(Context context) {
        return (MetaDataController.isDataLoaded(context) && TrackerController.isDataLoaded(context));
    }

    /**
     * Loads initial data (Meta Data and Data Values). Which data is enabled is defined by the
     * enableLoading method
     */
    static void loadInitialData(Context context, DhisApi dhisApi)
            throws APIException, IllegalStateException {
        UiUtils.postProgressMessage(context.getString(org.hisp.dhis.android.sdk.R.string.finishing_up),
                LoadingMessageEvent.EventType.STARTUP);
        if (!MetaDataController.isDataLoaded(context)) {
            Log.d(CLASS_TAG, "loading initial metadata");
            loadMetaData(context, SyncStrategy.DOWNLOAD_ALL, dhisApi);
            Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.INITIAL_SYNCING_END));
            String message = "";
            message = context.getString(R.string.finishing_up);
            UiUtils.postProgressMessage(message, LoadingMessageEvent.EventType.STARTUP);
            loadDataValues(context, SyncStrategy.DOWNLOAD_ALL, dhisApi);
            Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.INITIAL_SYNCING_END));
        } else if (!TrackerController.isDataLoaded(context)) {
            Log.d(CLASS_TAG, "loading initial datavalues");
            String message = "";
            message = context.getString(R.string.finishing_up);
            UiUtils.postProgressMessage(message, LoadingMessageEvent.EventType.STARTUP);
            loadDataValues(context, SyncStrategy.DOWNLOAD_ALL, dhisApi);
        }
    }

    /**
     * Initiates loading of metadata from the server. To update existing data, rather use
     * synchronizeMetaData to save data.
     *
     * @param context
     */
    static void loadMetaData(Context context, SyncStrategy syncStrategy, DhisApi dhisApi) throws APIException {

        if (context == null) {
            Log.i(CLASS_TAG, "Unable to load metadata. We have no valid context.");
            return;
        }

        Log.d(CLASS_TAG, "loading metadata!");
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        try {
            MetaDataController.loadMetaData(context, dhisApi, syncStrategy);
        } catch (APIException e) {
            //to make sure we stop showing loading indicator
            Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
            throw e;
        }
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
    }

    static void loadDataValues(Context context, SyncStrategy syncStrategy, DhisApi dhisApi) throws APIException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        try {
            TrackerController.loadDataValues(context, dhisApi, syncStrategy);
        } catch (APIException e) {
            //to make sure we stop showing loading indicator
            Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
            throw e;
        }
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
    }


    static void syncRemotelyDeletedData(Context context, DhisApi dhisApi) throws APIException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        try {
            TrackerController.syncRemotelyDeletedData(context, dhisApi);
        } catch (APIException e) {
            //to make sure we stop showing loading indicator
            Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
            throw e;
        }
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
    }
}
