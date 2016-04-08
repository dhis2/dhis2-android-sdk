package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeApiClient;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class TrackedEntityAttributeApiClient implements ITrackedEntityAttributeApiClient {
    private final TrackedEntityAttributeApiClientRetrofit trackedEntityAttributeApiClientRetrofit;

    public TrackedEntityAttributeApiClient(TrackedEntityAttributeApiClientRetrofit trackedEntityAttributeApiClientRetrofit) {
        this.trackedEntityAttributeApiClientRetrofit = trackedEntityAttributeApiClientRetrofit;
    }

    @Override
    public List<TrackedEntityAttribute> getTrackedEntityAttributes(Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<TrackedEntityAttribute> getTrackedEntityAttributes(Fields fields, Set<String> trackedEnityAttributeUids) throws ApiException {
        return getCollection(apiResource, fields, null, trackedEnityAttributeUids);
    }

    private final ApiResource<TrackedEntityAttribute> apiResource =
            new ApiResource<TrackedEntityAttribute>() {

                @Override
                public String getResourceName() {
                    return "trackedEntityAttributes";
                }

                @Override
                public String getBasicProperties() {
                    return "id,displayName";
                }

                @Override
                public String getAllProperties() {
                    return "id,name,displayName,created,lastUpdated,access," +
                            "unique,programScope,orgunitScope," +
                            "displayInListNoProgram,displayOnVisitSchedule,externalAccess," +
                            "valueType,confidential,inherit,sortOrderVisitSchedule,dimension," +
                            "sortOrderInListNoProgram";
                }

                public Call<Map<String, List<TrackedEntityAttribute>>> getEntities(
                        Map<String, String> queryMap, List<String> filters) throws ApiException {
                    return trackedEntityAttributeApiClientRetrofit
                            .getTrackedEntityAttributes(queryMap, filters);
                }
            };
}
