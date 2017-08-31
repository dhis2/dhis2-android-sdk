package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.network.response.ApiResponse2;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.data.common.ARemoteDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;

public class TrackedEntityInstanceRemoteDataSource  extends ARemoteDataSource {

    public TrackedEntityInstanceRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }


    public TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstance) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        TrackedEntityInstance updatedTrackedEntityInstance = dhisApi
                .getTrackedEntityInstance(trackedEntityInstance, QUERY_PARAMS);

        return updatedTrackedEntityInstance;
    }

    public ImportSummary save(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance.getCreated() == null) {
            return postTrackedEntityInstance(trackedEntityInstance, dhisApi);
        } else {
            return putTrackedEntityInstance(trackedEntityInstance, dhisApi);
        }
    }

    public List<ImportSummary2> save(List<TrackedEntityInstance> trackedEntityInstances) {
        Map<String, List<TrackedEntityInstance>> map = new HashMap<>();
        map.put("trackedEntityInstances", trackedEntityInstances);
        return batchTrackedEntityInstances(map, dhisApi);
    }

    private List<ImportSummary2> batchTrackedEntityInstances(Map<String, List<TrackedEntityInstance>> trackedEntityInstances, DhisApi dhisApi) throws
            APIException {
        ApiResponse2 apiResponse = dhisApi.postTrackedEntityInstances(trackedEntityInstances);
        return apiResponse.getImportSummaries();
    }

    private ImportSummary postTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.postTrackedEntityInstance(trackedEntityInstance);
        return getImportSummary(response);
    }

    private ImportSummary putTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.putTrackedEntityInstance(trackedEntityInstance.getUid(), trackedEntityInstance);
        return getImportSummary(response);
    }
}