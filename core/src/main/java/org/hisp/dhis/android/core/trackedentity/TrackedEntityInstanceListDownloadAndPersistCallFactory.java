package org.hisp.dhis.android.core.trackedentity;

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
    private final ForeignKeyCleaner foreignKeyCleaner;
    private final TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory relationshipsCall;


    @Inject
    TrackedEntityInstanceListDownloadAndPersistCallFactory(
            DatabaseAdapter databaseAdapter,
            Retrofit retrofit,
            D2InternalModules internalModules,
            ForeignKeyCleaner foreignKeyCleaner,
            TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory relationshipsCall) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.foreignKeyCleaner = foreignKeyCleaner;
        this.relationshipsCall = relationshipsCall;
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

                if (!internalModules.systemInfo.publicModule.versionManager.is2_29()) {
                    executor.executeD2Call(relationshipsCall.getCall());
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return teis;
            }
        });
    }
}
