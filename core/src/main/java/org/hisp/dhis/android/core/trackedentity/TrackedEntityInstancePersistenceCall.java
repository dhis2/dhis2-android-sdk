package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.ForeignKeyCleaner;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.SearchOrganisationUnitCall;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserStore;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Retrofit;

final class TrackedEntityInstancePersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final SearchOrganisationUnitCall.Factory organisationUnitCallFactory;
    private final ForeignKeyCleaner foreignKeyCleaner;

    private final Collection<TrackedEntityInstance> trackedEntityInstances;

    private TrackedEntityInstancePersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityInstanceUidHelper uidsHelper,
            @NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore,
            @NonNull SearchOrganisationUnitCall.Factory organisationUnitCallFactory,
            @NonNull Collection<TrackedEntityInstance> trackedEntityInstances,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitCallFactory = organisationUnitCallFactory;
        this.trackedEntityInstances = trackedEntityInstances;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Void call() throws D2CallException {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(databaseAdapter, new Callable<Void>() {
            @Override
            public Void call() throws D2CallException {
                trackedEntityInstanceHandler.handleMany(trackedEntityInstances, false);
                Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Call<List<OrganisationUnit>> organisationUnitCall = organisationUnitCallFactory.create(
                            databaseAdapter, retrofit, searchOrgUnitUids, authenticatedUserModel.user());
                    executor.executeD2Call(organisationUnitCall);
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
                internalModules,
                TrackedEntityInstanceHandler.create(databaseAdapter, internalModules),
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                TrackedEntityInstanceUidHelperImpl.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                SearchOrganisationUnitCall.FACTORY,
                trackedEntityInstances,
                new ForeignKeyCleaner(databaseAdapter)
        );
    }
}
