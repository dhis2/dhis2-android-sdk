package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;
import org.hisp.dhis.android.core.utils.services.ApiPagingEngine;
import org.hisp.dhis.android.core.utils.services.Paging;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Retrofit;

@Reusable
public final class TrackedEntityInstanceWithLimitCallFactory {

    private final ResourceModel.Type resourceType = ResourceModel.Type.TRACKED_ENTITY_INSTANCE;

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;
    private final ResourceHandler resourceHandler;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ForeignKeyCleaner foreignKeyCleaner;

    @Inject
    TrackedEntityInstanceWithLimitCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull ResourceHandler resourceHandler,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.resourceHandler = resourceHandler;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    public Callable<Unit> getCall(final int teiLimit, final boolean limitByOrgUnit) {
        return new Callable<Unit>() {
            @Override
            public Unit call() throws D2Error {
                return getTrackedEntityInstances(teiLimit, limitByOrgUnit);
            }
        };
    }
    
    private Unit getTrackedEntityInstances(final int teiLimit, final boolean limitByOrgUnit) throws D2Error {
        final D2CallExecutor executor = new D2CallExecutor(databaseAdapter);

        return executor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() throws Exception {
                Collection<String> organisationUnitUids;
                TeiQuery.Builder teiQueryBuilder = TeiQuery.builder();
                int pageSize = teiQueryBuilder.build().pageSize();
                List<Paging> pagingList = ApiPagingEngine.getPaginationList(pageSize, teiLimit);

                String lastUpdatedStartDate = resourceHandler.getLastUpdated(resourceType);
                teiQueryBuilder.lastUpdatedStartDate(lastUpdatedStartDate);

                internalModules.systemInfo.publicModule.systemInfo.download().call();

                if (limitByOrgUnit) {
                    organisationUnitUids = getOrgUnitUids();
                    Set<String> orgUnitWrapper = new HashSet<>();
                    for (String orgUnitUid : organisationUnitUids) {
                        orgUnitWrapper.clear();
                        orgUnitWrapper.add(orgUnitUid);
                        teiQueryBuilder.orgUnits(orgUnitWrapper);
                        getTrackedEntityInstancesWithPaging(teiQueryBuilder, pagingList);
                    }
                } else {
                    organisationUnitUids = userOrganisationUnitLinkStore.queryRootCaptureOrganisationUnitUids();
                    teiQueryBuilder.orgUnits(organisationUnitUids).ouMode(OuMode.DESCENDANTS);
                    getTrackedEntityInstancesWithPaging(teiQueryBuilder, pagingList);
                }

                // TODO Wrap in try-catch?
                if (!internalModules.systemInfo.publicModule.versionManager.is2_29()) {
                    List<String> relationships = trackedEntityInstanceStore.queryRelationshipsUids();

                    if (!relationships.isEmpty()) {
                        Call<List<TrackedEntityInstance>> relationshipsCall =
                                TrackedEntityInstanceRelationshipDownloadAndPersistCall.create(
                                        databaseAdapter, retrofit, internalModules, relationships
                                );
                        executor.executeD2Call(relationshipsCall);
                    }
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return new Unit();
            }
        });
    }

    private void getTrackedEntityInstancesWithPaging(TeiQuery.Builder teiQueryBuilder, List<Paging> pagingList) {
        boolean successfulSync = true;

        for (Paging paging : pagingList) {
            try {
                teiQueryBuilder.page(paging.page()).pageSize(paging.pageSize());
                List<TrackedEntityInstance> pageTrackedEntityInstances =
                        TrackedEntityInstancesEndpointCall.create(retrofit, databaseAdapter, teiQueryBuilder.build())
                                .call();

                if (paging.isLastPage() && pageTrackedEntityInstances.size() > paging.previousItemsToSkipCount()) {
                    int toIndex = pageTrackedEntityInstances.size() <
                            paging.pageSize() - paging.posteriorItemsToSkipCount() ?
                            pageTrackedEntityInstances.size() :
                            paging.pageSize() - paging.posteriorItemsToSkipCount();

                    TrackedEntityInstancePersistenceCall.create(databaseAdapter, internalModules,
                            pageTrackedEntityInstances.subList(paging.previousItemsToSkipCount(), toIndex)).call();

                } else {
                    TrackedEntityInstancePersistenceCall.create(databaseAdapter,
                            internalModules, pageTrackedEntityInstances).call();
                }

                if (pageTrackedEntityInstances.size() < paging.pageSize()) {
                    break;
                }

            } catch (D2Error ignored) {
                successfulSync = false;
            }
        }

        if (successfulSync) {
            resourceHandler.handleResource(resourceType);
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
}