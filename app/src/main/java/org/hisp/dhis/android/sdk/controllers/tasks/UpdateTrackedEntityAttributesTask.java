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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * Can be used to check and download TrackedEntityAttributes that have been updated since last
 * time Option Sets
 * were updated.
 */
public class UpdateTrackedEntityAttributesTask implements INetworkTask {
    private final ApiRequestCallback<Object> callback;
    private int requestCounter = -1;
    private List<String> trackedEntityAttributesToLoad;

    /**
     *
     * @param networkManager
     * @param callback the Object in the callback is never used so can be set to null.
     */
    public UpdateTrackedEntityAttributesTask(NetworkManager networkManager,
                                             ApiRequestCallback<Object> callback) {
        isNull(callback, "ApiRequestCallback must not be null");
        this.callback = callback;
    }

    /**
     * Called if the update task fails for an unexpected reason.
     * @param holder
     */
    private void onUpdateFailed(ResponseHolder holder) {
        callback.onFailure(holder.getApiException());
    }

    /**
     * Called after the server has been queried to check what TrackedEntityAttributes have been updated since
     * last Meta Data sync.
     * @param holder
     */
    private void onQueryUpdatedTrackedEntityAttributesTaskFinish(ResponseHolder<List<TrackedEntityAttribute>> holder) {
        if( holder.getItem() != null ) {
            List<TrackedEntityAttribute> trackedEntityAttributes = holder.getItem();
            if( trackedEntityAttributes != null && trackedEntityAttributes.size() > 0 ) {
                trackedEntityAttributesToLoad = new ArrayList<>();
                for(TrackedEntityAttribute trackedEntityAttribute: trackedEntityAttributes) {
                    trackedEntityAttributesToLoad.add(trackedEntityAttribute.id);
                }
                loadTrackedEntityAttributes();
            } else {
                onFinishUpdatingTrackedEntityAttributes();
            }
        } else {
            onUpdateFailed(holder);
        }
    }

    /**
     * Loads all TrackedEntityAttributes that need updating.
     */
    private void loadTrackedEntityAttributes() {
        if(trackedEntityAttributesToLoad != null) {
            requestCounter = trackedEntityAttributesToLoad.size();
            if(requestCounter > 0)
                loadTrackedEntityAttribute(trackedEntityAttributesToLoad.get(requestCounter - 1));
        } else onFinishUpdatingTrackedEntityAttributes();
    }

    /**
     * Called when an option set has been loaded from the server
     * @param holder
     */
    private void onLoadTrackedEntityAttributeFinished(ResponseHolder<TrackedEntityAttribute> holder) {
        if( holder.getItem() != null ) {
            TrackedEntityAttribute trackedEntityAttribute = holder.getItem();
            boolean noUpdate = false;
            if(trackedEntityAttribute.id == null) noUpdate = true;
            if(noUpdate) {}
            else {
                TrackedEntityAttribute result = new Select().from(TrackedEntityAttribute.class).where(
                        Condition.column(TrackedEntityAttribute$Table.ID).
                                is(trackedEntityAttribute.id)).querySingle();
                if(result != null)
                    trackedEntityAttribute.async().update();
                else
                    trackedEntityAttribute.async().save();
            }
            requestCounter--;
            if(requestCounter > 0)
                loadTrackedEntityAttribute(trackedEntityAttributesToLoad.get(requestCounter - 1));
            else onFinishUpdatingTrackedEntityAttributes();
        } else {
            onUpdateFailed(holder);
        }

    }

    /**
     * Initiates loading of a TrackedEntityAttribute
     * @param id
     */
    private void loadTrackedEntityAttribute(String id) {
        final ResponseHolder<TrackedEntityAttribute> holder = new ResponseHolder<>();
        LoadTrackedEntityAttributeTask task = new LoadTrackedEntityAttributeTask(NetworkManager.getInstance(),
                new ApiRequestCallback<TrackedEntityAttribute>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            TrackedEntityAttribute trackedEntityAttribute = Dhis2.getInstance().getObjectMapper().readValue(response.getBody(), TrackedEntityAttribute.class);
                            holder.setItem(trackedEntityAttribute);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onLoadTrackedEntityAttributeFinished(holder);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onLoadTrackedEntityAttributeFinished(holder);
                    }
                }, id);
        task.execute();
    }

    /**
     * Called when the UpdateTrackedEntityAttributesTask is finished
     */
    private void onFinishUpdatingTrackedEntityAttributes() {
        callback.onSuccess(null);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                final ResponseHolder<List<TrackedEntityAttribute>> holder = new ResponseHolder<>();
                QueryUpdatedTrackedEntityAttributesTask task = new QueryUpdatedTrackedEntityAttributesTask(NetworkManager.getInstance(),
                        new ApiRequestCallback<List<TrackedEntityAttribute>>() {
                            @Override
                            public void onSuccess(Response response) {
                                holder.setResponse(response);
                                if( response == null ) { /*if the response is null its most like
                                                        because nothing needs to be updated.*/
                                    holder.setItem(new ArrayList<TrackedEntityAttribute>());
                                } else {
                                    try {
                                        JsonNode node = Dhis2.getInstance().getObjectMapper().
                                                readTree(response.getBody());
                                        node = node.get("trackedEntityAttributes");
                                        TypeReference<List<TrackedEntityAttribute>> typeRef =
                                                new TypeReference<List<TrackedEntityAttribute>>(){};
                                        List<TrackedEntityAttribute> trackedEntityAttributes = Dhis2.getInstance().getObjectMapper().
                                                readValue( node.traverse(), typeRef);
                                        holder.setItem(trackedEntityAttributes);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                                    }
                                }

                                onQueryUpdatedTrackedEntityAttributesTaskFinish(holder);
                            }

                            @Override
                            public void onFailure(APIException exception) {
                                holder.setApiException(exception);
                                onQueryUpdatedTrackedEntityAttributesTaskFinish(holder);
                            }
                        });
                task.execute();
            }
        }.start();
    }

    private class QueryUpdatedTrackedEntityAttributesTask implements INetworkTask {
        private final ApiRequest.Builder<List<TrackedEntityAttribute>> requestBuilder;

        private QueryUpdatedTrackedEntityAttributesTask(NetworkManager networkManager, ApiRequestCallback<List<TrackedEntityAttribute>> callback) {
            isNull(callback, "ApiRequestCallback must not be null");
            isNull(networkManager.getServerUrl(), "Server URL must not be null");
            isNull(networkManager.getHttpManager(), "HttpManager must not be null");
            isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            SystemInfo systemInfo = Dhis2.getInstance().getMetaDataController().getSystemInfo();
            if( systemInfo != null && systemInfo.getServerDate()!= null ) {
                String url = networkManager.getServerUrl() + "/api/trackedEntityAttributes?paging=false" +
                        "&fields=id&filter=lastUpdated:gt:" + systemInfo.getServerDate();

                Request request = new Request(RestMethod.GET, url, headers, null);

                requestBuilder = new ApiRequest.Builder<>();
                requestBuilder.setRequest(request);
                requestBuilder.setNetworkManager(networkManager.getHttpManager());
                requestBuilder.setRequestCallback(callback);
            } else {
                //if systemInfo is null most likely it means metadata hasn't been loaded before
                callback.onSuccess(null);
                requestBuilder = null;
            }
        }

        @Override
        public void execute() {
            new Thread() {
                public void run() {
                    if(requestBuilder != null)
                        requestBuilder.build().request();
                }
            }.start();
        }
    }
}
