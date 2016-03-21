/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.event;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.event.IEventApiClient;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.joda.time.DateTime;

import java.util.List;

public class EventApiClient implements IEventApiClient {

    private final EventApiClientRetrofit eventApiClientRetrofit;

    public EventApiClient(EventApiClientRetrofit eventApiClientRetrofit) {
        this.eventApiClientRetrofit = eventApiClientRetrofit;
    }

    @Override
    public List<Event> getEvents(Fields fields, DateTime lastUpdated, String... uids) throws ApiException {
        return null;
    }

//    @Override
//    public List<Event> getFullEvents(String s, String s1, int i, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("organisationUnit", s);
//        queryMap.put("program", s1);
//        queryMap.put("pageSize", Integer.toString(i));
//        queryMap.put("page", "0");
//
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//
//        JsonNode eventsJsonNode = call(eventApiClientRetrofit.getEvents(queryMap));
//        List<Event> updatedEvents = unwrap(eventsJsonNode);
//        return updatedEvents;
//    }
//
//    @Override
//    public List<Event> getFullEvents(String s, String s1, int i) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("organisationUnit", s);
//        queryMap.put("program", s1);
//        queryMap.put("pageSize", Integer.toString(i));
//        queryMap.put("page", "0");
//        JsonNode eventsJsonNode = call(eventApiClientRetrofit.getEvents(queryMap));
//        List<Event> updatedEvents = unwrap(eventsJsonNode);
//        return updatedEvents;
//    }
//
//    @Override
//    public List<Event> getFullEvents(String s, String s1, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("organisationUnit", s);
//        queryMap.put("program", s1);
//        queryMap.put("paging", "false");
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//        JsonNode eventsJsonNode = call(eventApiClientRetrofit.getEvents(queryMap));
//        List<Event> updatedEvents = unwrap(eventsJsonNode);
//        return updatedEvents;
//    }
//
//    @Override
//    public List<Event> getBasicEvents(String s, String s1, String s2, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("program", s);
//        queryMap.put("programStatus", s1);
//        queryMap.put("trackedEntityInstance", s2);
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//        JsonNode eventsJsonNode = call(eventApiClientRetrofit.getEvents(queryMap));
//        List<Event> updatedEvents = unwrap(eventsJsonNode);
//        return updatedEvents;
//    }
//
//    @Override
//    public List<Event> getFullEvents(String s, String s1, String s2, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("program", s);
//        queryMap.put("programStatus", s1);
//        queryMap.put("trackedEntityInstance", s2);
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//        JsonNode eventsJsonNode = call(eventApiClientRetrofit.getEvents(queryMap));
//        List<Event> updatedEvents = unwrap(eventsJsonNode);
//        return updatedEvents;
//    }
//
//    @Override
//    public Event getFullEvent(String s, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//        Event event = call(eventApiClientRetrofit.getEvent(s, queryMap));
//        return event;
//    }
//
//    @Override
//    public Event getBasicEvent(String s, DateTime dateTime) {
//        Map<String, String> queryMap = new HashMap<>();
//        if (dateTime != null) {
//            queryMap.put("lastUpdated", dateTime.toString());
//        }
//        Event event = call(eventApiClientRetrofit.getEvent(s, queryMap));
//        return event;
//    }
//
//    @Override
//    public ImportSummary postEvent(Event event) throws ApiException {
//        Response response = call(eventApiClientRetrofit.postEvent(event));
//        return unwrapImportSummary(response);
//    }
//
//    @Override
//    public ImportSummary putEvent(Event event) {
//        Response response = call(eventApiClientRetrofit.putEvent(event.getUId(), event));
//        return unwrapImportSummary(response);
//    }
//
//    public static List<Event> unwrap(JsonNode jsonNode) {
////        TypeReference<List<Event>> typeRef = new TypeReference<List<Event>>() {};
////        List<Event> events;
////        try {
////            if (jsonNode.has("events")) {
////                events = ObjectMapperProvider.getInstance().
////                        readValue(jsonNode.get("events").traverse(), typeRef);
////            } else {
////                events = new ArrayList<>();
////            }
////        } catch (IOException e) {
////            events = new ArrayList<>();
////            e.printStackTrace();
////        }
////        return events;
//        return null;
//    }
//
//    private static ImportSummary unwrapImportSummary(Response response) {
//        //because the web api almost randomly gives the responses in different forms, this
//        //method checks which one it is that is being returned, and parses accordingly.
////        try {
////            JsonNode node = ObjectMapperProvider.getInstance().
////                    readTree(response.raw().body().string());
////            if (node == null) {
////                return null;
////            } else if (node.has("response")) {
////                return getPutImportSummary(node);
////            } else {
////                return getPostImportSummary(node);
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////            return null;
////        }
//        return null;
//    }
//
//    private static ImportSummary getPostImportSummary(JsonNode jsonNode) throws IOException {
//        // return ObjectMapperProvider.getInstance().treeToValue(jsonNode, ImportSummary.class);
//        return null;
//    }
//
//    private static ImportSummary getPutImportSummary(JsonNode jsonNode) throws IOException {
////        ApiResponse apiResponse = ObjectMapperProvider.getInstance().treeToValue(jsonNode, ApiResponse.class);
////        if(apiResponse !=null && apiResponse.getImportSummaries() != null && !apiResponse.getImportSummaries().isEmpty()) {
////            return(apiResponse.getImportSummaries().get(0));
////        } else {
////            return null;
////        }
//        return null;
//    }
}
