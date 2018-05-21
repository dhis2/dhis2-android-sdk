package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceByUidEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

public final class TrackerDataCall extends SyncCall<List<TrackedEntityInstance>> {

    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    private TrackerDataCall(@NonNull DatabaseAdapter databaseAdapter,
                            @NonNull Retrofit retrofit,
                            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2CallException {
        super.setExecuted();

        Map<String, TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.querySynced();

        try {
            return TrackedEntityInstanceByUidEndPointCall.create(databaseAdapter,
                    retrofit, trackedEntityInstances.keySet()).call();
        } catch (D2CallException d2E) {
            throw d2E;
        } catch (Exception e) {
            throw D2CallException
                    .builder()
                    .errorDescription("Unexpected exception in TrackedEntityInstanceByUidEndPointCall")
                    .originalException(e)
                    .isHttpError(false).build();
        }
    }

    public static TrackerDataCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
        return new TrackerDataCall(
                databaseAdapter,
                retrofit,
                new TrackedEntityInstanceStoreImpl(databaseAdapter)
        );
    }
}
