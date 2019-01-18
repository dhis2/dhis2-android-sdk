package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Retrofit;

@Reusable
public final class TrackedEntityInstanceListDownloadAndPersistCallFactory {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ForeignKeyCleaner foreignKeyCleaner;


    @Inject
    TrackedEntityInstanceListDownloadAndPersistCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    public Callable<List<TrackedEntityInstance>> getCall(final Collection<String> trackedEntityInstanceUids) {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return downloadAndPersist(trackedEntityInstanceUids);
            }
        };
    }

    private List<TrackedEntityInstance> downloadAndPersist(final Collection<String> trackedEntityInstanceUids)
            throws D2Error {

        final D2CallExecutor executor = new D2CallExecutor(databaseAdapter);

        return executor.executeD2CallTransactionally(new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                if (trackedEntityInstanceUids == null) {
                    throw new IllegalArgumentException("UID list is null");
                }

                List<TrackedEntityInstance> teis = new ArrayList<>();
                D2CallExecutor executor = new D2CallExecutor(databaseAdapter);
                for (String uid : trackedEntityInstanceUids) {
                    Call<TrackedEntityInstance> teiCall = TrackedEntityInstanceDownloadByUidEndPointCall
                            .create(retrofit, APICallExecutorImpl.create(databaseAdapter), uid,
                                    TrackedEntityInstanceFields.allFields);
                    teis.add(executor.executeD2Call(teiCall));
                }

                executor.executeD2Call(TrackedEntityInstancePersistenceCall.create(databaseAdapter,
                        internalModules, teis));

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

                return teis;
            }
        });
    }
}
