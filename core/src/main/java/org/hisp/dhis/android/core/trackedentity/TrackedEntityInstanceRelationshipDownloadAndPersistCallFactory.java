package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Retrofit;

@Reusable
public final class TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final APICallExecutor apiCallExecutor;
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Inject
    TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull D2InternalModules internalModules,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.apiCallExecutor = apiCallExecutor;
        this.internalModules = internalModules;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    public Callable<List<TrackedEntityInstance>> getCall() {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return downloadAndPersist();
            }
        };
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private List<TrackedEntityInstance> downloadAndPersist() throws D2Error {
        List<String> relationships = trackedEntityInstanceStore.queryMissingRelationshipsUids();

        List<TrackedEntityInstance> teis = new ArrayList<>();
        if (!relationships.isEmpty()) {
            D2CallExecutor executor = new D2CallExecutor(databaseAdapter);
            for (String uid : relationships) {
                try {
                    Call<TrackedEntityInstance> teiCall = TrackedEntityInstanceDownloadByUidEndPointCall
                            .create(retrofit, apiCallExecutor, uid, TrackedEntityInstanceFields.asRelationshipFields);
                    teis.add(executor.executeD2Call(teiCall));
                } catch (D2Error ignored) {
                    // Ignore
                }
            }

            executor.executeD2Call(TrackedEntityInstanceRelationshipPersistenceCall.create(databaseAdapter,
                    internalModules, teis));
        }

        return teis;
    }
}
