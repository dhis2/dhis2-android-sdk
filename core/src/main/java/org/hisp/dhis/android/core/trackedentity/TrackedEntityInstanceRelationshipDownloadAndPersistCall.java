package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Retrofit;

public final class TrackedEntityInstanceRelationshipDownloadAndPersistCall extends SyncCall<List<TrackedEntityInstance>> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final D2InternalModules internalModules;

    private final Collection<String> trackedEntityInstanceUids;

    private TrackedEntityInstanceRelationshipDownloadAndPersistCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull D2InternalModules internalModules,
            @NonNull Collection<String> trackedEntityInstanceUids) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.internalModules = internalModules;
        this.trackedEntityInstanceUids = trackedEntityInstanceUids;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2CallException {
        setExecuted();

        if (trackedEntityInstanceUids == null) {
            throw new IllegalArgumentException("UID list is null");
        }

        List<TrackedEntityInstance> teis = new ArrayList<>();
        D2CallExecutor executor = new D2CallExecutor();
        for (String uid: trackedEntityInstanceUids) {
            Call<TrackedEntityInstance> teiCall = TrackedEntityInstanceDownloadByUidEndPointCall
                    .create(retrofit, uid, TrackedEntityInstance.asRelationshipFields);
            teis.add(executor.executeD2Call(teiCall));
        }

        executor.executeD2Call(TrackedEntityInstanceRelationshipPersistenceCall.create(databaseAdapter, retrofit,
                internalModules, teis));

        return teis;
    }

    public static Call<List<TrackedEntityInstance>> create(@NonNull DatabaseAdapter databaseAdapter,
                                                           @NonNull Retrofit retrofit,
                                                           @NonNull D2InternalModules internalModules,
                                                           @NonNull Collection<String> trackedEntityInstanceUids) {
        return new TrackedEntityInstanceRelationshipDownloadAndPersistCall(
                databaseAdapter,
                retrofit,
                internalModules,
                trackedEntityInstanceUids
        );
    }
}
