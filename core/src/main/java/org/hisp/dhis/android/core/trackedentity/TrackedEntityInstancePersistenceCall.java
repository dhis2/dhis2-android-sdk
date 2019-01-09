package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
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
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceUidHelper uidsHelper;
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;
    private final OrganisationUnitModuleDownloader organisationUnitDownloader;
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
            @NonNull OrganisationUnitModuleDownloader organisationUnitDownloader,
            @NonNull Collection<TrackedEntityInstance> trackedEntityInstances,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.uidsHelper = uidsHelper;
        this.authenticatedUserStore = authenticatedUserStore;
        this.organisationUnitDownloader = organisationUnitDownloader;
        this.trackedEntityInstances = trackedEntityInstances;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Void call() throws D2Error {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor(databaseAdapter);

        return executor.executeD2CallTransactionally(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                trackedEntityInstanceHandler.handleMany(trackedEntityInstances, false);

                Set<String> searchOrgUnitUids = uidsHelper.getMissingOrganisationUnitUids(trackedEntityInstances);

                if (!searchOrgUnitUids.isEmpty()) {
                    AuthenticatedUserModel authenticatedUserModel = authenticatedUserStore.selectFirst();

                    Callable<List<OrganisationUnit>> organisationUnitCall =
                            organisationUnitDownloader.downloadSearchOrganisationUnits(searchOrgUnitUids,
                                    User.builder().uid(authenticatedUserModel.user()).build());
                    organisationUnitCall.call();
                }

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
                TrackedEntityInstanceStoreImpl.create(databaseAdapter),
                TrackedEntityInstanceUidHelperImpl.create(databaseAdapter),
                AuthenticatedUserStore.create(databaseAdapter),
                internalModules.organisationUnit.moduleDownloader,
                trackedEntityInstances,
                ForeignKeyCleanerImpl.create(databaseAdapter)
        );
    }
}