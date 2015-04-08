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

package org.hisp.dhis2.android.sdk.controllers.datavalues;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.controllers.tasks.RegisterEnrollmentTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis2.android.sdk.persistence.models.ResponseBody;
import org.hisp.dhis2.android.sdk.utils.APIException;

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
    private Context context;

    void sendLocalData(Context context) {
        if(sending) return;
        sending = true;
        this.context = context;
        sendEnrollments();
    }

    public void onFinishSending(boolean success) {
        Log.d(CLASS_TAG, "onFinishSending" + success);
        //check if some failed items have been approved. Then delete the FailedItem
        List<FailedItem> failedItems = DataValueController.getFailedItems();
        if(failedItems!=null) {
            for(FailedItem failedItem: failedItems) {
                if(failedItem.getItem() == null) {
                    failedItem.delete(true);
                }
            }
        }

        sending = false;
        //update datavalues
        Dhis2.getInstance().getDataValueController().loadDataValues(context, true);
    }

    /**
     * Tries to send events
     */
    private void sendEvents() {
        localEvents = Select.all(Event.class, Condition.column(Event$Table.FROMSERVER).is(false));
        for(int i = 0; i<localEvents.size(); i++) {/* temporary workaround for not trying to upload events with local enrollment reference*/
            Event event = localEvents.get(i);
            if(event.getEnrollment() == null && event.enrollment != null) {
                localEvents.remove(i);
                i--;
            }
        }
        Log.e(CLASS_TAG, "got this many events:" + localEvents.size());
        sendCounter = localEvents.size();
        if(sendCounter>0) {
            sendEvent(localEvents.get(sendCounter-1));
        } else onFinishSending(true);
    }

    private void sendEvent(Event event) {
        Log.d(CLASS_TAG, "sending event: "+ event.event);
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
        localEnrollments = Select.all(Enrollment.class, Condition.column(Enrollment$Table.FROMSERVER).is(false));
        Log.e(CLASS_TAG, "got this many enrollments:" + localEnrollments.size());
        sendCounter = localEnrollments.size();
        if(sendCounter>0) {
            sendEnrollment(localEnrollments.get(sendCounter - 1));
        } else sendEvents();
    }

    private void sendEnrollment(Enrollment enrollment) {
        Log.d(CLASS_TAG, "sending enrollment: "+ enrollment.enrollment);
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

    private void onResponse(ResponseEvent responseEvent) {
            if( responseEvent.eventType == BaseEvent.EventType.sendEnrollment) {
                if(responseEvent.getResponseHolder().getApiException() != null) {
                    APIException apiException = responseEvent.getResponseHolder().getApiException();
                    FailedItem failedItem = new FailedItem();
                    failedItem.httpStatusCode = apiException.getResponse().getStatus();
                    failedItem.errorMessage = new String(apiException.getResponse().getBody());
                    failedItem.itemId = localEvents.get(sendCounter-1).localId; //todo: implement support for more item types in future (TrackedEntityInstance, Enrollment .. )
                    failedItem.itemType = FailedItem.ENROLLMENT;
                    failedItem.save(true);
                } else {
                    ImportSummary importSummary = (ImportSummary) responseEvent.getResponseHolder().getItem();
                    if (importSummary.status.equals(ImportSummary.SUCCESS)) {
                        Enrollment enrollment = localEnrollments.get(sendCounter - 1);
                        List<Event> events = enrollment.getEvents(true);
                        //updating any local events that had reference to local enrollment to new
                        //reference from server.
                        for (Event event : events) {
                            event.enrollment = importSummary.reference;
                            event.update(true);
                            // need to only update the single field ..!;
                            //new Update().table(Event.class).set(Condition.column(Event$Table.ENROLLMENT).is(importSummary.reference)).where(Condition.column(Event$Table.LOCALID).is(event.localId)).query();
                        }
                        enrollment.enrollment = importSummary.reference;
                        enrollment.fromServer = true;
                        enrollment.update(true);
                        localEnrollments.remove(sendCounter - 1);
                    } else if (importSummary.status.equals((ImportSummary.ERROR))) {
                        Log.d(CLASS_TAG, "failed.. ");
                        FailedItem failedItem = new FailedItem();
                        failedItem.importSummary = importSummary;
                        failedItem.itemId = localEnrollments.get(sendCounter-1).localId; //todo: implement support for more item types in future (TrackedEntityInstance, Enrollment .. )
                        failedItem.itemType = FailedItem.ENROLLMENT;
                        failedItem.httpStatusCode = 200;
                        failedItem.save(true);
                        Log.d(CLASS_TAG, "saved item: " + failedItem.itemId + ":" + failedItem.itemType);
                    }
                }
                sendCounter--;
                if(sendCounter > 0)
                    sendEnrollment(localEnrollments.get(sendCounter-1));
                else
                    sendEvents();

            }
            if (responseEvent.eventType == BaseEvent.EventType.sendEvent) {
                if(responseEvent.getResponseHolder().getApiException() != null) {
                    APIException apiException = responseEvent.getResponseHolder().getApiException();
                    FailedItem failedItem = new FailedItem();
                    if(apiException.getResponse() == null) {

                    } else {
                        failedItem.httpStatusCode = apiException.getResponse().getStatus();
                        failedItem.errorMessage = new String(apiException.getResponse().getBody());
                    }

                    failedItem.itemId = localEvents.get(sendCounter-1).localId; //todo: implement support for more item types in future (TrackedEntityInstance, Enrollment .. )
                    failedItem.itemType = FailedItem.EVENT;
                    failedItem.save(true);
                } else {
                    ResponseBody responseBody = (ResponseBody) responseEvent.getResponseHolder().getItem();
                    if( responseBody.importSummaries.get(0).status.equals(ImportSummary.SUCCESS)) {
                        Event event = localEvents.get(sendCounter-1);
                        List<DataValue> dataValues = event.getDataValues();
                        event.event = responseBody.importSummaries.get(0).reference;
                        for(DataValue dataValue: dataValues) {
                            dataValue.localEventId = event.localId;
                            dataValue.event = event.event;
                            dataValue.update(true);
                        }
                        event.fromServer = true;
                        event.update(true);
                        localEvents.remove(sendCounter-1);
                    } else if (responseBody.importSummaries.get(0).status.equals((ImportSummary.ERROR)) ){
                        Log.d(CLASS_TAG, "failed.. ");
                        FailedItem failedItem = new FailedItem();
                        failedItem.importSummary = responseBody.importSummaries.get(0);
                        failedItem.itemId = localEvents.get(sendCounter-1).localId; //todo: implement support for more item types in future (TrackedEntityInstance, Enrollment .. )
                        failedItem.itemType = FailedItem.EVENT;
                        failedItem.httpStatusCode = 200; // the error is DHIS 2 internal, nothing wrong with connection
                        failedItem.save(true);
                        Log.d(CLASS_TAG, "saved item: " + failedItem.itemId + ":" + failedItem.itemType);
                    }
                }
                sendCounter--;
                if(sendCounter > 0)
                    sendEvent(localEvents.get(sendCounter-1));
                else
                    onFinishSending(true);
            }
    }

}
