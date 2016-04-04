package org.hisp.dhis.client.sdk.android.event;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.ApiResponse;
import org.hisp.dhis.client.sdk.core.common.utils.CollectionUtils;
import org.hisp.dhis.client.sdk.core.event.IEventApiClient;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.call;
import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.unwrap;

public class EventApiClient implements IEventApiClient {
    private final EventApiClientRetrofit eventApiclientRetrofit;

    public EventApiClient(EventApiClientRetrofit eventApiclientRetrofit) {
        this.eventApiclientRetrofit = eventApiclientRetrofit;
    }

    @Override
    public List<Event> getEvents(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {

        Map<String, String> queryMap = new HashMap<>();

        /* disable paging */
        queryMap.put("skipPaging", "true");

        /* filter programs by lastUpdated field */
        if (lastUpdated != null) {
            queryMap.put("lastUpdated", lastUpdated.toString());
        }

        switch (fields) {
            case BASIC: {
                queryMap.put("fields", "event");
                break;
            }
            case ALL: {
                queryMap.put("fields", "event,name,displayName,created,lastUpdated,access," +
                        "program,programStage,status,orgUnit,eventDate,dueDate," +
                        "coordinate,dataValues");
                break;
            }
        }

        List<Event> allEvents = new ArrayList<>();
        if (uids != null && !uids.isEmpty()) {

            // splitting up request into chunks
            List<String> idFilters = buildIdFilter(uids);
            for (String idFilter : idFilters) {
                // List<String> combinedFilters = new ArrayList<>(filters);
                Map<String, String> combinedFilters = new HashMap<>(queryMap);
                combinedFilters.put("event", idFilter);

                // downloading subset of programs
                allEvents.addAll(unwrap(call(
                        eventApiclientRetrofit.getEvents(queryMap)), "events"));
            }
        } else {
            allEvents.addAll(unwrap(call(
                    eventApiclientRetrofit.getEvents(queryMap)), "events"));
        }

        return allEvents;
    }

    @Override
    public ApiResponse postEvents(List<Event> events) throws ApiException {
        return call(eventApiclientRetrofit.postEvents(events));
    }

    @Override
    public ApiResponse deleteEvent(Event event) throws ApiException {
        return call(eventApiclientRetrofit.deleteEvent(event.getUId()));
    }

    private static List<String> buildIdFilter(Set<String> ids) {
        List<String> idFilters = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            List<List<String>> splittedIds = CollectionUtils.slice(new ArrayList<>(ids), 64);
            for (List<String> listOfIds : splittedIds) {
                idFilters.add(CollectionUtils.join(listOfIds, ";"));
            }
        }

        return idFilters;
    }
}
