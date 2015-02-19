package org.hisp.dhis2.android.sdk.controllers.tasks;

import org.hisp.dhis2.android.sdk.network.http.ApiRequest;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Header;
import org.hisp.dhis2.android.sdk.network.http.Request;
import org.hisp.dhis2.android.sdk.network.http.RestMethod;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis2.android.sdk.utils.Preconditions.isNull;


public class GetOrganisationUnitTask implements INetworkTask {
    private final ApiRequest.Builder<OrganisationUnit> mRequestBuilder;

    public GetOrganisationUnitTask(NetworkManager networkManager,
                                   ApiRequestCallback<OrganisationUnit> callback,
                                   String orgUnitId) {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(orgUnitId, "OrganisationUnit id must not be null");

        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(networkManager.getCredentials(), "Credentials must not be null");

        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", networkManager.getCredentials()));
        headers.add(new Header("Accept", "application/json"));

        String url = networkManager.getServerUrl() + "/api/organisationUnits/"
                + orgUnitId;
        Request request = new Request(RestMethod.GET, url, headers, null);

        mRequestBuilder = new ApiRequest.Builder<>();
        mRequestBuilder.setRequest(request);
        mRequestBuilder.setNetworkManager(networkManager.getHttpManager());
        mRequestBuilder.setRequestCallback(callback);

    }

    @Override
    public void execute() {
        mRequestBuilder.build().request();
    }
}
