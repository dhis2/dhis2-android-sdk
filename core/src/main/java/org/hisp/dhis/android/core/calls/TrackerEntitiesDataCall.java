package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.trackedentity.TeiQuery;
import org.hisp.dhis.android.core.trackedentity.TeisEndPointCall;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

public final class TrackerEntitiesDataCall extends SyncCall<Response> {

    private final int teiLimitByOrgUnit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final ObjectWithoutUidStore<UserOrganisationUnitLinkModel> userOrganisationUnitLinkStore;

    private TrackerEntitiesDataCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull ObjectWithoutUidStore<UserOrganisationUnitLinkModel> userOrganisationUnitLinkStore,
            int teiLimitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.teiLimitByOrgUnit = teiLimitByOrgUnit;
    }

    @Override
    public Response call() throws Exception {
        this.setExecuted();

        Response response = null;

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            response = SystemInfoCall.FACTORY.create(databaseAdapter, retrofit).call();

            if (!response.isSuccessful()) {
                return response;
            }

            SystemInfo systemInfo = (SystemInfo) response.body();
            GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                    systemInfo.serverDate());

            response = trackerCall(genericCallData);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            transaction.setSuccessful();

            return response;
        } finally {
            transaction.end();
        }
    }
    
    private Response trackerCall(GenericCallData genericCallData) throws Exception {
        Response response = null;

        Set<UserOrganisationUnitLinkModel> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll(
                UserOrganisationUnitLinkModel.factory);

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLinkModel linkModel: userOrganisationUnitLinks) {
            if (linkModel.organisationUnitScope().equals(
                    OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(linkModel.organisationUnit());
            }
        }

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
            genericCallData.resourceHandler().handleResource(ResourceModel.Type.TRACKED_ENTITY_INSTANCE,
                    genericCallData.serverDate());
        }

        return response;
    }


    public static TrackerEntitiesDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                 int teiLimitByOrgUnit) {
        return new TrackerEntitiesDataCall(
                databaseAdapter,
                retrofit,
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                teiLimitByOrgUnit
        );
    }
}