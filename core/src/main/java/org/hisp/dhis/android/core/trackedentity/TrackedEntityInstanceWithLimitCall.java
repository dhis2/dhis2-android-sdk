package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

public final class TrackedEntityInstanceWithLimitCall extends SyncCall<Unit> {

    private final boolean limitByOrgUnit;
    private final int teiLimit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;

    private TrackedEntityInstanceWithLimitCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            int teiLimit,
            boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.teiLimit = teiLimit;
        this.limitByOrgUnit = limitByOrgUnit;
    }

    @Override
    public Unit call() throws D2Error {
        this.setExecuted();

        return new D2CallExecutor().executeD2CallTransactionally(databaseAdapter,
                new Callable<Unit>() {
                    @Override
                    public Unit call() {
                        return getTrackedEntityInstances();
                    }
                });
    }
    
    private Unit getTrackedEntityInstances() {
        Collection<String> organisationUnitUids;
        TeiQuery.Builder teiQueryBuilder = TeiQuery.Builder.create();
        int pageSize = teiQueryBuilder.build().getPageSize();
        List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, teiLimit);

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
            Set<String> orgUnitWrapper = new HashSet<>();
            for (String orgUnitUid : organisationUnitUids) {
                orgUnitWrapper.clear();
                orgUnitWrapper.add(orgUnitUid);
                teiQueryBuilder.withOrgUnits(orgUnitWrapper);
                getTrackedEntityInstancesWithPaging(teiQueryBuilder, pagingList);
            }
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            teiQueryBuilder.withOrgUnits(organisationUnitUids).withOuMode(OuMode.DESCENDANTS);
            getTrackedEntityInstancesWithPaging(teiQueryBuilder, pagingList);
        }

        return new Unit();
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private void getTrackedEntityInstancesWithPaging(TeiQuery.Builder teiQueryBuilder, List<Paging> pagingList) {
        D2CallExecutor executor = new D2CallExecutor();

        for (Paging paging : pagingList) {

            try {
                teiQueryBuilder.withPage(paging.page()).withPageSize(paging.pageSize());
                List<TrackedEntityInstance> pageTrackedEntityInstances = executor.executeD2Call(
                        TrackedEntityInstancesEndpointCall.create(retrofit, databaseAdapter, teiQueryBuilder.build()));

                if (paging.isLastPage()) {
                    int previousItemsToSkip = pageTrackedEntityInstances.size()
                            + paging.previousItemsToSkipCount() - paging.pageSize();
                    int toIndex = previousItemsToSkip < 0 ? pageTrackedEntityInstances.size() :
                            pageTrackedEntityInstances.size() - previousItemsToSkip;

                    executor.executeD2Call(
                            TrackedEntityInstancePersistenceCall.create(databaseAdapter, retrofit, internalModules,
                                    pageTrackedEntityInstances.subList(paging.previousItemsToSkipCount(), toIndex)));
                } else {
                    executor.executeD2Call(TrackedEntityInstancePersistenceCall.create(databaseAdapter, retrofit,
                            internalModules, pageTrackedEntityInstances));
                }

                if (pageTrackedEntityInstances.size() < paging.pageSize()) {
                    break;
                }

            } catch (D2Error ignored) {
                // The D2Error is ignored so that all calls are executed.
            }
        }
    }

    private Set<String> getOrgUnitUids() {
        List<UserOrganisationUnitLinkModel> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll();

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLinkModel linkModel: userOrganisationUnitLinks) {
            if (linkModel.organisationUnitScope().equals(
                    OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(linkModel.organisationUnit());
            }
        }

        return organisationUnitUids;
    }

    public static TrackedEntityInstanceWithLimitCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                            D2InternalModules internalModules,
                                                            int teiLimit, boolean limitByOrgUnit) {
        return new TrackedEntityInstanceWithLimitCall(
                databaseAdapter,
                retrofit,
                internalModules,
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                teiLimit,
                limitByOrgUnit
        );
    }
}