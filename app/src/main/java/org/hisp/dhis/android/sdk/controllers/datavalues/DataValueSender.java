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

package org.hisp.dhis.android.sdk.controllers.datavalues;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterEnrollmentTask;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterTrackedEntityInstanceTask;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.network.http.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
public final class DataValueSender {

    public static DataValueSender getInstance() {
        return dataValueSender;
    }

    private static final DataValueSender dataValueSender;

    static {
        dataValueSender = new DataValueSender();
    }

    private DataValueSender() {}

    private static final String CLASS_TAG = "DataValueSender";

    boolean sending = false;

    static void sendLocalData(final ApiRequestCallback callback) {
        if(Dhis2.isLoading()) {
            callback.onSuccess(null);
            return;
        }
        if(!NetworkManager.isOnline()) {
            ResponseHolder holder = new ResponseHolder<>();
//            holder.setApiException(APIException.networkError
//                    (NetworkManager.getInstance().getServerUrl(), null));
            Log.d(CLASS_TAG, "NO internet!");
            callback.onFailure(null);
            return;
        }
        getInstance().sending = true;
        new Thread() {
            public void run() {
                final OnFinishSendingCallback finalSendCallback = new OnFinishSendingCallback(callback);
                final ApiRequestCallback initiateSendEventsCallback = new ApiRequestCallback() {
                    @Override
                    public void onSuccess(ResponseHolder responseHolder) {
                        //temporary fix for waiting for TransactionManager to finish and update references
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendEvents(finalSendCallback);
                    }

                    @Override
                    public void onFailure(ResponseHolder responseHolder) {
                        finalSendCallback.onFailure(responseHolder);
                    }
                };
                ApiRequestCallback initiateSendEnrollmentsCallback = new ApiRequestCallback() {
                    @Override
                    public void onSuccess(ResponseHolder responseHolder) {
                        //temporary fix for waiting for TransactionManager to finish and update references
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendEnrollments(initiateSendEventsCallback);
                    }

                    @Override
                    public void onFailure(ResponseHolder responseHolder) {
                        initiateSendEventsCallback.onFailure(responseHolder);
                    }
                };
                sendTrackedEntityInstances(initiateSendEnrollmentsCallback);
            }
        }.start();
    }

    public static boolean isSending() {
        return getInstance().sending;
    }

    public static void clearFailedItem(String type, long id) {
        FailedItem item = DataValueController.getFailedItem(type, id);
        if (item != null) {
            item.async().delete();
        }
    }

    private static class OnFinishSendingCallback implements ApiRequestCallback {

        private final ApiRequestCallback callback;

        private OnFinishSendingCallback(ApiRequestCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            Log.d(CLASS_TAG, "onFinishSending" + true);
            Dhis2.hasUnSynchronizedDatavalues = false;
            getInstance().sending = false;
            callback.onSuccess(null);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            Log.d(CLASS_TAG, "onFinishSending" + false);
            Dhis2.hasUnSynchronizedDatavalues = false;
            getInstance().sending = false;
            callback.onFailure(null);
        }
    }

    /**
     * Initiates a sequence that attempts sending all events in the local database that have not been synchronized to the server
     *
     * @param callback called when the sequence is done
     */
    private static void sendEvents(ApiRequestCallback callback) {
        List<Event> events = new Select().from(Event.class).where(Condition.column(Event$Table.FROMSERVER).is(false)).queryList();
        sendEvents(callback, events);
    }

    /**
     * Initiates a sequence that attemps sending the given list of events to the server
     *
     * @param parentCallback called when the sequence is done
     * @param events
     */
    public static void initSendEvents(ApiRequestCallback parentCallback, List<Event> events) {

        if (Dhis2.isLoading()) {
            parentCallback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback = new DoneSendingCallback(parentCallback);
        sendEvents(callback, events);
    }

    private static void sendEvents(ApiRequestCallback callback, List<Event> events) {
        ListIterator<Event> eventListIterator = null;
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {/* removing events with local enrollment reference. In this case, the enrollment needs to be synced first*/
                Event event = events.get(i);
                if (Utils.isLocal(event.getEnrollment()) && event.getEnrollment() != null/*if enrollments==null, then it is probably a single event without reg*/) {
                    events.remove(i);
                    i--;
                    continue;
                }
            }
            Log.d(CLASS_TAG, "got this many events to send:" + events.size());
            eventListIterator = events.listIterator();
        }
        SendNextEventCallback sendNextEventCallback = new SendNextEventCallback(callback, eventListIterator);
        if (eventListIterator != null && eventListIterator.hasNext()) {
            sendEvent(sendNextEventCallback, eventListIterator.next());
        } else {
            callback.onSuccess(null);
        }
    }

    static class SendNextEventCallback implements ApiRequestCallback {

        private final ApiRequestCallback parentCallback;
        private final ListIterator<Event> eventListIterator;

