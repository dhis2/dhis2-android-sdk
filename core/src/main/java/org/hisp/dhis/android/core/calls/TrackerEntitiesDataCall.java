package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.trackedentity.TeiQuery;
import org.hisp.dhis.android.core.trackedentity.TeisEndPointCall;

import java.util.Date;
import java.util.Set;

import retrofit2.Response;

public final class TrackerEntitiesDataCall implements Call<Response> {

    private boolean isExecuted;
    private final int teiLimitByOrgUnit;
    private final GenericCallData genericCallData;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    private TrackerEntitiesDataCall(@NonNull GenericCallData genericCallData,
                                    @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
                                    int teiLimitByOrgUnit) {
        this.genericCallData = genericCallData;
        this.organisationUnitStore = organisationUnitStore;
        this.teiLimitByOrgUnit = teiLimitByOrgUnit;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        Response response = null;

        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();

        try {
            response = SystemInfoCall.FACTORY.create(genericCallData).call();

            if (!response.isSuccessful()) {
                return response;
            }

            SystemInfo systemInfo = (SystemInfo) response.body();
            Date serverDate = systemInfo.serverDate();

            response = trackerCall(serverDate);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();

            return response;
        } finally {
            transaction.end();
        }
    }
    
    private Response trackerCall(Date serverDate) throws Exception {
        Response response = null;

        Set<String> organisationUnitUids = organisationUnitStore.selectUids();

        int pageSize = TeiQuery.Builder.create().build().getPageSize();

        int numPages = (int) Math.ceil((double) teiLimitByOrgUnit / pageSize);

        int pageLimit = 0;

        for (String orgUnitUid : organisationUnitUids) {

            int teisDownloaded = 0;

            for (int page = 1; page <= numPages; page++) {

                if (page == numPages && teiLimitByOrgUnit > 0) {
                    pageLimit = teiLimitByOrgUnit - teisDownloaded;
                }

                TeiQuery teiQuery = TeiQuery.
                        Builder.create()
                        .withOrgUnit(orgUnitUid)
                        .withPage(page)
                        .withPageLimit(pageLimit)
                        .build();

                response = TeisEndPointCall.create(genericCallData, teiQuery).call();

                if (!response.isSuccessful()) {
                    return response;
                }

                teisDownloaded = teisDownloaded + teiQuery.getPageSize();
            }

        }

        if (response != null && response.isSuccessful()) {
            genericCallData.resourceHandler().handleResource(ResourceModel.Type.TRACKED_ENTITY_INSTANCE, serverDate);
        }

        return response;
    }


    public static TrackerEntitiesDataCall create(GenericCallData genericCallData, int teiLimitByOrgUnit) {
        return new TrackerEntitiesDataCall(
                genericCallData,
                OrganisationUnitStore.create(genericCallData.databaseAdapter()),
                teiLimitByOrgUnit
        );
    }
}
