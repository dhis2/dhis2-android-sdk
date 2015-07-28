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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueSender;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class RegisterEventTask implements INetworkTask {

    private final static String CLASS_TAG = "RegisterEventTask";

    private final ApiRequest.Builder<Object> requestBuilder;

    /**
     *
     * @param networkManager
     * @param callback
     * @param event
     * @param dataValues can be null if no values are entered
     */
    public RegisterEventTask(NetworkManager networkManager,
                           ApiRequestCallback<Object> callback, Event event,
                           List<DataValue> dataValues) {

        try {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(event, "Event must not be null");
        } catch(IllegalArgumentException e) {
            ResponseHolder holder = new ResponseHolder<>();
            holder.setApiException(APIException.unexpectedError(e.getMessage(), e));
            callback.onFailure(holder);
        }

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Content-Type", "application/json"));

        event.setDataValues(null);
        if(event.getLongitude()==null) event.setLongitude(new Double(0));
        if(event.getLatitude()==null) event.setLatitude(new Double(0));
        if(Utils.isLocal(event.getEvent())) {
            event.setEvent(null);
        }
        byte[] body = null;
        try {
            body = Dhis2.getInstance().getObjectMapper().writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Log.e(CLASS_TAG, new String(body));

        String url = networkManager.getServerUrl() + "/api/events";
        Request request = new Request(RestMethod.POST, url, headers, body);

        RegisterEventCallback registerEventCallback = new RegisterEventCallback(callback, event.getLocalId());
        ResponseBodyCallback responseBodyCallback = new ResponseBodyCallback(registerEventCallback);

        requestBuilder = new ApiRequest.Builder<>();
        requestBuilder.setRequest(request);
        requestBuilder.setNetworkManager(networkManager.getHttpManager());
        requestBuilder.setRequestCallback(responseBodyCallback);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                requestBuilder.build().request();
            }
        }.start();
    }

    static class RegisterEventCallback implements ApiRequestCallback<ImportSummary> {

        private final long localReferenceId;
        private final ApiRequestCallback parentCallback;
        public RegisterEventCallback(ApiRequestCallback parentCallback, long localReferenceId) {
            this.parentCallback = parentCallback;
            this.localReferenceId = localReferenceId;
        }

        @Override
        public void onSuccess(ResponseHolder<ImportSummary> responseHolder) {
            if(responseHolder.getApiException() != null) {
                APIException apiException = responseHolder.getApiException();
                DataValueSender.handleError(apiException, FailedItem.EVENT, localReferenceId);
                parentCallback.onFailure(responseHolder);
            } else {
                ImportSummary importSummary = responseHolder.getItem();
                if (importSummary!=null) {
                    if( importSummary.getStatus().equals(ImportSummary.SUCCESS)) {
                        new Update(DataValue.class).set(Condition.column
                                (DataValue$Table.EVENT).is
                                (importSummary.getReference())).where(Condition.column(DataValue$Table.LOCALEVENTID).is(localReferenceId)).async().execute();

                        new Update(Event.class).set(Condition.column
                                (Event$Table.EVENT).is
                                (importSummary.getReference()), Condition.column(Event$Table.FROMSERVER).
                                is(true)).where(Condition.column(Event$Table.LOCALID).is(localReferenceId)).async().execute();
                        DataValueSender.clearFailedItem(FailedItem.EVENT, localReferenceId);
                    } else if (importSummary.getStatus().equals((ImportSummary.ERROR)) ){
                        Log.d(CLASS_TAG, "failed.. ");
                        DataValueSender.handleError(importSummary, FailedItem.EVENT, 200, localReferenceId);
                    }
                }
                parentCallback.onSuccess(responseHolder);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            parentCallback.onFailure(responseHolder);
        }
    }

    static class ResponseBodyCallback implements ApiRequestCallback {

        private final ApiRequestCallback parentCallback;
        public ResponseBodyCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder holder) {
            Log.d(CLASS_TAG, "response: " + new String(holder.getResponse().getBody()));
            try {
                ApiResponse apiResponse = Dhis2.getInstance().getObjectMapper().
                        readValue(holder.getResponse().getBody(), ApiResponse.class);
                if(apiResponse !=null && apiResponse.getImportSummaries()!=null && !apiResponse.getImportSummaries().isEmpty()) {
                    holder.setItem(apiResponse.getImportSummaries().get(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
                holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
            }
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            Log.d(CLASS_TAG, "onFailure responsebody");
            parentCallback.onSuccess(holder);
        }
    }
}