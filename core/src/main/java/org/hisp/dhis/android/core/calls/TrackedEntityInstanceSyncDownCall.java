package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceListDownloadAndPersistCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

public final class TrackedEntityInstanceSyncDownCall extends SyncCall<List<TrackedEntityInstance>> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    private TrackedEntityInstanceSyncDownCall(@NonNull DatabaseAdapter databaseAdapter,
                                              @NonNull Retrofit retrofit,
                                              @NonNull D2InternalModules internalModules,
                                              @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2Error {
        setExecuted();
        Map<String, TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.querySynced();
        Call<List<TrackedEntityInstance>> call = TrackedEntityInstanceListDownloadAndPersistCall
                .create(databaseAdapter, retrofit, internalModules, trackedEntityInstances.keySet());
        return new D2CallExecutor(databaseAdapter).executeD2Call(call);
    }

    public static TrackedEntityInstanceSyncDownCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                           D2InternalModules internalModules) {
        return new TrackedEntityInstanceSyncDownCall(
                databaseAdapter,
                retrofit,
                internalModules,
                new TrackedEntityInstanceStoreImpl(databaseAdapter)
        );
    }
}
