package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Retrofit;

public final class TrackedEntityInstanceWithLimitCall extends SyncCall<List<TrackedEntityInstance>> {

    private final boolean limitByOrgUnit;
    private final int teiLimit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final D2CallException.Builder httpExceptionBuilder;

    private TrackedEntityInstanceWithLimitCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            int teiLimit,
            boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
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
            List<TrackedEntityInstance> trackedEntityInstances = getTrackedEntityInstances();

            transaction.setSuccessful();

            return trackedEntityInstances;

        } catch (Exception e) {
            throw httpExceptionBuilder.originalException(e).build();
        } finally {
            transaction.end();
        }
    }
    
    private List<TrackedEntityInstance> getTrackedEntityInstances() throws D2CallException {
        Set<String> organisationUnitUids;
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        TeiQuery.Builder teiQueryBuilder = TeiQuery.Builder.create();
        int pageSize = teiQueryBuilder.build().getPageSize();
        int numPages = (int) Math.ceil((double) teiLimit / pageSize);

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
            Set<String> orgUnitWrapper = new HashSet<>();
            for (String orgUnitUid : organisationUnitUids) {
                orgUnitWrapper.clear();
                orgUnitWrapper.add(orgUnitUid);
                teiQueryBuilder.withOrgUnits(orgUnitWrapper);
                trackedEntityInstances.addAll(getTrackedEntityInstancesWithPaging(teiQueryBuilder, pageSize, numPages));
            }
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            teiQueryBuilder.withOrgUnits(organisationUnitUids).withOuMode(OuMode.DESCENDANTS);
            trackedEntityInstances = getTrackedEntityInstancesWithPaging(teiQueryBuilder, pageSize, numPages);
        }

        return trackedEntityInstances;
    }

    private List<TrackedEntityInstance> getTrackedEntityInstancesWithPaging(
            TeiQuery.Builder teiQueryBuilder, int pageSize, int numPages)
            throws D2CallException {
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
        D2CallExecutor executor = new D2CallExecutor();

        for (int page = 1; page <= numPages; page++) {
            if (page == numPages) {
                teiQueryBuilder.withPage(page).withPageLimit(teiLimit - ((page - 1) * pageSize));
            }
            teiQueryBuilder.withPage(page);
            List<TrackedEntityInstance> pageTrackedEntityInstances = executor.executeD2Call(
                    TrackedEntityInstancesEndpointCall.create(retrofit, teiQueryBuilder.build()));

            trackedEntityInstances.addAll(pageTrackedEntityInstances);

            if (pageTrackedEntityInstances.size() < pageSize) {
                break;
            }
        }

        executor.executeD2Call(
                TrackedEntityInstancePersistenceCall.create(databaseAdapter, retrofit, trackedEntityInstances));

        return trackedEntityInstances;
    }

    private Set<String> getOrgUnitUids() {
        Set<UserOrganisationUnitLinkModel> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll(
                UserOrganisationUnitLinkModel.factory);

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLinkModel linkModel: userOrganisationUnitLinks) {
            if (linkModel.organisationUnitScope().equals(
                    OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(linkModel.organisationUnit());
            }
        }

        return organisationUnitUids;
    }

    public static TrackedEntityInstanceWithLimitCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                            int teiLimit, boolean limitByOrgUnit) {
        return new TrackedEntityInstanceWithLimitCall(
                databaseAdapter,
                retrofit,
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                teiLimit,
                limitByOrgUnit
        );
    }
}