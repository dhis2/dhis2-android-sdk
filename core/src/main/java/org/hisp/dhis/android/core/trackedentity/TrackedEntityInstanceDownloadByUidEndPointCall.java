package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;

import retrofit2.Retrofit;

final class TrackedEntityInstanceDownloadByUidEndPointCall extends SyncCall<TrackedEntityInstance> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final String uid;
    private final Fields<TrackedEntityInstance> fields;

    private TrackedEntityInstanceDownloadByUidEndPointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull String uid,
            @NonNull Fields<TrackedEntityInstance> fields) {

        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.uid = uid;
        this.fields = fields;
    }

    @Override
    public TrackedEntityInstance call() throws D2CallException {
        setExecuted();

        if (uid == null) {
            throw new IllegalArgumentException("UID null");
        }

        retrofit2.Call<TrackedEntityInstance> call =
                trackedEntityInstanceService.getTrackedEntityInstance(uid, fields, true);

        return new APICallExecutor().executeObjectCall(call);
    }

    public static Call<TrackedEntityInstance> create(@NonNull Retrofit retrofit,
                                                     @NonNull String uid,
                                                     @NonNull Fields<TrackedEntityInstance> fields) {
        return new TrackedEntityInstanceDownloadByUidEndPointCall(
                retrofit.create(TrackedEntityInstanceService.class),
                uid,
                fields
        );
    }
}
