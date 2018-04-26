package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BlockCallData;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.Map;

import retrofit2.Response;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class TrackerDataCall extends SyncCall {

    private final BlockCallData blockCallData;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    private TrackerDataCall(@NonNull BlockCallData blockCallData,
                            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.blockCallData = blockCallData;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    @Override
    public Response call() throws Exception {
        super.setExecuted();

        Response response = null;

        Map<String, TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.querySynced();

        if (!trackedEntityInstances.isEmpty()) {
            Transaction transaction = blockCallData.databaseAdapter().beginNewTransaction();
            try {
                response = trackedEntityInstanceCall(trackedEntityInstances);
                transaction.setSuccessful();
            } finally {
                transaction.end();
            }
        }

        return response;
    }

    private Response trackedEntityInstanceCall(
            Map<String, TrackedEntityInstance> trackedEntityInstances) throws Exception {
        Response response = null;

        for (Map.Entry<String, TrackedEntityInstance> entry : trackedEntityInstances.entrySet()) {

            response = TrackedEntityInstanceEndPointCall.create(blockCallData,
                    entry.getValue().uid()).call();

            if (!response.isSuccessful()) {
                return response;
            }
        }

        return response;
    }

    public static TrackerDataCall create(BlockCallData blockCallData) {
        return new TrackerDataCall(
                blockCallData,
                new TrackedEntityInstanceStoreImpl(blockCallData.databaseAdapter())
        );
    }
}
