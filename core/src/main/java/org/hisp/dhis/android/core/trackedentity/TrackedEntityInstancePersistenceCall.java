package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitOnDemandCall;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

final class TrackedEntityInstancePersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final APICallExecutor apiCallExecutor;
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final SearchOrganisationUnitOnDemandCall.Factory searchOrganisationUnitOnDemandCallFactory;
    private final ForeignKeyCleaner foreignKeyCleaner;

    private final Collection<TrackedEntityInstance> trackedEntityInstances;

    private TrackedEntityInstancePersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull D2InternalModules internalModules,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityInstanceUidHelper uidsHelper,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull SearchOrganisationUnitOnDemandCall.Factory searchOrganisationUnitOnDemandCallFactory,
            @NonNull Collection<TrackedEntityInstance> trackedEntityInstances,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.apiCallExecutor = apiCallExecutor;
        this.internalModules = internalModules;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.searchOrganisationUnitOnDemandCallFactory = searchOrganisationUnitOnDemandCallFactory;
        this.trackedEntityInstances = trackedEntityInstances;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Void call() throws D2Error {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                trackedEntityInstanceHandler.handleMany(trackedEntityInstances, false);

                Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Call<List<OrganisationUnit>> organisationUnitCall =
                            searchOrganisationUnitOnDemandCallFactory.create(
                                databaseAdapter, retrofit, searchOrgUnitUids,
                                User.builder().uid(authenticatedUserModel.user()).build(), apiCallExecutor);
                    organisationUnitCall.call();
                }

                if (!internalModules.systemInfo.publicModule.versionManager.is2_29()) {
                    // TODO Replace by method 'selectUidsWhere' from IdentifiableObjectStore once migrated
                    Set<String> relationships = trackedEntityInstanceStore.queryRelationships().keySet();

                    if (!relationships.isEmpty()) {
                        Call<List<TrackedEntityInstance>> relationshipsCall =
                                TrackedEntityInstanceRelationshipDownloadAndPersistCall.create(
                                        databaseAdapter, retrofit, internalModules, relationships
                                );
                        executor.executeD2Call(relationshipsCall);
                    }
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return null;
            }
        });
    }

    public static TrackedEntityInstancePersistenceCall create(DatabaseAdapter databaseAdapter,
                                                              Retrofit retrofit,
                                                              D2InternalModules internalModules,
                                                              Collection<TrackedEntityInstance>
                                                                      trackedEntityInstances) {
        return new TrackedEntityInstancePersistenceCall(
                databaseAdapter,
                retrofit,
                APICallExecutorImpl.create(databaseAdapter),
                internalModules,
                TrackedEntityInstanceHandler.create(databaseAdapter, internalModules),
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                TrackedEntityInstanceUidHelperImpl.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                SearchOrganisationUnitOnDemandCall.FACTORY,
                trackedEntityInstances,
                ForeignKeyCleanerImpl.create(databaseAdapter)
        );
    }
}