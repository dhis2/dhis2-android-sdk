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

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.datavalues.TrackedEntityInstancesResultHolder;
import org.hisp.dhis.android.sdk.controllers.wrappers.TrackedEntityInstancesWrapper;
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

/**
 * Task for loading Tracked Entity Instances in batch from the trackedEntityInstances/ endpoint
 * in the api. Due to the structure of the output from the endpoint a special wrapper class
 * is needed to handle the response of this task.
 */
public class LoadTrackedEntityInstancesTask implements INetworkTask {
    //private final ApiRequestCallback<TrackedEntityInstancesResultHolder> parentCallback;
    private final ApiRequest.Builder<TrackedEntityInstancesResultHolder> requestBuilder;
    public final static String CLASS_TAG = LoadTrackedEntityInstancesTask.class.getSimpleName();

    /**
     * Task for initiating loading of all TrackedEntityInstances with full attributes, relationships, enrollments, and events
     * for a given OrganisationUnit. If desired, the number can be limited
     *
     * @param networkManager
     * @param callback
     * @param organisationUnit
     * @param limit            the max number of TEIs to load. Pass 0 to load all
     */
    public LoadTrackedEntityInstancesTask(NetworkManager networkManager,
                                          ApiRequestCallback<TrackedEntityInstancesResultHolder> callback,
                                          String organisationUnit, int limit) {
        requestBuilder = new ApiRequest.Builder<>();
        List<TrackedEntityInstance> trackedEntityInstances = new LinkedList<>();
        List<Enrollment> enrollments = new LinkedList<>();
        List<Event> events = new LinkedList<>();
        FullLoadCallback fullLoadCallback = new FullLoadCallback(callback, trackedEntityInstances, enrollments, events);
        LoadTrackedEntityInstancesCallback loadTrackedEntityInstancesCallback = new LoadTrackedEntityInstancesCallback(fullLoadCallback, trackedEntityInstances, enrollments, events, false);
        try {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            isNull(organisationUnit, "OrganisationUnit must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            String url = networkManager.getServerUrl() + "/api/trackedEntityInstances?ou="
                    + organisationUnit;
            if (limit <= 0) {
                url += "&paging=false";
            } else {
                url += "?page=0&pageSize=" + limit;
            }

            Request request = new Request(RestMethod.GET, url, headers, null);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadTrackedEntityInstancesCallback);
        } catch (IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, CLASS_TAG, new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadTrackedEntityInstancesCallback);
        }
    }

    /**
     * Task for initiating loading of a given list of TrackedEntityInstances, with full attributes,
     * relationships, enrollments, and events
     *
     * @param networkManager
     * @param callback
     * @param trackedEntityInstances
     */
    public LoadTrackedEntityInstancesTask(NetworkManager networkManager,
                                          ApiRequestCallback<TrackedEntityInstancesResultHolder> callback,
                                          List<TrackedEntityInstance> trackedEntityInstances) {
        requestBuilder = new ApiRequest.Builder<>();
        List<Enrollment> enrollments = new LinkedList<>();
        List<Event> events = new LinkedList<>();
        FullLoadCallback fullLoadCallback = new FullLoadCallback(callback, trackedEntityInstances, enrollments, events);
        ListIterator<TrackedEntityInstance> trackedEntityInstanceIterator = trackedEntityInstances.listIterator();
        LoadNextTrackedEntityInstanceCallback loadNextTrackedEntityInstanceCallback = new
                LoadNextTrackedEntityInstanceCallback(trackedEntityInstances,
                trackedEntityInstanceIterator, fullLoadCallback, enrollments, events, false);
        LoadTrackedEntityInstanceCallback loadTrackedEntityInstanceCallback = new LoadTrackedEntityInstanceCallback(loadNextTrackedEntityInstanceCallback, false);
        try {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            isNull(trackedEntityInstances, "TrackedEntityInstances must not be null");
            isNull(trackedEntityInstances.get(0), "there must be at least on TrackedEntityInstance");

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));
            int current = 1;
            int length = trackedEntityInstances.size();
            String loading = "Loading Tracked Entity Instance";
            if (Dhis2.getInstance().getActivity() != null) {
                loading = Dhis2.getInstance().getActivity().getString(R.string.loading_tracked_entity_instances);
            }
            Dhis2.postProgressMessage(loading + " " + current + "/" + length);
            String url = networkManager.getServerUrl() + "/api/trackedEntityInstances/" + trackedEntityInstanceIterator.next().getTrackedEntityInstance();

            Request request = new Request(RestMethod.GET, url, headers, null);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadTrackedEntityInstanceCallback);
        } catch (IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, e.toString(), new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadTrackedEntityInstanceCallback);
        } catch (IndexOutOfBoundsException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, e.toString(), new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(loadTrackedEntityInstanceCallback);
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

    /**
     * To be used with loading a list of TrackedEntityInstances from the trackedEntityInstances endpoint
     * The callback initiates loading each TrackedEntityInstance with attributes, relationships, and enrollments/events
     */
    public class LoadTrackedEntityInstancesCallback implements ApiRequestCallback {
        final List<TrackedEntityInstance> trackedEntityInstances;
        final List<Enrollment> enrollments;
        final List<Event> events;
        final ApiRequestCallback parentCallback;
        final boolean synchronizing;

        public LoadTrackedEntityInstancesCallback(ApiRequestCallback parentCallback, List<TrackedEntityInstance> trackedEntityInstances, List<Enrollment> enrollments, List<Event> events, boolean synchronizing) {
            this.parentCallback = parentCallback;
            this.trackedEntityInstances = trackedEntityInstances;
            this.enrollments = enrollments;
            this.events = events;
            this.synchronizing = synchronizing;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            try {
                List<TrackedEntityInstance> trackedEntityInstances = TrackedEntityInstancesWrapper.parseTrackedEntityInstances(holder.getResponse().getBody());
                if (trackedEntityInstances == null || trackedEntityInstances.isEmpty()) {
                    holder.setItem(new Object[]{this.trackedEntityInstances, enrollments, events});
                    parentCallback.onSuccess(holder);
                } else {
                    this.trackedEntityInstances.addAll(trackedEntityInstances);
                    loadTrackedEntityInstances(this.trackedEntityInstances, holder, parentCallback, enrollments, events, synchronizing);
                }
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
     * Initiates the loading sequences of a list of given TrackedEntityInstances
     * The loading sequence loads attributes and relationships, enrollments, and events for each passed TrackedEntityInstance
     *
     * @param trackedEntityInstances
     * @param holder
     * @param callback
     * @param enrollments
     * @param events
     * @param synchronizing
     */
    public static void loadTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances, ResponseHolder holder, ApiRequestCallback callback, List<Enrollment> enrollments, List<Event> events, boolean synchronizing) {
        if (trackedEntityInstances == null || trackedEntityInstances.isEmpty()) {
            callback.onSuccess(holder);
            return;
        }
        ListIterator<TrackedEntityInstance> trackedEntityInstanceIterator = trackedEntityInstances.listIterator();
        LoadNextTrackedEntityInstanceCallback loadNextTrackedEntityInstanceCallback = new LoadNextTrackedEntityInstanceCallback(trackedEntityInstances, trackedEntityInstanceIterator, callback, enrollments, events, synchronizing);
        loadTrackedEntityInstance(loadNextTrackedEntityInstanceCallback, trackedEntityInstanceIterator.next(), synchronizing);
    }

    /**
     * Initiates the loading sequence of attributes and relationships, enrollments, and events for a given TrackedEntityInstance.
     * After the sequence is finished, the given onSuccess of the given callback is called.
     *
     * @param callback
     * @param trackedEntityInstance
     * @param synchronizing
     */
    public static void loadTrackedEntityInstance(ApiRequestCallback callback, TrackedEntityInstance trackedEntityInstance, boolean synchronizing) {
        LoadTrackedEntityInstanceTask task = new LoadTrackedEntityInstanceTask(NetworkManager.getInstance(), callback, trackedEntityInstance.getTrackedEntityInstance(), synchronizing);
        task.execute();
    }

    /**
     * Can be used to initiate a sequence for loading attributes and relationships, enrollments,
     * and events, using an iterator of a list of TrackedEntityInstances
     */
    public static class LoadNextTrackedEntityInstanceCallback implements ApiRequestCallback {
        public static final String TAG = LoadNextTrackedEntityInstanceCallback.class.getSimpleName();
        final List<TrackedEntityInstance> trackedEntityInstances;
        final ListIterator<TrackedEntityInstance> trackedEntityInstanceIterator;
        final ApiRequestCallback parentCallback;
        final List<Enrollment> enrollments;
        final List<Event> events;
        final boolean synchronizing;

        public LoadNextTrackedEntityInstanceCallback(List<TrackedEntityInstance> trackedEntityInstances, ListIterator<TrackedEntityInstance> trackedEntityInstanceIterator, ApiRequestCallback parentCallback, List<Enrollment> enrollments, List<Event> events, boolean synchronizing) {
            this.trackedEntityInstances = trackedEntityInstances;
            this.trackedEntityInstanceIterator = trackedEntityInstanceIterator;
            this.parentCallback = parentCallback;
            this.enrollments = enrollments;
            this.events = events;
            this.synchronizing = synchronizing;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            Object[] items = (Object[]) holder.getItem();
            TrackedEntityInstance trackedEntityInstance = (TrackedEntityInstance) items[0];
            trackedEntityInstanceIterator.set(trackedEntityInstance);
            List<Enrollment> enrollments = (List<Enrollment>) items[1];
            List<Event> events = (List<Event>) items[2];
            this.enrollments.addAll(enrollments);
            this.events.addAll(events);
            if (trackedEntityInstanceIterator.hasNext()) {
                loadNext();
            } else {
                finish(holder);
            }
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            Log.d(TAG, "onFailure");
            if (holder.getApiException().getResponse() == null) {

            } else if (holder.getApiException().getResponse().getBody() != null) {
                Log.e(TAG, new String(holder.getApiException().getResponse().getBody()));
            } else {
                Log.e(TAG, holder.getApiException().getResponse().getReason());
            }

            try {
                trackedEntityInstanceIterator.remove();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (trackedEntityInstanceIterator.hasNext()) {
                loadNext();
            } else {
                finish(holder);
            }
        }

        public void finish(ResponseHolder holder) {
            holder.setItem(new Object[]{enrollments, events});
            parentCallback.onSuccess(holder);
        }

        public void loadNext() {
            TrackedEntityInstance tei = trackedEntityInstanceIterator.next();
            int length = trackedEntityInstances.size();
            int current = trackedEntityInstances.indexOf(tei) + 1;

            String loading = "Loading Tracked Entity Instance";
            if (Dhis2.getInstance().getActivity() != null) {
                loading = Dhis2.getInstance().getActivity().getString(R.string.loading_tracked_entity_instances);
            }
            Dhis2.postProgressMessage(loading + " " + current + "/" + length);
            LoadNextTrackedEntityInstanceCallback loadNextTrackedEntityInstanceCallback = new LoadNextTrackedEntityInstanceCallback(trackedEntityInstances, trackedEntityInstanceIterator, parentCallback, enrollments, events, synchronizing);
            loadTrackedEntityInstance(loadNextTrackedEntityInstanceCallback, tei, synchronizing);
        }
    }

    /**
     * To be used as a final callback when loading TrackedEntityInstances with attributes, enrollments, and events.
     * The callback returns a response containing a pointer to an array containing lists of 0) TrackedEntityInstances, 1) Enrollments, and 2) Events
     */
    private class FullLoadCallback implements ApiRequestCallback<TrackedEntityInstancesResultHolder> {
        final ApiRequestCallback<TrackedEntityInstancesResultHolder> parentCallback;
        final List<TrackedEntityInstance> trackedEntityInstances;
        final List<Enrollment> enrollments;
        final List<Event> events;

        public FullLoadCallback(ApiRequestCallback<TrackedEntityInstancesResultHolder> parentCallback,
                                List<TrackedEntityInstance> trackedEntityInstances,
                                List<Enrollment> enrollments, List<Event> events) {
            this.parentCallback = parentCallback;
            this.trackedEntityInstances = trackedEntityInstances;
            this.enrollments = enrollments;
            this.events = events;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            TrackedEntityInstancesResultHolder result = new TrackedEntityInstancesResultHolder(trackedEntityInstances, enrollments, events);
            holder.setItem(result);
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            TrackedEntityInstancesResultHolder result = new TrackedEntityInstancesResultHolder(trackedEntityInstances, enrollments, events);
            holder.setItem(result);
            parentCallback.onFailure(holder);
        }
    }
}
