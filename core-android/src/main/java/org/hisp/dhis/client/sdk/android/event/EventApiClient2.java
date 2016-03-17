package org.hisp.dhis.client.sdk.android.event;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.event.IEventApiClient;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class EventApiClient2 implements IEventApiClient {
    private final EventApiClientRetrofit eventApiclientRetrofit;

    public EventApiClient2(EventApiClientRetrofit eventApiclientRetrofit) {
        this.eventApiclientRetrofit = eventApiclientRetrofit;
    }

    @Override
    public List<Event> getEvents(Fields fields, DateTime lastUpdated,
                                     String... uids) throws ApiException {
        ApiResource<Event> apiResource = new ApiResource<Event>() {

            @Override
            public String getResourceName() {
                return "events";
            }

            @Override
            public String getBasicProperties() {
                return "event";
            }

            @Override
            public String getAllProperties() {
                return "event,programStage,program,status,orgUnit,eventDate," +
                        "dueDate,name,displayName,created,lastUpdated,access,dataValues" +
                        "coordinate,trackedEntityInstance,enrollment";
            }

            @Override
            public Call<Map<String, List<Event>>> getEntities(
                    Map<String, String> queryMap, List<String> filters) throws ApiException {
                return eventApiclientRetrofit.getEvents(queryMap, filters);
            }
        };

        return getCollection(apiResource, fields, lastUpdated, uids);
    }
}
