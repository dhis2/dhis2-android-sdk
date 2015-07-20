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
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public class LoadRelationshipTypesTask implements INetworkTask {
    private final ApiRequest.Builder<List<RelationshipType>> requestBuilder;

    public LoadRelationshipTypesTask(NetworkManager networkManager,
                                     ApiRequestCallback<List<RelationshipType>> callback, boolean updating) {

        try {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        } catch(IllegalArgumentException e) {
            ResponseHolder holder = new ResponseHolder<>();
            holder.setApiException(APIException.unexpectedError(e.getMessage(), e));
            callback.onFailure(holder);
        }

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Accept", "application/json"));

        String url = networkManager.getServerUrl() + "/api/relationshipTypes.json?paging=false" +
                "&fields=[:all]";
        if(updating) {
            SystemInfo systemInfo = Dhis2.getInstance().getMetaDataController().getSystemInfo();
            if( systemInfo != null && systemInfo.getServerDate()!= null ) {
                url += "&filter=lastUpdated:gt:" + systemInfo.getServerDate();
            }
        }

        LoadRelationshipTypesCallback loadRelationshipTypesCallback = new LoadRelationshipTypesCallback(callback);

        Request request = new Request(RestMethod.GET, url, headers, null);

        requestBuilder = new ApiRequest.Builder<>();
        requestBuilder.setRequest(request);
        requestBuilder.setNetworkManager(networkManager.getHttpManager());
        requestBuilder.setRequestCallback(loadRelationshipTypesCallback);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                requestBuilder.build().request();
            }
        }.start();
    }

    class LoadRelationshipTypesCallback implements ApiRequestCallback {

        final ApiRequestCallback parentCallback;
        public LoadRelationshipTypesCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            try {
                JsonNode node = Dhis2.getInstance().getObjectMapper().
                        readTree(holder.getResponse().getBody());
                node = node.get("relationshipTypes");
                if (node == null) {
                    holder.setItem(new ArrayList<RelationshipType>());
                } else {
                    TypeReference<List<RelationshipType>> typeRef =
                            new TypeReference<List<RelationshipType>>() {
                            };
                    List<RelationshipType> relationshipTypes = Dhis2.getInstance().getObjectMapper().
                            readValue(node.traverse(), typeRef);
                    holder.setItem(relationshipTypes);
                }
            } catch (IOException e) {
                e.printStackTrace();
                holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
            }
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            parentCallback.onFailure(holder);
        }
    }
}
