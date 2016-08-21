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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.Credentials;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.network.RepoManager;
import org.hisp.dhis.android.sdk.network.Session;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.LastUpdatedManager;
import org.hisp.dhis.android.sdk.utils.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.joda.time.DateTime;

public final class DhisController {
    private static final String CLASS_TAG = "Dhis2";

    public final static String LAST_UPDATED_METADATA = "lastupdated_metadata";
    public final static String LAST_UPDATED_DATAVALUES = "lastupdated_datavalues";

    /* flags used to determine what data to be loaded from the server */
    //public static final String LOAD_TRACKER = "load_tracker";
    //public static final String LOAD_EVENTCAPTURE = "load_eventcapture";
    public final static String QUEUED = "queued";
    public final static String PREFS_NAME = "DHIS2";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String SERVER = "server";
    private final static String CREDENTIALS = "credentials";

    private boolean shouldForceSyncMetadataOnSwipe;

    /**
     * Variable hasUnSynchronizedDatavalues
     * Is set to true when submitting changes in DataEntryFragment, TrackedEntityInstanceProfileFragment (in Tracker Capture) and when no network is available
     * Is set to false when DataValueSender.onFinishSending(true)
     */
    public static boolean hasUnSynchronizedDatavalues;

    private ObjectMapper objectMapper;
    private DhisApi dhisApi;
    private Session session;
    private SyncDateWrapper syncDateWrapper;
    private AppPreferencesImpl appPreferences;


    private boolean blocking = false;

    public static DhisController getInstance() {
        return Dhis2Application.dhisController;
    }

    public DhisController(Context context) {
        objectMapper = getObjectMapper();
        LastUpdatedManager.init(context);
        DateTimeManager.init(context);
        appPreferences = new AppPreferencesImpl(context);
        syncDateWrapper = new SyncDateWrapper(context, appPreferences);
        shouldForceSyncMetadataOnSwipe = false;
    }

    public void init() {
        readSession();
    }

    public DhisApi getDhisApi() {
        return dhisApi;
    }

    /**
     * Initiates synchronization with server. Updates MetaData, sends locally saved data, loads
     * new data values from server.
     *
     * @param context
     */
    static void synchronize(final Context context)
            throws APIException, IllegalStateException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));

        sendData();
        if(getInstance().shouldForceSyncMetadataOnSwipe) {
            loadData(context);
        }
        getInstance().getSyncDateWrapper().setLastSyncedNow();
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
    }

    static void synchronize(final Context context, boolean syncMetadata)
            throws APIException, IllegalStateException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));

        sendData();
        if(syncMetadata) {
            loadData(context);
        }
        getInstance().getSyncDateWrapper().setLastSyncedNow();
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_END));
    }

    public static void forceSynchronize(Context context) throws APIException, IllegalStateException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        sendData();
        DateTime serverDateTime = null;
        if(getInstance().getDhisApi() != null ) {
            serverDateTime = getInstance().getDhisApi().getSystemInfo().getServerDate();
        }

        LoadingController.loadMetaData(context, getInstance().getDhisApi(), true, serverDateTime);
        LoadingController.loadDataValues(context, getInstance().getDhisApi(), serverDateTime);
        getInstance().getSyncDateWrapper().setLastSyncedNow();
    }

    static void loadData(Context context) throws APIException, IllegalStateException {
        DateTime serverDateTime = null;
        if(getInstance().getDhisApi() != null ) {
            serverDateTime = getInstance().getDhisApi().getSystemInfo().getServerDate();
        }
        LoadingController.loadMetaData(context, getInstance().getDhisApi(), false, serverDateTime);
        LoadingController.loadDataValues(context, getInstance().getDhisApi(), serverDateTime);
        postMetadataSyncEventToFabricAnswers();
    }

    static void sendData() throws APIException, IllegalStateException {
        TrackerController.sendLocalData(getInstance().getDhisApi());
        postDataValueSyncEventToFabricAnswers();
    }

    static UserAccount logInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        return signInUser(serverUrl, credentials);
    }

    static UserAccount confirmUser(Credentials credentials) throws APIException {
        return signInUser(getInstance().session.getServerUrl(), credentials);
    }

    static UserAccount signInUser(HttpUrl serverUrl, Credentials credentials) throws APIException {
        DhisApi dhisApi = RepoManager
                .createService(serverUrl, credentials);
        UserAccount user = (new UserController(dhisApi)
                .logInUser(serverUrl, credentials));

        // fetch meta data from disk
        readSession();
        return user;
    }

    static void logOutUser(Context context) throws APIException {
        (new UserController(getInstance().dhisApi)).logOut();

        // fetch meta data from disk
        readSession();

        MetaDataController.clearMetaDataLoadedFlags();
        TrackerController.clearDataValueLoadedFlags();
        MetaDataController.wipe();
        LoadingController.clearLoadFlags(context);
    }

    public static boolean isUserLoggedIn() {
        return getInstance().getSession() != null &&
                getInstance().getSession().getServerUrl() != null &&
                getInstance().getSession().getCredentials() != null;
    }

    private static void readSession() {
        getInstance().session = LastUpdatedManager.getInstance().get();
        getInstance().dhisApi = null;

        if (isUserLoggedIn()) {
            getInstance().dhisApi = RepoManager.createService(
                    getInstance().getSession().getServerUrl(),
                    getInstance().getSession().getCredentials()
            );
        }
    }

    public static void invalidateSession() {
        LastUpdatedManager.getInstance().invalidate();

        // fetch meta data from disk
        readSession();
    }

    public Session getSession() {
        return session;
    }

    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
        }
        return objectMapper;
    }

    public boolean isShouldForceSyncMetadataOnSwipe() {
        return shouldForceSyncMetadataOnSwipe;
    }

    public void setShouldForceSyncMetadataOnSwipe(boolean shouldForceSyncMetadataOnSwipe) {
        this.shouldForceSyncMetadataOnSwipe = shouldForceSyncMetadataOnSwipe;
    }

    public SyncDateWrapper getSyncDateWrapper() {
        return getInstance().syncDateWrapper;
    }

    public static void postDataValueSyncEventToFabricAnswers() {
        if(getInstance().getSession() != null && getInstance().getSession().getServerUrl().url().toString().contains("ishp.dhis.dhmis.org")) {
            CustomEvent customEvent = new CustomEvent("Datavalue sync event");
            customEvent.putCustomAttribute("Data value sync",1);
            Answers.getInstance().logCustom(customEvent);
        }
    }

    public static void postMetadataSyncEventToFabricAnswers() {
        if(getInstance().getSession() != null && getInstance().getSession().getServerUrl().url().toString().contains("ishp.dhis.dhmis.org")) {
            CustomEvent customEvent = new CustomEvent("Metadata sync event");
            customEvent.putCustomAttribute("Metadata sync", 1);
            Answers.getInstance().logCustom(customEvent);
        }
    }
}
