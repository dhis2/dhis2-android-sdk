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

package org.hisp.dhis.android.sdk.controllers.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public class LoadEnrollmentsTask implements INetworkTask {
    public static final String TAG = LoadEnrollmentsTask.class.getSimpleName();
    private final ApiRequest.Builder<Object[]> requestBuilder;

    public LoadEnrollmentsTask(NetworkManager networkManager,
                               ApiRequestCallback<Object[]> callback,
                               String organisationUnitId, String programId) {

        requestBuilder = new ApiRequest.Builder<>();
        try {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            isNull(organisationUnitId, "OrganisationUnit Id must not be null");
            isNull(programId, "Program Id must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            String url = networkManager.getServerUrl() + "/api/enrollments?page=0&pageSize=200&ou=" +
                    organisationUnitId + "&program=" + programId;

            Request request = new Request(RestMethod.GET, url, headers, null);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
        } catch (IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, TAG, new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
        }


    }

    public LoadEnrollmentsTask(final NetworkManager networkManager,
                               final ApiRequestCallback<Object[]> callback,
                               TrackedEntityInstance trackedEntityInstance, boolean synchronizing) {
        Log.d(TAG, "loadEnrollmentsTask");

        requestBuilder = new ApiRequest.Builder<>();
        LoadEnrollmentsCallback loadEnrollmentsCallback = new LoadEnrollmentsCallback(callback, synchronizing, trackedEntityInstance);
        try {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            isNull(trackedEntityInstance, "TrackedEntityInstance must not be null");

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));


            String url = networkManager.getServerUrl() + "/api/enrollments?ouMode=ACCESSIBLE&trackedEntityInstance=" + trackedEntityInstance.getTrackedEntityInstance();

            Request request = new Request(RestMethod.GET, url, headers, null);
            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadEnrollmentsCallback);
        } catch (IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, TAG, new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadEnrollmentsCallback);
        }
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

class LoadEnrollmentsCallback implements ApiRequestCallback {
    final ApiRequestCallback parentCallback;
    final TrackedEntityInstance trackedEntityInstance;
    final List<Enrollment> enrollments;
    final List<Event> events;

    final boolean synchronizing;

    /**
     * Callback to be used with loading Enrollments and Events for a TrackedEntityInstance
     * If successful, returns an array of two Lists containing 0) Enrollments, and 1) Events
     *
     * @param parentCallback
     * @param synchronizing
     * @param trackedEntityInstance
     */
    public LoadEnrollmentsCallback(ApiRequestCallback parentCallback, boolean synchronizing, TrackedEntityInstance trackedEntityInstance) {
        this.parentCallback = parentCallback;
        this.synchronizing = synchronizing;
        this.trackedEntityInstance = trackedEntityInstance;
        this.enrollments = new LinkedList<>();
        this.events = new LinkedList<>();
    }

