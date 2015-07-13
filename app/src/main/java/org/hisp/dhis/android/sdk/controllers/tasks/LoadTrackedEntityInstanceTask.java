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

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * Task for loading a given Tracked Entity Instance by UID
 * Enrollments and Events can be triggered to be loaded as well for specified programs
 */
public class LoadTrackedEntityInstanceTask implements INetworkTask {
    private final ApiRequest.Builder<Object[]> requestBuilder;

    public LoadTrackedEntityInstanceTask(final NetworkManager networkManager,
                                         final ApiRequestCallback<Object[]> parentCallback, final String UID,
                                         final boolean synchronizing) {

        LoadTrackedEntityInstanceCallback callback = new LoadTrackedEntityInstanceCallback(parentCallback, synchronizing);

        requestBuilder = new ApiRequest.Builder<>();
        try {
            isNull(parentCallback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            String url = networkManager.getServerUrl() + "/api/trackedEntityInstances/"+UID;

            Request request = new Request(RestMethod.GET, url, headers, null);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
        } catch(IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, "dummy", new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
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

class LoadTrackedEntityInstanceCallback implements ApiRequestCallback {
    public static final String TAG = LoadTrackedEntityInstanceCallback.class.getSimpleName();
    final ApiRequestCallback parentCallback;
    final boolean synchronizing;

    public LoadTrackedEntityInstanceCallback(ApiRequestCallback parentCallback, boolean synchronizing) {
        this.parentCallback = parentCallback;
        this.synchronizing = synchronizing;
    }

    @Override
    public void onSuccess(ResponseHolder holder) {
        try {
            Log.d(TAG, "onsuccess");
            TrackedEntityInstance trackedEntityInstance = Dhis2.getInstance().getObjectMapper().readValue(holder.getResponse().getBody(), TrackedEntityInstance.class);
            if(trackedEntityInstance.trackedEntityInstance != null) {
                LoadEnrollmentsTask task = new LoadEnrollmentsTask(NetworkManager.getInstance(), parentCallback, trackedEntityInstance, synchronizing);
                task.execute();
            } else {
                holder.setItem(new Object[]{trackedEntityInstance, new LinkedList<Enrollment>(), new LinkedList<Event>()});
                parentCallback.onSuccess(holder);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if(holder.getApiException()==null) {
                holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
            }
            parentCallback.onFailure(holder);
        }
    }

    @Override
    public void onFailure(ResponseHolder holder) {
        Log.d(TAG, "onFailure");
        parentCallback.onFailure(holder);
    }
}
