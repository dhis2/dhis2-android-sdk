package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.trackedentity.TeiQuery;
import org.hisp.dhis.android.core.trackedentity.TeisEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import retrofit2.Retrofit;

public final class TrackerEntitiesDataCall extends SyncCall<List<TrackedEntityInstance>> {

    private final boolean limitByOrgUnit;
    private final int teiLimit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final D2CallException.Builder httpExceptionBuilder;

    private TrackerEntitiesDataCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            int teiLimit,
            boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.organisationUnitStore = organisationUnitStore;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.teiLimit = teiLimit;
        this.limitByOrgUnit = limitByOrgUnit;
        this.httpExceptionBuilder = D2CallException.builder().isHttpError(true).errorDescription("TEIs call failed");
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2CallException {
        this.setExecuted();
        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            Response<SystemInfo> systemInfoResponse = SystemInfoCall.FACTORY.create(databaseAdapter, retrofit).call();

            if (!systemInfoResponse.isSuccessful()) {
                throw httpExceptionBuilder.httpErrorCode(systemInfoResponse.code()).build();
            }

            SystemInfo systemInfo = systemInfoResponse.body();
            GenericCallData genericCallData = GenericCallData.create(databaseAdapter, retrofit,
                    systemInfo.serverDate());

            return trackerCall(genericCallData);

        } catch (Exception e) {
            throw httpExceptionBuilder.originalException(e).build();
        } finally {
            transaction.end();
        }
    }
    
    private List<TrackedEntityInstance> trackerCall(GenericCallData genericCallData) throws D2CallException {
        Set<String> organisationUnitUids;
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        TeiQuery.Builder teiQueryBuilder = TeiQuery.Builder.create();
        int pageSize = TeiQuery.Builder.create().build().getPageSize();
        int numPages = (int) Math.ceil((double) teiLimit / pageSize);

        if (limitByOrgUnit) {
            organisationUnitUids = organisationUnitStore.selectUids();
            Set<String> orgUnitWrapper = new HashSet<>();
            for (String orgUnitUid : organisationUnitUids) {
                orgUnitWrapper.clear();
                orgUnitWrapper.add(orgUnitUid);
                teiQueryBuilder.withOrgUnits(orgUnitWrapper);
                trackedEntityInstances.addAll(getTrackedEntityInstances(teiQueryBuilder, genericCallData, pageSize,
                        numPages));
            }
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            teiQueryBuilder.withOrgUnits(organisationUnitUids).withOuMode(OuMode.DESCENDANTS);
            trackedEntityInstances = getTrackedEntityInstances(teiQueryBuilder, genericCallData, pageSize, numPages);
        }

        return trackedEntityInstances;
    }

    private List<TrackedEntityInstance> getTrackedEntityInstances(TeiQuery.Builder teiQueryBuilder,
            GenericCallData genericCallData, int pageSize, int numPages) throws D2CallException {
        Response<Payload<TrackedEntityInstance>> trackerCallResponse;
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();

        for (int page = 1; page <= numPages; page++) {
            try {
                if (page == numPages) {
                    teiQueryBuilder.withPage(page).withPageLimit(teiLimit - ((page - 1) * pageSize));
                }
                teiQueryBuilder.withPage(page);
                 trackerCallResponse = TeisEndPointCall.create(genericCallData, teiQueryBuilder.build()).call();

                if (!trackerCallResponse.isSuccessful()) {
                    throw httpExceptionBuilder.httpErrorCode(trackerCallResponse.code()).build();
                }

                trackedEntityInstances.addAll(trackerCallResponse.body().items());
            } catch (Exception e) {
                throw httpExceptionBuilder.originalException(e).build();
            }
        }
        return trackedEntityInstances;
    }

    public static TrackerEntitiesDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                 int teiLimit, boolean limitByOrgUnit) {
        return new TrackerEntitiesDataCall(
                databaseAdapter,
                retrofit,
                OrganisationUnitStore.create(databaseAdapter),
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                teiLimit,
                limitByOrgUnit
        );
    }
}