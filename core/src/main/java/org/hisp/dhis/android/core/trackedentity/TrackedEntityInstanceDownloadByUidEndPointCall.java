package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.maintenance.D2Error;

import retrofit2.Retrofit;

final class TrackedEntityInstanceDownloadByUidEndPointCall extends SyncCall<Payload<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final String uid;
    private final Fields<TrackedEntityInstance> fields;
    private final APICallExecutor apiCallExecutor;

    private TrackedEntityInstanceDownloadByUidEndPointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull String uid,
            @NonNull Fields<TrackedEntityInstance> fields,
            @NonNull APICallExecutor apiCallExecutor) {

        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.uid = uid;
        this.fields = fields;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public Payload<TrackedEntityInstance> call() throws D2Error {
        setExecuted();

        if (uid == null) {
            throw new IllegalArgumentException("UID null");
        }

        retrofit2.Call<Payload<TrackedEntityInstance>> call =
                trackedEntityInstanceService.getTrackedEntityInstance(uid, fields, true);

        return apiCallExecutor.executeObjectCall(call);
    }

    public static Call<Payload<TrackedEntityInstance>> create(@NonNull Retrofit retrofit,
                                                              @NonNull APICallExecutor apiCallExecutor,
                                                              @NonNull String uid,
                                                              @NonNull Fields<TrackedEntityInstance> fields) {
        return new TrackedEntityInstanceDownloadByUidEndPointCall(
                retrofit.create(TrackedEntityInstanceService.class),
                uid,
                fields,
                apiCallExecutor);
    }
}
