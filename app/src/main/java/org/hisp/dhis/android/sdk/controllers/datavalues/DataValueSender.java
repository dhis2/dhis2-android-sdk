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

package org.hisp.dhis.android.sdk.controllers.datavalues;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterEnrollmentTask;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis.android.sdk.controllers.tasks.RegisterTrackedEntityInstanceTask;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis.android.sdk.events.InvalidateEvent;
import org.hisp.dhis.android.sdk.events.ResponseEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.ResponseBody;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
public class DataValueSender {

    private static final String CLASS_TAG = "DataValueSender";

    boolean sending = false;
    private int sendCounter = -1;
    private List<Event> localEvents = null;
    private List<Enrollment> localEnrollments = null;
    private List<TrackedEntityInstance> localTrackedEntityInstances = null;
    private Context context;
    private ApiRequestCallback callback;

    void sendLocalData(Context context, ApiRequestCallback callback) {
        if(Dhis2.isLoading()) return;
        this.callback = callback;
        sending = true;
        this.context = context;
        new Thread() {
            public void run() {
                /*if(!NetworkManager.hasInternetConnection()) onFinishSending(false);
                else */sendTrackedEntityInstances();
            }
        }.start();
    }

    private void onFinishSending(boolean success) {
        Log.d(CLASS_TAG, "onFinishSending" + success);
        //check if some failed items have been approved. Then delete the FailedItem
        List<FailedItem> failedItems = DataValueController.getFailedItems();
        if(failedItems!=null) {
            for(FailedItem failedItem: failedItems) {
                if(failedItem.getItem() == null) {
                    failedItem.async().delete();
                }
            }
        }

        InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.dataValuesSent);
        Dhis2Application.getEventBus().post(event);

