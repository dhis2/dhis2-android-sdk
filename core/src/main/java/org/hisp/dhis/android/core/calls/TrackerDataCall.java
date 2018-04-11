package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEndPointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.Date;
import java.util.Map;

import retrofit2.Response;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class TrackerDataCall implements Call<Response> {

    private final GenericCallData genericCallData;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    private boolean isExecuted;

    private TrackerDataCall(@NonNull GenericCallData genericCallData,
                            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.genericCallData = genericCallData;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }


    @Override
    public Response call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        Response response = null;

        Map<String, TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.querySynced();

        if (!trackedEntityInstances.isEmpty()) {
            Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
            try {
                response = SystemInfoCall.FACTORY.create(genericCallData).call();

                if (!response.isSuccessful()) {
                    return response;
                }

                SystemInfo systemInfo = (SystemInfo) response.body();
                Date serverDate = systemInfo.serverDate();

                response = trackedEntityInstanceCall(genericCallData, serverDate, trackedEntityInstances);

                transaction.setSuccessful();

            } finally {
                transaction.end();
            }
        }

        return response;
    }

    private Response trackedEntityInstanceCall(GenericCallData genericCallData, Date serverDate,
            Map<String, TrackedEntityInstance> trackedEntityInstances) throws Exception {
        Response response = null;

        for (Map.Entry<String, TrackedEntityInstance> entry : trackedEntityInstances.entrySet()) {

            response = TrackedEntityInstanceEndPointCall.create(genericCallData, serverDate,
                    entry.getValue().uid()).call();

            if (!response.isSuccessful()) {
                return response;
            }
        }

        return response;
    }

    public static TrackerDataCall create(GenericCallData genericCallData) {
        return new TrackerDataCall(
                genericCallData,
                new TrackedEntityInstanceStoreImpl(genericCallData.databaseAdapter())
        );
    }
}
