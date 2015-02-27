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

package org.hisp.dhis2.android.sdk.controllers;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.ResponseBody;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 * Handles management of data values
 */
public class DataValueController {

    private boolean sending = false;
    private static final String CLASS_TAG = DataValueController.class.getName();
    private int sendCounter = -1;
    private List<Event> localEvents = null;

    public DataValueController() {
        Dhis2Application.bus.register(this);
    }

    public Event getEvent(String eventId) {
        Log.e(CLASS_TAG, "getting event for: " + eventId);
        List<Event> result = Select.all(Event.class, Condition.column(Event$Table.ID).is(eventId));
        if( result != null && !result.isEmpty() ) return result.get(0);
        else return null;
    }

    /**
     * Returns a list of failed items from the database, or null if there are none.
     * @return
     */
    public List<FailedItem> getFailedItems() {
        List<FailedItem> failedItems = Select.all(FailedItem.class);
        if(failedItems == null || failedItems.size() <= 0) return null;
        else return failedItems;
    }

    /**
     * Tries to send locally stored data to the server
     */
    public void sendLocalData() {
        Log.d(CLASS_TAG, "sending local data");
        //String serverUrl = Dhis2.getInstance().getServer()
        if(sending || Dhis2.getInstance().getMetaDataController().isLoading() ||
                Dhis2.getInstance().getMetaDataController().isSynchronizing()) return;
        sending = true;
        sendEvents();
    }

    /**
     * Tries to send events
     */
    private void sendEvents() {
        localEvents = Select.all(Event.class, Condition.column(Event$Table.ID).like(Dhis2.QUEUED+"%"));
        Log.e(CLASS_TAG, "got this many events:" + localEvents.size());
        sendCounter = localEvents.size();
        if(sendCounter>0) {
            sendEvent(localEvents.get(sendCounter-1));
        } else onFinishSending();
    }

    private void sendEvent(Event event) {
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
                        Dhis2Application.bus.post(responseEvent);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(responseEvent);
                    }
                }, event, event.getDataValues());
        task.execute();
    }

    public void onFinishSending() {
        //check if some failed items have been approved. Then delete the FailedItem
        List<FailedItem> failedItems = getFailedItems();
        if(failedItems!=null) {
            for(FailedItem failedItem: failedItems) {
                if(failedItem.getItem() == null) failedItem.delete(false);
            }
        }

        sending = false;
    }

    @Subscribe
    public void onResponse(DataValueResponseEvent responseEvent) {
        Log.e(CLASS_TAG, "onResponse");
        if (responseEvent.getResponseHolder().getItem() != null) {
            if (responseEvent.eventType == BaseEvent.EventType.sendEvent) {
                ResponseBody responseBody = (ResponseBody) responseEvent.getResponseHolder().getItem();
                if( responseBody.importSummaries.get(0).status.equals(ImportSummary.SUCCESS)) {
                    Event event = localEvents.get(sendCounter-1);
                    event.delete(false);
                    localEvents.remove(sendCounter-1);
                } else if (responseBody.importSummaries.get(0).status.equals((ImportSummary.ERROR)) ){
                    FailedItem failedItem = new FailedItem();
                    failedItem.importSummary = responseBody.importSummaries.get(0);
                    failedItem.itemId = localEvents.get(sendCounter-1).id; //todo: implement support for more item types in future (TrackedEntityInstance, Enrollment .. )
                    failedItem.itemType = FailedItem.EVENT;
                    failedItem.save(false);
                }
                sendCounter--;
                if(sendCounter > 0)
                    sendEvent(localEvents.get(sendCounter-1));
                else
                    onFinishSending();
            }
        } else {
            //TODO: handle exceptions..
            if(responseEvent.getResponseHolder() != null && responseEvent.getResponseHolder().getApiException() != null)
                responseEvent.getResponseHolder().getApiException().printStackTrace();
            onFinishSending();
        }
    }

    public boolean isSending() {
        return sending;
    }

}
