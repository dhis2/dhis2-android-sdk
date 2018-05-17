package org.hisp.dhis.android.core.calls;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Payload;
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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
    private final D2CallException.Builder httpExceptionBuilder;

    private TrackerEntitiesDataCall(@NonNull DatabaseAdapter databaseAdapter,
                                    @NonNull Retrofit retrofit,
                                    @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore,
                                    int teiLimit,
                                    boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.organisationUnitStore = organisationUnitStore;
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
        Set<String> organisationUnitUids = organisationUnitStore.selectUids();

        List<TrackedEntityInstance> trackedEntityInstances = limitByOrgUnit ? teiSyncByOrgUnit(organisationUnitUids, genericCallData) :
                teiGlobalSync(organisationUnitUids, genericCallData);

        genericCallData.resourceHandler().handleResource(ResourceModel.Type.TRACKED_ENTITY_INSTANCE,
                genericCallData.serverDate());

        return trackedEntityInstances;
    }

    private List<TrackedEntityInstance> teiSyncByOrgUnit(
            Set<String> organisationUnitUids, GenericCallData genericCallData) throws D2CallException {
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        Response<Payload<TrackedEntityInstance>> trackerCallResponse = null;
        TeiQuery.Builder teiQueryBuilder = TeiQuery.Builder.create().withPageLimit(0);
        int numPages = (int) Math.ceil((double) teiLimit / TeiQuery.Builder.create().build().getPageSize());

        for (String orgUnitUid : organisationUnitUids) {
            teiQueryBuilder.withOrgUnits(new HashSet<>(Arrays.asList(orgUnitUid)));
            for (int page = 1; page <= numPages; page++) {
                try {
                    if (page == numPages) {
                        teiQueryBuilder.withPage(page).withPageLimit(teiLimit - trackedEntityInstances.size());
                    }
                    teiQueryBuilder.withPage(page);
                    trackerCallResponse = TeisEndPointCall.create(genericCallData, teiQueryBuilder.build()).call();

                    if (trackerCallResponse.isSuccessful()) {
                        trackedEntityInstances.addAll(trackerCallResponse.body().items());
                    }
                } catch (Exception e) {
                    throw httpExceptionBuilder.httpErrorCode(trackerCallResponse.code()).build();
                }
            }
        }
        return trackedEntityInstances;
    }

    private List<TrackedEntityInstance> teiGlobalSync(
            Set<String> organisationUnitUids, GenericCallData genericCallData) throws D2CallException {
        return new ArrayList<>();
    }


    public static TrackerEntitiesDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                 int teiLimit, boolean limitByOrgUnit) {
        return new TrackerEntitiesDataCall(
                databaseAdapter,
                retrofit,
                OrganisationUnitStore.create(databaseAdapter),
                teiLimit,
                limitByOrgUnit
        );
    }
}
