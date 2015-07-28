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
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class RegisterEnrollmentTask implements INetworkTask {

    private final static String CLASS_TAG = "RegisterEnrollmentTask";

    private final ApiRequest.Builder<Object> requestBuilder;

    /**
     *
     * @param networkManager
     * @param callback
     * @param enrollment
     */
    public RegisterEnrollmentTask(NetworkManager networkManager,
                                  ApiRequestCallback<Object> callback, Enrollment enrollment) {

        requestBuilder = new ApiRequest.Builder<>();
        try {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(enrollment, "Enrollment must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Content-Type", "application/json"));

            if(Utils.isLocal(enrollment.getEnrollment())) {
                enrollment.setEnrollment(null);
            }

            byte[] body = null;
            try {
                body = Dhis2.getInstance().getObjectMapper().writeValueAsBytes(enrollment);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Log.e(CLASS_TAG, new String(body));

            String url = networkManager.getServerUrl() + "/api/enrollments";
            RestMethod restMethod = RestMethod.POST;



            //updating if the enrollment has a valid UID
            if(enrollment.getEnrollment() != null) {
                url += "/" + enrollment.getEnrollment();
                restMethod = RestMethod.PUT;
            }
            Request request = new Request(restMethod, url, headers, body);

            SendEnrollmentCallback sendEnrollmentCallback = new
                    SendEnrollmentCallback(callback, enrollment.getLocalId());
            //RegisterTrackedEntityInstanceTask.ImportSummaryCallback importSummaryCallback = new
            //        RegisterTrackedEntityInstanceTask.ImportSummaryCallback(sendEnrollmentCallback);
            RegisterEventTask.ResponseBodyCallback responseBodyCallback =
                    new RegisterEventTask.ResponseBodyCallback(sendEnrollmentCallback);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(responseBodyCallback);
        } catch(IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, CLASS_TAG, new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
            ResponseHolder holder = new ResponseHolder();
            holder.setApiException(APIException.unexpectedError(e.getMessage(), e));
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

    static class SendEnrollmentCallback implements ApiRequestCallback<ImportSummary> {

        private final ApiRequestCallback parentCallback;
        private final long localEnrollmentReference;
        public SendEnrollmentCallback(ApiRequestCallback parentCallback, long localEnrollmentReference) {
            this.parentCallback = parentCallback;
            this.localEnrollmentReference = localEnrollmentReference;
        }

        @Override
        public void onSuccess(ResponseHolder<ImportSummary> holder) {
            if(holder.getApiException() != null) {
                APIException apiException = holder.getApiException();
                DataValueSender.handleError(apiException, FailedItem.ENROLLMENT, localEnrollmentReference);
                parentCallback.onFailure(holder);
            } else {
                ImportSummary importSummary = holder.getItem();
                if (importSummary.getStatus().equals(ImportSummary.SUCCESS)) {
                    //updating any local events that had reference to local enrollment to new
                    //reference from server.
                    new Update(Event.class).set(Condition.column
                            (Event$Table.ENROLLMENT).is
                            (importSummary.getReference())).where(Condition.column(Event$Table.LOCALENROLLMENTID).is(localEnrollmentReference)).async().execute();

                    new Update(Enrollment.class).set(Condition.column
                            (Enrollment$Table.ENROLLMENT).is
                            (importSummary.getReference()), Condition.column(Enrollment$Table.FROMSERVER)
                            .is(true)).where(Condition.column(Enrollment$Table.LOCALID).is
                            (localEnrollmentReference)).async().execute();
                    DataValueSender.clearFailedItem(FailedItem.ENROLLMENT, localEnrollmentReference);
                } else if (importSummary.getStatus().equals((ImportSummary.ERROR))) {
                    Log.d(CLASS_TAG, "failed.. ");
                    DataValueSender.handleError(importSummary, FailedItem.ENROLLMENT, 200, localEnrollmentReference);
                }
                parentCallback.onSuccess(holder);
            }
        }

        @Override
        public void onFailure(ResponseHolder holder) {
            parentCallback.onFailure(holder);
        }
    }
}