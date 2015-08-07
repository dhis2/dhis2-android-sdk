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
import org.hisp.dhis.android.sdk.controllers.wrappers.TrackedEntityInstancesWrapper;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.network.http.APIException;

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
public class QueryTrackedEntityInstancesTask implements INetworkTask {
    private final ApiRequest.Builder<Object> requestBuilder;
    public final static String CLASS_TAG = QueryTrackedEntityInstancesTask.class.getSimpleName();

    public QueryTrackedEntityInstancesTask(NetworkManager networkManager,
                                           ApiRequestCallback<Object> callback,
                                           String organisationUnit,
                                           String program,
                                           String queryString,
                                           TrackedEntityAttributeValue... params) {
        requestBuilder = new ApiRequest.Builder<>();

        try {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            isNull(organisationUnit, "OrgUnit must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            String url = networkManager.getServerUrl() + "/api/trackedEntityInstances?paging=false&ou="
                    +organisationUnit;
            if(program!=null) {
                url += "&program="+program;
            }

            List<TrackedEntityAttributeValue> valueParams = new LinkedList<>();
            if( params != null ) {
                for(TrackedEntityAttributeValue teav: params ) {
                    if( teav != null && teav.getValue() != null ) {
                        if( !teav.getValue().isEmpty() ) {
                            valueParams.add( teav );
                        }
                    }
                }
            }

            if(queryString!=null && !queryString.isEmpty() && valueParams.isEmpty() ) {
                url+="&query=LIKE:"+queryString;
            }
            Log.d(CLASS_TAG, "queryString: " + queryString);

            for(TrackedEntityAttributeValue param: valueParams) {
                if(param!=null && param.getValue()!=null && !param.getValue().isEmpty()) {
                    url+="&filter="+param.getTrackedEntityAttributeId()+":LIKE:"+param.getValue();
                }
            }

            Request request = new Request(RestMethod.GET, url, headers, null);
            QueryCallback queryCallback = new QueryCallback(callback);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(queryCallback);
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

    public class QueryCallback implements ApiRequestCallback {
        private final ApiRequestCallback parentCallback;

        public QueryCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {

            try {
                JsonNode node = Dhis2.getInstance().getObjectMapper().
                        readTree(holder.getResponse().getBody());
                node = node.get("trackedEntityInstances");
                TypeReference<List<TrackedEntityInstance>> typeRef =
                        new TypeReference<List<TrackedEntityInstance>>() {
                        };
                List<TrackedEntityInstance> trackedEntityInstances = null; // < DHIS2.20 TrackedEntityInstancesWrapper.parseTrackedEntityInstances(holder.getResponse().getBody());
                if(node != null) {
                    trackedEntityInstances = Dhis2.getInstance().getObjectMapper().
                            readValue(node.traverse(), typeRef);
                }
                holder.setItem(trackedEntityInstances);
                parentCallback.onSuccess(holder);
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
            parentCallback.onFailure(holder);
        }
    }
}
