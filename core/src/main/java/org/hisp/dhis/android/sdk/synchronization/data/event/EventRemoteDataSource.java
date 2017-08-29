package org.hisp.dhis.android.sdk.synchronization.data.event;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.data.common.RemoteDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;

public class EventRemoteDataSource extends RemoteDataSource {

    public EventRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }

    public Event getEvent(String event) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        Event updatedEvent = dhisApi
                .getEvent(event, QUERY_PARAMS);

        return updatedEvent;
    }

    public ImportSummary save(Event event) {
        if (event.getCreated() == null) {
            return postEvent(event, dhisApi);
        } else {
            return putEvent(event, dhisApi);
        }
    }

    public List<ImportSummary> save(Map<String, List<Event>> events) {
        return batchEvents(events, dhisApi);
    }

    private List<ImportSummary> batchEvents(Map<String, List<Event>> events, DhisApi dhisApi) throws APIException {
        ApiResponse apiResponse = dhisApi.postEvents(events);
        return apiResponse.getImportSummaries();
    }

    private ImportSummary postEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.postEvent(event);

        return getImportSummary(response);
    }

    private ImportSummary putEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.putEvent(event.getEvent(), event);

        return getImportSummary(response);
    }
}
