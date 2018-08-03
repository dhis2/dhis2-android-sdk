package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;


import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.data.common.ARemoteDataSource;
import org.hisp.dhis.android.sdk.utils.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class TrackedEntityInstanceRemoteDataSource  extends ARemoteDataSource {

    public TrackedEntityInstanceRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }


    public TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstance) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        TrackedEntityInstance updatedTrackedEntityInstance = null;
        try {
            if(!dhisApi.getTrackedEntityInstance(trackedEntityInstance, QUERY_PARAMS).isExecuted()){
                updatedTrackedEntityInstance = dhisApi
                        .getTrackedEntityInstance(trackedEntityInstance, QUERY_PARAMS).execute().body();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedTrackedEntityInstance;
    }

    public ImportSummary save(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance.getCreated() == null) {
            return postTrackedEntityInstance(trackedEntityInstance, dhisApi);
        } else {
            return putTrackedEntityInstance(trackedEntityInstance, dhisApi);
        }
    }

    public List<ImportSummary> save(List<TrackedEntityInstance> trackedEntityInstances) {
        Map<String, List<TrackedEntityInstance>> map = new HashMap<>();
        map.put("trackedEntityInstances", trackedEntityInstances);
        return batchTrackedEntityInstances(map, dhisApi);
    }

    private List<ImportSummary> batchTrackedEntityInstances(Map<String, List<TrackedEntityInstance>> trackedEntityInstances, DhisApi dhisApi) throws
            APIException {
        ApiResponse apiResponse = null;
        Response response = null;
        try {
            response = dhisApi.postTrackedEntityInstances(trackedEntityInstances).execute();
            apiResponse = DhisController.getInstance().getObjectMapper().
                    readValue(((ResponseBody)response.body()).string(), ApiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiResponse.getImportSummaries();
    }

    private ImportSummary postTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        Response response = null;
        try {
            response = dhisApi.postTrackedEntityInstance(trackedEntityInstance).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getImportSummary(response);
    }

    private ImportSummary putTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        Response response = null;
        try {

            response = dhisApi.putTrackedEntityInstance(trackedEntityInstance.getUid(), trackedEntityInstance).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return getImportSummary(response);
    }
}