        public SendNextEventCallback(ApiRequestCallback parentCallback, ListIterator<Event> eventListIterator) {
            this.eventListIterator = eventListIterator;
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            if (eventListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            if (eventListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        public void sendNext() {
            SendNextEventCallback sendNextEventCallback = new SendNextEventCallback(parentCallback, eventListIterator);
            sendEvent(sendNextEventCallback, eventListIterator.next());
        }

        public void finish(ResponseHolder holder) {
            parentCallback.onSuccess(holder);
        }
    }


    /**
     * Attempts to register the given Event on the server
     *
     * @param callback
     * @param event
     */
    public static void initSendEvent(ApiRequestCallback callback, Event event) {
        if (Dhis2.isLoading()) {
            callback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback1 = new DoneSendingCallback(callback);
        sendEvent(callback1, event);
    }

    private static void sendEvent(ApiRequestCallback callback, Event event) {
        Log.d(CLASS_TAG, "sending event: " + event.getEvent());

        RegisterEventTask task = new RegisterEventTask(NetworkManager.getInstance(), callback
                , event, event.getDataValues());
        task.execute();
    }

    /**
     * Initiates a sequence that attemps sending all enrollments in the local database that has not yet been synchronized with server
     *
     * @param callback called when the sequence is done
     */
    private static void sendEnrollments(ApiRequestCallback callback) {
        List<Enrollment> enrollments = new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.FROMSERVER).is(false)).queryList();
        sendEnrollments(callback, enrollments);
    }

    /**
     * Initiates a sequence for sending the given list of enrollments
     *
     * @param parentCallback called when the sequence is done
     * @param enrollments
     */
    public static void initSendEnrollments(ApiRequestCallback parentCallback, List<Enrollment> enrollments) {
        if (Dhis2.isLoading()) {
            parentCallback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback = new DoneSendingCallback(parentCallback);
        sendEnrollments(callback, enrollments);
    }

    private static void sendEnrollments(ApiRequestCallback callback, List<Enrollment> enrollments) {
        ListIterator<Enrollment> enrollmentListIterator = null;
        if (enrollments != null) {
            for (int i = 0; i < enrollments.size(); i++) {/* workaround for not attempting to upload enrollments with local tei reference*/
                Enrollment enrollment = enrollments.get(i);
                if (Utils.isLocal(enrollment.getTrackedEntityInstance())) {
                    enrollments.remove(i);
                    i--;
                }
            }
            Log.d(CLASS_TAG, "got this many enrollments to send:" + enrollments.size());
            enrollmentListIterator = enrollments.listIterator();
        }
        SendNextEnrollmentCallback sendNextEnrollmentCallback = new SendNextEnrollmentCallback(callback, enrollmentListIterator);
        if (enrollmentListIterator != null && enrollmentListIterator.hasNext()) {
            sendEnrollment(sendNextEnrollmentCallback, enrollmentListIterator.next());
        } else {
            callback.onSuccess(null);
        }
    }

    static class SendNextEnrollmentCallback implements ApiRequestCallback {

        private final ApiRequestCallback parentCallback;
        private final ListIterator<Enrollment> enrollmentListIterator;

        public SendNextEnrollmentCallback(ApiRequestCallback parentCallback, ListIterator<Enrollment> enrollmentListIterator) {
            this.enrollmentListIterator = enrollmentListIterator;
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            if (enrollmentListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            if (enrollmentListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        public void sendNext() {
            SendNextEnrollmentCallback sendNextEnrollmentCallback = new SendNextEnrollmentCallback(parentCallback, enrollmentListIterator);
            sendEnrollment(sendNextEnrollmentCallback, enrollmentListIterator.next());
        }

        public void finish(ResponseHolder holder) {
            parentCallback.onSuccess(holder);
        }
    }

    /**
     * Attempts registering the given enrollment on the server
     *
     * @param parentCallback
     * @param enrollment
     */
    public static void initSendEnrollment(ApiRequestCallback parentCallback, Enrollment enrollment) {
        if (Dhis2.isLoading()) {
            parentCallback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback = new DoneSendingCallback(parentCallback);
        sendEnrollment(callback, enrollment);
    }

    private static void sendEnrollment(ApiRequestCallback parentCallback, Enrollment enrollment) {
        Log.d(CLASS_TAG, "sending enrollment: " + enrollment.getEnrollment());
        RegisterEnrollmentTask task = new RegisterEnrollmentTask(NetworkManager.getInstance(), parentCallback
                , enrollment);
        task.execute();
    }

    /**
     * Initiates a sequence that attempts sending and registering the given list of Tracked Entity Instances to the server
     *
     * @param parentCallback         called when the sequence is done
     * @param trackedEntityInstances
     */
    public static void initSendTrackedEntityInstances(ApiRequestCallback parentCallback, List<TrackedEntityInstance> trackedEntityInstances) {
        if (Dhis2.isLoading()) {
            parentCallback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback = new DoneSendingCallback(parentCallback);
        sendTrackedEntityInstances(callback, trackedEntityInstances);
    }

    /**
     * Initiates sending and registering of all locally created TrackedEntityInstance to the server.
     *
     * @param callback called when the sequence is done
     */
    private static void sendTrackedEntityInstances(ApiRequestCallback callback) {
        List<TrackedEntityInstance> trackedEntityInstances = new Select().from(TrackedEntityInstance.class).where(Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(false)).queryList();
        sendTrackedEntityInstances(callback, trackedEntityInstances);
    }

    private static void sendTrackedEntityInstances(ApiRequestCallback callback, List<TrackedEntityInstance> trackedEntityInstances) {
        Log.d(CLASS_TAG, "got this many trackedEntityInstances to send:" + trackedEntityInstances.size());
        ListIterator<TrackedEntityInstance> trackedEntityInstanceListIterator = null;
        if (trackedEntityInstances != null) {
            trackedEntityInstanceListIterator = trackedEntityInstances.listIterator();
        }
        SendNextTrackedEntityInstanceCallback sendNextTrackedEntityInstanceCallback = new SendNextTrackedEntityInstanceCallback(callback, trackedEntityInstanceListIterator);

        if (trackedEntityInstanceListIterator != null && trackedEntityInstanceListIterator.hasNext()) {
            sendTrackedEntityInstance(sendNextTrackedEntityInstanceCallback, trackedEntityInstanceListIterator.next());
        } else {
            callback.onSuccess(null);
        }
    }

    static class SendNextTrackedEntityInstanceCallback implements ApiRequestCallback {

        private final ApiRequestCallback parentCallback;
        private final ListIterator<TrackedEntityInstance> trackedEntityInstanceListIterator;

        public SendNextTrackedEntityInstanceCallback(ApiRequestCallback parentCallback, ListIterator<TrackedEntityInstance> trackedEntityInstanceListIterator) {
            this.trackedEntityInstanceListIterator = trackedEntityInstanceListIterator;
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            if (trackedEntityInstanceListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            if (trackedEntityInstanceListIterator.hasNext()) {
                sendNext();
            } else {
                finish(responseHolder);
            }
        }

        public void sendNext() {
            SendNextTrackedEntityInstanceCallback sendNextTrackedEntityInstanceCallback = new SendNextTrackedEntityInstanceCallback(parentCallback, trackedEntityInstanceListIterator);
            sendTrackedEntityInstance(sendNextTrackedEntityInstanceCallback, trackedEntityInstanceListIterator.next());
        }

        public void finish(ResponseHolder holder) {
            parentCallback.onSuccess(holder);
        }
    }

    public static void initSendTrackedEntityInstance(ApiRequestCallback parentCallback, TrackedEntityInstance trackedEntityInstance) {
        if (Dhis2.isLoading()) {
            parentCallback.onSuccess(null);
            return;
        }
        getInstance().sending = true;
        DoneSendingCallback callback = new DoneSendingCallback(parentCallback);
        sendTrackedEntityInstance(callback, trackedEntityInstance);
    }

    /**
     * Callback class to be used to set the sending flag to false after a sending task has finished
     */
    private static class DoneSendingCallback implements ApiRequestCallback {
        final ApiRequestCallback parentCallback;

        private DoneSendingCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            getInstance().sending = false;
            parentCallback.onSuccess(responseHolder);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            getInstance().sending = false;
            parentCallback.onSuccess(responseHolder);
        }
    }

    private static void sendTrackedEntityInstance(ApiRequestCallback parentCallback, TrackedEntityInstance trackedEntityInstance) {
        Log.d(CLASS_TAG, "sending tei: " + trackedEntityInstance.getTrackedEntityInstance());

        RegisterTrackedEntityInstanceTask task = new RegisterTrackedEntityInstanceTask(NetworkManager.getInstance(),
                parentCallback, trackedEntityInstance);
        task.execute();
    }

    public static void handleError(APIException apiException, String type, long id) {
        if (apiException.getResponse() != null && apiException.getResponse().getBody() != null) {
            Log.e(CLASS_TAG, new String(apiException.getResponse().getBody()));
        }
        if (apiException.isNetworkError()) {
            Dhis2.hasUnSynchronizedDatavalues = true; //todo what is the logic here??
            return; //if item failed due to network error then there is no need to store error info
        }
        FailedItem failedItem = new FailedItem();
        if (apiException.getResponse() != null) {
            failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
            failedItem.setErrorMessage(new String(apiException.getResponse().getBody()));
        }
        failedItem.setItemId(id);
        failedItem.setItemType(type);
        failedItem.save();
        if( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                conflict.setImportSummary( failedItem.getImportSummary().getId() );
                conflict.save();
            }
        }
    }

    public static void handleError(ImportSummary importSummary, String type, int code, long id) {
        FailedItem failedItem = new FailedItem();
        failedItem.setImportSummary(importSummary);
        failedItem.setItemId(id);
        failedItem.setItemType(type);
        failedItem.setHttpStatusCode(code);
        failedItem.save();
        if( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                conflict.setImportSummary( failedItem.getImportSummary().getId() );
                conflict.save();
            }
        }
        Log.d(CLASS_TAG, "saved item: " + failedItem.getItemId()+ ":" + failedItem.getItemType());
    }
}
