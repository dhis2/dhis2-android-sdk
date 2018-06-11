package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;

import retrofit2.Retrofit;

final class TrackedEntityInstanceDownloadByUidEndPointCall extends SyncCall<TrackedEntityInstance> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final String uid;

    private TrackedEntityInstanceDownloadByUidEndPointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull String uid) {

        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.uid = uid;
    }

    @Override
    public TrackedEntityInstance call() throws D2CallException {
        setExecuted();

        if (uid == null) {
            throw D2CallException.builder().isHttpError(false).errorDescription("UID null").build();
        }

        retrofit2.Call<TrackedEntityInstance> call =
                trackedEntityInstanceService.getTrackedEntityInstance(uid,
                        TrackedEntityInstance.allFields, true);

        return new APICallExecutor().executeObjectCall(call);
    }

    public static Call<TrackedEntityInstance> create(Retrofit retrofit, String uid) {
        return new TrackedEntityInstanceDownloadByUidEndPointCall(
                retrofit.create(TrackedEntityInstanceService.class),
                uid
        );
    }
}
