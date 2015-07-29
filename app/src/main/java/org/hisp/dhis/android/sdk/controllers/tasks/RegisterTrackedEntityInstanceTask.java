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
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
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
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.Relationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * @author Simen Skogly Russnes on 13.04.15.
 */
public class RegisterTrackedEntityInstanceTask implements INetworkTask {

    private final static String CLASS_TAG = RegisterTrackedEntityInstanceTask.class.getSimpleName();

    private final ApiRequest.Builder<Object> requestBuilder;

    /**
     *
     * @param networkManager
     * @param callback
     * @param trackedEntityInstance
     */
    public RegisterTrackedEntityInstanceTask(NetworkManager networkManager,
                                             ApiRequestCallback<Object> callback,
                                             TrackedEntityInstance trackedEntityInstance) {
        try {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(trackedEntityInstance, "TrackedEntityInstance must not be null");
        } catch(IllegalArgumentException e) {
            ResponseHolder holder = new ResponseHolder<>();
            holder.setApiException(APIException.unexpectedError(e.getMessage(), e));
            callback.onFailure(holder);
        }

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Content-Type", "application/json"));

        List<Relationship> relationships = trackedEntityInstance.getRelationships();
        String referenceId = trackedEntityInstance.getTrackedEntityInstance();
        long referenceLocalId = trackedEntityInstance.getLocalId();
        if(Utils.isLocal(trackedEntityInstance.getTrackedEntityInstance())) {
            trackedEntityInstance.setTrackedEntityInstance(null);
        }
        if(relationships != null) {
            ListIterator<Relationship> it = relationships.listIterator();
            while( it.hasNext() ) {
                Relationship relationship = it.next();
                if( Utils.isLocal(relationship.getTrackedEntityInstanceA()) || Utils.isLocal(relationship.getTrackedEntityInstanceB() ) ) {
                    it.remove();
                }
            }
        }
        trackedEntityInstance.setRelationships(relationships);

        byte[] body = null;
        try {
            body = Dhis2.getInstance().getObjectMapper().writeValueAsBytes(trackedEntityInstance);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Log.e(CLASS_TAG, new String(body));

        String url = networkManager.getServerUrl() + "/api/trackedEntityInstances";

        if(trackedEntityInstance.getTrackedEntityInstance() != null)
        {
            url += "/" + trackedEntityInstance.getTrackedEntityInstance();
        }

        Request request;

        if(url.equalsIgnoreCase(networkManager.getServerUrl() + "/api/trackedEntityInstances"))
            request = new Request(RestMethod.POST, url, headers, body);
        else
            request = new Request(RestMethod.PUT, url, headers, body);

        SendTrackedEntityInstanceCallback sendTrackedEntityInstanceCallback = new
                SendTrackedEntityInstanceCallback(referenceId, referenceLocalId, callback);
        ImportSummaryCallback importSummaryCallback = new
                ImportSummaryCallback(sendTrackedEntityInstanceCallback);

        requestBuilder = new ApiRequest.Builder<>();
        requestBuilder.setRequest(request);
        requestBuilder.setNetworkManager(networkManager.getHttpManager());
        requestBuilder.setRequestCallback(importSummaryCallback);
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                requestBuilder.build().request();
            }
        }.start();
    }

    static class SendTrackedEntityInstanceCallback<T> implements ApiRequestCallback<ImportSummary> {

        private final ApiRequestCallback parentCallback;
        private final String trackedEntityInstanceReference;
        private final long trackedEntityInstanceLocalIdReference;
        public SendTrackedEntityInstanceCallback(String trackedEntityInstanceReference, long trackedEntityInstanceLocalIdReference, ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
            this.trackedEntityInstanceReference = trackedEntityInstanceReference;
            this.trackedEntityInstanceLocalIdReference = trackedEntityInstanceLocalIdReference;
        }

        @Override
        public void onSuccess(ResponseHolder<ImportSummary> responseHolder) {
            if(responseHolder.getApiException() != null) {
                APIException apiException = responseHolder.getApiException();
                DataValueSender.handleError(apiException, FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstanceLocalIdReference);
                parentCallback.onFailure(responseHolder);
            } else {
                ImportSummary importSummary = responseHolder.getItem();
                if (importSummary.getStatus().equals(ImportSummary.SUCCESS)) {
                    //update references with uid received from server
                    new Update(TrackedEntityAttributeValue.class).set(Condition.column
                            (TrackedEntityAttributeValue$Table.TRACKEDENTITYINSTANCEID).is
                            (importSummary.getReference())).where(Condition.column(TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(trackedEntityInstanceLocalIdReference)).async().execute();

                    new Update(Event.class).set(Condition.column(Event$Table.
                            TRACKEDENTITYINSTANCE).is(importSummary.getReference())).where(Condition.
                            column(Event$Table.TRACKEDENTITYINSTANCE).is(trackedEntityInstanceReference)).async().execute();

                    new Update(Enrollment.class).set(Condition.column
                            (Enrollment$Table.TRACKEDENTITYINSTANCE).is(importSummary.getReference())).
                            where(Condition.column(Enrollment$Table.TRACKEDENTITYINSTANCE).is
                                    (trackedEntityInstanceReference)).async().execute();

                    long updated = new Update(Relationship.class).set(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEA
                            ).is(importSummary.getReference())).where(Condition.
                            column(Relationship$Table.TRACKEDENTITYINSTANCEA).is(trackedEntityInstanceReference)).count();

                    updated += new Update(Relationship.class).set(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEB
                    ).is(importSummary.getReference())).where(Condition.
                            column(Relationship$Table.TRACKEDENTITYINSTANCEB).is(trackedEntityInstanceReference)).count();

                    Log.d(CLASS_TAG, "updated relationships: " + updated);

                    /* mechanism for triggering updating of relationships
                    * a relationship can only be uploaded if both involved teis are sent to server
                    * and have a valid UID.
                    * So, we check if this tei was just updated with a valid reference, and if there now
                    * exist >0 relationships that are valid. If >0 relationship is valid, it
                    * should get uploaded, as it is the first time it has been valid. */
                    boolean hasValidRelationship = false;
                    if( Utils.isLocal( trackedEntityInstanceReference ) ) {
                        List<Relationship> teiIsB = new Select().from(Relationship.class).where(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEB).is(importSummary.getReference())).queryList();
                        List<Relationship> teiIsA = new Select().from(Relationship.class).where(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEA).is(importSummary.getReference())).queryList();
                        if( teiIsB != null ) {
                            for( Relationship relationship: teiIsB ) {
                                if( !Utils.isLocal( relationship.getTrackedEntityInstanceA() ) ) {
                                    hasValidRelationship = true;
                                }
                            }
                        }
                        if( teiIsA != null ) {
                            for( Relationship relationship: teiIsA ) {
                                if( !Utils.isLocal( relationship.getTrackedEntityInstanceB() ) ) {
                                    hasValidRelationship = true;
                                }
                            }
                        }
                    }
                    boolean fullySynced = !( hasValidRelationship && updated > 0 );

                    new Update(TrackedEntityInstance.class).set(Condition.column
                            (TrackedEntityInstance$Table.TRACKEDENTITYINSTANCE).is
                            (importSummary.getReference()), Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(fullySynced)).
                            where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(trackedEntityInstanceLocalIdReference)).async().execute();

                    DataValueSender.clearFailedItem(FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstanceLocalIdReference);
                } else if (importSummary.getStatus().equals((ImportSummary.ERROR))) {
                    Log.d(CLASS_TAG, "failed.. ");
                    DataValueSender.handleError(importSummary, FailedItem.TRACKEDENTITYINSTANCE, 200, trackedEntityInstanceLocalIdReference);
                }
                parentCallback.onSuccess(responseHolder);
            }
        }

        @Override
        public void onFailure(ResponseHolder<ImportSummary> responseHolder) {
            parentCallback.onFailure(responseHolder);
        }
    }

    static class ImportSummaryCallback<T> implements ApiRequestCallback<ImportSummary> {

        private final ApiRequestCallback parentCallback;
        public ImportSummaryCallback(ApiRequestCallback parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder<ImportSummary> holder) {
            Log.d(CLASS_TAG, "response: " + new String(holder.getResponse().getBody()));
            try {
                ImportSummary importSummary = Dhis2.getInstance().getObjectMapper().
                        readValue(holder.getResponse().getBody(), ImportSummary.class);
                holder.setItem(importSummary);
            } catch (IOException e) {
                e.printStackTrace();
                holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
            }
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder<ImportSummary> holder) {
            parentCallback.onSuccess(holder);
        }
    }
}