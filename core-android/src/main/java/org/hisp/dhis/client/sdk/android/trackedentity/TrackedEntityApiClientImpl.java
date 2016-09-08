package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityApiClient;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;


public class TrackedEntityApiClientImpl implements TrackedEntityApiClient {
    private final TrackedEntityApiClientRetrofit trackedEntityApiClientRetrofit;

    public TrackedEntityApiClientImpl(TrackedEntityApiClientRetrofit retrofitClient) {
        this.trackedEntityApiClientRetrofit = retrofitClient;
    }

    private ApiResource<TrackedEntity> apiResource = new ApiResource<TrackedEntity>() {

        @Override
        public String getResourceName() {
            return "trackedEntities";
        }

        @Override
        public String getBasicProperties() {
            return "id";
        }

        @Override
        public String getAllProperties() {
            return "id,name,displayName,created,lastUpdated,access";
        }

        @Override
        public String getDescendantProperties() {
            throw new UnsupportedOperationException();
        }

        public Call<Map<String, List<TrackedEntity>>> getEntities(
                Map<String, String> queryMap, List<String> filters) throws ApiException {
            return trackedEntityApiClientRetrofit
                    .getTrackedEntities(queryMap, filters);
        }
    };
    @Override
    public List<TrackedEntity> getTrackedEntities(Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<TrackedEntity> getTrackedEntities(Fields fields, Set<String> trackedEntityUids) throws ApiException {
        return getCollection(apiResource, "trackedEntities.id", fields, null, trackedEntityUids);
    }
}
