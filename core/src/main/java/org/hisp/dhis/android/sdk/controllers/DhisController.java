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
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.LastUpdatedManager;
import org.hisp.dhis.android.sdk.synchronization.data.enrollment.EnrollmentLocalDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.enrollment.EnrollmentRemoteDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.enrollment.EnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventLocalDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventRemoteDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.event.EventRepository;
import org.hisp.dhis.android.sdk.synchronization.data.faileditem.FailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance
        .TrackedEntityInstanceLocalDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance.TrackedEntityInstanceRemoteDataSource;
import org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance
        .TrackedEntityInstanceRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.app.SyncAppUseCase;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .ITrackedEntityInstanceRepository;
import org.hisp.dhis.android.sdk.utils.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;

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
    public static final Float START_LATEST_API_VERSION =2.29f;

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
    }

    public void init() {
        readSession();
    }

    public DhisApi getDhisApi() {
        return dhisApi;
    }

    static void syncRemotelyDeletedData(Context context) throws APIException, IllegalStateException {
        LoadingController.syncRemotelyDeletedData(context, getInstance().getDhisApi());
    }

    /**
     * Initiates synchronization with server. Updates MetaData, sends locally saved data, loads
     * new data values from server.
     *
     * @param context
     */
    static void synchronize(final Context context, SyncStrategy syncStrategy)
            throws APIException, IllegalStateException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        sendData();
        loadData(context, syncStrategy);
        getInstance().getSyncDateWrapper().setLastSyncedNow();
    }

    public static void forceSynchronize(Context context) throws APIException, IllegalStateException {
        Dhis2Application.getEventBus().post(new UiEvent(UiEvent.UiEventType.SYNCING_START));
        sendData();
        LoadingController.loadMetaData(context, SyncStrategy.DOWNLOAD_ALL, getInstance().getDhisApi());
        LoadingController.loadDataValues(context, SyncStrategy.DOWNLOAD_ALL, getInstance().getDhisApi());
        getInstance().getSyncDateWrapper().setLastSyncedNow();
    }

    static void loadData(Context context, SyncStrategy syncStrategy)  throws APIException, IllegalStateException {
        LoadingController.loadMetaData(context, syncStrategy, getInstance().getDhisApi());
        LoadingController.loadDataValues(context, syncStrategy, getInstance().getDhisApi());
    }

    static void sendData() throws APIException, IllegalStateException {
        EnrollmentLocalDataSource enrollmentLocalDataSource = new EnrollmentLocalDataSource();
        EnrollmentRemoteDataSource enrollmentRemoteDataSource = new EnrollmentRemoteDataSource(DhisController.getInstance().getDhisApi());
        IEnrollmentRepository enrollmentRepository = new EnrollmentRepository(enrollmentLocalDataSource, enrollmentRemoteDataSource);

        EventLocalDataSource mLocalDataSource = new EventLocalDataSource();
        EventRemoteDataSource mRemoteDataSource = new EventRemoteDataSource(DhisController.getInstance().getDhisApi());
        EventRepository eventRepository = new EventRepository(mLocalDataSource, mRemoteDataSource);
        FailedItemRepository failedItemRepository = new FailedItemRepository();

        TrackedEntityInstanceLocalDataSource trackedEntityInstanceLocalDataSource = new TrackedEntityInstanceLocalDataSource();
        TrackedEntityInstanceRemoteDataSource trackedEntityInstanceRemoteDataSource = new TrackedEntityInstanceRemoteDataSource(DhisController.getInstance().getDhisApi());
        ITrackedEntityInstanceRepository trackedEntityInstanceRepository = new TrackedEntityInstanceRepository(trackedEntityInstanceLocalDataSource, trackedEntityInstanceRemoteDataSource);
        SyncAppUseCase syncAppUseCase = new SyncAppUseCase(trackedEntityInstanceRepository, enrollmentRepository, eventRepository, failedItemRepository);
        syncAppUseCase.execute();
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
        SystemInfo systemInfo = dhisApi.getSystemInfo();
        systemInfo.save();
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

    public SyncDateWrapper getSyncDateWrapper() {
        return getInstance().syncDateWrapper;
    }

    public boolean isLoggedInServerWithLatestApiVersion() {
        SystemInfo systemInfo = MetaDataController.getSystemInfo();
        Float serverVersion = systemInfo.getVersionNumber();
        return serverVersion >= START_LATEST_API_VERSION;
    }
}
