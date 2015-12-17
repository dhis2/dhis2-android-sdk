package org.hisp.dhis.client.sdk.android.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.client.sdk.android.api.utils.ObjectMapperProvider;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.event.IEventApiClient;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Response;

import static org.hisp.dhis.client.sdk.android.api.utils.NetworkUtils.call;

public class EventApiClient implements IEventApiClient {

    private final EventApiClientRetrofit mEventApiClientRetrofit;

    public EventApiClient(EventApiClientRetrofit mEventApiClientRetrofit) {
        this.mEventApiClientRetrofit = mEventApiClientRetrofit;
    }

    @Override
    public List<Event> getFullEvents(String s, String s1, int i, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("organisationUnit", s);
        queryMap.put("program", s1);
        queryMap.put("pageSize", Integer.toString(i));
        queryMap.put("page", "0");
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode eventsJsonNode = call(mEventApiClientRetrofit.getEvents(queryMap));
        List<Event> updatedEvents = unwrap(eventsJsonNode);
        return updatedEvents;
    }

    @Override
    public List<Event> getFullEvents(String s, String s1, int i) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("organisationUnit", s);
        queryMap.put("program", s1);
        queryMap.put("pageSize", Integer.toString(i));
        queryMap.put("page", "0");
        JsonNode eventsJsonNode = call(mEventApiClientRetrofit.getEvents(queryMap));
        List<Event> updatedEvents = unwrap(eventsJsonNode);
        return updatedEvents;
    }

    @Override
    public List<Event> getFullEvents(String s, String s1, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("organisationUnit", s);
        queryMap.put("program", s1);
        queryMap.put("paging", "false");
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode eventsJsonNode = call(mEventApiClientRetrofit.getEvents(queryMap));
        List<Event> updatedEvents = unwrap(eventsJsonNode);
        return updatedEvents;
    }

    @Override
    public List<Event> getBasicEvents(String s, String s1, String s2, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("program", s);
        queryMap.put("programStatus", s1);
        queryMap.put("trackedEntityInstance", s2);
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode eventsJsonNode = call(mEventApiClientRetrofit.getEvents(queryMap));
        List<Event> updatedEvents = unwrap(eventsJsonNode);
        return updatedEvents;
    }

    @Override
    public List<Event> getFullEvents(String s, String s1, String s2, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("program", s);
        queryMap.put("programStatus", s1);
        queryMap.put("trackedEntityInstance", s2);
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        JsonNode eventsJsonNode = call(mEventApiClientRetrofit.getEvents(queryMap));
        List<Event> updatedEvents = unwrap(eventsJsonNode);
        return updatedEvents;
    }

    @Override
    public Event getFullEvent(String s, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        Event event = call(mEventApiClientRetrofit.getEvent(s, queryMap));
        return event;
    }

    @Override
    public Event getBasicEvent(String s, DateTime dateTime) {
        Map<String, String> queryMap = new HashMap<>();
        if (dateTime != null) {
            queryMap.put("lastUpdated", dateTime.toString());
        }
        Event event = call(mEventApiClientRetrofit.getEvent(s, queryMap));
        return event;
    }

    @Override
    public ImportSummary postEvent(Event event) throws ApiException {
        Response response = call(mEventApiClientRetrofit.postEvent(event));
        return unwrapImportSummary(response);
    }

    @Override
    public ImportSummary putEvent(Event event) {
        Response response = call(mEventApiClientRetrofit.putEvent(event.getUId(), event));
        return unwrapImportSummary(response);
    }

    public static List<Event> unwrap(JsonNode jsonNode) {
        TypeReference<List<Event>> typeRef = new TypeReference<List<Event>>() {};
        List<Event> events;
        try {
            if (jsonNode.has("events")) {
                events = ObjectMapperProvider.getInstance().
                        readValue(jsonNode.get("events").traverse(), typeRef);
            } else {
                events = new ArrayList<>();
            }
        } catch (IOException e) {
            events = new ArrayList<>();
            e.printStackTrace();
        }
        return events;
    }

    private static ImportSummary unwrapImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        try {
            JsonNode node = ObjectMapperProvider.getInstance().
                    readTree(response.raw().body().string());
            if (node == null) {
                return null;
            } else if (node.has("response")) {
                return getPutImportSummary(node);
            } else {
                return getPostImportSummary(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ImportSummary getPostImportSummary(JsonNode jsonNode) throws IOException {
        return ObjectMapperProvider.getInstance().treeToValue(jsonNode, ImportSummary.class);
    }

    private static ImportSummary getPutImportSummary(JsonNode jsonNode) throws IOException {
//        ApiResponse apiResponse = ObjectMapperProvider.getInstance().treeToValue(jsonNode, ApiResponse.class);
//        if(apiResponse !=null && apiResponse.getImportSummaries() != null && !apiResponse.getImportSummaries().isEmpty()) {
//            return(apiResponse.getImportSummaries().get(0));
//        } else {
//            return null;
//        }
        return null;
    }
}
