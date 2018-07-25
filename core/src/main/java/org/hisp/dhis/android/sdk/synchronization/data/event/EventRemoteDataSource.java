package org.hisp.dhis.android.sdk.synchronization.data.event;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.data.common.ARemoteDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EventRemoteDataSource extends ARemoteDataSource {

    public EventRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }

    public Event getEvent(String event) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        Event updatedEvent = null;
        try {
            updatedEvent = dhisApi
                    .getEvent(event, QUERY_PARAMS).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedEvent;
    }


    public ImportSummary update(Event event) {
        if(event.isDeleted()){
            return delete(event, dhisApi);
        }else {
            return save(event);
        }
    }

    private ImportSummary save(Event event) {
        if (event.getCreated() == null) {
            return postEvent(event, dhisApi);
        } else {
            return putEvent(event, dhisApi);
        }
    }

    public List<ImportSummary> save(List<Event> events) {
        Map<String, List<Event>> eventsMap = new HashMap<>();
        eventsMap.put("events", events);

        return batchEvents(eventsMap, dhisApi);
    }

    private List<ImportSummary> batchEvents(Map<String, List<Event>> events, DhisApi dhisApi) throws APIException {
        Response response = null;
        ApiResponse apiResponse =null;
        try {
            response = dhisApi.postEvents(events).execute();
            apiResponse = DhisController.getInstance().getObjectMapper().
                    readValue(((ResponseBody)response.body()).string(), ApiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return apiResponse.getImportSummaries();
    }

    private List<ImportSummary> batchDeletedEvents(Map<String, List<Event>> events, DhisApi dhisApi) throws  APIException {
        for(Event event:events.get("events")){
            event.setStatus(null);
        }
        ApiResponse apiResponse = null;
        try {
            apiResponse = dhisApi.postDeletedEvents(events).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Event event:events.get("events")){
            event.setStatus(Event.STATUS_DELETED);
        }
        return apiResponse.getImportSummaries();
    }

    private ImportSummary delete(Event event, DhisApi dhisApi) throws  APIException {
        Response response = null;
        try {
            response = dhisApi.deleteEvent(event.getUid()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getImportSummary(response);
    }

    public List<ImportSummary> delete(List<Event> events) throws  APIException{
        Map<String, List<Event>> eventsMap = new HashMap<>();
        eventsMap.put("events", events);
        return batchDeletedEvents(eventsMap, dhisApi);
    }

    private ImportSummary postEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = null;
        try {
            response = dhisApi.postEvent(event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getImportSummary(response);
    }

    private ImportSummary putEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = null;
        try {
            response = dhisApi.putEvent(event.getEvent(), event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getImportSummary(response);
    }
}