    @Override
    public void onSuccess(ResponseHolder holder) {
        try {
            JsonNode node = Dhis2.getInstance().getObjectMapper().
                    readTree(holder.getResponse().getBody());
            node = node.get("enrollments");
            if (node == null) { /* in case there are no enrollments */
                finish(holder);
            } else {
                TypeReference<List<Enrollment>> typeRef =
                        new TypeReference<List<Enrollment>>() {
                        };
                List<Enrollment> enrollments = Dhis2.getInstance().getObjectMapper().
                        readValue(node.traverse(), typeRef);
                if (enrollments != null || !enrollments.isEmpty()) {
                    this.enrollments.addAll(enrollments);
                    loadEvents(trackedEntityInstance, this.enrollments, events, parentCallback, synchronizing);
                } else {
                    finish(holder);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            parentCallback.onFailure(holder);
        }
    }

    public void finish(ResponseHolder holder) {
        holder.setItem(new Object[]{trackedEntityInstance, this.enrollments, this.events});
        parentCallback.onSuccess(holder);
    }

    @Override
    public void onFailure(ResponseHolder holder) {
        parentCallback.onFailure(holder);
    }

    /**
     * Initiates a sequence of loading all Events for a given list of Enrollments.
     * Those Enrollments that for some reason fail to load events due to any error is removed
     * from the finally returned list.
     *
     * @param enrollments
     * @param events
     * @param callback
     * @param synchronizing
     */
    public static void loadEvents(TrackedEntityInstance trackedEntityInstance, List<Enrollment> enrollments, List<Event> events, ApiRequestCallback callback, boolean synchronizing) {
        ListIterator<Enrollment> enrollmentIterator = enrollments.listIterator();
        LoadEventsCallback loadEventsCallback = new LoadEventsCallback(callback, trackedEntityInstance, enrollments);
        LoadEventsForNextEnrollmentCallback loadEventsForNextEnrollmentCallback = new LoadEventsForNextEnrollmentCallback(events, loadEventsCallback, enrollmentIterator, synchronizing);
        loadEventsForEnrollment(enrollmentIterator.next(), loadEventsForNextEnrollmentCallback, synchronizing);
    }

    /**
     * Can be used as a final callback of a sequence of loading all events for a given list of enrollments
     * Upon finishing, an array of two lists are returned as the item of a Response
     * 0) the given list of enrollments, 1) the events
     */
    private static class LoadEventsCallback implements ApiRequestCallback {
        private final ApiRequestCallback parentCallback;
        private final TrackedEntityInstance trackedEntityInstance;
        private final List<Enrollment> enrollments;

        public LoadEventsCallback(ApiRequestCallback parentCallback, TrackedEntityInstance trackedEntityInstance, List<Enrollment> enrollments) {
            this.parentCallback = parentCallback;
            this.enrollments = enrollments;
            this.trackedEntityInstance = trackedEntityInstance;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            holder.setItem(new Object[]{trackedEntityInstance, enrollments, holder.getItem()});
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            parentCallback.onFailure(holder);
        }
    }

    /**
     * Initiates loading events for a given enrollment. Upon finishing, the given callback is called
     *
     * @param enrollment
     * @param parentCallback
     * @param synchronizing
     */
    public static void loadEventsForEnrollment(final Enrollment enrollment, final ApiRequestCallback parentCallback, final boolean synchronizing) {
        LoadEventsForEnrollmentCallback loadEventsForEnrollmentCallback = new LoadEventsForEnrollmentCallback(parentCallback);
        LoadEventsTask task = new LoadEventsTask(NetworkManager.getInstance(), loadEventsForEnrollmentCallback, enrollment, synchronizing);
        task.execute();
    }

    /**
     * Can be used as a callback when loading Events for an Enrollment
     * returns a response with item as a List of Events
     */
    private static class LoadEventsForEnrollmentCallback implements ApiRequestCallback {
        final ApiRequestCallback parentCallback;

        public LoadEventsForEnrollmentCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            try {
                List<Event> events = new LinkedList<>();
                JsonNode node = Dhis2.getInstance().getObjectMapper().
                        readTree(holder.getResponse().getBody());
                node = node.get("events");
                if (node == null) {

                } else {
                    TypeReference<List<Event>> typeRef =
                            new TypeReference<List<Event>>() {
                            };
                    List<Event> eventsList = Dhis2.getInstance().getObjectMapper().
                            readValue(node.traverse(), typeRef);
                    events.addAll(eventsList);
                }
                holder.setItem(events);
                parentCallback.onSuccess(holder);
            } catch (IOException e) {
                e.printStackTrace();
                parentCallback.onFailure(holder);
            }
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            parentCallback.onFailure(holder);
        }
    }

    /**
     * Can be used to initiate loading the events for the next item in an iterator of an enrollment list
     * If loading of events fail, the enrollment is removed from the iterator to avoid incomplete data
     */
    private static class LoadEventsForNextEnrollmentCallback implements ApiRequestCallback {
        final ApiRequestCallback parentCallback;
        final List<Event> eventsList;
        final ListIterator<Enrollment> enrollmentIterator;
        final boolean synchronizing;

        public LoadEventsForNextEnrollmentCallback(List<Event> eventsList, ApiRequestCallback parentCallback, ListIterator<Enrollment> enrollmentIterator, boolean synchronizing) {
            this.eventsList = eventsList;
            this.parentCallback = parentCallback;
            this.enrollmentIterator = enrollmentIterator;
            this.synchronizing = synchronizing;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            List<Event> events = (List<Event>) holder.getItem();
            eventsList.addAll(events);
            if (enrollmentIterator.hasNext()) {
                loadNext();
            } else {
                finish(holder);
            }
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            enrollmentIterator.remove(); //an error occured when loading events for this enrollment, remove it to avoid incomplete data
            if (enrollmentIterator.hasNext()) {
                loadNext();
            } else {
                finish(holder);
            }
        }

        public void finish(ResponseHolder holder) {
            holder.setItem(eventsList);
            parentCallback.onSuccess(holder);
        }

        public void loadNext() {
            LoadEventsForNextEnrollmentCallback loadEventsForNextEnrollmentCallback = new LoadEventsForNextEnrollmentCallback(eventsList, parentCallback, enrollmentIterator, synchronizing);
            loadEventsForEnrollment(enrollmentIterator.next(), loadEventsForNextEnrollmentCallback, synchronizing);
        }
    }
}
