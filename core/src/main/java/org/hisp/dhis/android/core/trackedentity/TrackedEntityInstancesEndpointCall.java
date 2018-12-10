package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public final class TrackedEntityInstancesEndpointCall extends SyncCall<List<TrackedEntityInstance>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TeiQuery trackerQuery;
    private final APICallExecutor apiCallExecutor;

    private TrackedEntityInstancesEndpointCall(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TeiQuery trackerQuery, APICallExecutor apiCallExecutor) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public List<TrackedEntityInstance> call() throws D2Error {
        setExecuted();

        Call<Payload<TrackedEntityInstance>> call = trackedEntityInstanceService.getTrackedEntityInstances(
                Utils.joinCollectionWithSeparator(trackerQuery.orgUnits(), ";"),
                trackerQuery.ouMode().name(), TrackedEntityInstance.allFields, Boolean.TRUE,
                trackerQuery.page(), trackerQuery.pageSize(), trackerQuery.lastUpdatedStartDate(), true);

        return apiCallExecutor.executePayloadCall(call);
    }

    public static TrackedEntityInstancesEndpointCall create(Retrofit retrofit, DatabaseAdapter databaseAdapter,
                                                            TeiQuery teiQuery) {
        return new TrackedEntityInstancesEndpointCall(
                retrofit.create(TrackedEntityInstanceService.class),
                teiQuery,
                APICallExecutorImpl.create(databaseAdapter));
    }
}