package org.hisp.dhis2.android.sdk.controllers;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.RegisterEventTask;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 * Handles management of data values
 */
public class DataValueController {

    private static final String CLASS_TAG = DataValueController.class.getName();
    private int sendCounter = -1;

    /**
     * Tries to send locally stored data to the server
     */
    public void sendLocalData() {
        sendEvents();
    }

    /**
     * Tries to send events
     */
    private void sendEvents() {
        List<Event> localEvents = Select.all(Event.class, Condition.column(Event$Table.ID).like(Dhis2.QUEUED+"%"));
        Log.e("ddd", "got this many events:" + localEvents.size());
        sendCounter = localEvents.size();
        if(sendCounter>0) {
            sendEvent(localEvents.get(sendCounter-1));
        }
    }

    private void sendEvent(Event event) {
        final ResponseHolder<Event> holder = new ResponseHolder<>();
        final ResponseEvent<Event> responseEvent = new
                ResponseEvent<Event>(ResponseEvent.EventType.sendEvent);
        responseEvent.setResponseHolder(holder);
        RegisterEventTask task = new RegisterEventTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        /*try {
                            Program program = Dhis2.getInstance().getObjectMapper().readValue(response.getBody(), Program.class);
                            holder.setItem(program);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }*/
                        Log.e("ddd", "response: "+new String(response.getBody()));
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

}