        sending = false;
        if(success) {
            callback.onSuccess(null);
        } else {
            callback.onFailure(null);
        }
    }

    /**
     * Tries to send events
     */
    private void sendEvents() {
        localEvents = new Select().from(Event.class).where(Condition.column(Event$Table.FROMSERVER).is(false)).queryList();
        for(int i = 0; i<localEvents.size(); i++) {/* temporary workaround for not trying to upload events with local enrollment reference*/
            Event event = localEvents.get(i);
            if(event.getEnrollment() == null && event.getEnrollment() != null) {
                localEvents.remove(i);
                i--;
            }
        }
        Log.d(CLASS_TAG, "got this many events:" + localEvents.size());
        sendCounter = localEvents.size();
        if(sendCounter>0) {
            sendEvent(localEvents.get(sendCounter-1));
        } else onFinishSending(true);
    }

    private void sendEvent(Event event) {
        Log.d(CLASS_TAG, "sending event: "+ event.getEvent());
        final ResponseHolder<ResponseBody> holder = new ResponseHolder<>();
        final DataValueResponseEvent<ResponseBody> responseEvent = new
                DataValueResponseEvent<ResponseBody>(ResponseEvent.EventType.sendEvent);
        responseEvent.setResponseHolder(holder);
        RegisterEventTask task = new RegisterEventTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "response: " + new String(response.getBody()));
                        try {
                            ResponseBody responseBody = Dhis2.getInstance().getObjectMapper().
                                    readValue(response.getBody(), ResponseBody.class);
                            holder.setItem(responseBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(responseEvent);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(responseEvent);
                    }
                }, event, event.getDataValues());
        task.execute();
    }

    /**
     * loads currently new enrollments from local database and queues for sending. Enrollments
     * added to the database while sending occurs will not be sent until next sending is initiated
     */
    private void sendEnrollments() {
        localEnrollments = new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.FROMSERVER).is(false)).queryList();
        for(int i = 0; i<localEnrollments.size(); i++) {/* workaround for not attempting to upload enrollments with local tei reference*/
            Enrollment enrollment = localEnrollments.get(i);
            if(enrollment.getTrackedEntityInstance() == null) {
                localEnrollments.remove(i);
                i--;
            }
        }
        Log.d(CLASS_TAG, "got this many enrollments:" + localEnrollments.size());
        sendCounter = localEnrollments.size();
        if(sendCounter>0) {
            sendEnrollment(localEnrollments.get(sendCounter - 1));
        } else sendEvents();
    }

    private void sendEnrollment(Enrollment enrollment) {
        Log.d(CLASS_TAG, "sending enrollment: "+ enrollment.getEnrollment());
        final ResponseHolder<ImportSummary> holder = new ResponseHolder<>();
        final DataValueResponseEvent<ImportSummary> responseEvent = new
                DataValueResponseEvent<ImportSummary>(ResponseEvent.EventType.sendEnrollment);
        responseEvent.setResponseHolder(holder);
        RegisterEnrollmentTask task = new RegisterEnrollmentTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "response: " + new String(response.getBody()));
                        try {
                            ImportSummary importSummary = Dhis2.getInstance().getObjectMapper().
                                    readValue(response.getBody(), ImportSummary.class);
                            holder.setItem(importSummary);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(responseEvent);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(responseEvent);
                    }
                }, enrollment);
        task.execute();
    }

    /**
     * Initiates sending and registering of locally created TrackedEntityInstance to the server.
     */
    private void sendTrackedEntityInstances() {
        localTrackedEntityInstances = new Select().from(TrackedEntityInstance.class).where(Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(false)).queryList();
        Log.d(CLASS_TAG, "got this many trackedEntityInstances:" + localTrackedEntityInstances.size());
        sendCounter = localTrackedEntityInstances.size();
        if(sendCounter>0) {
            sendTrackedEntityInstance(localTrackedEntityInstances.get(sendCounter - 1));
        } else sendEnrollments();
    }

    private void sendTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        Log.d(CLASS_TAG, "sending tei: "+ trackedEntityInstance.trackedEntityInstance);
        final ResponseHolder<ImportSummary> holder = new ResponseHolder<>();
        final DataValueResponseEvent<ImportSummary> responseEvent = new
                DataValueResponseEvent<ImportSummary>(ResponseEvent.EventType.sendTrackedEntityInstance);
        responseEvent.setResponseHolder(holder);
        RegisterTrackedEntityInstanceTask task = new RegisterTrackedEntityInstanceTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "response: " + new String(response.getBody()));
                        try {
                            ImportSummary importSummary = Dhis2.getInstance().getObjectMapper().
                                    readValue(response.getBody(), ImportSummary.class);
                            holder.setItem(importSummary);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(responseEvent);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(responseEvent);
                    }
                }, trackedEntityInstance);
        task.execute();
    }

    private void onResponse(ResponseEvent responseEvent) {
        if( responseEvent.eventType == BaseEvent.EventType.sendTrackedEntityInstance) {
            if(responseEvent.getResponseHolder().getApiException() != null) {

                APIException apiException = responseEvent.getResponseHolder().getApiException();
                handleError(apiException, FailedItem.TRACKEDENTITYINSTANCE, localTrackedEntityInstances.get(sendCounter-1).localId);
            } else {
                ImportSummary importSummary = (ImportSummary) responseEvent.getResponseHolder().getItem();
                if (importSummary.getStatus().equals(ImportSummary.SUCCESS)) {
                    //update references with uid received from server
                    TrackedEntityInstance trackedEntityInstance = localTrackedEntityInstances.get(sendCounter - 1);
                    new Update(TrackedEntityAttributeValue.class).set(Condition.column
                            (TrackedEntityAttributeValue$Table.TRACKEDENTITYINSTANCEID).is
                            (importSummary.getReference())).where(Condition.column(TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(trackedEntityInstance.localId)).async().execute();

                    new Update(Event.class).set(Condition.column(Event$Table.
                            TRACKEDENTITYINSTANCE).is(importSummary.getReference())).where(Condition.
                            column(Event$Table.TRACKEDENTITYINSTANCE).is(trackedEntityInstance.
                            trackedEntityInstance)).async().execute();

                    new Update(Enrollment.class).set(Condition.column
                            (Enrollment$Table.TRACKEDENTITYINSTANCE).is(importSummary.getReference())).
                            where(Condition.column(Enrollment$Table.TRACKEDENTITYINSTANCE).is
                                    (trackedEntityInstance.trackedEntityInstance)).async().execute();

                    new Update(TrackedEntityInstance.class).set(Condition.column
                            (TrackedEntityInstance$Table.TRACKEDENTITYINSTANCE).is
                            (importSummary.getReference()), Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(true)).
                            where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(trackedEntityInstance.localId)).async().execute();
                    localTrackedEntityInstances.remove(sendCounter - 1);
                } else if (importSummary.getStatus().equals((ImportSummary.ERROR))) {
                    Log.d(CLASS_TAG, "failed.. ");
                    FailedItem failedItem = new FailedItem();
                    failedItem.setImportSummary(importSummary);
                    failedItem.setItemId(localTrackedEntityInstances.get(sendCounter-1).localId);
                    failedItem.setItemType(FailedItem.TRACKEDENTITYINSTANCE);
                    failedItem.setHttpStatusCode(200);
                    failedItem.async().save();
                    Log.d(CLASS_TAG, "saved item: " + failedItem.getItemId() + ":" + failedItem.getItemType());
                }
            }
            sendCounter--;
            if(sendCounter > 0)
                sendTrackedEntityInstance(localTrackedEntityInstances.get(sendCounter-1));
            else
            {
                //temporary fix for waiting for TransactionManager to finish and update references
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendEnrollments();
            }


        }
        else if( responseEvent.eventType == BaseEvent.EventType.sendEnrollment) {
            if(responseEvent.getResponseHolder().getApiException() != null) {
                APIException apiException = responseEvent.getResponseHolder().getApiException();
                handleError(apiException, FailedItem.ENROLLMENT, localEnrollments.get(sendCounter-1).localId);
            } else {
                ImportSummary importSummary = (ImportSummary) responseEvent.getResponseHolder().getItem();
                if (importSummary.getStatus().equals(ImportSummary.SUCCESS)) {
                    Enrollment enrollment = localEnrollments.get(sendCounter - 1);
                    //updating any local events that had reference to local enrollment to new
                    //reference from server.
                    new Update(Event.class).set(Condition.column
                            (Event$Table.ENROLLMENT).is
                            (importSummary.getReference())).where(Condition.column(Event$Table.LOCALENROLLMENTID).is(enrollment.localId)).async().execute();

                    new Update(Enrollment.class).set(Condition.column
                            (Enrollment$Table.ENROLLMENT).is
                            (importSummary.getReference()), Condition.column(Enrollment$Table.FROMSERVER)
                            .is(true)).where(Condition.column(Enrollment$Table.LOCALID).is
                            (enrollment.localId)).async().execute();
                    localEnrollments.remove(sendCounter - 1);
                } else if (importSummary.getStatus().equals((ImportSummary.ERROR))) {
                    Log.d(CLASS_TAG, "failed.. ");
                    FailedItem failedItem = new FailedItem();
                    failedItem.setImportSummary(importSummary);
                    failedItem.setItemId(localEnrollments.get(sendCounter-1).localId);
                    failedItem.setItemType(FailedItem.ENROLLMENT);
                    failedItem.setHttpStatusCode(200);
                    failedItem.async().save();
                    Log.d(CLASS_TAG, "saved item: " + failedItem.getItemId()+ ":" + failedItem.getItemType());
                }
            }
            sendCounter--;
            if(sendCounter > 0)
                sendEnrollment(localEnrollments.get(sendCounter-1));
            else
            {
                //temporary fix for waiting for TransactionManager to finish and update references
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendEvents();
            }

        }
        else if (responseEvent.eventType == BaseEvent.EventType.sendEvent) {
            if(responseEvent.getResponseHolder().getApiException() != null) {
                APIException apiException = responseEvent.getResponseHolder().getApiException();
                handleError(apiException, FailedItem.EVENT, localEvents.get(sendCounter-1).getLocalId());
            } else {
                ResponseBody responseBody = (ResponseBody) responseEvent.getResponseHolder().getItem();
                if( responseBody.getImportSummaries().get(0).getStatus().equals(ImportSummary.SUCCESS)) {
                    ImportSummary importSummary = responseBody.getImportSummaries().get(0);
                    Event event = localEvents.get(sendCounter-1);
                    List<DataValue> dataValues = event.getDataValues();
                    event.setEvent(responseBody.getImportSummaries().get(0).getReference());
                    new Update(DataValue.class).set(Condition.column
                            (DataValue$Table.EVENT).is
                            (importSummary.getReference())).where(Condition.column(DataValue$Table.LOCALEVENTID).is(event.getLocalId())).async().execute();

                    new Update(Event.class).set(Condition.column
                            (Event$Table.EVENT).is
                            (importSummary.getReference()), Condition.column(Event$Table.FROMSERVER).
                            is(true)).where(Condition.column(Event$Table.LOCALID).is(event.getLocalId())).async().execute();
                    localEvents.remove(sendCounter-1);
                } else if (responseBody.getImportSummaries().get(0).getStatus().equals((ImportSummary.ERROR)) ){
                    Log.d(CLASS_TAG, "failed.. ");
                    FailedItem failedItem = new FailedItem();
                    failedItem.setImportSummary(responseBody.getImportSummaries().get(0));
                    failedItem.setItemId(localEvents.get(sendCounter-1).getLocalId());
                    failedItem.setItemType(FailedItem.EVENT);
                    failedItem.setHttpStatusCode(200); // the error is DHIS 2 internal, nothing wrong with connection
                    failedItem.async().save();
                    Log.d(CLASS_TAG, "saved item: " + failedItem.getItemId() + ":" + failedItem.getItemType());
                }
            }
            sendCounter--;
            if(sendCounter > 0)
                sendEvent(localEvents.get(sendCounter-1));
            else
                onFinishSending(true);
        }
    }

    private void handleError(APIException apiException, String type, long id) {
        if(apiException.getResponse() != null && apiException.getResponse().getBody()!=null) {
            Log.e(CLASS_TAG, new String(apiException.getResponse().getBody()));
        }
        if(apiException.isNetworkError()) {
            return; //if item failed due to network error then there is no need to store error info
        }
        FailedItem failedItem = new FailedItem();
        if(apiException.getResponse() != null) {
            failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
            failedItem.setErrorMessage(new String(apiException.getResponse().getBody()));
        }
        if(type.equals(FailedItem.EVENT)) {
            failedItem.setItemId(localEvents.get(sendCounter-1).getLocalId());
        } else if(type.equals(FailedItem.ENROLLMENT)) {
            failedItem.setItemId(localEnrollments.get(sendCounter-1).localId);
        } else if(type.equals(FailedItem.TRACKEDENTITYINSTANCE)) {
            failedItem.setItemId(localTrackedEntityInstances.get(sendCounter-1).localId);
        }
        failedItem.setItemType(type);
        failedItem.async().save();
    }
}
