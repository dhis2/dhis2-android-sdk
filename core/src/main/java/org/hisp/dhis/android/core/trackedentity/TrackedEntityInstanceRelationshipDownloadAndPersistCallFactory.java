package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory {

    private final APICallExecutor apiCallExecutor;
    private final D2CallExecutor d2CallExecutor;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityInstanceService service;
    private final TrackedEntityInstanceRelationshipPersistenceCallFactory persistenceCallFactory;

    @Inject
    TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory(
            @NonNull APICallExecutor apiCallExecutor,
            @NonNull D2CallExecutor d2CallExecutor,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityInstanceService service,
            @NonNull TrackedEntityInstanceRelationshipPersistenceCallFactory persistenceCallFactory) {
        this.apiCallExecutor = apiCallExecutor;
        this.d2CallExecutor = d2CallExecutor;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.service = service;
        this.persistenceCallFactory = persistenceCallFactory;
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

            for (String uid : relationships) {
                try {
                    retrofit2.Call<TrackedEntityInstance> teiCall =
                            service.getTrackedEntityInstance(uid, TrackedEntityInstanceFields.allFields, true);
                    teis.add(apiCallExecutor.executeObjectCall(teiCall));
                } catch (D2Error ignored) {
                    // Ignore
                }
            }

            d2CallExecutor.executeD2Call(persistenceCallFactory.getCall(teis));
        }

        return teis;
    }
}
