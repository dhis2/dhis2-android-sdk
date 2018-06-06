package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public final class TrackedEntityInstancesEndpointCall extends SyncCall<List<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TeiQuery trackerQuery;

    private TrackedEntityInstancesEndpointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TeiQuery trackerQuery) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2CallException {
        super.setExecuted();

        Integer teisToRequest = Math.min(trackerQuery.getPageLimit(), trackerQuery.getPageSize());
        Call<Payload<TrackedEntityInstance>> call = trackedEntityInstanceService.getTrackedEntityInstances(
                Utils.joinCollectionWithSeparator(trackerQuery.getOrgUnits(), ";"),
                trackerQuery.getOuMode().name(), TrackedEntityInstance.allFields, Boolean.TRUE,
                trackerQuery.getPage(), teisToRequest);

        return new APICallExecutor().executePayloadCall(call);
    }

    public static TrackedEntityInstancesEndpointCall create(Retrofit retrofit, TeiQuery teiQuery) {
        return new TrackedEntityInstancesEndpointCall(
                retrofit.create(TrackedEntityInstanceService.class),
                teiQuery);
    }
}