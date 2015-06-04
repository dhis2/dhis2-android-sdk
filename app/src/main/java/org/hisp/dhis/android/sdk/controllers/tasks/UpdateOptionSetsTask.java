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

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * Can be used to check and download Option Sets that have been updated since last time Option Sets
 * were updated.
 */
public class UpdateOptionSetsTask implements INetworkTask {
    private final ApiRequestCallback<Object> callback;
    private int requestCounter = -1;
    private List<String> optionSetsToLoad;

    /**
     *
     * @param networkManager
     * @param callback the Object in the callback is never used so can be set to null.
     */
    public UpdateOptionSetsTask(NetworkManager networkManager,
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
     * Called after the server has been queried to check what OptionSets have been updated since
     * last Meta Data sync.
     * @param holder
     */
    private void onQueryUpdatedOptionSetsTaskFinish(ResponseHolder<List<OptionSet>> holder) {
        if( holder.getItem() != null ) {
            List<OptionSet> optionSets = holder.getItem();
            if( optionSets != null && optionSets.size() > 0 ) {
                optionSetsToLoad = new ArrayList<>();
                for(OptionSet optionSet: optionSets) {
                    optionSetsToLoad.add(optionSet.id);
                }
                loadOptionSets();
            } else {
                onFinishUpdatingOptionSets();
            }
        } else {
            onUpdateFailed(holder);
        }
    }

    /**
     * Loads all OptionSets that need updating.
     */
    private void loadOptionSets() {
        if(optionSetsToLoad != null) {
            requestCounter = optionSetsToLoad.size();
            if(requestCounter > 0)
                loadOptionSet(optionSetsToLoad.get(requestCounter-1));
        } else onFinishUpdatingOptionSets();
    }

    /**
     * Called when an option set has been loaded from the server
     * @param holder
     */
    private void onLoadOptionSetFinished(ResponseHolder<OptionSet> holder) {
        if( holder.getItem() != null ) {
            OptionSet optionSet = holder.getItem();
            boolean noUpdate = false;
            if(optionSet.id == null) noUpdate = true;
            if(noUpdate) {}
            else {
                /**
                 * Firstly delete the old options for the already stored Option set in case
                 * some of them have been deleted.
                 */
                OptionSet oldOptionSet = Dhis2.getInstance().getMetaDataController().getOptionSet(optionSet.id);
                if( oldOptionSet!=null ) {
                    for(Option option: oldOptionSet.getOptions()) {
                        //option.delete(true);
                        option.async().save();
                    }
                }
                oldOptionSet = null;
                int index = 0;
                for( Option o: optionSet.getOptions()) {
                    o.setSortIndex(index);
                    o.setOptionSet( optionSet.getId() );
                    //o.save(true);
                    o.async().save();
                    index ++;
                }
                //optionSet.save(true);
                optionSet.async().save();
            }
            requestCounter--;
            if(requestCounter > 0)
                loadOptionSet(optionSetsToLoad.get(requestCounter-1));
            else onFinishUpdatingOptionSets();
        } else {
            onUpdateFailed(holder);
        }

    }

    /**
     * Initiates loading of an OptionSet
     * @param optionSetId
     */
    private void loadOptionSet(String optionSetId) {
        final ResponseHolder<OptionSet> holder = new ResponseHolder<>();
        LoadOptionSetTask task = new LoadOptionSetTask(NetworkManager.getInstance(),
                new ApiRequestCallback<OptionSet>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            OptionSet optionSet = Dhis2.getInstance().getObjectMapper().readValue(response.getBody(), OptionSet.class);
                            holder.setItem(optionSet);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onLoadOptionSetFinished(holder);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onLoadOptionSetFinished(holder);
                    }
                }, optionSetId, true);
        task.execute();
    }

    /**
     * Called when the UpdateOptionSetsTask is finished
     */
    private void onFinishUpdatingOptionSets() {
        callback.onSuccess(null);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                final ResponseHolder<List<OptionSet>> holder = new ResponseHolder<>();
                QueryUpdatedOptionSetsTask task = new QueryUpdatedOptionSetsTask(NetworkManager.getInstance(),
                        new ApiRequestCallback<List<OptionSet>>() {
                            @Override
                            public void onSuccess(Response response) {
                                holder.setResponse(response);
                                if( response == null ) { /*if the response is null its most like
                                                        because nothing needs to be updated.*/
                                    holder.setItem(new ArrayList<OptionSet>());
                                } else {
                                    try {
                                        JsonNode node = Dhis2.getInstance().getObjectMapper().
                                                readTree(response.getBody());
                                        node = node.get("optionSets");
                                        TypeReference<List<OptionSet>> typeRef =
                                                new TypeReference<List<OptionSet>>(){};
                                        List<OptionSet> optionSets = Dhis2.getInstance().getObjectMapper().
                                                readValue( node.traverse(), typeRef);
                                        holder.setItem(optionSets);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                                    }
                                }

                                onQueryUpdatedOptionSetsTaskFinish(holder);
                            }

                            @Override
                            public void onFailure(APIException exception) {
                                holder.setApiException(exception);
                                onQueryUpdatedOptionSetsTaskFinish(holder);
                            }
                        });
                task.execute();
            }
        }.start();
    }

    private class QueryUpdatedOptionSetsTask implements INetworkTask {
        private final ApiRequest.Builder<List<OptionSet>> requestBuilder;

        private QueryUpdatedOptionSetsTask(NetworkManager networkManager, ApiRequestCallback<List<OptionSet>> callback) {
            try {
                isNull(callback, "ApiRequestCallback must not be null");
                isNull(networkManager.getServerUrl(), "Server URL must not be null");
                isNull(networkManager.getHttpManager(), "HttpManager must not be null");
                isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
            } catch(IllegalArgumentException e) {
                callback.onFailure(APIException.unexpectedError(e.getMessage(), e));
            }

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Accept", "application/json"));

            SystemInfo systemInfo = Dhis2.getInstance().getMetaDataController().getSystemInfo();
            if( systemInfo != null && systemInfo.getServerDate() != null ) {
                String url = networkManager.getServerUrl() + "/api/optionSets?paging=false" +
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
                /*
                Throwable throwable = new UnknownError("missing serverDate. See UpdateOptionSetsTask");
                callback.onFailure(APIException.unexpectedError("missing serverDate. See UpdateOptionSetsTask", throwable));*/
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
